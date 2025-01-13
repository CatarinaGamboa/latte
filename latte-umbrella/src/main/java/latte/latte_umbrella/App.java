package latte.latte_umbrella;

import java.security.InvalidParameterException;

import spoon.Launcher;
import spoon.processing.ProcessingManager;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.factory.Factory;
import spoon.support.QueueProcessingManager;
import typechecking.Processor;

/**
 * App for Latte Verification
 */
public class App {
	
	/**
	 * Main method, launches the verification on the project in the filepath (currently the path is hardcoded)
	 * @param args
	 */
	public static void main( String[] args ){
    	String allPath = "latte-umbrella/src/main/java/examples/MyStack.java";
    	launcher(allPath);
    }
    
    
    /**
	 * 
	 * @param filePath
	 */
    public static void launcher(String filePath) {
    
	    if (filePath == null) throw new InvalidParameterException("The path to the file is null");

	    Launcher launcher = new Launcher();
	    System.out.println("File path in launch before spoon:" + filePath);
	    launcher.addInputResource(filePath);
	    launcher.getEnvironment().setNoClasspath(true);
	    // optional
	    // launcher.getEnvironment().setSourceClasspath(
	    // "lib1.jar:lib2.jar".split(":"));
	    launcher.getEnvironment().setComplianceLevel(8);
	    launcher.run();
	
	    final Factory factory = launcher.getFactory();
        final Processor processor = new Processor(factory);
	    final ProcessingManager processingManager = new QueueProcessingManager(factory);

   
	    processingManager.addProcessor(processor);
	
	    // To only search the last package - less time spent
	    CtPackage v = factory.Package().getAll().stream().reduce((first, second) -> second).orElse(null);
	    if (v != null)
			processingManager.process(v);
			
	        
	    // To search all previous packages
	    // processingManager.process(factory.Package().getRootPackage());
    }
}
