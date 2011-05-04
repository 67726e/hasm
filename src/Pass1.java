import java.io.*;
import java.util.regex.*;
import java.util.HashMap;

public class Pass1 {
	public Pass1(File source) {
		this.temp = new File(source.getName() + ".tmp");												// Create the temporary file
		this.tempLabel = new File(source.getName() + ".label.tmp");

		lineNumber = 0;																					// Used to indicate what the line number of the file we are working on
		recursionCounter = 0;																			// Used to tell if we are in the middle of a recursive call (anything above 0 = recursion)
		whitespaceSymbols = Pattern.compile("(#|,)\\s(%|\\$)");											// Used to match a hash (#) or comma (,) followed by a whitespace and a percent/dollar sign (%/$)
		whitespace = Pattern.compile("\\s\\s", Pattern.CASE_INSENSITIVE);								// Used to match any instance of two consecutive instances of whitespace; Allow unicode whitespace
		hexadecimal = Pattern.compile("([a-f]|[0-9])");													// Used to match a hexadecimal number

		directives = new String[] {"include", "includebin", "address", "byte", "word",					// Listing of all directive commands
			"inesprg", "ineschr", "inesmir", "inesplc", "inesreg"};
		mnemonics = new String[] {"adc", "and", "asl", "bcc", "bcs", "beq", "bit", "bmi", "bne",		// listing of all 6502 mnemonics
			"bpl", "brk", "bvc", "bvs", "clc", "cld", "cli", "clv", "cmp", "cpx", "cpy", "dec",
			"dex", "dey", "eor", "inc", "inx", "iny", "jmp", "jsr", "lda", "ldx", "ldy", "lsr",
			"nop", "ora", "pha", "php", "pla", "plp", "rol", "ror", "rti", "rts", "sbc", "sec",
			"sed", "sei", "sta", "stx", "sty", "tax", "tay", "tsx", "txa", "txs", "tya"};
		relativeMnemonic = new String[] {"bpl", "bmi", "bvc", "bvs", "bcc", "bcs", "bne", "beq"};		// Listing of all mnemonics that use relative addressing

		operandMap = new HashMap<String, boolean[]>(56);												// Initialize the hash map w/ 56 entries
		// Accumulator - Immediate - Zero Page - Zero Page,x - Zero Page,y - Absolute - Absolute,X - Absolute,Y - Indirect - Indirect,X - Indirect,Y - Relative - Implied
		operandMap.put("adc", new boolean[]{false, true, true, true, false, true, true, true, false, true, true, false, false});		// Verified
		operandMap.put("and", new boolean[]{false, true, true, true, false, true, true, true, false, true, true, false, false});		// Verified
		operandMap.put("asl", new boolean[]{true, false, true, true, false, true, true, false, false, false, false, false, false});		// Verified
		operandMap.put("bcc", new boolean[]{false, false, false, false, false, false, false, false, false, false, false, true, false});	// Verified
		operandMap.put("bcs", new boolean[]{false, false, false, false, false, false, false, false, false, false, false, true, false});	// Verified
		operandMap.put("beq", new boolean[]{false, false, false, false, false, false, false, false, false, false, false, true, false});	// Verified
		operandMap.put("bit", new boolean[]{false, false, true, false, false, true, false, false, false, false, false, false, false});	// Verified
		operandMap.put("bmi", new boolean[]{false, false, false, false, false, false, false, false, false, false, false, true, false});	// Verified
		operandMap.put("bne", new boolean[]{false, false, false, false, false, false, false, false, false, false, false, true, false});	// Verified
		operandMap.put("bpl", new boolean[]{false, false, false, false, false, false, false, false, false, false, false, true, false});	// Verified
		operandMap.put("brk", new boolean[]{false, false, false, false, false, false, false, false, false, false, false, false, true});	// Verified
		operandMap.put("bvc", new boolean[]{false, false, false, false, false, false, false, false, false, false, false, true, false});	// Verified
		operandMap.put("bvs", new boolean[]{false, false, false, false, false, false, false, false, false, false, false, true, false});	// Verified
		operandMap.put("clc", new boolean[]{false, false, false, false, false, false, false, false, false, false, false, false, true});	// Verified
		operandMap.put("cld", new boolean[]{false, false, false, false, false, false, false, false, false, false, false, false, true});	// Verified
		operandMap.put("cli", new boolean[]{false, false, false, false, false, false, false, false, false, false, false, false, true});	// Verified
		operandMap.put("clv", new boolean[]{false, false, false, false, false, false, false, false, false, false, false, false, true});	// Verified
		operandMap.put("cmp", new boolean[]{false, true, true, true, false, true, true, true, false, true, true, false, false});		// Verified
		operandMap.put("cpx", new boolean[]{false, true, true, false, false, true, false, false, false, false, false, false, false});	// Verified
		operandMap.put("cpy", new boolean[]{false, true, true, false, false, true, false, false, false, false, false, false, false});	// Verified
		operandMap.put("dec", new boolean[]{false, false, true, true, false, true, true, false, false, false, false, false, false});	// Verified
		operandMap.put("dex", new boolean[]{false, false, false, false, false, false, false, false, false, false, false, false, true});	// Verified
		operandMap.put("dey", new boolean[]{false, false, false, false, false, false, false, false, false, false, false, false, true});	// Verified
		operandMap.put("eor", new boolean[]{false, true, true, true, false, true, true, true, false, true, true, false, false});		// Verified
		operandMap.put("inc", new boolean[]{false, false, true, true, false, true, true, false, false, false, false, false, false});	// Verified
		operandMap.put("inx", new boolean[]{false, false, false, false, false, false, false, false, false, false, false, false, true});	// Verified
		operandMap.put("iny", new boolean[]{false, false, false, false, false, false, false, false, false, false, false, false, true});	// Verified
		operandMap.put("jmp", new boolean[]{false, false, false, false, false, true, false, false, true, false, false, false, false});	// Verified
		operandMap.put("jsr", new boolean[]{false, false, false, false, false, true, false, false, false, false, false, false, false});	// Verified
		operandMap.put("lda", new boolean[]{false, true, true, true, false, true, true, true, false, true, true, false, false});		// Verified
		operandMap.put("ldx", new boolean[]{false, true, true, false, true, true, false, true, false, false, false, false, false});		// Verified
		operandMap.put("ldy", new boolean[]{false, true, true, true, false, true, true, false, false, false, false, false, false});		// Verified
		operandMap.put("lsr", new boolean[]{true, false, true, true, false, true, true, false, false, false, false, false, false});		// Verified
		operandMap.put("nop", new boolean[]{false, false, false, false, false, false, false, false, false, false, false, false, true});	// Verified
		operandMap.put("ora", new boolean[]{false, true, true, true, false, true, true, true, false, true, true, false, false});		// Verified
		operandMap.put("pha", new boolean[]{false, false, false, false, false, false, false, false, false, false, false, false, true});	// Verified
		operandMap.put("php", new boolean[]{false, false, false, false, false, false, false, false, false, false, false, false, true});	// Verified
		operandMap.put("pla", new boolean[]{false, false, false, false, false, false, false, false, false, false, false, false, true});	// Verified
		operandMap.put("plp", new boolean[]{false, false, false, false, false, false, false, false, false, false, false, false, true});	// Verified
		operandMap.put("rol", new boolean[]{true, false, true, true, false, true, true, false, false, false, false, false, false});		// Verified
		operandMap.put("ror", new boolean[]{true, false, true, true, false, true, true, false, false, false, false, false, false});		// Verified
		operandMap.put("rti", new boolean[]{false, false, false, false, false, false, false, false, false, false, false, false, true});	// Verified
		operandMap.put("rts", new boolean[]{false, false, false, false, false, false, false, false, false, false, false, false, true});	// Verified
		operandMap.put("sbc", new boolean[]{false, true, true, true, false, true, true, true, false, true, true, false, false});		// Verified
		operandMap.put("sec", new boolean[]{false, false, false, false, false, false, false, false, false, false, false, false, true});	// Verified
		operandMap.put("sed", new boolean[]{false, false, false, false, false, false, false, false, false, false, false, false, true});	// Verified
		operandMap.put("sei", new boolean[]{false, false, false, false, false, false, false, false, false, false, false, false, true});	// Verified
		operandMap.put("sta", new boolean[]{false, true, true, true, false, true, true, true, false, true, true, false, false});		// Verified
		operandMap.put("stx", new boolean[]{false, false, true, false, true, true, false, false, false, false, false, false, false});	// Verified
		operandMap.put("sty", new boolean[]{false, false, true, true, false, true, false, false, false, false, false, false, false});	// Verified
		operandMap.put("tax", new boolean[]{false, false, false, false, false, false, false, false, false, false, false, false, true});	// Verified
		operandMap.put("tay", new boolean[]{false, false, false, false, false, false, false, false, false, false, false, false, true});	// Verified
		operandMap.put("tsx", new boolean[]{false, false, false, false, false, false, false, false, false, false, false, false, true});	// Verified
		operandMap.put("txa", new boolean[]{false, false, false, false, false, false, false, false, false, false, false, false, true});	// Verified
		operandMap.put("txs", new boolean[]{false, false, false, false, false, false, false, false, false, false, false, false, true});	// Verified
		operandMap.put("tya", new boolean[]{false, false, false, false, false, false, false, false, false, false, false, false, true});	// Verified
		// Accumulator - Immediate - Zero Page - Zero Page,x - Zero Page,y - Absolute - Absolute,X - Absolute,Y - Indirect - Indirect,X - Indirect,Y - Relative

		iNES = new byte[16];																			// Initialize the 16 byte iNES header array
		iNES[0] = (byte)0x4E;	// 'N'
		iNES[1] = (byte)0x45;	// 'E'
		iNES[2] = (byte)0x53;	// 'S'
		iNES[3] = (byte)0x1A;	// EOF Byte
		iNES[4] = (byte)0;		// 8KB PRG Bank x 0 -- Default
		iNES[5] = (byte)0;		// 8KB CHR Bank x 0 -- Default
		iNES[6] = (byte)0;		// Horizontal Mirroring
		iNES[7] = (byte)0;		// No 'PlayChoice' info
		iNES[8] = (byte)0;		// 8KB PRG RAM -- Default 0
		iNES[9] = (byte)0;		// NTSC -- Default
		iNES[10] = (byte)0;		// NTSC -- Default
		iNES[11] = (byte)0;		// null
		iNES[12] = (byte)0;		// null
		iNES[13] = (byte)0;		// null
		iNES[14] = (byte)0;		// null
		iNES[15] = (byte)0;		// null

		try {
			out = new BufferedWriter(new FileWriter(temp));												// Used to write to the temp file
			outLabel = new BufferedWriter(new FileWriter(tempLabel));									// Used to write to the label temp file
		}
		catch (IOException e) { Error.fatalError(1); }													// 1 -- Cannot write to designated file
	}

	public int getLabels() { return labelCount; }														// Returns the number of labels in the label file
	public int getMaxAddress() { return addressMax; }													// Returns the highest address reached in the program
	public byte[] getiNES() { return iNES; }															// Returns the 16 byte iNES header
	public boolean hasError() { return error; }															// Used to check if there were errors parsing the file

	public void parse(File source) {
		BufferedReader in = null;																		// Used to read in the source file
		String modLine;																					// Holds the modified line
		boolean blockComment = false;																	// Used to determine if we are parsing a block comment
		boolean indented;																				// Used to tell if the line was indented
		recursionCounter++;																				// Increase the recursion counter

		try { in = new BufferedReader(new FileReader(source)); }										// Used to read the source file
		catch (FileNotFoundException e) { Error.fatalError(0); }										// 0 -- Cannot find file

		try {
			while ((line = in.readLine()) != null) {
				boolean write = true;																	// Determines if the line is written to the .tmp file
				lineNumber++;
				modLine = line.toLowerCase();															// Start the modified copy of the line, make it lowercase for standardization

				int blockIndex = modLine.indexOf("/*");
				int colonIndex = modLine.indexOf(";");

				if (blockIndex != -1 && (blockIndex < colonIndex || colonIndex == -1)) blockComment = true;	// If we have a block comment, setup start to removal of comment
				if (!blockComment && colonIndex != -1) modLine = modLine.substring(0, colonIndex);		// Remove the line comment if it is not within a block comment

				/* Remove Block Comments */
				do {
					if (modLine.contains("/*")) blockComment = true;									// Check if there is a block comment (Used to check for /* comment 1 */ /* comment 2 */ situations)
					if (blockComment) {
						boolean blockStart = false;														// Used to determine if a block comment starts on this line
						boolean blockEnd = false;														// Used to determine if a block comment ends on this line

						if (modLine.indexOf("/*") != -1) blockStart = true;								// Check if a block comment starts on this line
						if (modLine.indexOf("*/") != -1) {												// Check if a block comment ends on this line
							blockEnd = true;
							blockComment = false;														// If above is true, block comment ended
						}

						if (blockStart && blockEnd) {													// If a block comment starts & ends on this line
							modLine = modLine.substring(0, line.indexOf("/*")) +						// Get the line before the start of the comment
								modLine.substring(modLine.indexOf("*/") + 2, modLine.length());			// Get the line after the comment ends
						} else if (!blockStart && blockEnd) {
							modLine = modLine.substring(modLine.indexOf("*/") + 2, modLine.length());	// Get the portion of the line after the comment ends
						} else if (blockStart && !blockEnd) {
							modLine = modLine.substring(0, modLine.indexOf("/*"));						// Get the portion of the string before the comment starts
						} else if (!blockStart && !blockEnd) {
							modLine = "";																// This line does not contains anything
						}
					}
				} while (modLine.contains("/*"));
				/* EO Remove Block Comment */

				/* Cleanup/Normalize Code */
				if (modLine.length() > 0 && modLine.charAt(0) == '\t') indented = true;					// Check if the line is indented (not a label)
				else indented = false;																	// Otherwise the line is not indented (a label)
				modLine = modLine.trim();																// Remove excess whitespace
				modLine = modLine.replaceAll("\t", " ");												// Remove all tabs
				matcher = whitespace.matcher(modLine);
				while (matcher.find()) {
					modLine = matcher.replaceAll(" "); 													// Remove excess whitespace
					matcher.reset(modLine); 															// Reset the matcher with the newly modified line
				}
				matcher = whitespaceSymbols.matcher(modLine);
				while (matcher.find()) modLine = matcher.replaceAll("$1$2");							// While the line contains a hash/comma followed by whitespace followed by a hexadecimal number, remove the whitespace
				/* EO Cleanup/Normalize */

				if (modLine.length() == 0) continue;													// If the line is blank, we do not write/analyze so we skip below


				/* Parse Code */
				if (!indented) {
					boolean labelCheck = writeLabel(modLine);											// Have the label analyzed
					if (!labelCheck) Error.codeError(line, lineNumber, 4);								// 4 -- The label supplied is not a valid label
					continue;																			// Don't need to write the label to the .tmp
				} else {
					if (modLine.charAt(0) == '.') {
						String currDirective = validateDirective(modLine);								// Have the directive validated

						if (currDirective.equals("ERROR")) {
							Error.codeError(line, lineNumber, 0);										// Report illegal directive w/ line number & original line
						} else if (currDirective.equals("include")) {			// INCLUDE Directive
							String fileName = retrieveQuote(modLine);									// Call funtion to retrive the data from in between the quotes

							if (!fileName.equals("ERROR")) {											// Check if we have an error
								File includeFile = new File(fileName);									// Create a file out of the included file
								if (!includeFile.exists()) {											// Check if the file does not exists
									Error.codeError(line, lineNumber, 2);								// 2 -- The file does not exist
									error = true;
								}
								else {																	// Otherwise the file exists
									int tmpLineNumber = lineNumber;										// Create temp to hold onto the current line number before recursive call
									String tmpLine = line;												// Create temp to hold onto the current line text before recursive call

									parse(includeFile);													// Recursively call this function to parse the file

									lineNumber = tmpLineNumber;											// Restore the current line number after recursion returns
									line = tmpLine;														// Restore the current line text after the recursive call
								}
							}
							write = false;																// This line is not to be written
						} else if (currDirective.equals("includebin")) {		// INCLUDEBIN Directive
							String fileName = retrieveQuote(modLine);									// Call function to retrieve the data from in between the quotes

							if (!fileName.equals("ERROR")) {
								File includeFile = new File(fileName);									// Create a file out of the included file

								if (!includeFile.exists()) {
									Error.codeError(line, lineNumber, 2);								// 2 -- The file does not exist
									error = true;
								} else {
									DataInputStream binIn = new DataInputStream(
										new FileInputStream(includeFile));								// Reads the binary data from the included file
									byte binByte;														// Holds the byte read from the file
									modLine = "";

									try {
										out.write((byte)0x25);											// Write out a '%' to signify binary data
										out.write((byte)0x20);											// Write out a ' ' as a buffer
										while (true) {
											binByte = binIn.readByte();									// Read in a binary byte from the file
											out.write(binByte);											// Write the binary data to the .tmp file
											address++;													// Increase the current address by 1 for every byte read
										}
									}
									catch (EOFException e) { binIn.close(); }
								}
							}
						} else if (currDirective.equals("address")) {			// ADDRESS Directive
							int start = modLine.indexOf("$");
							if (start == -1) {
								Error.codeError(line, lineNumber, 5);									// 5 -- Expected a $, the address could not be read
							} else {
								String operand = modLine.substring(start + 1, modLine.length());		// Get the new address
								try { address = Integer.parseInt(operand, 16); }						// Set the address to the new address
								catch (NumberFormatException e)
									{ Error.codeError(line, lineNumber, 6); }							// 6 -- Erroneous hexadecimal value
								if (address > addressMax) addressMax = address;							// Set the maximum address reached if needed
							}
						} else if (currDirective.equals("byte")) {				// BYTE Directive
							String[] ops = word_byteParse(modLine, 255);								// Call a function to retrieve the operand(s) from the line w/ a max number of 255
							modLine = "";																// No need to write this line
						} else if (currDirective.equals("word")) {				// WORD Directive
							String[] ops = word_byteParse(modLine, 65535);								// Call a function to retrieve the operand(s) from the line w/ a max number of 65.535
							modLine = "";																// No need to write this line
						} else if (currDirective.equals("inesprg")) {			// INESPRG Directive
							int result = fetchOperand(modLine);
							if (result == -2) {															// Check if a label was found
								Error.codeError(line, lineNumber, 13);									// 13 -- The given directive does not support the use of labels
								error = true;
							} else if (result != -1) iNES[4] = (byte)result;							// Reassign the PRG value of the iNES header
							write = false;																// We do not need to write this directive
						} else if (currDirective.equals("ineschr")) {			// INESCHR Directive
							int result = fetchOperand(modLine);
							if (result == -2) {															// Check if a label was found
								Error.codeError(line, lineNumber, 13);									// 13 -- The given directive does not support the use of labels
								error = true;
							} else if (result != -1) iNES[5] = (byte)result;							// Reassign the CHR value of the iNES header
							write = false;																// We do not need to write this directive
						} else if (currDirective.equals("inesmir")) {			// INESMIR Directive
							int result = fetchOperand(modLine);
							if (result == -2) {															// Check if a label was found
								Error.codeError(line, lineNumber, 13);									// 13 -- The given directive does not support the use of labels
								error = true;
							} else if (result != -1) iNES[6] = (byte)result;							// Reassign the MIR value of the iNES header
							write = false;																// We do not need to write this directive
						} else if (currDirective.equals("inesplc")) {			// INESPLC Directive
							int result = fetchOperand(modLine);
							if (result == -2) {															// Check if a label was found
								Error.codeError(line, lineNumber, 13);									// 13 -- The given directive does not support the use of labels
								error = true;
							} else if (result != -1) iNES[7] = (byte)result;							// Reassign the PlayChoice value of the iNES header
							write = false;																// We do not need to write this directive
						} else if (currDirective.equals("inesreg")) {			// INESREG Directive
							int result = fetchOperand(modLine);
							if (result == -2) {															// Check if a label was found
								Error.codeError(line, lineNumber, 13);									// 13 -- The given directive does not support the use of labels
								error = true;
							} else if (result != -1) iNES[9] = (byte)result;							// Reassign the Region value of the iNES header
							write = false;																// We do not need to write this directive
						}
					}

					/* Mnemonic Start */
					else {
						String mnemonic = validateMnemonic(modLine);									// Call function to validate & return the mnemonic from the line
						if (!mnemonic.equals("ERROR")) {												// Check to see if the mnemonic returned is fine
							String operand = retrieveOperand(modLine);									// Call funtion to extract the operand from the line
							String mode = identifyOperand(operand);										// Call function to determine the type of operand used

							if (mode.equals("LABEL")) {
								if (isRelative(mnemonic)) {												// Call function to determine if the mnemonic uses relative operands
									address += 2;														// Increase the address by 2 for relative mnemonic/operand
									modLine = "@ " + mnemonic + " " + operand + "-" + address;			// Write a line w/ '@' to indicate that we have a relative mnemonic & the address
									modLine += "#" + lineNumber + "-" + line;							// Append a token w/ the line number & original line to the end of the label
								} else {
									address += 3;														// Increase the address by 3 for absolute mnemonic/operand
									modLine = "* " + mnemonic + " " + operand;							// Write a line w/ '*' to indicate that we have a non-relative mnemonic
									modLine += "#" + lineNumber + "-" + line;							// Append a token w/ the line number & original line to the end of the label
								}
							} else if (!mode.equals("ERROR")) {
								boolean validMode = validateOperand(mnemonic, mode);					// Call function to determine if the operand mode works with the mnemonic
							}
						}
					}
					/* EO Mnemonic */
				}
				/* EO Parse Code */

				if (write) out.write(modLine + "\r\n");													// Write the modified line to the .tmp file
			}

			recursionCounter--;																			// Decrement the recursion counter
			if (recursionCounter == 0) {
				out.close();																			// Only end when we are done reading ALL source files
				outLabel.close();																		// The label temp file is also done being written to

				if (address > addressMax) addressMax = address;											// Set the maximum address reached if needed
			}
			try { in.close(); }
			catch (IOException e) { Error.fatalError(2); }											// 2 -- Cannot read the given file
		}
		catch (IOException e) { }
	}

	/*
	 * Recieves a label as a String and determines if it valid
	 * If the label is erroneous, returns false
	 * Otherwise returns true
	*/
	private boolean isLabelValid(String lbl) {
		char[] label = lbl.toCharArray();								// Load the label into a character array
		if (!Character.isLetter(label[0])) return false;				// If the first character is not a letter, return error
		if (label.length < 2) return false;								// If the label is not at least 2 characters, return error

		for (int i = 1; i < label.length; i++) {
			if (!Character.isLetterOrDigit(label[i]) && label[i] != '_') return false;	// If the contents of the label are not either letters, digits, or underscores, return error
		}

		return true;													// The label is valid at this point
	}

	/*
	 * Recieves a line and extracts the operand
	 * Returns -2 if the operand is a label
	 * Returns -1 if there is a problem
	 * Otherwise returns the operand value
	*/
	private int fetchOperand(String line) {
		try {
			if (!line.contains(" ")) throw new OperandException();		// If the line does not contain a space, return error
			line = line.substring(line.indexOf(" "), line.length());	// Retrieve the operand
			line = line.trim();

			if (line.charAt(1) == '$') {					// hexadecimal
				if (line.length() == 2) throw new OperandException();	// Validate line length
				line = line.substring(2, line.length());				// Retrieve only the numbers
				try { return Integer.parseInt(line, 16); }				// Return the parsed number
				catch (NumberFormatException e) { throw new OperandException(); }
			}
			else if (line.charAt(1) == '%') {				// binary
				if (line.length() == 2) throw new OperandException();	// Validate line length
				line = line.substring(2, line.length());				// Retrieve only the numbers
				try { return Integer.parseInt(line, 2); }				// Return the parsed number
				catch (NumberFormatException e) { throw new OperandException(); }
			}
			else if (Character.isDigit(line.charAt(1))) {	// decimal
				if (line.length() == 1) throw new OperandException();	// Validate line length
				line = line.substring(1, line.length());				// Retrieve only the numbers
				try { return Integer.parseInt(line); }					// Return the parsed number
				catch (NumberFormatException e) { throw new OperandException(); }
			} else {										// label
				if (isLabelValid(line)) return -2;						// Return label indicator
				else {
					Error.codeError(line, lineNumber, 12);				// 12 -- The given operand does not conform to spec
					error = true;
					return -1;
				}
			}
		}
		catch (OperandException e) {
			Error.codeError(line, lineNumber, 12);						// 12 -- The given operand does not conform to spec
			error = true;
			return -1;
		}
	}

	/*
	 * Recieves a mnemonic to be analyzed
	 * If the mnemonic used relative operands, returns true
	 * Otherwise returns false
	*/
	private boolean isRelative(String mnemonic) {
		for (String mnem : relativeMnemonic) {
			if (mnem.equals(mnemonic)) return true;		// If the mnemonic is a relative addressing mnemonic, return true
		}
		return false;									// Otherwise return false
	}

	/*
	 * Recieves a mnemonic and an operand to be validated
	 * Cross-check the mode against the mnemonic hashmap
	 * If the operand mode is valid, return true
	 * Otherwise return false
	*/
	private boolean validateOperand(String mnemonic, String mode) {
		// Accumulator - Immediate - Zero Page - Zero Page,x - Zero Page,y - Absolute - Absolute,X - Absolute,Y - Indirect - Indirect,X - Indirect,Y - Relative
		boolean[] modes = operandMap.get(mnemonic);			// Get the boolean array from the hashmap
		boolean valid = false;								// Used to determine if the operand mode matches up

		if (mode.equals("ACCUMULATOR")) {
			valid = modes[0];								// 0	- Accumulator
			address += 1;
		} else if (mode.equals("IMMEDIATE")) {
			valid = modes[1];								// 1	- Immediate
			address += 2;
		} else if (mode.equals("ZERO-PAGE")) {
			valid = modes[2];								// 2	- Zero-Page
			address += 2;
		} else if (mode.equals("ZERO-PAGE,X")) {
			valid = modes[3];								// 3	- Zero-Page,x
			address += 2;
		} else if (mode.equals("ZERO-PAGE,Y")) {
			valid = modes[4];								// 4	- Zero-Page,y
			address += 2;
		} else if (mode.equals("ABSOLUTE")) {
			valid = modes[5];								// 5	- Absolute
			address += 3;
		} else if (mode.equals("ABSOLUTE,X")) {
			valid = modes[6];								// 6	- Absolute,x
			address += 3;
		} else if (mode.equals("ABSOLUTE,Y")) {
			valid = modes[7];								// 7	- Absolute,y
			address += 3;
		} else if (mode.equals("INDIRECT")) {
			valid = modes[8];								// 8	- Indirect
			address += 3;
		} else if (mode.equals("INDIRECT,X")) {
			valid = modes[9];								// 9	- Indirect,x
			address += 2;
		} else if (mode.equals("INDIRECT,Y")) {
			valid = modes[10];								// 10	- Indirect,y
			address += 2;
		} else if (mode.equals("RELATIVE")) {
			valid = modes[11];								// 11	- Relative
			address += 2;
		} else if (mode.equals("IMPLIED")) {
			valid = modes[12];								// 12	- Implied
			address += 1;
		}

		if (!valid) {
			Error.codeError(line, lineNumber, 9);			// 9 -- The operand mode cannot be used with the mnemonic
			error = true;
		}

		return valid;										// Return the validity status
	}

	/*
	 * Recieves the operand as a String
	 * Determines what type of operand is being used
	 * If not identifiable, returns String "ERROR"
	 * Otherwise returns operand type
	*/
	private String identifyOperand(String operand) {
		char[] lbl = operand.toCharArray();
		if (lbl.length > 0 && lbl[0] != '$' && lbl[0] != '#' && lbl[0] != '(' && (lbl.length != 1 && lbl[0] != 'a')) {
			return "LABEL";									// If the operand is a label, return so
		}
		// check for indirect label e.g. (LABEL)

		operand.replaceAll("~", "`");						// '~' is used as a marker symbol. Replace all instances with a non-valid character
		matcher = hexadecimal.matcher(operand);				// Create matcher to replace hexadecimal numbers with marker symbol
		while (matcher.find()) {
			operand = matcher.replaceAll("~");				// Replace all hexadecimal numbers w/ a '~'
			matcher.reset(operand);							// Reset the line to work on the newly modified line
		}

		if (operand.equals("#$~~")) return "IMMEDIATE";		// If the operand is immediate, return so
		if (operand.equals("$~~")) return "ZERO-PAGE";		// If the operand is zero-page, return so
		if (operand.equals("$~~,x")) return "ZERO-PAGE,X";	// If the operand is zero-page,x, return so
		if (operand.equals("$~~,y")) return "ZERO-PAGE,Y";	// If the operand is zero-page,y, return so
		if (operand.equals("$~~~~")) return "ABSOLUTE";		// If the operand is absolute, return so
		if (operand.equals("$~~~~,x")) return "ABSOLUTE,X";	// If the operand is absolute,x, return so
		if (operand.equals("$~~~~,y")) return "ABSOLUTE,Y";	// If the operand is absolute,y, return so
		if (operand.equals("($~~~~)")) return "INDIRECT";	// If the operand is indirect, return so
		if (operand.equals("($~~,x)")) return "INDIRECT,X"; // If the operand is indirect,x, return so
		if (operand.equals("($~~,y)")) return "INDIRECT,Y";	// If the operand is indirect,y, return so
		if (operand.equals("~")) return "ACCUMULATOR";		// If the operand is accumulator, return so
		if (operand.equals("")) return "IMPLIED";			// If no operand, implied addressing is used

		Error.codeError(line, lineNumber, 8);				// 8 -- The given operand is not a recognized syntax
		error = true;
		return "ERROR";										// Otherwise the operand couldn't be identified, return "ERROR"
	}

	/*
	 * Recieves the line so the operand may be extracted from the line
	 * If the line is only an mnemonic, returns " " for relative operand
	 * Otherwise extract & return the operand from the string
	*/
	private String retrieveOperand(String operand) {
		if (operand.length() == 3) return "";			// No operand, return " " for relative operand
		return operand.substring(4, operand.length());	// Otherwise return the operand itself
	}

	/*
	 * Recieves the line so the mnemonic may be extracted & tested
	 * Gets the mnemonic from the line & compares it to an array of all mnemonics
	 * If the mnemonic is valid, return the mnemonic
	 * Otherwise return String "ERROR"
	*/
	private String validateMnemonic(String mnemonic) {
		int space = mnemonic.indexOf(" ");			// Get the location of the closest space

		if (space == 3) {
			mnemonic = mnemonic.substring(0, 3);	// Get the 3 char mnemonic
		} else if (space == -1) ;					// Mnemonic is the entire line

		for (String i : mnemonics) {
			if (mnemonic.equals(i)) {				// Check if the mnemonic matches up w/ a known mnemonic
				return mnemonic;					// Return the valid mnemonic
			}
		}

		Error.codeError(line, lineNumber, 3);		// 3 -- The mnemonic is not valid
		error = true;
		return "ERROR";								// Return an error
	}

	/*
	 * Recieves the modified line in the case of a BYTE or WORD directive
	 * Retrieves the operand(s) from the line and analyzes them for errors
	 * Validates the size of the operand(s) against the maximum size
	 * If all is well, returns an array containing the operand(s)
	 * Otherwise returns String "ERROR"
	*/
	private String[] word_byteParse(String operand, int maxSize) {
		operand = operand.substring(operand.indexOf(" ") + 1, operand.length());	// Remove the directive from the line, only leave the operand(s)
		String[] ops = operand.split(",");											// Split the operands into an array using a comma (,) as a token

		for (int i = 0; i < ops.length; i++) {
			if (ops[i].charAt(0) != '$') {
				Error.codeError(line, lineNumber, 5);								// 5 -- Expected a $, the address could not be read
				error = true;
				ops[0] = "ERROR";													// Signify error in operand(s)
			} else {
				ops[i] = ops[i].substring(1, ops[i].length());						// Remove the dollar sign from the number
				ops[i] = ops[i].trim();												// Remove trailing/excess whitespace
			}

			try {
				int number = Integer.parseInt(ops[i], 16);							// Check that the operands are valid hexadecimal numbers
				if (number > maxSize) 												// Check if the operand is greater than the maximum number
					Error.codeError(line, lineNumber, 7);							// 7 -- The number input is too large
			}
			catch (NumberFormatException e) {
				Error.codeError(line, lineNumber, 6);								// 6 -- Erroneous hexadecimal value
				error = true;
				ops[0] = "ERROR";													// Signify error in operand(s)
			}
		}

		if (!ops[0].equals("ERROR")) {
			try {
				if (maxSize == 255) out.write("# ");								// Write out a '#' token if the byte directive was used
				else if (maxSize == 65535) out.write("$ ");							// Write out a '$' token if the word directive was used

				for (int i = 0; i < ops.length; i++) {
					address++;														// Increase the address by 1
					if (maxSize == 65535) address++;								// If we have a word, increase by 2
					out.write(ops[i] + " ");										// Write out all the operands to the .tmp file
				}
			}
			catch (IOException e) {	Error.fatalError(1); }							// 1 -- Cannot write to designated file
		}

		return ops;																	// Return an array containing the operand(s)
	}

	/*
	 * Recieves a line contain an operand surrounded by quotes e.g. "source.asm"
	 * Retrieves the operand from the line & stripes the quotes
	 * If there were no errors, return the String from the quote
	 * Otherwise return String ERROR
	*/
	private String retrieveQuote(String quote) {
		int start = quote.indexOf("\"") + 1;										// Get the location of the first quotation mark (") - Add 1 to skip quotation mark when capturing file name
		int end = quote.indexOf("\"", start);										// Get the location of the second quatation mark

		if (start == -1 || end == -1) {
			Error.codeError(line, lineNumber, 1);									// 1 -- Expected a ", the file address was not completed
			error = true;
			return "ERROR";															// Return String ERROR for error code
		}

		return quote.substring(start, end);											// Return the value from in between the quotes
	}

	/*
	 * Recieves the line containing the directive to be parsed
	 * Captures the directive from the line & validates against a list of all directives
	 * If the directive is valid, return the directive
	 * Otherwise return String "ERROR"
	 *
	*/
	private String validateDirective(String directive) {
		directive = directive.substring(directive.indexOf(".") + 1,					// Retrieve the directive from the line w/o the period
			directive.indexOf(" "));												// Stop at the end of the directive (Do not fetch the operand)

		for (String dir : directives) {
			if (dir.equals(directive)) return directive;							// If the directive matches a valid directive from the list, return the directive
		}

		return "ERROR";																// We do not have a valid directive, report the error
	}

	/*
	 * Recieves a String that contains the label to be parsed
	 * Validates the label as a true label or othwerise
	 * If the label if valid, writes the label & address to the .label.temp file
	 * Return true if the label is valid, otherwise return false
	 *
	*/
	private boolean writeLabel(String label) {
		boolean validLabel = true;																		// Used to determine if the label is a valid label
		label = label.trim();																			// Remove all trailing/leading whitespace
		if (label.indexOf(":") == label.length() - 1) label = label.substring(0, label.length() - 1);	// Remove an ending colon if it exists
		char[] labelArray = label.toCharArray();														// Load the label into a char array
		for (int i = 0; i < label.length() && validLabel; i++) {										// Run through the array while we have a valid label
			char j = labelArray[i];																		// Create a temp char of the current label char
			if (!Character.isLetterOrDigit(j) && j != '_') validLabel = false;							// If the char is not 1) A letter or 2) A number or 3) An underscore the label is not valid
		}
		if (validLabel) {																				// Check to make sure the label is valid
			try {
				outLabel.write(label + "#" + address);													// Write the label, the token '#' and the address
				outLabel.newLine();																		// Write a newline
				labelCount++;																			// Increase the label counter
				return true;																			// All is well, this was a success
			}
			catch (IOException e) { Error.fatalError(1); }												// 1 -- Cannot write to designated file
		}
		return false;																					// If all else fails, we have an invalid label
	}



	/* Class Variables */
	private String line;																				// Holds the line currently being worked on by the parse() function
	private int lineNumber;																				// Holds the line number of the line currently being worked on by parse()

	private int recursionCounter;																		// Used to determine if we are in a recursive call or not
	private int address;																				// Used to calculate the current address for label value definition
	private int addressMax;																				// Holds the maximum address reached in the code
	private int labelCount;																				// Used to calculate the number of labels in the label file
	private boolean error;																				// Determines if there was an error
	private File temp;																					// File that holds 'parse' output
	private File tempLabel;																				// File that holds the labels found the label's address
	private BufferedWriter out;																			// Used to write to the temp file
	private BufferedWriter outLabel;																	// Used to write to the label temp file
	private Pattern whitespace;																			// Used to match a hash (#) or comma (,) followed by a whitespace and a percent/dollar sign (%/$)
	private Pattern whitespaceSymbols;																	// Used to match any instance of two consecutive instances of whitespace; Allow unicode whitespace
	private Pattern hexadecimal;																		// Used to match hexadecimal numbers
	private Matcher matcher;																			// Used as the matcher for all regexs

	private byte[] iNES;																				// Holds the 16 byte iNES header
	private String[] directives;																		// Holds all directive commands
	private String[] mnemonics;																			// Holds all mnemonics
	private String[] relativeMnemonic;																	// Holds all relative addressing mnemonics
	private HashMap<String, boolean[]> operandMap;														// Holds mnemonics as keys. Used to determine if the menmonic can have the operand
	private HashMap<String, Boolean> labelMap;															// Holds mnemonics as keys. Used to determine if the mnemonic can use labels in any way

	private class OperandException extends Exception {
		OperandException() {}
	}
}