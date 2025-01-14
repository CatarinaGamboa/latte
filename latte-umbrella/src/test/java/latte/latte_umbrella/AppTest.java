package latte.latte_umbrella;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.ArrayList;
import java.util.logging.Logger;

import org.junit.Test;

import context.SymbolicEnvironment;
import context.SymbolicValue;
import typechecking.LatteException;

/**
 * Unit test for simple App.
 */
public class AppTest {

    @Test
    public void testMyNode(){
        try {
            App.launcher("src/test/examples/MyNode.java");
        } catch (Exception e) {
            assertTrue(e instanceof LatteException);
            assertTrue(e.getMessage().contains("expected an assignment with permission UNIQUE but got BORROWED"));
        }
        
    }

    @Test
    public void testMyNodeCorrect(){
        try {
            App.launcher("src/test/examples/MyNodeCorrect.java");
        } catch (Exception e) {
            assert(false);
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
    public void testReachabilityUnitTest(){
        Logger logger = Logger.getLogger(AppTest.class.getName());
        //test
        SymbolicEnvironment se = SymbolicEnvironment.getInstance();
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
