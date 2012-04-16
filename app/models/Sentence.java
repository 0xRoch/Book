package models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.appengine.repackaged.com.google.common.base.Flag;
import play.cache.Cache;
import siena.*;
import siena.Index;
import siena.Model;
import siena.Query;
import siena.embed.EmbedIgnore;
import siena.embed.Embedded;
import siena.embed.EmbeddedMap;
import siena.core.lifecycle.PostDelete;

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