package typechecking;

import spoon.reflect.declaration.CtElement;

public class LatteException extends RuntimeException {
    private CtElement ce;
    private String shortMessage;
    public LatteException(String shortMessage, String fullMessage, CtElement ce) {
        super(fullMessage);
        this.ce = ce;
    }

    /*
     * Retain the element that caused the exception
     */
    public CtElement getElement(){
        return ce;
    }

    public String getShortMessage(){
        return shortMessage;
    }
}
