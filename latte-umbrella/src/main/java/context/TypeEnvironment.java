package context;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import spoon.reflect.declaration.CtClass;

/**
 * Type Environment class to store the classes of the variables in scope
 * \gamma ::= empty | x : C, \gamma
 */
public class TypeEnvironment {
	
	// Singleton instance
	private static TypeEnvironment instance;

	// Stack of maps to store the classes of the variables in scope
    private Stack<Map<Variable, CtClass<?>>> typeEnv;

	/**
	 * Singleton instance
	 * @return
	 */
	public static TypeEnvironment getInstance() {
		if (instance == null) instance = new TypeEnvironment();
		return instance;
	}
   
	/** 
	 * Constructor
	*/
	private TypeEnvironment() {
		typeEnv = new Stack<Map<Variable, CtClass<?>>>();
    }

	/**
	 * Enter a new scope
	 */
	public void enterScope() {
		typeEnv.push(new HashMap<Variable, CtClass<?>>());
	}
	
	/**
	 * Exit the current scope
	 */
	public void exitScope() {
		typeEnv.pop();
	}
	
	/**
	 * Add a variable to the current scope
	 * @param name
	 * @param klass
	 */
	public void add(String name, CtClass<?> klass) {
		typeEnv.peek().put(new Variable(name), klass);
	}

	/**
	 * Check if a variable is in scope
	 * @param name
	 * @return
	 */
	public boolean has(String name){
		for (int i = typeEnv.size() - 1; i >= 0; i--) {
			if (typeEnv.get(i).containsKey(new Variable(name))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Get the class of a variable
	 * @param name
	 * @return
	 */
	public CtClass<?> get(String name){
		for (int i = typeEnv.size() - 1; i >= 0; i--) {
			if (typeEnv.get(i).containsKey(new Variable(name))) {
				return typeEnv.get(i).get(new Variable(name));
			}
		}
		return null;
	}
}
