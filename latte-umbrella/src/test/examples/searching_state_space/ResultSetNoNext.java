
import java.util.TimerTask;

import specification.Borrowed;
import specification.Free;
import specification.Unique;

public class SSSResultSetNoNext {


    /*
     * Error - in ResultSet, after executing the query we need to call next() before getting a value
     */
     public static void example6367737(@Borrowed Connection con, String username, String password ) throws Exception {

      // Step 1 Prepare the statement
      PreparedStatement pstat =
        con.prepareStatement("select typeid from users where username=? and password=?");  

      // Step 2 Set parameters for the statement
      pstat.setString(1, username);
      pstat.setString(2, password);

      // Step 3 Execute the query
      ResultSet parentMessage = pstat.executeQuery("SELECT SUM(IMPORTANCE) AS IMPAVG FROM MAIL");

      float avgsum = parentMessage.getFloat("IMPAVG"); // Error because we are trying to get a value before next
      // To be correct we need to call next() before the getter
    }


    class Connection{

        // Result sets created using the returned PreparedStatement object will by default be type TYPE_FORWARD_ONLY 
        // and have a concurrency level of CONCUR_READ_ONLY.
        @Unique // @StateRefinement (return, to="TYPE_FORWARD_ONLY, CONCUR_READ_ONLY")
        public PreparedStatement prepareStatement(String s) {
            return new PreparedStatement();
        }

        // To change resultSetType
        // prepareStatement​(String sql, int resultSetType, int resultSetConcurrency){}
    }

    class PreparedStatement{
        void setString(int index, String s){}

        // @ReturnState( to="TYPE_FORWARD_ONLY, beforeRow")
        ResultSet executeQuery(String s){return null;}
    }

    //@StateSet({"beforeRow", "onRow"})
    class ResultSet{
        // @StateRefinement( from = "onRow")
        float getFloat(String s){return 0;}

        // @StateRefinement( to="onRow")
        void next(){}
    }


}


