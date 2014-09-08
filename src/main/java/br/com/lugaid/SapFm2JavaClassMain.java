package br.com.lugaid;

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.Scanner;

/**
 * This class is to write a console to input configuration to convert the SAP
 * function module parameters to classes.
 * 
 * @author Emerson Rancoletta
 * @version 1.0
 */
public class SapFm2JavaClassMain {
	private static final String MAIN_CLASS_PAT = "^([A-Z])+([a-zA-Z0-9])*$";

	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);

		String mainClass = "";
		String functionMod = "";
		String path = "";

		System.out.println("This program get import parameters"
				+ " from SAP Function Module and transfor to Java Classes and also genereta a "
				+ " handler class for easy to handle RFC call from SAP.");

		// Get main class name
		while (mainClass.isEmpty() || !mainClass.matches(MAIN_CLASS_PAT)) {
			System.out
					.println(String
							.format("1. Inform the name of the main class this name will be base for generate classes for parameters."
									+ "\nFor example if you input GetMaterial the names of the genterated classes will be:"
									+ "\n-GetMaterialImport"
									+ "\n-GetMaterialHandler"
									+ "\nInform the value (must match %s):",
									MAIN_CLASS_PAT));
			mainClass = in.nextLine();
		}

		// Get function module name
		while (functionMod.isEmpty()) {
			System.out.println("2. Input the SAP Function Module name:");

			functionMod = in.nextLine();
			functionMod = functionMod.trim().toUpperCase();
		}
		
		// Get path to write classes files
		while (path.isEmpty() || !Files.exists(FileSystems.getDefault().getPath(path))) {
			System.out.println("3. Input the path to generate classes files (path must exists):");

			path = in.nextLine();
		}
		
		in.close();

		// Call class to generate classes
		SapFm2JavaClasses sapfm = new SapFm2JavaClasses(mainClass, functionMod, FileSystems.getDefault().getPath(path));
		sapfm.generateClasses();
	}

}
