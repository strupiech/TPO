package Servlets;

import Services.IDbBookService;
import Services.MySqlDbBookService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/books")
public class BooksServlet extends HttpServlet {

    private IDbBookService service;

    public BooksServlet() {
        service = new MySqlDbBookService("BookBase", "192.168.99.100", 32768, "root", "password");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter outWriter = resp.getWriter();

        String bookId = req.getParameter("id");

        if (bookId == null) {
            List<String> books = service.getBooks();
            for (String book :
                    books) {
                outWriter.write("<p>" + book + "</p>");
            }
        }else if(bookId.equals("")){
            outWriter.write("<p> Nie podano numeru książki </p>");
        } else {
            outWriter.write("<p>" + service.getBook(req.getParameter("id")) + "</p>");
        }

        outWriter.println(
                "<form action=\"" + resp.encodeURL("/BookBase") +
                    "\" method=\"post\">" +
                    "<td><input type=\"submit\"value=\"Wróć na stronę główną\"></td>" +
                "</form>");

        outWriter.close();
    }


}
