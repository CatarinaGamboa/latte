package context;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

public class Context {
	
	private static Context instance;
	
    private Stack<Map<String, Variable>> variables;


    // SINGLETON
	public static Context getInstance() {
		if (instance == null) instance = new Context();
		return instance;
	}
   
	
	private Context() {
        variables = new Stack<Map<String, Variable>>();
        variables.push(new HashMap<String,Variable>());
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
		Iterator<Map<String, Variable>> it = variables.elements().asIterator();
		while(it.hasNext()) {
			sb.append("[");
			Map<String, Variable> map = it.next();
			for(Entry<String,Variable> e : map.entrySet()) {
				sb.append( e.getValue() + ", ");
			}
			sb.append("]");
		}
		return "Context: " + sb.toString();
	}

}
