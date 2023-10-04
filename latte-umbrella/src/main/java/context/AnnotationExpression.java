package context;

import java.lang.annotation.Annotation;

import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtElement;

public class AnnotationExpression {
	
	
	public boolean isOwned(CtElement element) {
		
		for (CtAnnotation<? extends Annotation> ann : element.getAnnotations()) {
	        String an = ann.getActualAnnotation().annotationType().getCanonicalName();
	        if (an.contentEquals("liquidjava.specification.Refinement")) {
	           return true;
	        } 
	    }
		return false;
		
	
	}
	
}