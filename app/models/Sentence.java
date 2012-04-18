package models;

import siena.*;
import siena.core.lifecycle.PostDelete;
import siena.embed.EmbedIgnore;
import siena.embed.EmbeddedMap;

import java.util.List;

@EmbeddedMap
public class Sentence extends Model {

    @Id
    public Long id;

    public String text;

    @Column("page")
    public Page page;

    @EmbedIgnore
    @Index("audioHash")
    public int audioHash;

    public Sentence(Page page) {
        this.page = page;
    }

    static Query<Sentence> all() {
        return Model.all(Sentence.class);
    }

    public static Sentence findById(Long id) {
        return all().filter("id", id).get();
    }

    public static List<Sentence> listByPage(Long id) {
        Page page = Page.findById(id);
        return all().filter("page", page).fetch();
    }

    @Override
    public String toString() {
        if (text != null) {
            return text;
        } else {
            return "Sentence "+id;
        }
    }

    @PostDelete
    private void removeOrphans() {
        Audio audio = Audio.findByAudioHash(this.audioHash);
        audio.delete();
    }
}