import specification.Borrowed;
import specification.Unique;

public class URLConnectionReuseConnection {
    
    public static void example4278917(@Borrowed URL address) {

        try {
            // Step 1) Open the connection
            URLConnection connection = address.openConnection(); // returns a unique object

            // Step 2) Connect
            connection.connect();
            
            /*  Other code in original question */
            
            // Step 3) Setup parameters and connection properties after connection == ERROR
            connection.setAllowUserInteraction(true);
            connection.addRequestProperty("AUTHENTICATION_REQUEST_PROPERTY", "authorizationRequest");
            connection.getHeaderFields();

        } catch (IOException e) {
            // Handle exceptions related to network or stream issues
            System.err.println("Error: " + e.getMessage());
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
    void addRequestProperty(String s1, String s2){}

    //@StateRefinement(this, from="interact")
    void getHeaderFields(){}

    //@StateRefinement(this, from="manipulate", to="interact")
    void connect() throws IOException {}
}

