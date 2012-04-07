package models;

import java.lang.annotation.*;
import java.lang.reflect.Type;
import java.util.List;

import com.google.appengine.api.datastore.Blob;

import play.data.Upload;
import play.data.binding.*;
import play.mvc.Http.Request;

@Global
public class GaeBlobBinder implements TypeBinder<Blob> {

    public Object bind(String string, Annotation[] antns, String value, Class type, Type type1) throws Exception {
        List<Upload> uploads = (List<Upload>)Request.current().args.get("__UPLOADS");
        for(Upload upload : uploads) {
            if(upload.getFieldName().equals(value)) {
            	byte[] data = upload.asBytes();

            	if(data.length > 0)
            		return new Blob(data);

                return null;
            }
        }
        return null;
    }
}