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

    // Provide the test cases for correct examples: file path and should pass (true)
    private static Stream<Arguments> provideCorrectTestCases() {
        return Stream.of(
            Arguments.of("src/test/examples/MyNodeCorrect.java"),
            Arguments.of("src/test/examples/MyNodePush.java"),
            Arguments.of("src/test/examples/MyNodePushPop.java"),
            Arguments.of("src/test/examples/MyNodeComplete.java"),
            Arguments.of("src/test/examples/MyStackFieldAssign.java"),
            Arguments.of("src/test/examples/BoxMain.java"),
            Arguments.of("src/test/examples/HttpEntityNoAnnotations.java"),
            Arguments.of("src/test/examples/searching_state_space/URLConnectionReuseConnection.java"),
            Arguments.of("src/test/examples/searching_state_space/URLConnectionSetProperty1.java"),
            Arguments.of("src/test/examples/searching_state_space/URLConnectionSetPropertyMultipleShort.java"),
            Arguments.of("src/test/examples/searching_state_space/TimerTaskCannotReschedule.java"),
            Arguments.of("src/test/examples/searching_state_space/ResultSetNoNext.java"),
            Arguments.of("src/test/examples/searching_state_space/ResultSetForwardOnly.java"),
            Arguments.of("src/test/examples/stack_overflow/MediaRecord.java")
        );
    }

    // Provide the test cases for incorrect examples: file path, should pass (false), and expected error message
    private static Stream<Arguments> provideIncorrectTestCases() {
        return Stream.of(
            Arguments.of("src/test/examples/MyNode.java", "UNIQUE but got BORROWED"),
            Arguments.of("src/test/examples/MyNodePushPopIncorrect.java", "FREE but got BOTTOM"),
            Arguments.of("src/test/examples/MyNodeNoDistinct.java", "Non-distinct parameters"),
            Arguments.of("src/test/examples/MyNodeCallUniqueFree.java", "FREE but got UNIQUE"),
            Arguments.of("src/test/examples/SmallestIncorrectExample.java", "UNIQUE but got BORROWED"),
            Arguments.of("src/test/examples/MyStackFieldAssignMethod.java", "UNIQUE but got SHARED"),
            Arguments.of("src/test/examples/FieldAccessNoThis.java", "UNIQUE but got SHARED"),
            Arguments.of("src/test/examples/FieldAccessRightNoThis.java", "FREE but got UNIQUE")
        );
    }

    @ParameterizedTest
    @MethodSource("provideCorrectTestCases")
    public void testCorrectApp(String filePath) {
        runTest(filePath, true, null);
    }

    @ParameterizedTest
    @MethodSource("provideIncorrectTestCases")
    public void testIncorrectApp(String filePath, String expectedErrorMessage) {
        runTest(filePath, false, expectedErrorMessage);
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
        // x->1; y->2
        SymbolicValue v3 = se.addField(v1, "f");
        // x->1; y->2, 1.f->3
        se.addVarSymbolicValue("z", v1);
        SymbolicValue v4 = se.get("z");
        // x->1; y->2, 1.f->3, z -> 1
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