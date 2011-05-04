import java.io.*;

public class hasm {
	/* Process user input via CLI
	 * Check for arguments regarding processing
	 * Check to make sure supplied source file exists
	 * Manage compilation steps via Pass objects
	*/
	public static void main(String[] args) {
		if (args.length == 0) {
			printDetails();											// Write out the program details
			System.exit(0);
		} else if (args.length == 1) {
			source = new File(args[0]);								// Create source file
		} else if (args.length > 1) {
			source = new File(args[args.length-1]);					// Create source file from the last argument

			for (int i = 0; i < args.length - 1; i++) {				// Iterate through all but the last argument
				if (args[i].equals("-?")) {					// HELP
					printDetails();									// Write out the program details
					System.exit(0);
				} else if (args[i].equals("-raw")) {		// RAW
					iNESWrite = false;
				} else {									// INVALID
					printDetails();									// Write out the program details
					System.exit(-1);								// Exit with an error
				}
			}
		}

		if (!source.exists()) Error.fatalError(0, source.getName());// 0 -- Cannot find file
		else {
			Pass1 pass1 = new Pass1(source);						// Create the first pass object
			pass1.parse(source);									// Run the first pass through the code
			if (!pass1.hasError()) {
				int addressMax = pass1.getMaxAddress();				// Get the maximum address reached in the code
				byte[] iNES = pass1.getiNES();						// Get the iNES header
				Pass2 pass2 = new Pass2(source, pass1.getLabels());	// Create second pass object
				if (!pass2.hasError()) {
					Pass3 pass3 = new Pass3(source, addressMax, iNES, iNESWrite);
				}
			}
		}
	}

	private static void printDetails() {
		System.out.println("HASM v1.0");									// Version information
		System.out.println("");
		System.out.println("hasm [options] source");						// Input schema
		System.out.println("");
		System.out.println("-raw	: Does not output the iNES header");
		System.out.println("-?	: Display program information");
		System.out.println("source	: File to be compiled");
	}

	/* Class Variables */
	private static File source;										// File used to hold the input source file
	private static boolean error = true;							// Boolean used to test if there were problems with the arguments

	/* Argument Variables*/
	private static boolean iNESWrite;								// Boolean used to indicate if the iNES header is to be written
}