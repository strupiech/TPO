package Services;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MySqlDbBookService implements IDbBookService {

    private String dbName;
    private String serverName;
    private int port;
    private String user;
    private String password;

    public MySqlDbBookService(String dbName, String serverName, int port, String user, String password) {
        this.dbName = dbName;
        this.serverName = serverName;
        this.port = port;
        this.user = user;
        this.password = password;
    }

    public Connection connect() {
        try {
            return DriverManager.getConnection("jdbc:mysql://" + serverName + ":" + port + "/" + dbName, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<String> getBooks() {
        List<String> books = new ArrayList<>();
        StringBuilder singleRecord;

        try (Connection connection = this.connect()) {
            if (connection == null) {
                books.add("Blad polaczenia z baza danych");
                return books;
            }

            PreparedStatement getBooksPrepareStatement = connection.prepareStatement("SELECT * FROM Book");
            ResultSet bookResultSet = getBooksPrepareStatement.executeQuery();

            while (bookResultSet.next()) {
                singleRecord = getConcatenatedStringBuilderWithAuthor(connection, bookResultSet);
                books.add(singleRecord.toString());
            }
            return books;

        } catch (SQLException e) {
            books.add(e.toString());
            return books;
        }
    }

    public String getBook(String id) {
        StringBuilder singleRecord = new StringBuilder();

        try (Connection connection = this.connect()) {
            if (connection == null)
                return "Blad polaczenia z baza danych";

            PreparedStatement getBookPreparedStatement = connection.prepareStatement("SELECT * FROM Book WHERE IdBook = ?");
            getBookPreparedStatement.setString(1, id);
            ResultSet bookResultSet = getBookPreparedStatement.executeQuery();

            if (bookResultSet.next())
                singleRecord = getConcatenatedStringBuilderWithAuthor(connection, bookResultSet);
            else
                singleRecord.append("Książka o podanym numerze nie istnieje");

            return singleRecord.toString();

        } catch (SQLException e) {
            return e.getMessage();
        }
    }

    private StringBuilder getConcatenatedStringBuilderWithAuthor(Connection connection, ResultSet bookResultSet) throws SQLException {
        PreparedStatement author = connection.prepareStatement("SELECT * FROM Author WHERE IdAuthor = ?");
        author.setString(1, bookResultSet.getString("IdAuthor"));
        ResultSet authorResultSet = author.executeQuery();
        authorResultSet.next();

        String title = bookResultSet.getString("Title");
        String authorName = authorResultSet.getString("Name");
        String authorSurname = authorResultSet.getString("Surname");
        return new StringBuilder().append("Tytuł książki: ").append(title).append("    Autor: ").append(authorName).append(" ").append(authorSurname);
    }

}
