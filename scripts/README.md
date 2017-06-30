## Simple-App Scripts

#### Content
- **simple-app (dir)**
    
    This is the not yet zipped archive of the example application. Containts model.xml, implementation artifacts and deployments artifacts
- **test (dir)**

   Contains a script for installing the application stack on a single vm. 
   For test purposes only. Do not use directly -- used by the setup script.
- **setup (script)**

    _usage:_      $ setup <target ip\>
    
    Sets up the environment needed for executing scripts on the target vm and triggers the installation script in the test-dir.

- **util (dir)**

    Contains utility scripts. These need to be present on target machine's 'normal' user's path and on the secure path (defined in /etc/sudoers; needed when executing scripts as root on ubuntu systems) in order to execute most implemenation artifacts.
