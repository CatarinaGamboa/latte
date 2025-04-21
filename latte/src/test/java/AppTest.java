import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import api.App;
import typechecking.LatteException;

public class AppTest {

    /*
     * === Helper Methods ===
     */

    private void assertLauncherPasses(String filePath) {
        assertDoesNotThrow(() -> App.launcher(filePath));
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

    @ParameterizedTest
    @CsvSource({
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
    })
    public void testCorrectExamples(String filePath) {
        assertLauncherPasses(filePath);
    }

    /*
     * === Parameterized Incorrect Examples ===
     */
    
    @ParameterizedTest
    @CsvSource({
        "src/test/examples/MyNode.java, UNIQUE but got BORROWED",
        "src/test/examples/MyNodePushPopIncorrect.java, FREE but got BOTTOM",
        "src/test/examples/MyNodeNoDistinct.java, Non-distinct parameters",
        "src/test/examples/MyNodeCallUniqueFree.java, FREE but got UNIQUE",
        "src/test/examples/SmallestIncorrectExample.java, UNIQUE but got BORROWED",
        "src/test/examples/MyStackFieldAssignMethod.java, UNIQUE but got SHARED",
        "src/test/examples/FieldAccessNoThis.java, UNIQUE but got SHARED",
        "src/test/examples/FieldAccessRightNoThis.java, FREE but got UNIQUE"
    })
    public void testIncorrectExamples(String filePath, String expectedMessage) {
        try {
            App.launcher(filePath);
        } catch (LatteException e) {
            assertTrue(e.getMessage().contains(expectedMessage));
        }
    }

    /*
     * === Other Unit Tests ===
     */
   
   @Test
   public void testReachabilityUnitTest() {
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
