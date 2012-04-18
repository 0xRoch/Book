package models;

import siena.*;
import siena.core.lifecycle.PostDelete;
import siena.embed.EmbedIgnore;

import java.util.List;

public class Book extends Model {

    @Id
    public Long id;
    
    public String name;

    @Column("language")
    public Language language;

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

    @PostDelete
    private void removeOrphans() {
        List<Page> pages = Page.listByBook(this.id);
        for (Page page:pages) {
            page.delete();
        }
        Picture picture = Picture.findByPictureHash(this.pictureHash);
        picture.delete();
    }
}