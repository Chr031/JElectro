# JElectro


JElectro is a messaging library that provides remote method invocation over java objects. 
It uses socket to connect different JVM, uses serialisation and proxies to expose and execute remote objects.

It provides a remote procedure call technic on a global set of separated virtual machines and tries to extend the princips of method invocation to allow any single connected virtual machine to call any method exposed on the network.
Basicaly this library allows bi-directional binding, remote callbacks and listeners to be executed. The notion of client and server is replace with the notion of node; Every nodes can expose instances that any other node can execute.

## Example

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

One can now define an interface and implementing it: 
```java
interface Calc { int sum(int a, int b);}
Calc c = new Calc() {public int sum(int a, int b) {return a+b;}};
```

In order to expose an instance :
```java
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
Calc c2 = j2.lookupUnique("calc",Calc.class);
int sum = c2.sum(12,9);
```
With those last lines, a proxy c is instanciated and will be used as any local java object to execute remotely the methods of the shared instance exposed by j1.

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
Binding, lookup and method execution will be perfomed transparently.
This is typicaly the case with a client server architecture. One server and many clients are connected together but clients don't see directly eachother. 

| j1 | j2 | j3 |
|:-------:|:-------:|:-------:|
| connected to j2 | | connected to j2 | 
| expose object c1 | expose object c2 | expose object c3|
| can access and execute c2 and c3 | can access and execute c1 and c3 | can access and execute c1 and c2 |
| to execute c3 message will be sent to j2 and j2 will route them to j3 | | to execute c1 message will be sent to j2 and j2 will route them to j1 |


### Remote Callbacks

It is sometimes practical to call a remote service and to be notified later of its execution. Therefor the class JElectroCallback can be used to perform this kind of usage :

First implement the interfaces of the callback and of the service and their implementation :
```java
// the callback interface needs to extends the class JElectroCallback to be proxified before the execution message is send
interface NumberCallback extends JElectroCallback {
  void onNumber(long number);
}

interface NumberService {
  void computeNumber(long number, NumberCallback callback);
}


class NumberCallbackImpl implements NumberCallback {
  public void onNumber(long number) {
    System.out.println(""Received "+ number);
  }
}

class NumberServiceImpl implements NumberService {
  public void computeNumber(long number, NumberCallback callback) {
    long l = 0;
    while (l<number) {
      if (isPrime(l)) 
        callback.onNumber(l);
      l++;
    }
  }
}
``` 


