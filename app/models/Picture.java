package models;

import com.google.appengine.api.datastore.Blob;
import play.data.binding.As;
import siena.*;
import siena.embed.EmbedIgnore;

import java.util.Date;

public class Picture extends Model {

    @Id
    public Long id;

    @Index("postedAt")
    @EmbedIgnore
    public Date postedAt;

    @EmbedIgnore
    @Column("picture")
    @As(binder=models.GaeBlobBinder.class)
    public Blob picture;

    @Index("pictureHash")
    @EmbedIgnore
    public int pictureHash;

    public Picture(Blob picture) {
       this.postedAt = new Date();
       this.picture = picture;
       this.pictureHash = picture.hashCode();
    }

    static Query<Picture> all() {
        return Model.all(Picture.class);
    }
    
    public static Query<Picture> query() {
    	return all();
    }
    
    public static Picture findByPictureHash(int hash) {
        return all().filter("pictureHash", hash).get();
    }

}