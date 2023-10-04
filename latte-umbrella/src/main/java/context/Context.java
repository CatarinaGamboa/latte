package context;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Context {
	
	private static Context instance;
	
    private Map<String, Variable> variables;


    // SINGLETON
	public static Context getInstance() {
		if (instance == null) instance = new Context();
		return instance;
	}
   
	
	private Context() {
        variables = new HashMap<>();
    }

	
	public void addToContext(String name, Variable ann) {
		variables.put(name, ann);
	}
	


	public Variable get(String name) {
		return variables.get(name);
	}


	public void reinitializeAllContext() {
		variables.clear();
	}

	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(Entry<String,Variable> e : variables.entrySet()) {
			sb.append( e.getValue() + ", ");
		}
		return "Context [variables=" + sb.toString() + "]";
	}

}
