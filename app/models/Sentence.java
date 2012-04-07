package models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.appengine.repackaged.com.google.common.base.Flag;
import play.cache.Cache;
import siena.Id;
import siena.Index;
import siena.Model;
import siena.Query;
import siena.embed.EmbedIgnore;
import siena.embed.Embedded;
import siena.embed.EmbeddedMap;

@EmbeddedMap
public class Sentence extends Model {

    @Id
    public Long id;

    public String text;

    @EmbedIgnore
    @Index("audioHash")
    public int audioHash;

    static Query<Page> all() {
        return Model.all(Page.class);
    }

    public static Page findById(Long id) {
        return all().filter("id", id).get();
    }

}