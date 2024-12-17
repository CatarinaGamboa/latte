package context;

import java.util.HashMap;
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

    private Stack<Map<VariableHeapLoc, SymbolicValue>> symbEnv;

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
		symbEnv.peek().put(v, symb);
		return symb;
	}

	public SymbolicValue addField(String var, String field) {
		Variable f = new Variable(field);
		SymbolicValue symb = get(new Variable(var));

		FieldHeapLoc v = new FieldHeapLoc(symb, f);
		symbEnv.peek().put(v, new SymbolicValue(symbolic_counter++));
		return symb;
	}

	public SymbolicValue getFree(){
		return new SymbolicValue(symbolic_counter++);
	}

	public SymbolicValue get(Variable var) {
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
		symbEnv.push(new HashMap<VariableHeapLoc, SymbolicValue>());
	}
	
	/**
	 * Exit the current scope
	 */
	public void exitScope() {
		symbEnv.pop();
	}

  }


