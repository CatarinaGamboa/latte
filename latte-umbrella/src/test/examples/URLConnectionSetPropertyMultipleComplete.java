import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import specification.Borrowed;
import specification.Shared;
import specification.Unique;

public class URLConnectionSetPropertyMultipleComplete {

    String sessionId = "1234";
    public  void example5368535(@Borrowed URL address, String content) {
        try {
            URLConnection con = openConnection(address, true, true, "POST");
        
            //ERROR write before set
            writeToOutput(con, content); // writeOutput calls  cnx.getOutputStream()
            setCookies(con); // writeOutput calls cnx.setRequestProperty

            con.connect();

            System.out.println("Request completed successfully.");
        } catch (IOException e) {
            // Handle exceptions related to network or stream issues
            System.err.println("Error: " + e.getMessage());
        }
    }

    // Exactly from the original code
    @Unique
    public URLConnection openConnection(@Borrowed URL url, boolean in, boolean out, String requestMethode) throws IOException{
        URLConnection con = url.openConnection();
        con.setDoInput(in);
        con.setDoOutput (out);
        con.setRequestMethod(requestMethode);
        con.setRequestProperty ("Content-Type", "application/x-www-form-urlencoded");
        return con;
    }

    // Set cookies
    private  void setCookies(@Borrowed URLConnection cnx) {
        if (sessionId != null) {
            cnx.setRequestProperty("Cookie", sessionId);
            System.out.println("Cookie set: " + sessionId);
        }
    }

    // Write content
    private void writeToOutput(@Borrowed URLConnection cnx, String content) throws IOException {
        try {
            OutputStream os = cnx.getOutputStream() ;
            os.write(content.getBytes());
            os.flush();
            os.close();
        } catch (IOException e) {
            System.err.println("Error writing content: " + e.getMessage());
            throw e;
        }
    }
 
}


class URL{
    @Unique // @StateRefinement (return, to="manipulate")
    public URLConnection openConnection() {
        return new URLConnection();
    }
}


/**
 * --- openConnection() ----> [MANIPULATE PARAMS] ------> connect() -----> [INTERACT]
 *                            setAllowUserInteraction()                 getContent()
 *                            addRequestProperty()...                   getHeaderFields()...
 */
// @StateSet({"manipulate", "interact"})
class URLConnection{

    //@StateRefinement(this, from="manipulate")
    void setAllowUserInteraction(boolean b){}

    //@StateRefinement(this, from="manipulate")
    void setDoOutput(boolean b){}

    //@StateRefinement(this, from="manipulate")
    void setRequestProperty(String a, String b){}
    
    //@StateRefinement(this, from="manipulate")
    void setRequestMethod(String a){}
    
    //@StateRefinement(this, from="manipulate")
    void setDoInput(boolean b){}

    //@StateRefinement(this, from="manipulate")
    void addRequestProperty(String s1, String s2){}

    //@StateRefinement(this, from="manipulate", to="interact")
    void connect() throws IOException {}

    //@StateRefinement(this, from="interact")
    void getContent(){}


    //@StateRefinement(this, from="interact")
    @Unique //????? Not sure: Returns an input stream that reads from this open connection. 
    InputStream getInputStream(){ return null;}


    //@StateRefinement(this, from="interact")
    @Shared //????? Not sure: Returns an output stream that writes to this connection.
    OutputStream getOutputStream(){ return null;}
    
}