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
import siena.core.lifecycle.PostDelete;

public class Language extends Model {

    @Id
    public Long id;

    public String iso;

    public String name;

    @EmbedIgnore @Filter("language")
    public Query<Book> books;

    static Query<Language> all() {
        return Model.all(Language.class);
    }

    public Language(String name, String iso) {
        this.name = name;
        this.iso = iso;
    }

    public static Language findById(Long id) {
        return all().filter("id", id).get();
    }

    public static List<Language> languages() {
    	return all().fetch();
    }

    @Override
    public String toString() {
        return name;
    }

    @PostDelete
    private void removeOrphans() {
        List<Book> books = this.books.fetch();
        for (Book book:books) {
            book.delete();
        }
    }
}