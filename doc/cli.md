# CLI

Steps to build and run **TOSCA2vSphere**:
1. Build the project with:
    ```
    mvn package
    ```
2. Start **TOSCA2vSphere** with:
    ```
    java -jar target/de.toscana.transformator-1.0-SNAPSHOT.jar model.zip
    ```
    Replace *model.zip* with the file you want to open.

3. If parsing the file was successful you can now use the click
    1. You can create everything needed to run your model if you enter:
        ```
        create
        ```
    2. if the model was created you can start it with:
        ```
        start
        ```
    3. and finally there is the possibility to stop it with:
        ```
        stop
        ```
    It is mandatory that `create` has to be called before `start` and `start` has to be called before `stop`. If not, there will be an error.
