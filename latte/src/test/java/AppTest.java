

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
    public void testMyNode() {
        assertLauncherFails("src/test/examples/MyNode.java", "UNIQUE but got BORROWED");
    }

    @Test
    public void testMyNodePushPopIncorrect() {
        assertLauncherFails("src/test/examples/MyNodePushPopIncorrect.java", "FREE but got BOTTOM");
    }

    @Test
    public void testMyNodeNoDistinct() {
        assertLauncherFails("src/test/examples/MyNodeNoDistinct.java", "Non-distinct parameters");
    }

    @Test
    public void testMyNodeCallUniqueFree() {
        assertLauncherFails("src/test/examples/MyNodeCallUniqueFree.java", "FREE but got UNIQUE");
    }

    @Test
    public void testSmallestIncorrectExample() {
        assertLauncherFails("src/test/examples/SmallestIncorrectExample.java", "UNIQUE but got BORROWED");
    }

    @Test
    public void testMyStackFieldAssignMethod() {
        assertLauncherFails("src/test/examples/MyStackFieldAssignMethod.java", "UNIQUE but got SHARED");
    }

    @Test
    public void testFieldAccessNoThis() {
        assertLauncherFails("src/test/examples/FieldAccessNoThis.java", "UNIQUE but got SHARED");
    }

    @Test
    public void testFieldAccessRightNoThis() {
        assertLauncherFails("src/test/examples/FieldAccessRightNoThis.java", "FREE but got UNIQUE");
    }

    /*
     * === Reachability Unit Test ===
     */

    @Test
    public void testReachabilityUnitTest() {
        Logger logger = Logger.getLogger(AppTest.class.getName());

        SymbolicEnvironment se = new SymbolicEnvironment();
        se.enterScope();
        SymbolicValue v1 = se.addVariable("x");
        SymbolicValue v2 = se.addVariable("y");
        SymbolicValue v3 = se.addField(v1, "f");
        se.addVarSymbolicValue("z", v1);
        SymbolicValue v4 = se.get("z");

        logger.info(se.toString());

        assertFalse(se.canReach(v1, v2, new ArrayList<>()));
        assertTrue(se.canReach(v1, v3, new ArrayList<>()));
        assertTrue(se.canReach(v1, v4, new ArrayList<>()));
    }
}
