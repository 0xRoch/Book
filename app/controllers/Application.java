package controllers;

import play.*;
import play.mvc.*;

import java.util.*;

import models.*;

public class Application extends Controller {

    public static void index() {
        List<Book> books = Book.books();
        render(books);
    }

    public static void book(Long id) {
        Book book = Book.findById(id);
        render(book);
    }

}