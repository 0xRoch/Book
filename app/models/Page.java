package models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.appengine.api.datastore.Blob;
import play.cache.Cache;
import siena.*;
import siena.Id;
import siena.Index;
import siena.Model;
import siena.Query;
import siena.embed.EmbedIgnore;
import siena.embed.Embedded;
import siena.embed.EmbeddedMap;

@EmbeddedMap
public class Page extends Model {

    @Id
    public Long id;

    @Column("book")
    public Book book;

    @EmbedIgnore
    @Index("pictureHash")
    public int pictureHash;

    @Embedded
    public List<Sentence> sentences;

    public Page(Book book) {
        this.book = book;
    }

    static Query<Page> all() {
        return Model.all(Page.class);
    }

    public static Page findById(Long id) {
        return all().filter("id", id).get();
    }

    public static List<Page> listByBook(Long id) {
        Book book = Book.findById(id);
        return all().filter("book", book).fetch();
    }

}