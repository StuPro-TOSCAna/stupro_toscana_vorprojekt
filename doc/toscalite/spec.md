# TOSCAlite Specification

## Introduction

TOSCAlite is a very lightweight version of the OASIS TOSCA standard. It can be used to model a deployment based on Linux systems (mostly Ubuntu 16.04 LTS).

## Nodes

A node defined in the ``model.xml`` (see [Packaging](#packaging)) represents a node in a TOSCAlite topology.
TOSCAlite defines two types of nodes. It is not possible to extend TOSCAlite with more types of nodes.

In XML a node gets defined as follows:

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
* ``Name`` - This defines the node name. A node's name is used to uniquely identify the node in the topology. It is the node's primary identifier. Node names are only allowed to contain the following characters ``abcdefghijklmnopqrstuvwxyz-_1234567890``
* ``Properties`` -  This block defines the node's properties. It has to contain property elements, each with an attribute Key. The attribute key is required to be a lower-case name without spaces (allowed characters `abcdefghijklmnopqrstuvxyz`).

The ``Type`` and ``Name`` elements are always required. ``Properties`` are mandatory in machine nodes and optional in service nodes.

### Machines

A machine node represents a Linux-based system, usually Ubuntu 16.04. The deployment system does not check whether the system is running, as long as the machine is reachable with SSH. Therefore SSH has to be installed on every target machine.
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
**Explanation:**
* ``Type`` - For a machine node the type always has to be ``machine``. In a service node it would be ``service``.
* `Name` - As specified earlier, a name refers to the unique name of a node. (See [Nodes](#nodes))
* `Properties` - The properties block is mandatory in a machine node, because it is used to define the IP address (of the host), a username and password. The specific attribute keys for these mandatory properties can be taken from the example above.
    * For simplicity TOSCAlite only supports SSH authentication based on username and password. Public/private key authentication is not supported.

### Services

A service represents the part of a topology that has to be installed on a machine or on top of other services. The steps to install, start or stop the service are defined in shell scripts (see [Implementation Artifacts](#implementation-artifacts)). Files other than shell scripts get defined in deployment artifacts.

#### Implementation Artifacts

Implementation artifacts have to implement the lifecycle operations for a service. Due to TOSCAlite requiring Linux-based systems, shell scripts have to be used to perform these operations.

The following operations are supported:
* Create - Mandatory for every service. Used to create the service. This script/executable should install all the packages required to run the service.
* Start - Optional. Used to start the service.
* Stop - Optional. Used to stop the service.

##### Execution of Implementation Artifacts

Before any script gets executed, the whole TOSCAlite archive is loaded onto every virtual machine and extracted there. All the extracted files will be located in the home directory of the user that is defined in the machine node.

The scripts get executed in the directory they are located in. This means that the TOSCAlite deployment system will automatically change the parent working directory to the directory in which the script is located.

#### Deployment Artifacts
Deployment artifacts are files, such as executables, that get loaded onto the machine with the implementation artifacts when creating the service node. A service can have multiple deployment artifacts. There is no limitation for file types. The ability to process the deployment artifacts has to be implemented in the shell script.

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
* `ImplementationArtifacts` - The implementation artifact section contains paths to shell scripts that perform specific operations (start, stop and create) on the machine.
    * `Create` (**Required**) - Defines the path to the shell script that has to be executed when deploying the application.
    * `Start` (**Optional**) - Defines the path to the shell script that has to be executed when starting the application.
    * `Stop` (**Optional**) - Defines the path to the shell script that has to be executed when stopping the application.
    * How the paths of artifacts have to be specified is described under *Artifact Paths*
* `DeploymentArtifacts` - Defines a list of paths for deployment artifacts.

#### Artifact Paths

The path defined in either implementation artifacts or deployment artifacts is relative by default.

For service nodes the relative path in the archive is converted to `/<Nodename>/<Path>` by default. For example the path `install.sh` of the node `apache` gets converted to `/apache/install.sh` in the archive.

In order to define absolute paths within the archive a `/` has to be added in front.

### Environment Variable Mapping of Properties

The value of properties gets mapped to environment variables in the following naming scheme: `<Nodename>_<PropertyKey>`.
The key and the node name get converted into upper case letters. Before the execution of the scripts starts, all environment variables get created. It is possible to access all properties, even the ones from another node, as long as both nodes belong to the same machine.

## Relationships

In order to model a topology, it is necessary to model the edges (relationships) of a topology graph. Relationships get defined in their own block, seperate from nodes themselves (See [Packaging/model.xml](#modelxml)). Every relationship in XML gets defined as follows:

```XML
<Relationship>
    <Type>type</Type>
    <Source>source-name</Source>
    <Target>target-name</Target>
</Relationship>
```

**Explanation**:
* `Type` - Just like with nodes, this represents the type of the relationship and is either `hostedOn` for a "hostedOn" relationship or `connectsTo` for a "connectsTo" relationship.
* `Source` - The name of the source node of the relationship
* `Target` - The name of the target node of the relationship

All elements described above are mandatory to describe a relationship.

### HostedOn
If two nodes are in a hostedOn relationship, they will be deployed on the same machine. The target node is always the parent node of the source node. This means that when deploying an application, the target will be created and started before the source. When stopping, the implementation artifacts of the service nodes will be executed in the  reverse order.

The hostedOn relationship does not allow self connections or machine nodes as sources, only as targets.

### ConnectsTo

The connectsTo relationship describes a network connection between two nodes. Just like the hostedOn relationship, connectsTo is a directional relationship. In order to allow the source node to connect to the target node, it needs to know the connection information (e.g. IP address). This information gets programmed into the application using an implementation artifact (shell script) that gets executed after the service node was created.

A example definition for a connectsTo relationship:

```XML
<Relationship>
    <Type>connectsTo</Type>
    <Source>wordpress</Source>
    <Target>mysql</Target>
    <ImplementationArtifact>connect-to-database.sh</ImplementationArtifact>
</Relationship>
```

If the path in the implementation artifact is relative, the TOSCAlite deployment system assumes the file is located in a folder called `relationships` within the ZIP archive.

The connectsTo relationship does not allow self connections or connections with (or between) machine nodes.

## Packaging

An application modeled in TOSCAlite gets packaged as a `.zip` archive. This archive contains all the artifacts and the `model.xml` which describes the topology graph. The folder structure of the archive should contain one folder for every service node defined in the `model.xml`. Each folder contains all artifacts associated with this node (if no absolute path gets defined). The implementation artifacts for connectsTo relationships will be stored in a folder called `relationships`.

### model.xml

The ``model.xml`` describes the topology graph. Nodes and relationships get described in seperate blocks.

An empty model.xml file gets defined as follows:
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
