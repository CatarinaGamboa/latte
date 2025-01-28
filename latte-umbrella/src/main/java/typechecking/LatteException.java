package typechecking;

import spoon.reflect.declaration.CtElement;

public class LatteException extends RuntimeException {
    private CtElement ce;
    public LatteException(String message, CtElement ce) {
        super(message);
        this.ce = ce;
    }

    /*
     * Retain the element that caused the exception
     */
    public CtElement getElement(){
        return ce;
    }
}
