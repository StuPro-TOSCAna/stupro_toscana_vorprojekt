<?php
include_once "mysql-credentials.php";

$post = $_POST['task'];
if ($post != "") {
    saveToDb($post);
}

function saveToDb($task)
{
    $conn = newDbConnection();
    if (!$conn->query("INSERT INTO tasks(task) VALUES('".$task."')")) {
        echo("Creating task failed");
    }
    $conn->close();
}
function readFromDb()
{
    $sql = "select * from tasks";
    $conn = newDbConnection();
    $result = $conn->query($sql);
    if ($result->num_rows > 0) {
        // output data of each row
    while ($row = $result->fetch_assoc()) {
        echo "id: " . $row["id"]. " - Task: " . $row["task"]."<br>";
    }
    } else {
        echo "0 results";
    }
    $conn->close();
}
function newDbConnection()
{
    extract($GLOBALS);
   $conn = new mysqli($host, $username, $password, $database, $port);
    if ($conn->connect_error) {
        die("Connection failed: " . $conn->connect_error);
    }
    return $conn;
}
?>
<!DOCTYPE html>
<html>
   <head>
      <meta charset="utf-8">
      <title>SimpleTaskApp</title>
      <style>
      body {
         font-family: sans-serif;
      }
      </style>
   </head>
   <body>
      <h1>SimpleTaskApp</h1>
      <form class="insertTask" action="index.php" method="post">
         <input type="text" name="task" />
         <button type="submit" name="button">submit</button>
      </form>
      <?php
      readFromDb();
       ?>
   </body>
</html>
