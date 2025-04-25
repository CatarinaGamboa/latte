package api;
import java.io.File;
import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.Scanner;

import com.google.gson.Gson;

import spoon.Launcher;
import spoon.processing.ProcessingManager;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.factory.Factory;
import spoon.support.QueueProcessingManager;
import typechecking.LatteException;
import typechecking.LatteProcessor;

/**
 * App for Latte Verification
 */
public class App {
	
	/**
	 * Main method, launches the verification on the project in the filepath (currently the path is hardcoded)
	 * @param args
	 */
	public static void main( String[] args ){

		if (args.length == 0) {
			System.out.println("Please enter the path to the file you want to process");
			String allPath = "latte/src/main/java/examples/MyStackTest.java";
			launcher(allPath, true);

		} else if (args.length == 1 && args[0].equals("-multi")) {
			// Analyze multiple files from command line
			Scanner scanner = new Scanner(System.in);
			while(scanner.hasNextLine()){
				String filePath = scanner.nextLine();
				launcher(filePath, true);
			}
			scanner.close();
		}
    }    
    
    /**
	 * 
	 * @param filePath
	 */
    public static void launcher(String filePath, boolean justJson) {
    
	    if (filePath == null) throw new InvalidParameterException("The path to the file is null");
		
		File file = new File(filePath);

        String outputDirectory = file.getParent()+"/../target/generated-sources";

        // Ensure the output directory exists or create it
        File outputDir = new File(outputDirectory);
        if (!outputDir.exists()) {
            if (outputDir.mkdirs()) {
                System.out.println("Output directory created.");
            } else {
                System.out.println("Failed to create output directory.");
                return;
            }
        }

	    Launcher launcher = new Launcher();
	    System.out.println("File path in launch before spoon:" + filePath);
	    launcher.addInputResource(filePath);
	    launcher.getEnvironment().setNoClasspath(true);
		
		launcher.setSourceOutputDirectory(outputDirectory);
	    // optional
	    // launcher.getEnvironment().setSourceClasspath(
	    // "lib1.jar:lib2.jar".split(":"));
	    launcher.getEnvironment().setComplianceLevel(8);
	    launcher.run();
	
	    final Factory factory = launcher.getFactory();
        final LatteProcessor processor = new LatteProcessor(factory);
	    final ProcessingManager processingManager = new QueueProcessingManager(factory);

   
	    processingManager.addProcessor(processor);
		try{
			
			// To only search the last package - less time spent
			CtPackage v = factory.Package().getAll().stream().reduce((first, second) -> second).orElse(null);
			if (v != null)
				processingManager.process(v);	

			// To search all previous packages
	    	// processingManager.process(factory.Package().getRootPackage());
			System.out.println(":::SUCCESS::: "+filePath);
		} catch(LatteException e){

			// Print error for commandline use
			System.out.println(e.getMessage() + "\n");
			Arrays.stream(e.getStackTrace()).forEach(element -> 
            	System.out.println("\tat " + element)
       		 );

			// Add error for JSON
			CtElement ce = e.getElement();
			SourcePosition sp = ce.getPosition();
			JsonError error = new JsonError(sp.getLine(), sp.getColumn(), 
											sp.getEndLine(), sp.getEndColumn(), e.getMessage(), e.getFullMessage());
			String json = new Gson().toJson(error); // using Gson to convert object to JSON
			System.err.println(json);
			if (!justJson)
				throw e;
			

		} finally {
			// Delete the output directory
			deleteDirectory(outputDir);
		}
    }

	   // Recursively delete files and subdirectories
	   public static boolean deleteDirectory(File directory) {
        if (directory.isDirectory()) {
            // Get all files and subdirectories
            String[] files = directory.list();
            if (files != null) {
                for (String file : files) {
                    File subFile = new File(directory, file);
                    if (!deleteDirectory(subFile)) {
                        return false;  // Failed to delete a subdirectory/file
                    }
                }
            }
        }
        // Delete the directory itself after contents are deleted
		// Try to delete the directory itself after its contents are deleted
		boolean isDeleted = directory.delete();
		if (!isDeleted) {
			System.out.println("Failed to delete directory: " + directory.getPath());
		}
		return isDeleted;
    }
}