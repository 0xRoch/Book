package controllers;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.repackaged.com.google.common.base.Flag;
import play.*;
import play.mvc.*;

import java.util.*;

import models.*;

public class Admin extends Controller {

    public static void index() {
        List<Book> books = Book.books();
        render(books);
    }

    public static void book(Long id) {
        if (id == null) {
            render();
        } else {
            Book book = Book.findById(id);
            List<Page> pages = Page.listByBook(id);
            render(book, pages);
        }
    }

    public static void submitBook(Long id, String name) {
        Book currentBook;
        if (id == null) {
            currentBook = new Book(name);
        } else {
            currentBook = Book.findById(id);
        }
        currentBook.update();
        book(id);
    }

    public static void deleteBook(Long id) {
        Book book = Book.findById(id);
        book.delete();
        index();
    }

    public static void page(Long id, int p) {
        Page page = Page.findById(id);
        List<Sentence> sentences = Sentence.listByPage(id);
        render(page, sentences, p);
    }

    public static void submitPage(Long id) {
        Book book = Book.findById(id);
        Page page = new Page(book);
        page.update();
        book(id);
    }

    public static void deletePage(Long id) {
        Page page = Page.findById(id);
        Long bookId = page.book.id;
        page.delete();
        book(bookId);
    }

    public static void sentence(Long id) {
        Sentence sentence = Sentence.findById(id);
        render(sentence);
    }

    public static void submitSentence(Long id) {
        Page page = Page.findById(id);
        Sentence sentence = new Sentence(page);
        sentence.update();
        book(page.book.id);
    }

    public static void updateSentence(Long id, String text) {
        Sentence sentence = Sentence.findById(id);
        sentence.text = text;
        sentence.update();
    }

    public static void uploadCover(Long id, Blob picture) {
        Book book = Book.findById(id);
        Picture newPic = new Picture(picture);
        newPic.update();
        book.pictureHash = newPic.pictureHash;
        book.update();
        book(id);
    }

    public static void uploadPagePicture(Long id, int pos, Blob picture) {
        Page page = Page.findById(id);
        Picture newPic = new Picture(picture);
        newPic.update();
        if (pos == 1) {
            page.pictureHash1 = newPic.pictureHash;
        } else if (pos == 2) {
            page.pictureHash2 = newPic.pictureHash;
        } else if (pos == 3) {
            page.pictureHash3 = newPic.pictureHash;
        }
        page.update();
    }

    public static void uploadAudio(Long id, Blob audio) {
        Sentence sentence = Sentence.findById(id);

    }
 }