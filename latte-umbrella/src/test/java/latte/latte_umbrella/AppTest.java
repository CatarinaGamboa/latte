package latte.latte_umbrella;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

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


}
