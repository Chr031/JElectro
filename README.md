# JElectro


JElectro is a messaging library that provides remote method invocation (RMI) over java objects. 
It uses socket to connect different JVM, uses java proxies to expose and execute remote objects and serialisation for the 

This java library provides a remote method Invocation (RMI) technic on a global set of separated virtual machines.
Its purpose is to extends the princip of method invocation to allow any single connected virtual machine to call any method exposed on the network.
Basicaly this library allows bi-directional binding and remote callbacks and listeners. The notion of client and server is replace with the notion of node; Every node can expose instances that any other node can execute.

## Concept

By starting to work with this library you will first instanciate nodes.
The base instance is called JElectro and you instanciate it by giving it a name:
```java
JElectro j1 = new JElectro("Node-1");
```
Then this new node instance should be able to accept other nodes connections:

```java
// j1 will open the port 12001 and listen to any incomming connection
j1.listenTo(12001);
```

One can now define an interface and implementing it in order to expose an instance :
```java
interface Calc { int sum(int a, int b);}
Calc c = new Calc() {public int sum(int a, int b) {return a+b;}};

// exposing/exporting/binding the instance c of Calc
j1.bind("calc",c);
```

c is now registered and can be accessed by any other node connected to j
Let's first create a new node. (This part can/should be run in an other program or on an other machine.)
```java
JElectro j2 = new JElectro("Node-2");
j2.connectTo("<The address of the machine wich run j1>", 12001);
```

j2 is now connected to j1 and can retrieve and use the instance c exposed by j1 as it was its own service:
```java
Calc c = j2.lookupUnique("calc",Calc.class);
int sum = c.sum(12,9);
```


## Bidirection and propagation



