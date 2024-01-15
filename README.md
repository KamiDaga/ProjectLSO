This is a possible implementation of a system regarding the simulation of a cafe where bartenders are robots.
The architecture is a classic Client-Server, where the server is multithreaded and has various constraints of working since it can only go in a specific flow of direction. Those constraints are implemented through utilization
of mutex locks and condition variables in C. 
Clients have the possibility to register through the Android App (developed in Java) and its information are stored in a MySql database.
