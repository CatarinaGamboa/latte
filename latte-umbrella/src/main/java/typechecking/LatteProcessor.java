package typechecking;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import context.ClassLevelMaps;
import context.Context;
import context.PermissionEnvironment;
import context.SymbolicEnvironment;
import context.TypeEnvironment;
import spoon.reflect.visitor.CtScanner;

abstract class LatteProcessor extends CtScanner{
    Context context;
	TypeEnvironment typeEnv;
	SymbolicEnvironment symbEnv;
	PermissionEnvironment permEnv;
	ClassLevelMaps maps;
    private static Logger logger = LoggerFactory.getLogger(LatteProcessor.class);

    final String THIS = "this";

    int loggingSpaces = 0;

    public LatteProcessor(Context context, TypeEnvironment typeEnv, SymbolicEnvironment symbEnv, 
            PermissionEnvironment permEnv, ClassLevelMaps maps) {
            this.context = context; 
            this.typeEnv = typeEnv;
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
	protected void logError(String text) {
		logger.error(" ".repeat(4*loggingSpaces) + "|- " + text);
	}

    	/**
	 * Enter scopes from all environments
	 */
	protected void enterScopes(){
		typeEnv.enterScope();
		symbEnv.enterScope();
		permEnv.enterScope();
		loggingSpaces++;
	}

	/**
	 * Exit scopes from all environments
	 */
	protected void exitScopes(){
		typeEnv.exitScope();
		symbEnv.exitScope();
		permEnv.exitScope();
		loggingSpaces--;
	}
}