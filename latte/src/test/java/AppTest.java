

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.junit.jupiter.api.Test;

import context.SymbolicEnvironment;
import context.SymbolicValue;
import typechecking.LatteException;

import api.App;
/**
 * Unit test for simple App.
 */

public class AppTest {

    /*
     * === Helper Methods ===
     */

    private void assertLauncherPasses(String filePath) {
        assertDoesNotThrow(() -> App.launcher(filePath));
    }

    private void assertLauncherFails(String filePath, String expectedMessage) {
        Exception exception = assertThrows(LatteException.class, () -> App.launcher(filePath));
        assertTrue(exception.getMessage().contains(expectedMessage));
    }

    /*
     * === Correct Examples ===
     */

    private static final List<String> CORRECT_FILES = List.of(
        "src/test/examples/MyNodeCorrect.java",
        "src/test/examples/MyNodePush.java",
        "src/test/examples/MyNodePushPop.java",
        "src/test/examples/MyNodeComplete.java",
        "src/test/examples/MyStackFieldAssign.java",
        "src/test/examples/BoxMain.java",
        "src/test/examples/HttpEntityNoAnnotations.java",
        "src/test/examples/searching_state_space/URLConnectionReuseConnection.java",
        "src/test/examples/searching_state_space/URLConnectionSetProperty1.java",
        "src/test/examples/searching_state_space/URLConnectionSetPropertyMultipleShort.java",
        "src/test/examples/searching_state_space/TimerTaskCannotReschedule.java",
        "src/test/examples/searching_state_space/ResultSetNoNext.java",
        "src/test/examples/searching_state_space/ResultSetForwardOnly.java",
        "src/test/examples/stack_overflow/MediaRecord.java"
    );

    @Test
    public void testCorrectExamples() {
        CORRECT_FILES.forEach(this::assertLauncherPasses);
    }

    /*
     * === Incorrect Examples ===
     */

    @Test
    public void testMyNode(){
        try {
            App.launcher("src/test/examples/MyNode.java");
        } catch (Exception e) {
            assertTrue(e instanceof LatteException);
            assertTrue(e.getMessage().contains("UNIQUE but got BORROWED"));
        }
        
    }

   @Test
   public void testMyNodePushPopIncorrect(){
       try {
           App.launcher("src/test/examples/MyNodePushPopIncorrect.java");
       } catch (Exception e) {
           System.out.println(e.getMessage());
           assertTrue(e instanceof LatteException);
           assertTrue(e.getMessage().contains("FREE but got BOTTOM"));
       }
       
   }

   @Test
   public void testMyNodeNoDistinct(){
       try {
           App.launcher("src/test/examples/MyNodeNoDistinct.java");
       } catch (Exception e) {
           assertTrue(e instanceof LatteException);
           assertTrue(e.getMessage().contains("Non-distinct parameters"));
       }   
   }

   @Test
   public void testMyNodeCallUniqueFree(){
       try {
           App.launcher("src/test/examples/MyNodeCallUniqueFree.java");
       } catch (Exception e) {
           assertTrue(e instanceof LatteException);
           assertTrue(e.getMessage().contains("FREE but got UNIQUE"));
       }
       
   }

   @Test
   public void testSmallestIncorrectExample(){
       try {
           App.launcher("src/test/examples/SmallestIncorrectExample.java");
       } catch (Exception e) {
           assertTrue(e instanceof LatteException);
           assertTrue(e.getMessage().contains("UNIQUE but got BORROWED"));
       }
       
   }

   @Test
   public void testMyStackFieldAssignMethod(){
       try {
           App.launcher("src/test/examples/MyStackFieldAssignMethod.java");
       } catch (Exception e) {
           assertTrue(e instanceof LatteException);
           assertTrue(e.getMessage().contains("UNIQUE but got SHARED"));
       }
   }

   @Test
   public void testFieldAccessNoThis(){
       try {
           App.launcher("src/test/examples/FieldAccessNoThis.java");
       } catch (Exception e) {
           assertTrue(e instanceof LatteException);
           assertTrue(e.getMessage().contains("UNIQUE but got SHARED"));
       }
   }

   @Test
   public void testFieldAccessRightNoThis(){
       try {
           App.launcher("src/test/examples/FieldAccessRightNoThis.java");
       } catch (Exception e) {
           assertTrue(e instanceof LatteException);
           assertTrue(e.getMessage().contains("FREE but got UNIQUE"));
       }
   }


   @Test
   public void testReachabilityUnitTest(){
       Logger logger = Logger.getLogger(AppTest.class.getName());
       //test
       SymbolicEnvironment se = new SymbolicEnvironment();
       se.enterScope();
       SymbolicValue v1 = se.addVariable("x");
       // x->1
       SymbolicValue v2 = se.addVariable("y");
       // x->1; y->2
       SymbolicValue v3 = se.addField(v1,"f");
       // x->1; y->2, 1.f->3
       se.addVarSymbolicValue("z", v1);
       SymbolicValue v4 = se.get("z");
       // x->1; y->2, 1.f->3, z -> 1

       logger.info(se.toString());

       boolean b = se.canReach(v1, v2, new ArrayList<>());
       logger.info(v1.toString() + " can reach " +  v2.toString() + "? " + b);
       assertFalse(b);
       
       boolean b1 = se.canReach(v1, v3, new ArrayList<>());
       logger.info(v1.toString() + " can reach " +  v3.toString() + "? " + b1);
       assertTrue(b1);

       
       boolean b2 = se.canReach(v1, v4, new ArrayList<>());
       logger.info(v1.toString() + " can reach " +  v4.toString() + "? " + b1);
       assertTrue(b2);
   }


}