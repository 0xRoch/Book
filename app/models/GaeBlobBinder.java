package models;

import com.google.appengine.api.datastore.Blob;
import play.data.Upload;
import play.data.binding.Global;
import play.data.binding.TypeBinder;
import play.mvc.Http.Request;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;

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