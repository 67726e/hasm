public class Error {
	public Error() {}

	/*
	 * Recieves an integer that is used to determine the error message
	 * Error message is then output
	 * The program will then exit w/ an error code (-1)
	 * Exits program, does not return anything
	*/
	public static void fatalError(int status) {
		String error;

		switch (status) {
		case 0:															// 0 -- Cannot find file
			error = "Cannot find file";
			break;
		case 1:															// 1 -- Cannot write to designated file
			error = "Cannot write to file";
			break;
		case 2:															// 2 -- Cannot read the given file
			error = "Cannot read file";
			break;
		case 3:															// 3 -- The file being read is corrupt
			error = "Corrupt file";
			break;
		default:
			error = "Undefined error";
			break;
		}

		System.out.println(error);										// Ouput the error message
		System.exit(-1);												// Exit with an error
	}

	/*
	 * Recieves an error status code and additional message to be put out
	 * Program then exits with error status code (-1)
	*/
	public static void fatalError(int status, String message) {
		String error = "";

		switch (status) {
		case 0:															// 0 -- Cannot find specified file
			error = "Cannot find file: ";
			break;
		case 1:															// 1 -- Cannot write to the specified file
			error = "Cannot write to file: ";
			break;
		case 2:															// 2 -- Cannot read the specified file
			error = "Cannot read file: ";
			break;
		default:
			System.out.println("Undefined error");
			System.exit(-1);
		}

		error += message;
		System.out.println(error);										// Output the error message
		System.exit(-1);												// Exit with an error
	}

	/*
	 * Recieves a String containing the line on which the error occurred, the line number, and the error status as an int
	 * The appropriate error message is generated based on the int value
	 * Line number & error information output
	 * Erroneous line output
	 * Returns void
	*/
	public static void codeError(String line, int lineNumber, int status) {
		String error;

		switch (status) {
		case 0:															// 0 -- Directive supplied is not a valid directive
			error = "Illegal directive";
			break;
		case 1:															// 1 -- Expected a ", the file address was not completed
			error = "Expected \"";
			break;
		case 2:															// 2 -- The file does not exist
			error = "File not found";
			break;
		case 3:															// 3 -- The mnemonic is not valid
			error = "Invalid mnemonic";
			break;
		case 4:															// 4 -- The label supplied is not a valid label
			error = "Invalid label";
			break;
		case 5:															// 5 -- Expected a $, the address could not be read
			error = "Expected a $";
			break;
		case 6:															// 6 -- Erroneous hexadecimal value
			error = "Invalid hexadecimal value";
			break;
		case 7:															// 7 -- The given number is too large
			error = "Number overflow";
			break;
		case 8:															// 8 -- The given operand is not a recognized syntax
			error = "Invalid operand syntax";
			break;
		case 9:															// 9 -- The operand mode cannot be used with the mnemonic
			error = "Operand mode is not allowed with this mnemonic";
			break;
		case 10:														// 10 -- The given label cannot be found
			error = "Unidentified label";
			break;
		case 11:														// 11 -- The branch is out of range
			error = "Branch out of range";
			break;
		case 12:														// 12 -- The given operand does not conform to spec
			error = "Invalid operand";
			break;
		case 13:														// 13 -- The given directive does not support the use of labels
			error = "The directive does not support labels";
			break;
		default:														// If all else fails, we have a default error
			error = "Undefined error";
			break;
		}

		System.out.println(lineNumber + ": " + error);
		System.out.println(line);
		System.out.println("");
	}
}