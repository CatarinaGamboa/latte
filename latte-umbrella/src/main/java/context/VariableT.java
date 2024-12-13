package context;

import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.reference.CtTypeReference;

public class VariableT {
	
	String name;
	String klass;
	UniquenessAnnotation annotation;
	

	CtElement element;
	
	/**
	 * Creates a variable object that saves relevant information for type checking
	 * @param field variable element with information to save. It can be a CtField or CtParameter, for example
	 */
	public VariableT(CtVariable<?> field) {
		
		String fieldName = field.getSimpleName(); //TODO: change to scale
		CtTypeReference<?> type =  field.getType();
		UniquenessAnnotation au = new UniquenessAnnotation(field);
		
		name = fieldName;
		klass = type.getSimpleName();  //TODO: change to scale
		annotation = au;
		
		element = field;
	}
	
	

	public String getName() {
		return name;
	}


	public String getKlass() {
		return klass;
	}


	public UniquenessAnnotation getAnnotation() {
		return annotation;
	}


	public CtElement getElement() {
		return element;
	}


	@Override
	public String toString() {
		return name + ": " + annotation + " " + klass;
	}
	
	
	
	
}
