# Example `model.xml`

The following XML code represents a complete example of a Topology.
It models a WordPress application running on Apache with PHP on one machine and the Database (MySQL) running on anotherone.
```XML
<Model>
    <Nodes>
        <Node>
            <Type>machine</Type>
            <Name>webserver</Name>
            <Properties>
                <Property key="ip">192.168.178.1</Property>
                <Property key="username">root</Property>
                <Property key="password">password</Property>
            </Properties>
        </Node>
        <Node>
            <Type>machine</Type>
            <Name>database</Name>
            <Properties>
                <Property key="ip">192.168.178.2</Property>
                <Property key="username">root</Property>
                <Property key="password">pass word</Property>
            </Properties>
        </Node>
        <Node>
            <Type>service</Type>
            <Name>apache-php</Name>
            <ImplementationArtifacts>
                <Create>install.sh</Create>
                <Start>start.sh</Start>
                <Stop>stop.sh</Stop>
            </ImplementationArtifacts>
            <DeploymentArtifacts>
                <DeploymentArtifact>configurations.zip</DeploymentArtifact>
            </DeploymentArtifacts>
            <Properties>
                <Property key="port">8080</Property>
            </Properties>
        </Node>
        <Node>
            <Type>service</Type>
            <Name>wordpress</Name>
            <ImplementationArtifacts>
                <Create>install.sh</Create>
            </ImplementationArtifacts>
            <DeploymentArtifacts>
                <DeploymentArtifact>wp.zip</DeploymentArtifact>
            </DeploymentArtifacts>
            <Properties>
                <Property key="defaultUsername">admin</Property>
                <Property key="defaultPassword">password</Property>
                <Property key="databaseType">MySQL</Property>
            </Properties>
        </Node>
        <Node>
            <Name>mysql</Name>
            <Type>service</Type>
            <ImplementationArtifacts>
                <Create>/mysql/install.sh</Create>
                <Start>/mysql/start.sh</Start>
                <Stop>/mysql/stop.sh</Stop>
            </ImplementationArtifacts>
            <DeploymentArtifacts>
                <DeploymentArtifact>/wordpress/wp.sql</DeploymentArtifact>
            </DeploymentArtifacts>
            <Properties>
                <Property key="port">8080</Property>
                <Property key="rootPassword">password</Property>
            </Properties>
        </Node>
    </Nodes>
    <Relationships>
        <Relationship>
            <Type>hostedOn</Type>
            <Source>apache-php</Source>
            <Target>webserver</Target>
        </Relationship>
        <Relationship>
            <Type>hostedOn</Type>
            <Source>wordpress</Source>
            <Target>apache-php</Target>
        </Relationship>
        <Relationship>
            <Type>hostedOn</Type>
            <Source>mysql</Source>
            <Target>database</Target>
        </Relationship>
        <Relationship>
            <Type>connectsTo</Type>
            <Source>wordpress</Source>
            <Target>mysql</Target>
            <ImplementationArtifact>wp-connection-init.sh</ImplementationArtifact>
        </Relationship>
    </Relationships>
</Model>
```
