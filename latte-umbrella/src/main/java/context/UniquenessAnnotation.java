package context;

import java.lang.annotation.Annotation;

import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtElement;

/**
 * Matched the annotation to the uniqueness enum type type
 */
public class UniquenessAnnotation {
	
	Uniqueness annotation;
	CtElement path;

	public UniquenessAnnotation(CtElement element) {
		for (CtAnnotation<? extends Annotation> ann : element.getAnnotations()) {
	        String an = ann.getActualAnnotation().annotationType().getCanonicalName();
	        if (an.contentEquals("specification.Unique")) {
	           this.annotation = Uniqueness.UNIQUE;
	        } 
	        else if (an.contentEquals("specification.Shared")) {
		       this.annotation = Uniqueness.SHARED;
		    } 
	        else if (an.contentEquals("specification.Borrowed")) {
			   this.annotation = Uniqueness.BORROWED;
			}
			else if (an.contentEquals("specification.Free")) {
				this.annotation = Uniqueness.FREE;
			 }
	        
	    }
		if (annotation == null) this.annotation = Uniqueness.SHARED; //Default
	}
	
	public UniquenessAnnotation(Uniqueness at) {	
		annotation = at;
	}

	public void setToBottom() {
		annotation = Uniqueness.BOTTOM;
	}
	
	public void setToUniquePath(CtElement path) {
		this.annotation = Uniqueness.UNIQUE;
		this.path = path;
	}
	
	public boolean isFree(){
		return annotation.equals(Uniqueness.FREE);
	}

	public boolean isOwned() {
		return annotation.equals(Uniqueness.BORROWED);
	}
	
	public boolean isShared() {
		return annotation.equals(Uniqueness.SHARED);
	}
	
	public boolean isUnique() {
		return annotation.equals(Uniqueness.UNIQUE);
	}
	
	public boolean isBottom() {
		return annotation.equals(Uniqueness.BOTTOM);
	}

	public boolean isLessEqualThan(Uniqueness other) {
		return annotation.isLessEqualThan(other);
	}

	public boolean isGreaterEqualThan(Uniqueness other) {
		return annotation.isGreaterEqualThan(other);
	}

	public boolean annotationEquals(Uniqueness un) {
        return annotation.equals(un);
    }

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof UniquenessAnnotation) {
			UniquenessAnnotation other = (UniquenessAnnotation) obj;
			return annotation.equals(other.annotation);
		}
		return false;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(annotation.name());
		if(path != null)
			sb.append(path.toString());
		return sb.toString();
	}



}


