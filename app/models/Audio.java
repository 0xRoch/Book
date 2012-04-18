package models;

import com.google.appengine.api.datastore.Blob;
import play.data.binding.As;
import siena.*;
import siena.embed.EmbedIgnore;

import java.util.Date;

public class Audio extends Model {

    @Id
    public Long id;

    @Index("postedAt")
    @EmbedIgnore
    public Date postedAt;

    @EmbedIgnore
    @Column("picture")
    @As(binder=models.GaeBlobBinder.class)
    public Blob audio;

    @Index("pictureHash")
    @EmbedIgnore
    public int audioHash;

    public Audio(Blob audio) {
       this.postedAt = new Date();
       this.audio = audio;
       this.audioHash = audio.hashCode();
    }

    static Query<Audio> all() {
        return Model.all(Audio.class);
    }

    public static Query<Audio> query() {
    	return all();
    }

    public static Audio findByAudioHash(int hash) {
        return all().filter("audioHash", hash).get();
    }

}