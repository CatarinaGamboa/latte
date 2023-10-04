package context;

import java.lang.annotation.Annotation;

import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtElement;

public class AnnotationUniqueness {
	
	enum AnnotationType {
		  OWNED,
		  SHARED,
		  UNIQUE,
		  ALIAS,
		  BOTTOM
	}
	
	AnnotationType annotation;
	CtElement path;

	
	
	public AnnotationUniqueness(CtElement element) {
		for (CtAnnotation<? extends Annotation> ann : element.getAnnotations()) {
	        String an = ann.getActualAnnotation().annotationType().getCanonicalName();
	        if (an.contentEquals("specification.Unique")) {
	           this.annotation = AnnotationType.UNIQUE;
	        } 
	        else if (an.contentEquals("specification.Shared")) {
		       this.annotation = AnnotationType.SHARED;
		    } 
	        else if (an.contentEquals("specification.Owned")) {
			   this.annotation = AnnotationType.OWNED;
			}
	        
	    }
		
		if (annotation == null) this.annotation = AnnotationType.SHARED; //Default
	}
	
	public AnnotationUniqueness() {	}
	
	public void setToBottom() {
		annotation = AnnotationType.BOTTOM;
	}
	
	public void setToUniquePath(CtElement path) {
		this.annotation = AnnotationType.UNIQUE;
		this.path = path;
	}
	
	public void setToAlias(CtElement path) {
		this.annotation = AnnotationType.ALIAS;
		this.path = path;
	}
	
	
	public boolean isOwned() {
		return annotation.equals(AnnotationType.OWNED);
	}
	
	public boolean isShared() {
		return annotation.equals(AnnotationType.SHARED);
	}
	
	public boolean isUnique() {
		return annotation.equals(AnnotationType.UNIQUE);
	}
	
	public boolean isAlias() {
		return annotation.equals(AnnotationType.ALIAS);
	}
	
	public boolean isBottom() {
		return annotation.equals(AnnotationType.BOTTOM);
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(annotation.name());
		if(path != null)
			sb.append(path.toString());
		return sb.toString();
	}


}


