package typechecking;

import spoon.reflect.declaration.CtElement;

public class LatteException extends RuntimeException {
    private CtElement ce;
    public LatteException(String message, CtElement ce) {
        super(message);
        this.ce = ce;
    }

    public CtElement getElement(){
        return ce;
    }
}
