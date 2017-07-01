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
        <Property key="keyname">value</Property>
    </Properties>
</Node>
```
**Explanation:**
* ``Type`` - This defines the node type. In TOSCAlite only two types are allowed. (``service`` and ``machine``)
* ``Ǹame`` - The name of a node is used to uniquely identify the node in the topology. It is the nodes primary identifier. The names are only allowed to contain the following characters ``abcdefghijklmnopqrstuvwxyz-_1234567890``
* ``Properties`` -  Has to contain Property elements, with the attribute Key. the key is required to be a lower-case name without spaces (allowed characters `abcdefghijklmnopqrstuvxyz`).

The ``Type`` and ``Name`` elements are always required. ``Properties`` can be optional, at least in service nodes.

### Machines

A machine node represents a Linux-based system, probably Ubuntu 16.04. The deployment system does not care where the system is running, as long as the machine is reachable with SSH.
This specific type of node gets described as follows:

```XML
<Node>
    <Type>machine</Type>
    <Name>database</Name>
    <Properties>
        <Property key="ip">192.168.178.1</Property>
        <Property key="username">root</Property>
        <Property key="password">1234567</Property>
    </Properties>
</Node>
```
**Explanations:**
* ``Type`` - For a service node the type has to always be ``service``.
* `Name` - See node description.
* `Properties` - The Properties block is required in a machine node, because it is used to define IP-Adress (Host), Username and Password. The names for those can be taken from the example above.
    * For simplicity TOSCAlite only supports SSH-Authentication based on username and password. Public/Private Key authentication is not supported.

### Services

A Service represents a part of a Topology that has to be installed on a machine or on top of other services. The steps to install (Start and stop) the service are defined in shell scripts (See *Implementation Artifacts*). Files other then shell scripts get defined in deployment artifacts.

#### Implementation Artifacts

Implementation artifacts have to implement the lifecycle operations for a service. Due to TOSCAlite requiring Linux-based systems shell scripts have to be used to perform these operations.

The following operations are supported:
* Create - Required by every service. Used to create the service. This has to install all the packages required to run the service, for example.
* Start - Optional. Used to start the service.
* Stop - Optional. Used to stop the service.

#### Deployment Artifacts
Deployment artifacts are files, such as executables, that get loaded with the implementation artifact when creating the service node. A service can have multiple deployment artifacts. There is no limitation for file types. The ability to process the deployment artifacts has to be implemented in the shell script.

#### Folder Structure on target machine

TODO!

#### Definition in XML

```XML
<Node>
    <Type>service</Type>
    <Name>apache-php</Name>
    <ImplementationArtifacts>
        <Create>apache-php/install.sh</Create>
        <Start>apache-php/start.sh</Start>
        <Stop>apache-php/stop.sh</Stop>
    </ImplementationArtifacts>
    <DeploymentArtifacts>
        <DeploymentArtifact>apache-php/configurations.zip</DeploymentArtifact>
    </DeploymentArtifacts>
    <Properties>
        <Property key="port">8080</Property>
    </Properties>
</Node>
```
**Explanation:**
* `ImplementationArtifacts` - The ImplementationArtifact section contains paths to shell scripts that perform specific operations (Start, Stop and Create) on the Machine
    * `Create` (**Required**) - Defines the path to the shell script that has to be executed when deploying the application.
    * `Start` (**Optional**) - Defines the path to the shell script that has to be executed when starting the application.
    * `Stop` (**Optional**) - Defines the path to the shell script that has to be executed when stopping the application.
    * How the paths of artifacts have to look is described under *Artifact Paths*
* `DeploymentArtifacts` - List of paths for deployment artifacts

#### Artifact Paths

The path defined in either implementation artifacts or deployment artifacts is relative by default.

For service nodes the relative path in the archive is converted to `/<Nodename>/<Path>` by default. For example the path `install.sh` of the node `apache` gets converted to `/apache/install.sh` in the archive.

In order to define absolute paths within the archive a `/` has to be added in front.

### Environment-Variable Mapping of Properties

The value of properties gets mapped to environment variables in the following naming scheme: `<Nodename>_<PropertyKey>`.
The key and the node name get converted to Upper case letters. Before the execution of the scripts starts all envirionment variables get created. It is possible to access all Properties event the ones from another node as long as both nodes belong to the same machine.

## Relationships

### HostedOn

### ConnectsTo

## Packaging

### model.xml
