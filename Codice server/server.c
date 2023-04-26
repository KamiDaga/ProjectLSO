#include <netinet/in.h>
#include <unistd.h>
#include <pthread.h>
#include <stdio.h>
#include <signal.h>
#include <stdlib.h>
#include <arpa/inet.h>
#include <string.h>
#include <errno.h>

//mutex per la lista
pthread_mutex_t mutexQueue;

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
    strcpy(addio,"Alla prossima bevuta!\n");
    send(clientconn->socketc,addio,sizeof(addio),0);
    pthread_mutex_lock(&mutexQueue);
    clientconn->state = "GONE";
    pthread_mutex_unlock(&mutexQueue);
    //printf("%s\n",clientconn->state);
    
}

void serve(pnodet* clientconn, char* drink)
{
    char* request = "Mentre preparo il tuo drink, ti andrebbe una chiacchierata?\n";
    //printf("%s\n",drink);
    char risposta[20];
    int prepare = 1;
    int quittiamo = 0;
    send(clientconn->socketc, request,strlen(request),0);
    while(quittiamo == 0)
    {
        memset(risposta,0,sizeof(risposta));
        if(read(clientconn->socketc,risposta,sizeof(risposta))==0)
            {
                strcpy(risposta,"gone");
            }
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
        if(prepare == 1 && strcmp(risposta,"oos")!=0)
        {
            if(strcmp(risposta,"Si")==0)
            {
                    //domande e risposte
            }
            else
            {
                sleep(5);
                char rispostaS[100];
                memset(rispostaS,0,sizeof(rispostaS));
                sprintf(rispostaS,"Ecco il tuo %s!\n",drink);
                send(clientconn->socketc,rispostaS,sizeof(rispostaS),0);
            }
            quittiamo = 1;
        }
    }
    pthread_mutex_lock(&mutexQueue);
    clientconn->state = "SERVED";
    pthread_mutex_unlock(&mutexQueue);
    while(strcmp(clientconn->state,"FAREWELLING")!=0)
    {
        
    }
    farewell(clientconn);
}

void order(pnodet* clientconn)
{
    char* menu = "Margarita\nCosmopolitan\nDaiquiri\nGimlet\nManhattan\n";
    char risposta[20];
    int quittiamo = 0;
    int oos = 0;
    send(clientconn->socketc, menu,strlen(menu),0);
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
                char rispostaS[100];
                memset(rispostaS,0,sizeof(rispostaS));
                sprintf(rispostaS,"Ottima scelta!\n");
                send(clientconn->socketc, rispostaS,strlen(rispostaS),0);
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
    printf("Benvenuto a %s\n",address);
    pthread_mutex_lock(&mutexQueue);
    clientconn->state = "WAITING";
    pthread_mutex_unlock(&mutexQueue);
    //printf("%s\n", clientconn->state);
    while(strcmp(clientconn->state,"ORDERING")!=0)
    {

    }
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

void outofsight(void* tid)
{
    pthread_t id = *(pthread_t*) tid;
    printf("Monitoro il thread %d\n", id);
    pnodet* nodo;
    pthread_mutex_lock(&mutexQueue);
    nodo = find(id);
    pthread_mutex_unlock(&mutexQueue);
    while(1)
    {
        //accepr
    }
}

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
    printlist();
    checkfarewelling();
    removeDisconnected();
    checkwaiting();
    printlist();
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

int main()
{
    // if((queuemanager = fork()) == 0)
    // {
    //     managequeue();
    // }
    signal(SIGPIPE,nada);
    pthread_mutex_init(&mutexQueue,NULL);
    pnodet* node = NULL;    
    mainthread = pthread_self();
    
    int sockets = socket(AF_INET, SOCK_STREAM, 0);
    struct sockaddr_in indirizzo;

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
    };
        
    while(1)
    {
        // if(head == NULL)
        // {
        node = (pnodet*)malloc(sizeof(pnodet));
            // head->next = NULL;
        // }
        // else
        // {
        //     pnodet* nodo = (pnodet*)malloc(sizeof(pnodet));
        //     nodo->next = head;
        //     head = nodo;
        // }
        socklen_t length = sizeof(node->indirizzoc);
        node->socketc = accept(sockets,&(node->indirizzoc), &length);
        if(node->socketc <0)
        {
            printf("Errore in accept\n");
            perror("Info: ");
        }
        // if((node->id = fork())==0)
        // {
        node->state = "WELCOME";
        printf("%s\n",node->state);
        pthread_mutex_lock(&mutexQueue);
        enqueue(node);
        pthread_create(&node->id,NULL,welcome,(void*)node);
        pthread_detach(node->id);
        pthread_mutex_unlock(&mutexQueue);

        
        // }
    }
    
}



