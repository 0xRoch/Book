package models;

import siena.Filter;
import siena.Id;
import siena.Model;
import siena.Query;
import siena.core.lifecycle.PostDelete;
import siena.embed.EmbedIgnore;

import java.util.List;

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

    public static Language findByName(String name) {
        return all().filter("name", name).get();
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