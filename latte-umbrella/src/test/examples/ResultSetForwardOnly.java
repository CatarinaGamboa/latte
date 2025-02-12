package examples;


import java.util.TimerTask;

import specification.Borrowed;
import specification.Free;
import specification.Unique;

public class ResultSetForwardOnly {
    /*
     * Error ResultSet is FORWARD_ONLY and we try to get a value before
     */
    public static void example6367737(Connection con, String username, String password ) throws Exception {

        // Step 1) Prepare the statement
        PreparedStatement pstat =
          con.prepareStatement("select typeid from users where username=? and password=?");  
  
        // Step 2) Set parameters for the statement
        pstat.setString(1, username);
        pstat.setString(2, password);
  
        // Step 3) Execute the query
        ResultSet rs = pstat.executeQuery();
  
        // Step 4) Process the result
        int rowCount=0;
        while(rs.next()){       
            rowCount++;         
        }
  
        // ERROR! because it is FORWARD_ONLY, we cannot go back and check beforeFirst
        rs.beforeFirst(); 
  
        int typeID = 0; // ...
  
        // To be correct we need to change the resultset to be scrollable
        /*PreparedStatement pstat =      
          con.prepareStatement("select typeid from users where username=? and password=?",
          ResultSet.TYPE_SCROLL_SENSITIVE, 
          ResultSet.CONCUR_UPDATABLE);
        */
      }


    class Connection{

        // Result sets created using the returned PreparedStatement object will by default be type TYPE_FORWARD_ONLY 
        // and have a concurrency level of CONCUR_READ_ONLY.
        @Unique // @StateRefinement (return, to="TYPE_FORWARD_ONLY, CONCUR_READ_ONLY")
        public PreparedStatement prepareStatement(String s) {
            return new PreparedStatement();
        }

        @Unique // @StateRefinement (return, to="type == resultSetType")
        public PreparedStatement prepareStatement​(String sql, int resultSetType, int resultSetConcurrency){
            return new PreparedStatement();

        }
    }

    //  @Ghost("type")
    class PreparedStatement{

        void setString(int index, String s){}

        // @StateRefinement(return, to="type != TYPE_FORWARD_ONLY, col == -1")
        ResultSet executeQuery(String s){return null;}

        // @StateRefinement(return, to="type, col == -1")
        ResultSet executeQuery(){return null;}
    }

    // @Ghost("type")
    class ResultSet{

        // @StateRefinement(this, type == )
        void beforeFirst(){}

        // @StateRefinement(this, col > 0)
        float getFloat(String s){return 0;}

        // @StateRefinement(this, col > 0)
        int getInt(int s){return 0;}

        // @StateRefinement(this, col == old(col) + 1)
        boolean next(){return true;}
    }


}


