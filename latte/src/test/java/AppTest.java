import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import api.App;
import context.SymbolicEnvironment;
import context.SymbolicValue;
import typechecking.LatteException;

public class AppTest {

    // Provide the test cases: file path, should pass (boolean), and expected error message (if any)
    private static Stream<Arguments> provideTestCases() {
        return Stream.of(
            Arguments.of("src/test/examples/MyNodeCorrect.java", true, null),
            Arguments.of("src/test/examples/MyNodePush.java", true, null),
            Arguments.of("src/test/examples/MyNodePushPop.java", true, null),
            Arguments.of("src/test/examples/MyNodeComplete.java", true, null),
            Arguments.of("src/test/examples/MyStackFieldAssign.java", true, null),
            Arguments.of("src/test/examples/BoxMain.java", true, null),
            Arguments.of("src/test/examples/HttpEntityNoAnnotations.java", true, null),
            Arguments.of("src/test/examples/searching_state_space/URLConnectionReuseConnection.java", true, null),
            Arguments.of("src/test/examples/searching_state_space/URLConnectionSetProperty1.java", true, null),
            Arguments.of("src/test/examples/searching_state_space/URLConnectionSetPropertyMultipleShort.java", true, null),
            Arguments.of("src/test/examples/searching_state_space/TimerTaskCannotReschedule.java", true, null),
            Arguments.of("src/test/examples/searching_state_space/ResultSetNoNext.java", true, null),
            Arguments.of("src/test/examples/searching_state_space/ResultSetForwardOnly.java", true, null),
            Arguments.of("src/test/examples/stack_overflow/MediaRecord.java", true, null),
            Arguments.of("src/test/examples/MyNode.java", false, "UNIQUE but got BORROWED"),
            Arguments.of("src/test/examples/MyNodePushPopIncorrect.java", false, "FREE but got BOTTOM"),
            Arguments.of("src/test/examples/MyNodeNoDistinct.java", false, "Non-distinct parameters"),
            Arguments.of("src/test/examples/MyNodeCallUniqueFree.java", false, "FREE but got UNIQUE"),
            Arguments.of("src/test/examples/SmallestIncorrectExample.java", false, "UNIQUE but got BORROWED"),
            Arguments.of("src/test/examples/MyStackFieldAssignMethod.java", false, "UNIQUE but got SHARED"),
            Arguments.of("src/test/examples/FieldAccessNoThis.java", false, "UNIQUE but got SHARED"),
            Arguments.of("src/test/examples/FieldAccessRightNoThis.java", false, "FREE but got UNIQUE")
        );
    }

    @ParameterizedTest
    @MethodSource("provideTestCases")
    public void testApp(String filePath, boolean shouldPass, String expectedErrorMessage) {
        runTest(filePath, shouldPass, expectedErrorMessage);
    }

    // The shared method that handles the actual test logic
    private void runTest(String filePath, boolean shouldPass, String expectedErrorMessage) {
        try {
            App.launcher(filePath);
            if (!shouldPass) {
                fail("Expected an exception but none was thrown.");
            }
        } catch (Exception e) {
            if (shouldPass) {
                fail("Unexpected exception: " + e.getMessage());
            } else {
                assertTrue(e instanceof LatteException);
                // Print the exception message for debugging
                System.out.println("Exception message: " + e.getMessage());
                assertTrue(e.getMessage().contains(expectedErrorMessage));
            }
        }
    }

    @Test
    public void testReachabilityUnitTest() {
        Logger logger = Logger.getLogger(AppTest.class.getName());

        // Create a symbolic environment
        SymbolicEnvironment se = new SymbolicEnvironment();
        se.enterScope();

        SymbolicValue v1 = se.addVariable("x");
        // x->1
        SymbolicValue v2 = se.addVariable("y");
        SymbolicValue v3 = se.addField(v1, "f");
        se.addVarSymbolicValue("z", v1);
        SymbolicValue v4 = se.get("z");

        logger.info(se.toString());

        // Test reachability between variables
        boolean b = se.canReach(v1, v2, new ArrayList<>());
        logger.info(v1.toString() + " can reach " + v2.toString() + "? " + b);
        assertFalse(b);

        boolean b1 = se.canReach(v1, v3, new ArrayList<>());
        logger.info(v1.toString() + " can reach " + v3.toString() + "? " + b1);
        assertTrue(b1);

        boolean b2 = se.canReach(v1, v4, new ArrayList<>());
        logger.info(v1.toString() + " can reach " + v4.toString() + "? " + b2);
        assertTrue(b2);
    }
}
