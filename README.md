# JElectro

This java library provides a remote method Invocation (RMI) technic on a global set of separated virtual machines.
Its purpose is to extends the princip of method invocation to allow any single connected virtual machine to call any method exposed on the network.
Basicaly this library allows bi-directional binding and remote callbacks and listeners. The notion of client and server is replace with the notion of node; Every node can expose instances that any other node can execute.

## Concept

By starting to work with this library you will first instanciate nodes
the base instance is called JElectro and you instanciate it by giving it a name:
```java
JElectro j = new JElectro("Node-1");
```


