package controllers;

import models.*;
import play.Logger;
import play.mvc.Controller;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;

public class Application extends Controller {

    public static void index(String language) {
        List<Book> books;
        if (language == null) {
            books = Book.books();
        } else {
            Language lang = Language.findByName(language);
            books = Book.books(lang);
        }
        List<Language> languages = Language.languages();
        render(books, languages);
    }

    public static void book(Long id) {
        Book book = Book.findById(id);
        List<Page> pages = Page.listByBook(id);
        List<Language> languages = Language.languages();
        render(book, pages, languages);
    }

    public static void page(Long id) {
        Page page = Page.findById(id);
        List<Sentence> sentences = Sentence.listByPage(id);
        render(page, sentences);
    }

    public static void audio(Long id) throws IOException {
        Sentence sentence = Sentence.findById(id);
        if (sentence.audioHash != 0) {
            Audio audio = Audio.findByAudioHash(sentence.audioHash);
            InputStream is = new ByteArrayInputStream(audio.audio.getBytes());
            renderBinary(is, "audio", "audio/mpeg", true);
        } else {
            Page page = Page.findById(sentence.page.id);
            Book book = Book.findById(page.book.id);
            Language language = Language.findById(book.language.id);
            URL url = new URL("http://translate.google.com/translate_tts?tl="+language.iso+"&q="+URLEncoder.encode(sentence.text, "UTF-8"));
            URLConnection conn =  url.openConnection();
            conn.addRequestProperty("User-Agent", "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Win64; x64; Trident/5.0)");
            response.cacheFor("168h");
            renderBinary(new BufferedInputStream(conn.getInputStream()), "audio", "audio/mpeg", true);
        }
    }
}