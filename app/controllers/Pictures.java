package controllers;

import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.Transform;
import models.Picture;
import play.mvc.Controller;

import java.io.*;

public class Pictures extends Controller {
    
    public static InputStream show(int hash) {
        Picture picture = Picture.findByPictureHash(hash);
        notFoundIfNull(picture);
        InputStream in = new ByteArrayInputStream(picture.picture.getBytes());
    	return in;
    }

    public static InputStream showThumb(int hash) {
        Picture picture = Picture.findByPictureHash(hash);
        notFoundIfNull(picture);
        InputStream in = new ByteArrayInputStream(Pictures.resizePicture(picture.picture.getBytes(), 200, 200));
    	return in;
    }
    
    public static InputStream showSized(int hash) {
        Picture picture = Picture.findByPictureHash(hash);
        notFoundIfNull(picture);
        InputStream in = new ByteArrayInputStream(Pictures.resizePicture(picture.picture.getBytes(), 300, 300));
    	return in;
    }
    
    public static byte[] resizePicture(byte[] picture, int width, int height) {
        Image oldImage = ImagesServiceFactory.makeImage(picture);
        ImagesService imagesService = ImagesServiceFactory.getImagesService();
        int w = 0;
        int h = 0;
        
        if (oldImage.getWidth() > oldImage.getHeight()) {
            w = 1000;
            h = height;
        } else {
            h = 1000;
            w = width;
        }
        Transform resize = ImagesServiceFactory.makeResize(w, h);
        Image resizedImage = imagesService.applyTransform(resize, oldImage);

        double crop_h = 0;
        double crop_w = 0;
        if (oldImage.getWidth() > oldImage.getHeight()) {
            crop_h = (double) 1.0;
            crop_w = (double) width / resizedImage.getWidth();
            if (crop_w > 1.0) {
                crop_w = 1.0;
            }
        } else {
            crop_h = (double) height / resizedImage.getHeight();
            crop_w = (double) 1.0;
        }

        Transform crop = ImagesServiceFactory.makeCrop(0.0, 0.0, crop_w, crop_h);
        Image cropedImage = imagesService.applyTransform(crop, resizedImage);

        return cropedImage.getImageData();
    }

}