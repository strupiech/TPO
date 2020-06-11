<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Wyszukiwarka</title>
</head>
<body>
<h2>Wyszukiwarka ksiazek</h2>
<form action="http://localhost:8080/BookBase/books">
    <label for="id">Id ksiazki:</label><br>
    <input type="text" id="id" name="id"><br>
    <input style="margin-top: 10px" type="submit" value="Pokaz ksiazke o wpisanym numerze">
</form>
<form action="http://localhost:8080/BookBase/books">
    <input style="margin-top: 10px" type="submit" value="Pokaz wszystkie ksiazki">
</form>
</body>
</html>
