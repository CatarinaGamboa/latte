
package latte;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import specification.Borrowed;
import specification.Free;
import specification.Shared;
import specification.Unique;

import java.io.UnsupportedEncodingException;

public class HttpEntityNoAnnotations {
    
    public static void test(HttpResponse r) throws UnsupportedEncodingException {
        HttpResponse response = r;
        InputStream in = response.getEntity().getContent();
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        response.getEntity().getContent(), "UTF-8")); // Second call to getEntity()

    //Cannot call getContent twice if the entity is not repeatable. There is a method isRepeatable() to check
    }
}

class HttpResponse {
    // TODO: STUB METHOD
    public HttpEntity getEntity() {
        return new HttpEntity();
    }
}

class HttpEntity {
    // TODO: STUB METHOD
    public InputStream getContent() {
        return (InputStream) new Object();
    }
}
