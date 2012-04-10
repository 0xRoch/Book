package models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import play.cache.Cache;
import siena.*;
import siena.Id;
import siena.Index;
import siena.Model;
import siena.Query;
import siena.embed.EmbedIgnore;
import siena.embed.Embedded;
import siena.embed.EmbeddedMap;

public class Book extends Model {

    @Id
    public Long id;
    
    public String name;

    @EmbedIgnore
    @Index("pictureHash")
    public int pictureHash;
    
    @EmbedIgnore @Filter("book")
    public Query<Page> pages;

    static Query<Book> all() {
        return Model.all(Book.class);
    }

    public Book(String name) {
           this.name = name;
    }

    public static Book findById(Long id) {
        return all().filter("id", id).get();
    }
    
    public static Book findByName(String name) {
        return all().filter("name", name).get();
    }

    public static List<Book> books() {
    	return all().order("name").fetch();
    }

    @Override
    public String toString() {
        return name;
    }
}