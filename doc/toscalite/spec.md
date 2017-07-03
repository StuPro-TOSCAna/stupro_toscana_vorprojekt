# TOSCAlite Specification

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
* ``Name`` - The name of a node is used to uniquely identify the node in the topology. It is the nodes primary identifier. The names are only allowed to contain the following characters ``abcdefghijklmnopqrstuvwxyz-_1234567890``
* ``Properties`` -  Has to contain Property elements, with the attribute Key. the key is required to be a lower-case name without spaces (allowed characters `abcdefghijklmnopqrstuvxyz`). 

The ``Type`` and ``Name`` elements are always required. The ``Properties``-Section is optional in Service Nodes and required in Machine Nodes.

### Machines

A Machine Node represents a Linux-based system, probably Ubuntu 16.04. The deployment system does not care where the system is running, as long as the machine is reachable with SSH. Therefore SSH has to be installed on every target machine.
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
* ``Type`` - For a Service Node the type has to always be ``service``.
* `Name` - See node description.
* `Properties` - The Properties block is required in a Machine Node, because it is used to define IP-Adress (Host), Username and Password. The names for those can be taken from the example above.
    * For simplicity TOSCAlite only supports SSH-Authentication based on username and password. Public/ Private Key authentication is not supported.

### Services

A Service represents a part of a Topology that has to be installed on a machine or on top of other services. The steps to install, start and stop) the service are defined in its Implementation Artifacts. If additional files are needed for deployment they can be defined as Deployment Artifacts. 

#### Implementation Artifacts

Implementation Artifacts have to implement the lifecycle operations for a service. Due to TOSCAlite requiring Linux-based systems shell scripts have to be used to perform these operations.

The following operations are supported:
* Create - Required by every service. Used to create the service. This script/ executable has to install all the packages required to run the service, for example.
* Start - Optional. Used to start the service.
* Stop - Optional. Used to stop the service.

##### Execution of Implementation Artifacts

Before any script gets executed, the whole TOSCAlite archive is loaded on to every virtual machine and extracted there. All the extracted files will be located in the home directiory of the user that is defined in the Machine Node. 

The scripts get executed in the directory they are located in, this means, that the TOSCAlite deployment system will automatically update the parent working directory to the directory in which the script is located.

#### Deployment Artifacts
A Deployment Artifact represents any file which can be used by Implementation Artifacts.
A Service can have none or multiple Deployment Artifacts.

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
        <DeploymentArtifact key="configuration">apache-php/configurations.zip</DeploymentArtifact>
    </DeploymentArtifacts>
    <Properties>
        <Property key="port">8080</Property>
    </Properties>
</Node>
```
**Explanation:**
* `ImplementationArtifacts` - The Implementation Artifact section contains paths to shell scripts that perform specific operations (Start, Stop and Create) on the Machine
    * `Create` (**Required**) - Defines the path to the shell script that has to be executed when deploying the application.
    * `Start` (**Optional**) - Defines the path to the shell script that has to be executed when starting the application.
    * `Stop` (**Optional**) - Defines the path to the shell script that has to be executed when stopping the application.
    * How the paths of artifacts have to look is described under *Artifact Paths*
* `DeploymentArtifacts` - List of paths for Deployment Artifacts

#### Artifact Paths

The path defined in either Implementation Artifacts or Deployment Artifacts is relative by default.

For Service Nodes the relative path in the archive is converted to `/<nodename>/<path>` by default. For example the path `install.sh` of the node `apache` gets converted to `/apache/install.sh` in the archive.

In order to define absolute paths within the archive a `/` has to be added in front.


### Usage of Properties and Deployment Artifacts in Implementation Artifacts
Implementation Artifacts can make use of the defined values of Properties and Deployment Artifacts. It is assured that every Impelementation Artifact has access to every Property and every Deployment Artifact via environment variables. 

##### Example

```XML
<Node>
  <Type>service</Type>
  <Name>database</Name>
  <Properties>
    <Property key="user">smith</Property>
  </Properties>
  <DeploymentArtifacts>
    <DeploymentArtifact key="createdb">database-creation.sql</DeploymentArtifact>
  </DeploymentArtifacts>
</Node>
```

- Property ```user``` of node ```database``` gets mapped to environment variable ```DATABASE_USER``` which holds value ```smith```
- DeploymentArtifact ```createdb``` of node ```database``` gets mapped to environment variable ```DATABASE_CREATEDB``` which holds value ```database-creation.sql```

When writing a bash script as Implementation Artifact for any node or relationship, these values can then be accessed with ```$DATABASE_USER``` and ```$DATABASE_CREATEDB```.

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

All of the elements described above are necessary for describing a relationship.

### HostedOn
If two nodes are in a hosted on relationship, they will be deployed on the same machine. The target node is always the parent node of the source node. When deploying an application, this means that the Target will be Created and started before the souce, when stopping the Implementation Artifacts of the Service Nodes will be executed in the opposite directon.

The hosted on relationship does not allow self connections and it does not allow machine Nodes as sources, only as targets.

### ConnectsTo

The connects to relationship describes a network connection between two nodes. Just like the hosted on relationship, this is a directional relationship. In order to allow the source node to connect to the target node it needs to know the connection information (e.g. IP-Adress). This information gets programmed into the application using a Implementation Artifact (Shell script) that gets executed after the Service node was created.

A example definition for a connects to relationship:

```XML
<Relationship>
    <Type>connectsTo</Type>
    <Source>wordpress</Source>
    <Target>mysql</Target>
    <ImplementationArtifact>connect-to-database.sh</ImplementationArtifact>
</Relationship>
```

If the path in the Implementation Artifact is relative the TOSCAlite deployment system assumes the file is located in a folder called `relationships` within the Zip archive.

The connects to relationship does not allow self connections and connections with (or between) Machine Nodes.

## Packaging

An application modeled in TOSCAlite gets packaged as a `.zip`-Archive, this archive contains all the artifacts and the `model.xml` which describes the topology graph. The folder structure of the archive should contain one folder for every Service Node defined in the `model.xml` this folder contains all artifacts associated with this node (if no absolute path gets defined). The Implementation Artifacts for connects to relationships will be stored in a folder called `relationships`  

### model.xml

The ``model.xml`` describes the topology graph. Nodes and Relationships get described in seperate blocks. 

An empty model.xml file looks as follows:
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
