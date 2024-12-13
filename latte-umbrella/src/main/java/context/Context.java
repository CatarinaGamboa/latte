package context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

import spoon.reflect.declaration.CtClass;

/**
 * Context class to store the environments
 */
public class Context {
	
	private static Context instance;
	
    private Stack<Map<String, Variable>> variables;
    
    private List<CtClass<?>> classes;


    // SINGLETON
	public static Context getInstance() {
		if (instance == null) instance = new Context();
		return instance;
	}
   
	
	private Context() {
        variables = new Stack<Map<String, Variable>>();
        variables.push(new HashMap<String,Variable>());
        classes = new ArrayList<CtClass<?>>();
    }

	public void enterScope() {
		variables.push(new HashMap<String,Variable>());
	}
	
	public void exitScope() {
		variables.pop();
	}
	
	public void addInScope(String name, Variable ann) {
		variables.peek().put(name, ann);
	}
	
	public void addClass(CtClass<?>klass) {
		classes.add(klass);
	}
	
	
	/**
	 * Retrieves an element with name from the context
	 * @param name
	 * @return Variable with the given name or null if it was not found in the context
	 */
	public Variable get(String name) {
		Variable var = null;
		boolean found = false;
		int i = 0;
		int maxCap = variables.capacity(); 
		while(!found && i < maxCap) {
			Map<String,Variable> map = variables.get(i);
			if(map.containsKey(name)) {
				var = map.get(name);
				found = true;
			}
		}
		return var;
	}


	public void reinitializeAllContext() {
		variables.clear();
	}

	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("variables:\n");
		Iterator<Map<String, Variable>> it = variables.elements().asIterator();
		while(it.hasNext()) {
			sb.append("  [");
			Map<String, Variable> map = it.next();
			for(Entry<String,Variable> e : map.entrySet()) {
				sb.append( e.getValue() + ", ");
			}
			sb.append("]");
		}
		
		sb.append("\n classes:\n");
		for(CtClass<?> klass : classes)
			sb.append("  " + klass.getSimpleName()+",");
		
		return "Context:{\n " + sb.toString() +"\n}";
	}

}
