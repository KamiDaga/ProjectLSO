#include <netinet/in.h>
#include <unistd.h>
#include <pthread.h>
#include <stdio.h>
#include <signal.h>
#include <stdlib.h>
#include <arpa/inet.h>
#include <string.h>
#include <errno.h>
#include <mysql/mysql.h>

//Struttura per il db
MYSQL *con;
//mutex per la lista
pthread_cond_t condMutexQueue;
pthread_mutex_t mutexQueue;
pthread_mutex_t mutexDb;

pthread_t mainthread; //processo che gestisce le chiamate
pthread_t queuemanager; //processo che gestisce le persone in ordering/serving

//Processi che gestiscono il flow da serving a ordering.
//Il farewelling viene fatto direttamente da queuemanager.
pthread_t cl1 =-1;
pthread_t cl2 =-1;
//Thread per l 'out of sight
// pthread_t oos1;
// pthread_t oos2;

// int cl1ToOOs = 0;
// int cl2ToOOs = 0;


int inServingWaiting = 0;

///////////////////////////////////////////
//Definizione dei nodi per mantenere tutti i client (socket e stati)
struct pnode{
    int socketc; //socket client
    struct sockaddr_in indirizzoc; //indirizzo client
    pthread_t id; //pthread id
    char* state;//Possibili valori:
    //WELCOME, WAITING, ORDERING, SERVING, SERVED, FAREWELLING,GONE

    struct pnode * next; //prossimo elemento
    struct pnode * prev; //elemento successivo
};

typedef struct pnode pnodet;
pnodet* head;
pnodet* tail;

void enqueue(pnodet* node)
{
    if(head==NULL)
    {
        head = node;
        tail = head;
        node->next = NULL;
        node->prev = NULL;
    }
    else
    {
        tail->next = node;
        tail->next->prev = tail;
        tail = tail->next;
        tail->next =NULL;
    }
}

void dequeue()
{
    if(head != NULL)
    {
        if(head == tail)
        {
            head = NULL;
            tail = NULL;
        }
        else if(head != tail)
        {
            pnodet* temp = head;
            head = temp->next;
            head->prev = NULL;
            temp->next = NULL;
        }
    }
}

void removeNode(pnodet* id)
{
    int found = 0;
    pnodet* explorer = head;
    pnodet* preexplorer = explorer;
    while(explorer != NULL && found == 0)
    {
        if(explorer == id)
        {
            found = 1;
        }
        else
        {
            preexplorer = explorer;
            explorer = explorer->next;
        }
    }

    if(explorer != NULL && explorer != preexplorer)
    {
        explorer->prev = NULL;
        preexplorer->next = explorer->next;
        explorer->next->prev = preexplorer;
        explorer->next = NULL;
    }
    else if(explorer != NULL && explorer == preexplorer)
    {
        dequeue();
    }
}

pnodet* find(pthread_t id)
{
    int found = 0;
    pnodet* explorer = head;
    while(explorer != NULL && found == 0)
    {
        if(explorer->id == id)
        {
            found = 1;
        }
        explorer = explorer->next;
    }

    return explorer;

}

void removeDisconnected()
{
    pnodet* explorer = head;
    while(explorer != NULL)
    {
        if(strcmp(explorer->state,"GONE")==0)
        {
            pnodet* tmp = explorer;
            explorer = explorer->next;
            removeNode(tmp);
            close(tmp->socketc);
            free(tmp);
        }
        else
            explorer = explorer->next;
    }
}

void printlist()
{
    printf("[ ");
    pnodet* explorer = head;
    while(explorer != NULL)
    {
        printf("%lu -",explorer->id);
        explorer = explorer->next;
    }
    printf(" ]\n");

}
///////////////////////////

//funzioni per la gestione degli stati
//////////////////////////////
void farewell(pnodet* clientconn)
{
    char addio[30];
    memset(addio,0,sizeof(addio));
    strcpy(addio,"Alla prossima bevuta!\n");
    send(clientconn->socketc,addio,sizeof(addio),0);
    pthread_mutex_lock(&mutexQueue);
    clientconn->state = "GONE";
    pthread_mutex_unlock(&mutexQueue);
    //printf("%s\n",clientconn->state);

}

void conversation(pnodet* clientconn)
{
    int fineDialogo = 0;
    while(fineDialogo == 0)
    {
        char domanda[500];
        char query[150];
        char rispostain[40];
        memset(rispostain,0,sizeof(rispostain));
        memset(domanda,0,sizeof(domanda));
        // char* rispostaS = "Dimmi un argomento!";
        // send(clientconn->socketc, rispostaS,strlen(rispostaS),0);
        
        if(read(clientconn->socketc,rispostain,sizeof(rispostain))==0)
        {
            strcpy(rispostain,"gone");
        }

        printf("Risposta: %s!\n",rispostain);
        if(strcmp(rispostain,"gone") == 0)
            fineDialogo = 1;
        else if(strcmp(rispostain,"inizio")==0)//Le domande vengono mandate al client nella forma
                                            //DOMANDA/RISPOSTAP/RISPOSTAN/FEEDBACKP/FEEDBACKN
        {  
            strcpy(domanda,"Ciao! Com'e' andata la tua giornata? Spero tutto bene./E' stata abbastanza tranquilla./E' stata un disastro./Mi fa piacere! Spero per te che continui cosi!/Mi dispiace! Spero di riuscire ad alleggeriri la giornata!");
            send(clientconn->socketc, domanda,strlen(domanda),0);
        }
        else if(strcmp(rispostain,"fine")==0) //La domanda finale non verra' risposta. Quindi mando DOMANDA////
        {
            strcpy(domanda,"Il tuo drink e' pronto. Spero di non essermi distratto troppo mentre parlavo con te. A presto!////");
            send(clientconn->socketc, domanda,strlen(domanda),0);
            fineDialogo=1;
        }
        else if (strlen(rispostain)==0)
        {}
        else //abbiamo un tag
        {
            sprintf(query,"SELECT domanda,rispostaP,rispostaN,feedbackP,feedbackN FROM Domande WHERE tag = '%s' ORDER BY RAND();",rispostain);
            pthread_mutex_lock(&mutexDb);
            mysql_query(con,query);
            MYSQL_RES* res = mysql_store_result(con);
            MYSQL_ROW row;
            if((row = mysql_fetch_row(res))==NULL) //se non si e' trovato nulla
            {
                mysql_free_result(res);
                memset(query,0,sizeof(query));
                sprintf(query,"SELECT domanda,rispostaP,rispostaN,feedbackP,feedbackN FROM Domande ORDER BY RAND();");
                mysql_query(con,query);
                res = mysql_store_result(con);
                row = mysql_fetch_row(res);
            }
            // else row = mysql_fetch_row(res);
            
            sprintf(domanda,"%s/%s/%s/%s/%s",row[0],row[1],row[2],row[3],row[4]);
            mysql_free_result(res);
            send(clientconn->socketc, domanda,strlen(domanda),0);
            pthread_mutex_unlock(&mutexDb);
        }
    }
}

void serve(pnodet* clientconn, char* drink)
{
    char* request = "Ottima scelta!\nMentre preparo il tuo drink, ti andrebbe una chiacchierata?\n";
    //printf("%s\n",drink);
    char risposta[20];
    int prepare = 1;
    int quittiamo = 0;
    int gochat = 0;
    send(clientconn->socketc, request,strlen(request),0);
    while(quittiamo == 0)
    {
        memset(risposta,0,sizeof(risposta));
        printf("%s sto nel while\n",risposta);
        if(read(clientconn->socketc,risposta,sizeof(risposta))==0)
            {
                strcpy(risposta,"gone");
            }
        printf("%s\n",risposta);
        if(strcmp(risposta,"gone")==0)
        {
            prepare = 0;
            quittiamo = 1;
        }
        else if(strcmp(risposta,"oos")==0)
        {
            char* rispostaS = prepare == 1 ? "A piu tardi!\n" : "Bentornato!";
            send(clientconn->socketc, rispostaS,strlen(rispostaS),0);
            prepare = prepare==1 ?  0 : 1;
        }
        else if(prepare ==1)
        {
            if(strcmp(risposta,"si")==0)
            { 
                gochat = 1;
                quittiamo = 1;
            }
        }
        else 
        {
            //niente siccome e' un comando inesistente, ne aspettiamo un'altro
        }
    }

    if(prepare ==1) //controlliamo non sia andato via o non si sia disconnesso
    {
        if(gochat == 1)
        {
            // printf("Yuhuuu\n");
            read(clientconn->socketc,risposta,sizeof(risposta));
            read(clientconn->socketc,risposta,sizeof(risposta));
            read(clientconn->socketc,risposta,sizeof(risposta));
            conversation(clientconn);
        }
        else
        {
            memset(risposta,0,sizeof(risposta));
            if(read(clientconn->socketc,risposta,sizeof(risposta))==0) //Essenzialmente il client dice al server che ha finito
                                                                    //Non importa il contenuto di risposta.
            {
                strcpy(risposta,"gone"); //Neanche in questo caso, e' solo per readability del codice
            }
        }
    }
    pthread_mutex_lock(&mutexQueue);
    clientconn->state = "SERVED";
    while(strcmp(clientconn->state,"FAREWELLING")!=0)
    {
        pthread_cond_wait(&condMutexQueue,&mutexQueue);
    }

    pthread_mutex_unlock(&mutexQueue);
    farewell(clientconn);
}

void order(pnodet* clientconn)
{
    char menu[500];
    int countm = 0;
    //contiene anche i tempi di preparazione e il prezzo
    char menuClient[600];
    int countmc = 0;
    char risposta[20];
    int quittiamo = 0;
    int oos = 0;
    memset(menu,0,sizeof(menu));
    sprintf(menu,"SELECT * FROM Elementi;");
    pthread_mutex_lock(&mutexDb);
    //estraggo il menu e creo le stringhe menu e menuClient
    mysql_query(con,menu);
    memset(menu,0,sizeof(menu));
    memset(menuClient,0,sizeof(menuClient));
    MYSQL_RES* result = mysql_store_result(con);
    MYSQL_ROW row;
    while((row = mysql_fetch_row(result)) != NULL)
    {
        int countr = 0;
        for( ;row[0][countr]!= '\0'; countr++)
        {
            menu[countm++] = row[0][countr];
            menuClient[countmc++] = row[0][countr];
        }
        menu[countm++] = '\n';
        menuClient[countmc++] = '-';
        countr = 0;
        for( ; row[1][countr]!='\0';countr++)
        {
            menuClient[countmc++] = row[1][countr];
        }
        menuClient[countmc++] = '-';
        countr = 0;
        for( ; row[2][countr] != '\0'; countr++)
        {
            menuClient[countmc++] = row[2][countr];
        } 
        menuClient[countmc++] = '\n';
    }
    menu[countm] = '\0';
    menuClient[countmc] = '\0';
    mysql_free_result(result);
    pthread_mutex_unlock(&mutexDb);
    send(clientconn->socketc, menuClient,strlen(menuClient),0);
    while(quittiamo == 0)
    {
        memset(risposta,0,sizeof(risposta));
        if(read(clientconn->socketc,risposta,sizeof(risposta))==0)
            strcpy(risposta,"gone");

        if(strcmp(risposta,"oos")==0)
        {
            char* rispostaS = oos == 0? "A piu tardi!\n" : "Bentornato!\n";
            send(clientconn->socketc, rispostaS,strlen(rispostaS),0);
            oos = oos==0? 1 : 0;
        }
        else if(strcmp(risposta,"gone")==0)
        {
            char* rispostaS = "Arrivederci!\n";
            send(clientconn->socketc, rispostaS,strlen(rispostaS),0);
            quittiamo = 1;
        }
        else if(oos==0)
        {
            int delmenu = 0;
            int i = 0;
            int j = 0;
            char elementocorrente[20];
            memset(elementocorrente,0,sizeof(elementocorrente));
            while(delmenu ==0 && menu[i] != '\0')
            {

                if(menu[i]!='\n')
                {
                    elementocorrente[j] = menu[i];
                    j++;
                    i++;
                }
                else if(menu[i] == '\n')
                {
                    elementocorrente[j] = '\0';
                    j = 0;
                    //printf("%s(client) == %s?",risposta, elementocorrente);
                    i++;
                    if(strcmp(elementocorrente,risposta)==0)
                        delmenu = 1;
                    memset(elementocorrente,0,sizeof(elementocorrente));
                }
            }
            if(delmenu==1)
            {
                // char rispostaS[100];
                // memset(rispostaS,0,sizeof(rispostaS));
                // sprintf(rispostaS,"Ottima scelta!\n");
                // send(clientconn->socketc, rispostaS,strlen(rispostaS),0);
                quittiamo = 1;
            }
            else
            {
                char* rispostaS= "Mi dispiace, non abbiamo trovato questo elemento\n";
                send(clientconn->socketc, rispostaS,strlen(rispostaS),0);
            }
        }
    }
    pthread_mutex_lock(&mutexQueue);
    clientconn->state = "SERVING";
    pthread_mutex_unlock(&mutexQueue);
    //printf("%s\n",clientconn->state);
    // char* inoltro = malloc(20*sizeof*inoltro);
    // sprintf(inoltro,"%s",risposta);
    serve(clientconn,risposta);
}

void welcome(void* client)
{
    pnodet* clientconn = (pnodet*) client;
    char address[INET_ADDRSTRLEN];
    inet_ntop(AF_INET,&clientconn->indirizzoc.sin_addr,address,INET_ADDRSTRLEN);
    printf("Benvenuto a %s\nIn attesa delle credenziali per effettuare il login.\n",address);
    char risposta[200];
    char state[20];
    int logged = 0;
    //finche non si effettua il login
    while(logged == 0)
    {
        int scorririsposta = 0;
        memset(risposta,0,sizeof(risposta));
        memset(state,0,sizeof(state));
        // int credenziali = 0;
        // printf("%d\n",credenziali);
        //Se si disconnette copiamo semplicemente quittiamo
        if(read(clientconn->socketc,risposta,sizeof(risposta))==0)
            strcpy(risposta,"gone");
        printf("%s\n",risposta);
        memset(state,0,sizeof(state));
        for(int i = 0; risposta[scorririsposta]!= '\0' && risposta[scorririsposta]!= '-'; i++)
        {
            state[i] = risposta[scorririsposta];
            scorririsposta++;
        }
        scorririsposta++;

        //Registrazione
        if(strcmp(state,"registerer")== 0)
        {
            // Risposta conterra' qualcosa del tipo USERNAME-PASSWORD-ANS1-ANS2
            char username[21];
            char password[13];
            char ans1[40];
            char ans2[40];
            memset(username,0,sizeof(username));
            memset(password,0,sizeof(password));
            memset(ans1,0,sizeof(ans1));
            memset(ans2,0,sizeof(ans2));
            char query[300];

            memset(query,0,sizeof(query));


            //Copio i valori nelle stringhe
            for(int i = 0; risposta[scorririsposta]!='-'; i++)
            {
                username[i] = risposta[scorririsposta];
                scorririsposta++;
            }
            scorririsposta++;
            for(int i = 0; risposta[scorririsposta]!='-'; i++)
            {
                password[i] = risposta[scorririsposta];
                scorririsposta++;
            }
            scorririsposta++;
            for(int i = 0; risposta[scorririsposta]!='-'; i++)
            {
                ans1[i] = risposta[scorririsposta];
                scorririsposta++;
            }
            scorririsposta++;
            for(int i = 0; risposta[scorririsposta]!='\0'; i++)
            {
                ans2[i] = risposta[scorririsposta];
                scorririsposta++;
            }

            sprintf(query,"SELECT * FROM Utenti WHERE username = '%s'",username);
            pthread_mutex_lock(&mutexDb);
            mysql_query(con,query);
            MYSQL_RES *result = mysql_store_result(con);
            if(mysql_num_rows(result)!=0)
            {
                printf("Ho fatto la query\n");
                memset(risposta,0,sizeof(risposta));
                strcpy(risposta,"Esiste gia' un utente con questo nome!\n");
                send(clientconn->socketc, risposta,strlen(risposta),0);
            }
            else
            {
                memset(query,0,sizeof(query));
                sprintf(query,"INSERT INTO Utenti(username,password,answer1,answer2) VALUES ('%s','%s','%s','%s');",username,password,ans1,ans2);
                pthread_mutex_lock(&mutexDb);
                mysql_query(con,query);
                pthread_mutex_unlock(&mutexDb);
                memset(risposta,0,sizeof(risposta));
                strcpy(risposta,"Utente registrato con successo! Effettua il login.\n");
                send(clientconn->socketc, risposta,strlen(risposta),0);
            }
            mysql_free_result(result);
            pthread_mutex_unlock(&mutexDb);

        }
        else if(strcmp(risposta,"gone") == 0)
        {
            logged = 1;
        }
        else//Se siamo in login, la risposta sara del tipo USERNAME-PASSWORD
        {
            char username[21];
            char password[13];
            char query[300];

            memset(username,0,sizeof(username));
            memset(password,0,sizeof(password));
            memset(query,0,sizeof(query));
            //Copio i valori nelle stringhe
            for(int i = 0; risposta[scorririsposta]!='-'; i++)
            {
                username[i] = risposta[scorririsposta];
                scorririsposta++;
            }
            printf("%s\n",username);
            scorririsposta++;
            for(int i = 0; risposta[scorririsposta]!='\0'; i++)
            {
                password[i] = risposta[scorririsposta];
                scorririsposta++;
            }

            sprintf(query,"SELECT password FROM Utenti WHERE username = '%s';",username);
            pthread_mutex_lock(&mutexDb);
            mysql_query(con,query);
            MYSQL_RES *result = mysql_store_result(con);
            //Se l'utente non esiste
            if(mysql_num_rows(result)==0)
            {
                memset(risposta,0,sizeof(risposta));
                strcpy(risposta,"Utente non trovato! Riprova.\n");
                send(clientconn->socketc, risposta,strlen(risposta),0);
            }
            else
            {
                MYSQL_ROW pass = mysql_fetch_row(result);

                if(strcmp(pass[0],password)==0)
                {
                    logged = 1;
                    memset(risposta,0,sizeof(risposta));
                    sprintf(risposta,"Benvenuto, %s!\n", username);
                    send(clientconn->socketc, risposta,strlen(risposta),0);
                }
                else {
                    memset(risposta,0,sizeof(risposta));
                    sprintf(risposta,"Password errata, %s!\n", username);
                    send(clientconn->socketc, risposta,strlen(risposta),0);
                }

            }
            mysql_free_result(result);
            pthread_mutex_unlock(&mutexDb);
        }

    }
    pthread_mutex_lock(&mutexQueue);
    clientconn->state = "WAITING";
    //printf("%s\n", clientconn->state);
    while(strcmp(clientconn->state,"ORDERING")!=0)
    {
        pthread_cond_wait(&condMutexQueue,&mutexQueue);
    }
    pthread_mutex_unlock(&mutexQueue);
    order(clientconn);
    // close(clientconn->socketc);
    // pthread_kill(mainthread,SIGUSR1);
    // pthread_mutex_lock(&mutexQueue);
    // close(clientconn->socketc);
    // removeNode(clientconn);
    // free(clientconn);
    // pthread_mutex_unlock(&mutexQueue);
    pthread_exit(0);
}

//void outofsight(void* tid)
// {
//     pthread_t id = *(pthread_t*) tid;
//     printf("Monitoro il thread %d\n", id);
//     pnodet* nodo;
//     pthread_mutex_lock(&mutexQueue);
//     nodo = find(id);
//     pthread_mutex_unlock(&mutexQueue);
//     while(1)
//     {
//         //accepr
//     }
// }

//////////////////////////////

//Funzioni per la gestione della coda da parte di queuemanager
void checkfarewelling()
{
    pnodet* explorer = head;
    while(explorer != NULL)
    {
        if(strcmp(explorer->state,"SERVED")==0)
        {



            if(explorer->id == cl1)
                cl1=-1;
                // pthread_cancel(oos1);
            else
                cl2=-1;
                // pthread_cancel(oos2);
            explorer->state = "FAREWELLING";
            inServingWaiting--;
        }
        explorer = explorer->next;
    }
}

void checkwaiting()
{
   pnodet* explorer = head;
    while(explorer != NULL && inServingWaiting<2)
    {
        if(strcmp(explorer->state,"WAITING")==0)
        {
            inServingWaiting++;
            if(cl1 == -1)
            {
                cl1 = explorer->id;
                // pthread_create(&oos1,NULL,outofsight,&cl1);
                // pthread_detach(oos1);
            }
            else
            {
                cl2 = explorer->id;
                // pthread_create(&oos2,NULL,outofsight,&cl2);
                // pthread_detach(oos2);
            }
            explorer->state = "ORDERING";

        }
        explorer = explorer->next;
    }
}
//Elimina i nodi di socket disconnesse e fa andare i nodi da waiting a ordering
void checklist()
{
    pthread_mutex_lock(&mutexQueue);
    //printlist();
    checkfarewelling();
    removeDisconnected();
    checkwaiting();
    //printlist();
    pthread_cond_broadcast(&condMutexQueue);
    pthread_mutex_unlock(&mutexQueue);
}


void managequeue(void* input)
{
    while(1)
    {
        sleep(5);
        checklist();
    }
}

///////////////////////////////

void nada()
{

}

int main(int argc, char** args)
{
    //Connessione al db
    con = mysql_init(NULL);

    if(con == NULL)
    {
        perror("Errore durante la creazione dell'istanza mysql\n");
        exit(1);
    }
    //Scherzavo questa e' la vera connessione
    if(mysql_real_connect(con,NULL,"a","a","robottino",0,NULL,0)==NULL)
    {
        fprintf(stderr,"Errore durante la connessione al db:%s\n",mysql_error(con));
        exit(1);
    }
    char creazione[7000];
    memset(creazione,0,sizeof(creazione));
    sprintf(creazione,"CREATE TABLE IF NOT EXISTS Utenti(username VARCHAR(21) PRIMARY KEY,password VARCHAR(13) NOT NULL,answer1 VARCHAR(100) NOT NULL, answer2 VARCHAR(100) NOT NULL)");
    mysql_query(con,creazione);
    memset(creazione,0,sizeof(creazione));
    sprintf(creazione,"CREATE TABLE IF NOT EXISTS Elementi(nome VARCHAR(50) PRIMARY KEY, prezzo DOUBLE PRECISION(7,4) NOT NULL,tempo SMALLINT UNSIGNED NOT NULL)");
    mysql_query(con,creazione);
    memset(creazione,0,sizeof(creazione));
    sprintf(creazione, "CREATE TABLE IF NOT EXISTS Domande(domanda VARCHAR(100) PRIMARY KEY, rispostaP VARCHAR(100) NOT NULL, rispostaN VARCHAR(100) NOT NULL, feedbackP VARCHAR(200) NOT NULL, feedbackN VARCHAR(200) NOT NULL, tag VARCHAR(20) NOT NULL)");
    mysql_query(con,creazione);
    if(argc == 2 && strcmp(args[1],"-d")==0)
    {
        memset(creazione,0,sizeof(creazione));
        sprintf(creazione,"INSERT INTO Elementi(nome,prezzo,tempo) VALUES('Negroni',6.50,15),('Tequila Sunrise',7.20,10),('Sex on the beach',6,10),('Aperol Spritz',5,12),('Pina Colada',7,14),('Mudslide',6,10),('Empress 75',8,15),('Rum Manhattan',6,10),('Negroski',5,11),('Martini Bianco',6.5,10),('Martini Rosso',6.5,10),('Mojito',7,15),('Cosmopolitan',6.5,13),('Gin fizz',9,14);");
        mysql_query(con,creazione);
        memset(creazione,0,sizeof(creazione));
        sprintf(creazione,"INSERT INTO Domande(domanda, rispostaP, rispostaN, feedbackP, feedbackN, tag)VALUES ('Ti piace giocare o guardare gli sport?', 'Li adoro!', 'Non sono il mio forte.', 'Fantastico! Lo sport può essere un ottimo modo per mantenersi attivi e divertirsi.', 'Va bene! Lo sport potrebbe non essere per tutti, e poi ci sono molte altre attività da esplorare.', 'Sport'),('Hai mai partecipato a una competizione sportiva?', 'Sì, l''ho fatto. E'' stato esilarante!', 'No, non l''ho fatto. Non adoro competere', 'Complimenti! Gareggiare nello sport e'' un''esperienza emozionante capace di spingerti al limite.', 'Beh, non tutti amano la competizione, e poi sono molti altri modi per godersi lo sport.', 'Sport'),('Ti piace guardare film?', 'Assolutamente. Sono un vero appassionato di cinema.', 'Non particolarmente. Preferisco altre forme di intrattenimento.', 'Anche a me! I film hanno il potere di intrattenere, ispirare e trasportarci in mondi diversi.', 'Ognuno ha preferenze diverse quando si tratta di intrattenersi, e sono felice che tu abbia i tuoi modi di farlo!', 'Film'),('Hai visto qualche film di recente?', 'Sì, ne ho visto uno fantastico la scorsa settimana!', 'No, non l''ho fatto. Sono stato impegnato con altre cose.', 'Ti capisco. È sempre emozionante scoprire nuovi film che lasciano un''impressione duratura.', 'Nessun problema! A volte la vita diventa frenetica, avrai sicuramente modo di recuperare!', 'Film'),('Ti interessa il giardinaggio?', 'Certamente! Trovo il giardinaggio molto gratificante.', 'No, non fa per me.', 'Ma davvero! Il giardinaggio ti permette di connetterti con la natura e creare splendidi spazi verdi.', 'Il giardinaggio richiede tempo e impegno, ed è perfettamente normale non potersi (o volersi) dedicare.', 'Giardinaggio'),('Hai delle piante o fiori nel tuo giardino?', 'Dovresti vederle! Sono orgoglioso del mio giardino.', 'No, non ne ho. Non ho né tempo né spazio per il giardinaggio.', 'Mi piacerebbe! Avere il proprio giardino può essere incredibilmente gratificante, ed è una gioia vedere le piante prosperare.', 'Mi dispiace. D''altronde hai ragione, c''e'' bisogno di spazio e tempo per curare il proprio giardino.', 'Giardinaggio'),('Ti piace ascoltare musica?', 'Assolutamente! La musica riesce a farmi vivere meglio.', 'Non molto. Preferisco la quiete del silenzio.', 'E ci credo! La musica ha il potere di toccare le nostre emozioni e creare momenti speciali.', 'Sicuramente anche il silenzio ha il proprio fascino. Come avrai notato, purtroppo non e'' proprio il forte di questo posto!', 'Musica'),('Hai mai partecipato a concerti di musica dal vivo?', 'Sì, l''ho fatto. L''energia e l''atmosfera erano incredibili!', 'No, farlo non mi attira.', 'I concerti dal vivo possono essere un''esperienza indimenticabile, con l''energia della folla e la musica che si fondono.', 'Certo, non sono per tutti. Alcuni preferiscono godersi la musica senza tutto cio'' che caratterizza un concerto.', 'Musica'),('Ti piace giocare ai videogiochi?', 'Sì, sono un vero e proprio giocatore!', 'No, non mi interessano i videogiochi.', 'I videogiochi offrono una forma unica di intrattenimento interattivo e possono essere molto divertenti.', 'I videogiochi non sono apprezzati da tutti, potresti preferire qualche hobby che ti mette in movimento!', 'Videogiochi'),('Hai provato qualche nuovo videogioco di recente?', 'Certamente! Adoro scoprire nuove esperienze di gioco.', 'No, non l''ho fatto. Non trovo i videogiochi interessanti.', 'Fantastico! Provare nuovi videogiochi può introdurti a mondi emozionanti e storie affascinanti.', 'Nessun problema! I videogiochi non sono per tutti, e ci sono molte altre forme di intrattenimento da apprezzare.', 'Videogiochi'),('Ti piace cucinare?', 'Certamente! Trovo gioia nel creare piatti deliziosi.', 'Non molto. Preferisco lasciare la cucina agli altri.', 'Mi sorprendi! Cucinare permette di esprimere la propria creatività e gustare i frutti del proprio impegno.', 'Ti capisco, anche io preferisco lasciare le padelle agli altri... anche perche'' non ho scelta!', 'Cucina'),('Di recente hai provato una nuova ricetta?', 'Sì! È sempre emozionante sperimentare nuovi sapori.', 'Non sono molto bravo in cucina.', 'Fantastico! Provare nuove ricette può ampliare i tuoi orizzonti culinari e portare a scoperte deliziose.', 'Nessun problema! La cucina è una competenza che richiede tempo per svilupparsi, e c''è sempre la possibilità di gustare pasti preparati da altri.', 'Cucina'),('Ti piace andare a teatro?', 'Assolutamente! Il teatro mi affascina come null''altro.', 'Non molto. Non è la mia forma di intrattenimento preferita.', 'Fantastico! Il teatro offre una combinazione unica di narrazione, performance e arte dal vivo.', 'Va bene! Il teatro non è per tutti, e ci sono molte altre forme di intrattenimento da esplorare.', 'Teatro'),('Hai assistito a qualche spettacolo teatrale di recente?', 'Sì, l''ho fatto. Il talento e l''arte sul palco erano affascinanti.', 'No, non l''ho fatto. Non ho avuto l''opportunità o l''interesse.', 'Meraviglioso! Assistere a spettacoli teatrali dal vivo può essere un''esperienza indimenticabile, ricca di performance avvincenti.', 'Nessun problema! Gli spettacoli teatrali non sono sempre accessibili, e ognuno ha interessi e preferenze diverse.', 'Teatro'),('Ti interessa qualche forma d''arte, come pittura, scultura o fotografia?', 'Certamente! Apprezzo e ammiro diverse forme d''arte.', 'Non molto. L''arte non mi colpisce particolarmente.', 'Meraviglioso! L''arte può ispirare, suscitare pensieri e permetterci di vedere il mondo da diverse prospettive.', 'Va bene! L''apprezzamento dell''arte è soggettivo, e ognuno ha gusti e preferenze diverse.', 'Arte'),('Hai mai creato un''opera d''arte tu stesso?', 'Sì, l''ho fatto. Esprimermi attraverso l''arte è terapeutico.', 'No, non l''ho fatto. Non ho il talento per le attività artistiche.', 'Fantastico! Creare arte può essere un modo gratificante ed espressivo per le emozioni e le idee.', 'Nessun problema! Le abilità artistiche variano, e ci sono molti modi per apprezzare e coinvolgersi nell''arte oltre alla creazione.', 'Arte'),('Ti piace leggere libri?', 'Assolutamente! I libri mi trasportano in mondi diversi.', 'Non molto. Preferisco altre forme di intrattenimento.', 'Fantastico! La lettura ci permette di immergerci in storie, ampliare le nostre conoscenze e vivere nuove prospettive.', 'Va bene! La lettura non è per tutti, e ci sono molte altre modalità per godersi storie e informazioni.', 'Lettura'),('Hai letto qualche libro di recente?', 'Sì, l''ho fatto. Non riuscivo a smettere di leggere!', 'No, non l''ho fatto. Non ho trovato il tempo o l''interesse.', 'Fantastico! Scoprire un libro avvincente può offrire ore di divertimento e lasciare un''impressione duratura.', 'Nessun problema! Trovare il tempo e l''interesse per la lettura può essere difficile, e ci saranno sempre opportunità per esplorare libri in futuro.', 'Lettura');");
        mysql_query(con,creazione);
    }
    signal(SIGPIPE,nada);

    pthread_cond_init(&condMutexQueue,NULL);

    pnodet* node = NULL;
    mainthread = pthread_self();

    int sockets = socket(AF_INET, SOCK_STREAM, 0);
    struct sockaddr_in indirizzo;

    int reuse = 1;
    if (setsockopt(sockets, SOL_SOCKET, SO_REUSEADDR, (const char*)&reuse, sizeof(reuse)) < 0)
        perror("Errore in set di opzioni\n");

    indirizzo.sin_family = AF_INET;
    indirizzo.sin_port = htons(5000);
    indirizzo.sin_addr.s_addr = htonl(INADDR_ANY);

    if(bind(sockets,(struct sockaddr* )&indirizzo,sizeof(indirizzo))<0)
    {
        perror("Errore in binding\n");
        exit(0);
    }

    if(listen(sockets,10)<0)
    {
        perror("Errore in listening\n");
        exit(0);
    }
    if(pthread_create(&queuemanager,NULL,managequeue, NULL)!=0)
    {
        perror("Errore tread delle queue");
        exit(1);
    };

    while(1)
    {
        node = (pnodet*)malloc(sizeof(pnodet));

        socklen_t length = sizeof(node->indirizzoc);
        node->socketc = accept(sockets,&(node->indirizzoc), &length);
        if(node->socketc <0)
        {
            printf("Errore in accept\n");
            perror("Info: ");
        }
        node->state = "WELCOME";
        printf("%s\n",node->state);
        pthread_mutex_lock(&mutexQueue);
        enqueue(node);
        pthread_create(&node->id,NULL,welcome,(void*)node);
        pthread_detach(node->id);
        pthread_mutex_unlock(&mutexQueue);
    }

}
