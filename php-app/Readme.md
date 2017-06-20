# SimpleTaskApp

This is a simple task app to test the transformator.

## Requirements
- the app needs the ```mysql-credentials.php``` - file, because it stores the credentials

   Example:
    ```
    <?php
    $servername = "localhost";
    $username = "nick";
    $password = "test";
    $database = "taskdb";
    ?>
    ```
- MySQL-DB generated with ```createdb.sql```.

   You can simply run:
   ```mysql -u username --password="password" < createdb.sql ```
