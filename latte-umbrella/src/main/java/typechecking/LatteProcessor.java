package typechecking;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import context.ClassLevelMaps;
import context.PermissionEnvironment;
import context.SymbolicEnvironment;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.CtScanner;

abstract class LatteProcessor extends CtScanner{
	SymbolicEnvironment symbEnv;
	PermissionEnvironment permEnv;
	ClassLevelMaps maps;
    private static Logger logger = LoggerFactory.getLogger(LatteProcessor.class);

    final String THIS = "this";

	// * Metadata keys:
	// * EVAL_KEY -> SymbolicValue
	final String EVAL_KEY = "symbolic_value"; 

    int loggingSpaces = 0;

    public LatteProcessor(  SymbolicEnvironment symbEnv, 
            PermissionEnvironment permEnv, ClassLevelMaps maps) {
            this.symbEnv = symbEnv;
            this.permEnv = permEnv;
            this.maps = maps;
    }
 
    /**
	 * Log info with indentation
	 * @param text
	 */
	protected void logInfo(String text) {
		logger.info(" ".repeat(4*loggingSpaces) + "|- " + text);
	}

	/**
	 * Log error with indentation
	 * @param text
	 */
	protected void logWarning(String text) {
		logger.warn(" ".repeat(4*loggingSpaces) + "|- " + text);
	}

	/**
	 * Log error with indentation
	 * @param text
     * @throws LatteException 
     */
    protected void logError(String text, CtElement ce) throws LatteException {
		logger.error(" ".repeat(4*loggingSpaces) + "|- " + text);
        // use string builder

        StringBuilder sb = new StringBuilder();

        String filePath = ce.getPosition().getFile().getAbsolutePath();
        int line = ce.getPosition().getLine();
        int column = ce.getPosition().getColumn();
        
        sb.append("\n------- Latte Error: -------\n")
          .append(text)
          .append("\n\tCode: ")
          .append(ce.toStringDebug()).append("\n")
          .append("\tFile: ")
          .append(filePath).append(":").append(line).append(":").append(column).append("\n"); // Clickable format
        
        throw new LatteException(sb.toString(), ce);   
	}

    /**
	 * Enter scopes from all environments
	 */
	protected void enterScopes(){
		symbEnv.enterScope();
		permEnv.enterScope();
		loggingSpaces++;
	}

	/**
	 * Exit scopes from all environments
	 */
	protected void exitScopes(){
		symbEnv.exitScope();
		permEnv.exitScope();
		loggingSpaces--;
	}
}