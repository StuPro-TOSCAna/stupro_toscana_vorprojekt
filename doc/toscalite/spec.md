# TOSCAlite specification

## Introduction

TOSCAlite is a very lightweight version of the OASIS TOSCA standard. It can be used to model a deployment based on Linux systems (mostly Ubuntu 16.04 LTS) 

## Nodes

A node defined in the ``model.xml`` (see Packaging) represents a node in a TOSCAlite topology. 
TOSCAlite defines two types of nodes. It is not possible to extend TOSCAlite with more types of nodes.

In XML a node gets defined as following:

```XML
<Node>
    <Type>type</Type>
    <Name>name</Name>
    <Properties>
        <PropKey>PropVal</PropKey>
    </Properties>
</Node>
```
**Explanation:**
* ``Type`` - This defines the node type. In TOSCAlite only two types are allowed. (``service`` and ``machine``)
* ``Ǹame`` - The name of a node is used to uniquely identify the node in the topology. It is the nodes primary identifier. The names are only allowed to contain the following characters ``abcdefghijklmnopqrstuvwxyz-_1234567890``
* ``Properties`` -  Every element in the Properties block gets interpreted as a Key-Value pair, where the element name is the key and the element content represents the value.

The ``Type`` and ``Name`` elements are always required. ``Properties`` can be optional, at least in service nodes.

### Machines

A machine node represents a Linux-based system, probably Ubuntu 16.04. The deployment system does not care where the system is running, as long as the machine is reachable with SSH.
This specific type of node gets described as follows:
```XML
<Node>
    <Type>machine</Type>
    <Name>database</Name>
    <Properties>
        <IP>192.168.178.2</IP>
        <Username>root</Username>
        <Password>pass</Password>
    </Properties>
</Node>
```
**Explanations:** 
* ``Type`` - For a service node the type has to always be ``service``.
* `Name` - See node description.
* `Properties` - The Properties block is required in a machine node, because it is used to define IP-Adress (Host), Username and Password. The names for those can be taken from the example above.
    * For simplicity TOSCAlite only supports SSH-Authentication based on username and password. Public/Private Key authentication is not supported.


### Services

## Relationships

### HostedOn

### ConnectsTo

## Packaging

### model.xml
