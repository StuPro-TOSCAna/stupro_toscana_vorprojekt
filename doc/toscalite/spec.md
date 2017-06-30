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

#### Definition in XML

```XML
<Node>
    <Type>service</Type>
    <Name>apache-php</Name>
    <ImplementationArtifacts>
        <Create>install.sh</Create>
        <Start>start.sh</Start>
        <Stop>stop.sh</Stop>
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

In order to model a topology it is necessary to model the edges (Relationships) of a topology graph. Relationships get defined in a seperate block than nodes themselves (See Packaging/model.xml). Every node in XML gets defined as follows.

```XML
<Relationship>
    <Type>type</Type>
    <Source>source-name</Source>
    <Target>target-name</Target>
</Relationship>
```

**Explanation**:
* `Type` - Just like with nodes, this represents the type of the relationship and is either `hostedOn` for a HostedOn relationship or `connectsTo` for a ConnectsTo relationship.
* `Source` - The name of the source node of the relationship
* `Target` - The name of the target node of the relationship

All of the elements described above are neccessary for describing a relationship.

### HostedOn
If two nodes are in a hosted on relationship, they will be deployed on the same machine. The target node is always the parent node of the source node. When deploying an application, this means that the Target will be Created and started before the souce, when stopping the implementation artifacts of the service nodes will be executed in the opposite directon.

The hosted on relationship does not allow self connections and it does not allow machine nodes as sources only as targets.

### ConnectsTo

The connects to relationship describes a network connection between two nodes. Just like the hosted on relationship, this is a directional relationship. In order to allow the source node to connect to the target node it needs to know the connection information (e.g. IP-Adress). This information gets programmed into the application using a implementation artifact (Shell script) that gets executed after the service node was created.

A example definition for a connects to relationship:

```XML
<Relationship>
    <Type>connectsTo</Type>
    <Source>wordpress</Source>
    <Target>mysql</Target>
    <ImplementationArtifact>connect-to-database.sh</ImplementationArtifact>
</Relationship>
```

If the path in the implementation artifact is relative the TOSCAlite deployment system assumes the file is located in a folder called `relationships` within the zip archive.

The connects to relationship does not allow self connections and connections with (or between) machine nodes.

## Packaging

A application modeled in TOSCAlite gets packaged as a `.zip`-Archive this archive contains all the artifacts and the `model.xml` which describes the topology graph. The folder structure of the archive should contain one folder for every service node defined in the `model.xml` this folder contains all artifacts associated with this node (if no absolute path gets defined). The Implementation artifacts for connects to relationships will be stored in a folder called `relationships`  

### model.xml

The ``model.xl`` describes the topology graph. Nodes and Relationships get described in seperate blocks. 

A empty model.xml file looks as follows:
```XML
<Model>
    <Nodes>
        <!-- Insert nodes here -->
    </Nodes>
    <Relationships>
        <!-- Insert relationships here -->
    </Relationships>
</Model>
```