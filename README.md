# JElectro



JElectro is a messaging library that provides remote method invocation over java objects. 
It uses socket to connect different JVM, uses serialisation and proxies to expose and execute remote objects.

It provides a remote procedure call technique on a global set of separated virtual machines and tries to extend the principles of method invocation to allow any single connected virtual machine to call any method exposed on the network.
Basically this library allows bi-directional binding, remote callbacks and listeners to be executed. The notion of client and server is replace with the notion of node; Every nodes can expose instances that any other node can execute.

## First Overview

By starting to work with this library you will first instantiate nodes.
The base instance is called JElectro and you instantiate it by giving it a name:
```java
JElectro j1 = new JElectro("Node-1");
```
Then this new node instance should be able to accept other nodes connections:

```java
// j1 will open the port 12001 and will listen to any incoming connection
j1.listenTo(12001);
```

One can now define an interface and implement it: 
```java
// The service interface
interface Calc { 
  int sum(int a, int b);
}
// Its anonymous implementation
Calc c = new Calc() {
  public int sum(int a, int b) {return a+b;}
};
```

In order to expose an instance :
```java
// exposing/exporting/binding the instance c of Calc
// the first argument of this call is the name of the shared instance,
// the second is the instance itself.
j1.bind("calc",c);
```

c is now registered and can be accessed by any other node connected to j1.
Let's first create a new node. (This part can/should be run in an other program or on an other machine.)
```java
JElectro j2 = new JElectro("Node-2");
j2.connectTo("<The address of the machine which run j1>", 12001);
```

j2 is now connected to j1 and can retrieve and use the instance c exposed by j1 as it was its own service:
```java
// the first argument of this call is the name of the instance as defined by the bind method.
// the second argument is the interface class itself to indicate how the returned object needs to be proxified.
Calc c2 = j2.lookupUnique("calc",Calc.class);
int sum = c2.sum(12,9);
```
With those last lines, a proxy c is instantiated and will be used as any local java object to execute remotely the methods of the shared instance exposed by j1.

To release the resources used by any instance j of the Class JElectro, its close method has to be called:


## Concept

### Bidirectional Binding and Message Propagation

This framework allows any node to expose any kind of interface implementation. In the previous example j2 could also expose an implementation.
Any JElectro instance can listen to many port and connect to many other instances 
```java
JElectro j = new JElectro("Node-1");
j.listenTo(12001);
j.connectTo("address2",12002);
j.connectTo("address3",12003);
```

Message propagation will be perform if several instances are not directly connected in order to provide the same kind of service as if they were directly connected. They will be able to expose and share object instances as if they were connected.
Binding, lookup and method execution will be performed transparently.
This is typically the case with a client server architecture. One server and many clients are connected together but clients don't see directly each other. 

| j1 | j2 | j3 |
|:-------:|:-------:|:-------:|
| connected to j2 | | connected to j2 | 
| expose object c1 | expose object c2 | expose object c3|
| can access and execute c2 and c3 | can access and execute c1 and c3 | can access and execute c1 and c2 |
| to execute c3 messages will be sent to j2 and j2 will route them to j3 | | to execute c1 messages will be sent to j2 and j2 will route them to j1 |


### Remote Callbacks

It is sometimes practical to call a remote service and to be notified later of its execution. Therefore the class JElectroCallback can be used to perform this kind non blocking call :

First implement the interfaces of the callback and of the service and their implementation :
```java
// The callback interface needs to extends the class JElectroCallback to be proxified 
// before the execution message to be sent
interface NumberCallback extends JElectroCallback {
  void onPrimeNumber(long number);
}

interface NumberService {
  void findPrimeNumbers(long number, NumberCallback callback);
}

class NumberServiceImpl implements NumberService {
  public void findPrimeNumbers(long number, NumberCallback callback) {
   
    // As this call should be non blocking, the process has to be run in an other thread.
    
    new Thread() {
      public void run() {
        long l = 0;
        while (l<number) {
          if (isPrime(l)) 
            // A call to the callback is performed : the caller of the method will be notified.
            callback.onPrimeNumber(l);
          l++;
        }
      }
    }.start();
  }
}
``` 

The last class implemented is non blocking. All the computations are done in a separate process. This is not necessary.

Building the two part of the code that will use those classes gives something like that :


 - Node 1 : caller node 
```java 
JElectro j1 = new JElectro("Node-1");
j1.connectTo("<j2's address>", <j2's port>);
NumberService ns = j1.lookupUnique("NumberService");
// Since the callback interface is defined in NumberService a lambda expression is possible :
List<Long> numberList = new ArrayList<>();
ns.findPrimeNumbers(11, (l) -> numberList.add(l));
// the population of numberList is in progress....
```

 - Node 2 : callee node
```java 
JElectro j2 = new JElectro("Node-2");
j1.listenTo(<j2's port>);
j2.bind("NumberService", new NumberServiceImpl());
```

From _Node 1_, the call to the method _computeNumber_ will trigger the method on _Node 2_. _Node 2_ will then call the callback (the lambda expression) in order to return 1 by 1 every results.  


### General considerations

- Java 1.8 compatible, previous versions of java won't work.
- All objects given in parameter as all returned objects must be serializable ie. they must all implement the Serializable interface.
- Exceptions are transported and thrown to the caller method. Exception are not encapsulated.
- Performance depends of course of the network, but also of the size of the object to be transported. On a local computer, a remote method execution with simple java types takes around 0.2 ms for two nodes directly connected.



#### keywords
RPC, RMI, Object Messaging, java, Serialization, Socket, TCP, Network.
