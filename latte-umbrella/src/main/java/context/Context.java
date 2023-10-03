package context;

import java.util.HashMap;
import java.util.Map;

public class Context {
	
	private static Context instance;
	
    private Map<String, AnnotationUniqueness> variables;


    // SINGLETON
	public static Context getInstance() {
		if (instance == null) instance = new Context();
		return instance;
	}
   
	
	private Context() {
        variables = new HashMap<>();
    }

	
	public void addToContext(String name, AnnotationUniqueness ann) {
		variables.put(name, ann);
	}
	
	public AnnotationUniqueness get(String name) {
		return variables.get(name);
	}


}
