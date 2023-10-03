package latte.latte_umbrella;

import java.security.InvalidParameterException;

import spoon.Launcher;
import spoon.processing.ProcessingManager;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.factory.Factory;
import spoon.support.QueueProcessingManager;
import typechecking.UniquenessProcessor;

/**
 * Hello world!
 *
 */
public class App {
	
    public static void main( String[] args ){
    	
    	String allPath = "../liquidjava-example/src/main/java/test/project";
    	launcher(allPath);
    }
    
    
    
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
	
	    System.out.println("before run");
	    launcher.run();
	
	    System.out.println("after run");
	
	    final Factory factory = launcher.getFactory();
        final UniquenessProcessor processor = new UniquenessProcessor(factory);
	    final ProcessingManager processingManager = new QueueProcessingManager(factory);

   
	    processingManager.addProcessor(processor);
	
	    System.out.println("before process");
	
	    // To only search the last package - less time spent
	    CtPackage v = factory.Package().getAll().stream().reduce((first, second) -> second).orElse(null);
	    if (v != null)
	        processingManager.process(v);
	    // To search all previous packages
	    // processingManager.process(factory.Package().getRootPackage());

    
    }
}
