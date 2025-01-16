package latte.latte_umbrella;

import java.io.File;
import java.security.InvalidParameterException;

import com.google.gson.Gson;

import spoon.Launcher;
import spoon.processing.ProcessingManager;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.factory.Factory;
import spoon.support.QueueProcessingManager;
import typechecking.LatteException;
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
		String allPath;
		if (args.length == 0)  allPath = "latte-umbrella/src/main/java/examples/MyStack.java";
		else allPath = args[0];
    	launcher(allPath);
    }
    
    
    /**
	 * 
	 * @param filePath
	 */
    public static void launcher(String filePath) {
    
	    if (filePath == null) throw new InvalidParameterException("The path to the file is null");

        String outputDirectory = "target/generated-sources";

        // Ensure the output directory exists or create it
        File outputDir = new File(outputDirectory);
        if (!outputDir.exists()) {
            if (outputDir.mkdirs()) {
                System.out.println("Output directory created.");
            } else {
                System.err.println("Failed to create output directory.");
                return;
            }
        }

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
		try{
			
			// To only search the last package - less time spent
			CtPackage v = factory.Package().getAll().stream().reduce((first, second) -> second).orElse(null);
			if (v != null)
				processingManager.process(v);
				
		} catch(LatteException e){
			CtElement ce = e.getElement();
			SourcePosition sp = ce.getPosition();
			JsonError error = new JsonError(sp.getLine(), sp.getColumn(), 
											sp.getEndLine(), sp.getEndColumn(), e.getMessage());
			String json = new Gson().toJson(error); // using Gson to convert object to JSON
			System.err.println(json);
			throw e;
		}
	        
	    // To search all previous packages
	    // processingManager.process(factory.Package().getRootPackage());
    }
}
