package models;

import play.Logger;
import siena.*;
import siena.core.lifecycle.PostDelete;
import siena.embed.EmbedIgnore;
import siena.embed.EmbeddedMap;

import java.util.List;

@EmbeddedMap
public class Page extends Model {

    @Id
    public Long id;

    @Column("book")
    public Book book;

    @EmbedIgnore
    @Index("pictureHash1")
    public int pictureHash1;

    @EmbedIgnore
    @Index("pictureHash2")
    public int pictureHash2;

    @EmbedIgnore
    @Index("pictureHash3")
    public int pictureHash3;

    public int layout;

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

    @PostDelete
    private void removeOrphans() {
        List<Sentence> sentences = Sentence.listByPage(this.id);
        for (Sentence sentence:sentences) {
            sentence.delete();
        }
        if (this.pictureHash1 != 0) {
            Picture picture1 = Picture.findByPictureHash(this.pictureHash1);
            picture1.delete();
        }
        if (this.pictureHash2 != 0) {
            Picture picture2 = Picture.findByPictureHash(this.pictureHash2);
            picture2.delete();
        }
        if (this.pictureHash3 != 0) {
            Picture picture3 = Picture.findByPictureHash(this.pictureHash3);
            picture3.delete();
        }
    }
}