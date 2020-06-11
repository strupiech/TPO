package Services;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface IDbBookService {
    Connection connect() throws SQLException;
    List<String> getBooks();
    String getBook(String id);
}
