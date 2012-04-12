package controllers;

import play.*;
import play.mvc.*;
import java.io.*;
import java.util.*;

import models.*;

public class Application extends Controller {

    public static void index() {
        List<Book> books = Book.books();
        render(books);
    }

    public static void book(Long id) {
        Book book = Book.findById(id);
        List<Page> pages = Page.listByBook(id);
        render(book, pages);
    }

    public static void page(Long id) {
        Page page = Page.findById(id);
        List<Sentence> sentences = Sentence.listByPage(id);
        render(page, sentences);
    }

    public static void audio(int hash) {
        Audio audio = Audio.findByAudioHash(hash);
        notFoundIfNull(audio);
        InputStream is = new ByteArrayInputStream(audio.audio.getBytes());
        renderBinary(is, "audio", "audio/mpeg", true);
    }
}