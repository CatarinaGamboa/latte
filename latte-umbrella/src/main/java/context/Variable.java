package context;

import java.util.Optional;

import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.reflect.reference.CtTypeReference;

public class Variable {
	
	String name;
	String klass;
	AnnotationUniqueness annotation;
	

	CtElement element;
	
	
	public Variable(CtField<?> field) {
		
		String fieldName = field.getSimpleName(); //TODO: change to scale
		CtTypeReference<?> type =  field.getType();
		Optional<AnnotationUniqueness> oau = AnnotationUniqueness.uniquenessAnnotation(field);
		
		name = fieldName;
		klass = type.getSimpleName(); //TODO: change to scale
		annotation = oau.orElse(null);
		
		element = field;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getKlass() {
		return klass;
	}


	public void setKlass(String klass) {
		this.klass = klass;
	}


	public AnnotationUniqueness getAnnotation() {
		return annotation;
	}


	public void setAnnotation(AnnotationUniqueness annotation) {
		this.annotation = annotation;
	}


	public CtElement getElement() {
		return element;
	}


	public void setElement(CtElement element) {
		this.element = element;
	}


	@Override
	public String toString() {
		return name + ": " + annotation + " " + klass;
	}
	
	
	
	
}
