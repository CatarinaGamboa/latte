package context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * Type Environment class to store the classes of the variables in scope
 * \gamma ::= empty | x : C, \gamma
 */
public class SymbolicEnvironment {
	
	// Singleton instance
	private static SymbolicEnvironment instance;

	int symbolic_counter = 0;

    private List<Map<VariableHeapLoc, SymbolicValue>> symbEnv;

	/**
	 * Singleton instance
	 * @return
	 */
	public static SymbolicEnvironment getInstance() {
		if (instance == null) instance = new SymbolicEnvironment();
		return instance;
	}

	private SymbolicEnvironment() {
		symbEnv = new Stack<Map<VariableHeapLoc, SymbolicValue>>();
	}

	public SymbolicValue addVariable(String var) {
		Variable v = new Variable(var);
		SymbolicValue symb = new SymbolicValue(symbolic_counter++);
		symbEnv.getLast().put(v, symb);
		return symb;
	}

	public SymbolicValue addField(String var, String field) {
		Variable f = new Variable(field);
		SymbolicValue symb = get(new Variable(var));

		FieldHeapLoc v = new FieldHeapLoc(symb, f);
		symbEnv.getLast().put(v, new SymbolicValue(symbolic_counter++));
		return symb;
	}

	public SymbolicValue addField(SymbolicValue symb, String field) {
		Variable f = new Variable(field);

		FieldHeapLoc v = new FieldHeapLoc(symb, f);
		symbEnv.getLast().put(v, new SymbolicValue(symbolic_counter++));
		return symb;
	}

	public SymbolicValue getFresh(){
		return new SymbolicValue(symbolic_counter++);
	}

	public SymbolicValue get(String name) {
		Variable var = new Variable(name);
		for (int i = symbEnv.size() - 1; i >= 0; i--) {
			if (symbEnv.get(i).containsKey(var)) {
				return symbEnv.get(i).get(var);
			}
		}
		return null;
	}

	public SymbolicValue get(Variable var) {
		for (int i = symbEnv.size() - 1; i >= 0; i--) {
			if (symbEnv.get(i).containsKey(var)) {
				return symbEnv.get(i).get(var);
			}
		}
		return null;
	}

	public SymbolicValue get(SymbolicValue symbolicValue, String field) {
		FieldHeapLoc var = new FieldHeapLoc(symbolicValue, field);
		for (int i = symbEnv.size() - 1; i >= 0; i--) {
			if (symbEnv.get(i).containsKey(var)) {
				return symbEnv.get(i).get(var);
			}
		}
		return null;
	}

	public SymbolicValue get(FieldHeapLoc var) {
		for (int i = symbEnv.size() - 1; i >= 0; i--) {
			if (symbEnv.get(i).containsKey(var)) {
				return symbEnv.get(i).get(var);
			}
		}
		return null;
	}

	/**
	 * Enter a new scope
	 */
	public void enterScope() {
		symbEnv.addLast(new HashMap<VariableHeapLoc, SymbolicValue>());
	}
	
	/**
	 * Exit the current scope
	 */
	public void exitScope() {
		symbEnv.removeLast();
	}

	public boolean hasValue(SymbolicValue v) {
		return symbEnv.stream()
				.map(innerMap -> innerMap.containsValue(v))
				.reduce(false, (a, b) -> a || b);
	}


	/**
	 * Remove unreachable values
	 * TODO: Test this
	 */
	public void removeUnreachableValues() {
		// get all symbolic values in the keys
		List<FieldHeapLoc> keys = new ArrayList<>();
		for (Map<VariableHeapLoc, SymbolicValue> map : symbEnv) {
			map.keySet().forEach(k -> {
				if (k instanceof FieldHeapLoc) {
					keys.add((FieldHeapLoc)k);
				}
			});
		}

		for (FieldHeapLoc key : keys) {
			SymbolicValue v = key.heapLoc;
			if (!hasValue(v)) {
				for (Map<VariableHeapLoc, SymbolicValue> map : symbEnv) {
					map.remove(key);
				}
			}
		}
	}


	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Symbolic Environment:\n");
	
		for (int i = 0; i < symbEnv.size(); i++) {
			Map<VariableHeapLoc, SymbolicValue> map = symbEnv.get(i);
			sb.append("  Map ").append(i + 1).append(":\n");
	
			for (Map.Entry<VariableHeapLoc, SymbolicValue> entry : map.entrySet()) {
				sb.append("    ")
				  .append(entry.getKey().toString()) // Key
				  .append(" -> ")
				  .append(entry.getValue().toString()) // Value
				  .append("\n");
			}
		}
	
		return sb.toString();
	}

  }


