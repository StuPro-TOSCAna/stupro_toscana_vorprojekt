# SimpleTaskApp

This is a simple task app to test the transformator.

## Requirements
1. the app needs the ```mysql-credentials.php``` - file, because it stores the credentials

   Example:
    ```php
    <?php
    $host = "localhost";
    $port = 3306; // standard port
    $username = "nick";
    $password = "test";
    $database = "taskdb";
    ?>
    ```
2. MySQL-DB generated with the ```createdb.sql``` file.

   You can simply run:

   ```mysql -u username --password="password" < createdb.sql ```
