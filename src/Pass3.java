import java.io.*;
import java.util.regex.*;
import java.util.HashMap;

class Pass3 {
	Pass3(File oSource, int addressMax, byte[] iNES, boolean iNESWrite) {
		this.addressMax = addressMax;												// Set the maximum address
		this.source = new File(oSource.getName() + ".tmp.fin");						// Set the source file
		this.iNESWrite = iNESWrite;													// Set the iNES write indicator
		String name = oSource.getName();
		if (name.indexOf(".") != -1) name = name.substring(0, name.indexOf("."));	// Get the file name w/o the file extension
		this.game = new File(name + ".nes");										// Set the output file name

		// Accumulator - Immediate - Zero Page - Zero Page,x - Zero Page,y - Absolute - Absolute,X - Absolute,Y - Indirect - Indirect,X - Indirect,Y - Relative - Implied
		opcodeMap = new HashMap<String, int[]>(56);					// Initialize the HashMap w/ all 56 entries
		opcodeMap.put("adc", new int[]{0xFF, 0x69, 0x65, 0x75, 0xFF, 0x6D, 0x7D, 0x79, 0xFF, 0x61, 0x71, 0xFF, 0xFF});	// Verified
		opcodeMap.put("and", new int[]{0xFF, 0x29, 0x25, 0x35, 0xFF, 0x2D, 0x3D, 0x39, 0xFF, 0x21, 0x31, 0xFF, 0xFF});	// Verified
		opcodeMap.put("asl", new int[]{0x0A, 0xFF, 0x06, 0x16, 0xFF, 0x0E, 0x1E, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF});	// Verified
		opcodeMap.put("bcc", new int[]{0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0x90, 0xFF});	// Verified
		opcodeMap.put("bcs", new int[]{0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xB0, 0xFF});	// Verified
		opcodeMap.put("beq", new int[]{0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xF0, 0xFF});	// Verified
		opcodeMap.put("bit", new int[]{0xFF, 0xFF, 0x24, 0xFF, 0xFF, 0x2C, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF});	// Verified
		opcodeMap.put("bmi", new int[]{0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0x30, 0xFF});	// Verified
		opcodeMap.put("bne", new int[]{0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xD0, 0xFF});	// Verified

		opcodeMap.put("bpl", new int[]{0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0x10, 0xFF});	// Verified
		opcodeMap.put("brk", new int[]{0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0x00});	// Verified
		opcodeMap.put("bvc", new int[]{0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0x50, 0xFF});	// Verified
		opcodeMap.put("bvs", new int[]{0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0x70, 0xFF});	// Verified
		opcodeMap.put("clc", new int[]{0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0x18});	// Verified
		opcodeMap.put("cld", new int[]{0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xD8});	// Verified
		opcodeMap.put("cli", new int[]{0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0x58});	// Verified
		opcodeMap.put("clv", new int[]{0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xB8});	// Verified
		opcodeMap.put("cmp", new int[]{0xFF, 0xC9, 0xC5, 0xD5, 0xFF, 0xCD, 0xDD, 0xD9, 0xFF, 0xC1, 0xD1, 0xFF, 0xFF});	// Verified
		opcodeMap.put("cpx", new int[]{0xFF, 0xE0, 0xE4, 0xFF, 0xFF, 0xEC, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF});	// Verified
		opcodeMap.put("cpy", new int[]{0xFF, 0xC0, 0xC4, 0xFF, 0xFF, 0xCC, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF});	// Verified
		opcodeMap.put("dec", new int[]{0xFF, 0xFF, 0xC6, 0xD6, 0xFF, 0xCE, 0xDE, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF});	// Verified

		opcodeMap.put("dex", new int[]{0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xCA});	// Verified
		opcodeMap.put("dey", new int[]{0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0x88});	// Verified
		opcodeMap.put("eor", new int[]{0xFF, 0x49, 0x45, 0x55, 0xFF, 0x4D, 0x5D, 0x59, 0xFF, 0x41, 0x51, 0xFF, 0xFF});	// Verified
		opcodeMap.put("inc", new int[]{0xFF, 0xFF, 0xE6, 0xF6, 0xFF, 0xEE, 0xFE, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF});	// Verified
		opcodeMap.put("inx", new int[]{0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xE8});	// Verified
		opcodeMap.put("iny", new int[]{0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xC8});	// Verified
		opcodeMap.put("jmp", new int[]{0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0x4C, 0xFF, 0xFF, 0x6C, 0xFF, 0xFF, 0xFF, 0xFF});	// Verified
		opcodeMap.put("jsr", new int[]{0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0x20, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF});	// Verified
		opcodeMap.put("lda", new int[]{0xFF, 0xA9, 0xA5, 0xB5, 0xFF, 0xAD, 0xBD, 0xB9, 0xFF, 0xA1, 0xB1, 0xFF, 0xFF});	// Verified
		opcodeMap.put("ldx", new int[]{0xFF, 0xA2, 0xA6, 0xFF, 0xB6, 0xAE, 0xFF, 0xBE, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF});	// Verified
		opcodeMap.put("ldy", new int[]{0xFF, 0xA0, 0xA4, 0xB4, 0xFF, 0xAC, 0xBC, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF});	// Verified
		opcodeMap.put("lsr", new int[]{0x4A, 0xFF, 0x46, 0x56, 0xFF, 0x4E, 0x5E, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF});	// Verified

		opcodeMap.put("nop", new int[]{0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xEA});	// Verified
		opcodeMap.put("ora", new int[]{0xFF, 0x09, 0x05, 0x15, 0xFF, 0x0D, 0x1D, 0x19, 0xFF, 0x01, 0x11, 0xFF, 0xFF});	// Verified
		opcodeMap.put("pha", new int[]{0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0x48});	// Verified
		opcodeMap.put("php", new int[]{0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0x08});	// Verified
		opcodeMap.put("pla", new int[]{0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0x68});	// Verified
		opcodeMap.put("plp", new int[]{0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0x28});	// Verified
		opcodeMap.put("rol", new int[]{0x2A, 0xFF, 0x26, 0x36, 0xFF, 0x2E, 0x3E, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF});	// Verified
		opcodeMap.put("ror", new int[]{0x6A, 0xFF, 0x66, 0x76, 0xFF, 0x6E, 0x7E, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF});	// Verified
		opcodeMap.put("rti", new int[]{0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0x40});	// Verified
		opcodeMap.put("rts", new int[]{0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0x60});	// Verified
		opcodeMap.put("sbc", new int[]{0xFF, 0xE9, 0xE5, 0xF5, 0xFF, 0xED, 0xFD, 0xF9, 0xFF, 0xE1, 0xF1, 0xFF, 0xFF});	// Verified
		opcodeMap.put("sec", new int[]{0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0x38});	// Verified

		opcodeMap.put("sed", new int[]{0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xF8});	// Verified
		opcodeMap.put("sei", new int[]{0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0x78});	// Verified
		opcodeMap.put("sta", new int[]{0xFF, 0xFF, 0x85, 0x95, 0xFF, 0x8D, 0x9D, 0x99, 0xFF, 0x81, 0x91, 0xFF, 0xFF});	// Verified
		opcodeMap.put("stx", new int[]{0xFF, 0xFF, 0x86, 0xFF, 0x96, 0x8E, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF});	// Verified
		opcodeMap.put("sty", new int[]{0xFF, 0xFF, 0x84, 0x94, 0xFF, 0x8C, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF});	// Verified
		opcodeMap.put("tax", new int[]{0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xAA});	// Verified
		opcodeMap.put("tay", new int[]{0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xA8});	// Verified
		opcodeMap.put("tsx", new int[]{0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xBA});	// Verified
		opcodeMap.put("txa", new int[]{0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0x8A});	// Verified
		opcodeMap.put("txs", new int[]{0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0x9A});	// Verified
		opcodeMap.put("tya", new int[]{0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0x98});	// Verified
		// Accumulator - Immediate - Zero Page - Zero Page,x - Zero Page,y - Absolute - Absolute,X - Absolute,Y - Indirect - Indirect,X - Indirect,Y - Relative - Implied

		this.iNES = iNES;											// Store the 16 byte iNES header
		ROM = new byte[ROMSize()];									// Create array that holds the data that will go into the ROM
		for (int i = 0; i < ROM.length; i++) ROM[i] = (byte)0xFF;	// Fill the ROM w/ 0xFF

		createROM();												// Create the ROM
		writeROM();													// Write out the NES ROM
		
		source.delete();											// Delete unneeded temp source file
	}

	/*
	 * Void function reads in the final source and modifies the ROM array based on the results
	 * Reads in the file and writes out the new contents to the appropriate addresses
	*/
	private void createROM() {
		BufferedReader in = null;												// File reader used to read in the final source file
		int address = 0;														// Keeps track of the current address
		String line = "";														// Holds the current line

		try { in = new BufferedReader(new FileReader(source)); }				// Create the file reader object
		catch (FileNotFoundException e) { Error.fatalError(2); }				// 2 -- Cannot read the given file

		try {
			while ((line = in.readLine()) != null) {
				char j = line.charAt(0);										// Get the first character for analysis

				if (j == '.') {				// Directive
					String directive = line.substring(1, line.indexOf(" "));	// Fetch the directive only
					if (directive.equals("address")) {
						String addr = line.substring(line.indexOf("$") + 1,
							line.length());										// Fetch the address
						address = Integer.parseInt(addr, 16);					// Set the new address to the specified address
					}
				} else if (j == '%') {		// Binary Data
					line = line.substring(line.indexOf(" ") +1, line.length());	// Fetch only the binary data
					char[] data = line.toCharArray();							// Load the string into a char array
					for (int i = 0; i < data.length; i++) {
						ROM[address++] = (byte)data[i];							// Place the data into the ROM array
					}
				} else if (j == '#') {		// Bytes
					line = line.substring(line.indexOf(" ") +1, line.length());	// Fetch only the bytes
					String[] data = line.split(" ");							// Split each byte into an array
					for (int i = 0; i < data.length; i++) {
						ROM[address++] = (byte)Integer.parseInt(data[i], 16);	// Store the bytes into the ROM array
					}
				} else if (j == '$') {		// Words
					line = line.substring(line.indexOf(" ") +1, line.length());	// Fetch only the words
					String[] data = line.split(" ");							// Split each word into an array
					for (int i = 0; i < data.length; i++) {
						String pt2 = data[i].substring(0, 2);					// Get the first two digits
						String pt1 = data[i].substring(2, 4);					// Get the second two digits
						ROM[address++] = (byte)Integer.parseInt(pt1, 16);		// Store the first byte
						ROM[address++] = (byte)Integer.parseInt(pt2, 16);		// Store the second byte
					}
				} else {					// Mnemonic
					if (line.contains(" ")) {
						String mnem = line.substring(0, line.indexOf(" "));		// Fetch the mnemonic
						String op = line.substring(line.indexOf(" ") + 1,
							line.length());										// Fetch the operand
						ROM[address++] = (byte)fetchOpcode(mnem, op);			// Set the opcode for the mnemonic
						int[] operands = fetchOperands(op);						// Fetch the operand values to be written to the ROM
						for (int i = 0; i < operands.length; i++) {
							ROM[address++] = (byte)operands[i];					// Write the operand values into the ROM
						}
					} else {
						if (opcodeMap.get(line) != null) {						// Check if the mnemonic is valid
							int opcode = opcodeMap.get(line)[12];				// Fetch the opcode for the implied mnemonic
							if (opcode == 0xFF) Error.fatalError(3);			// 3 -- The file being read is corrupt
							ROM[address++] = (byte)opcode;						// Store the opcode for the implied mnemonic
						} else { Error.fatalError(3); }							// 3 -- The file being read is corrupt
					}
				}
			}
			in.close();
		}
		catch (IOException e) { Error.fatalError(2); }							// 2 -- Cannot read the given file
		catch (NumberFormatException e) { e.printStackTrace(); Error.fatalError(3); }				// 3 -- The file being read is corrupt
	}

	/*
	 * Recieves an operand and returns the proper little-endian values
	 * Returns -1 if there is a problem
	 * Otherwise returns int array with the operand values
	*/
	private int[] fetchOperands(String op) {
		String oOp = op;													// Used to hold the original operand
		Pattern hexadecimal = Pattern.compile("([a-f]|[0-9])");				// Used to match a hexadecimal number
		Matcher matcher;
		matcher = hexadecimal.matcher(op);									// Create the matcher to replace all hexadecimal values with the marker
		while (matcher.find()) {
			op = matcher.replaceAll("~");									// Replace all instances with the marker symbol
			matcher.reset(op);												// Reset the matcher to ensure full coverage
		}

		if (op.equals("#$~~")) {
			op = oOp.substring(2, 4);										// Extract only the operand number
			return new int[]  { Integer.parseInt(op, 16) };					// Return the number in an int array
		} else if (op.equals("$~~")) {
			op = oOp.substring(1, 3);										// Extract only the operand number
			return new int[] { Integer.parseInt(op, 16) };					// Return the number in an int array
		} else if (op.equals("$~~,x")) {
			op = oOp.substring(1, 3);										// Extract only the operand number
			return new int[] { Integer.parseInt(op, 16) };					// Return the number in an int array
		} else if (op.equals("$~~,y")) {
			op = oOp.substring(1, 3);										// Extract only the operand number
			return new int[] { Integer.parseInt(op, 16) };					// Return the number in an int array
		} else if (op.equals("$~~~~")) {
			String pt2 = oOp.substring(1, 3);								// Extract the second half of the operand address
			String pt1 = oOp.substring(3, 5);								// Extract the first half of the operand address
			return new int[] { Integer.parseInt(pt1, 16),
				Integer.parseInt(pt2, 16) };								// Return the two operand parts in an int array
		} else if (op.equals("$~~~~,x")) {
			String pt2 = oOp.substring(1, 3);								// Extract the second half of the operand address
			String pt1 = oOp.substring(3, 5);								// Extract the first half of the operand address
			return new int[] { Integer.parseInt(pt1, 16),
				Integer.parseInt(pt2, 16) };								// Return the two operand parts in an int array
		} else if (op.equals("$~~~~,y")) {
			String pt2 = oOp.substring(1, 3);								// Extract the second half of the operand address
			String pt1 = oOp.substring(3, 5);								// Extract the first half of the operand address
			return new int[] { Integer.parseInt(pt1, 16),
				Integer.parseInt(pt2, 16) };								// Return the two operand parts in an int array
		} else if (op.equals("($~~~~)")) {
			String pt2 = oOp.substring(2, 4);								// Extract the second half of the operand address
			String pt1 = oOp.substring(4, 6);								// Extract the first half of the operand address
			return new int[] { Integer.parseInt(pt1, 16),
				Integer.parseInt(pt2, 16) };								// Return the two operand parts in an int array
		} else if (op.equals("($~~,x)")) {
			op = oOp.substring(2, 4);										// Extract only the operand number
			return new int[] { Integer.parseInt(op, 16) };					// Return the number in an int array
		} else if (op.equals("($~~,y)")) {
			op = oOp.substring(2, 4);										// Extract only the operand number
			return new int[] { Integer.parseInt(op, 16) };					// Return the number in an int array
		} else if (op.equals("~")) {
			return new int[0];												// No operand values for accumulator
		} else if (op.equals("~~")) {
			return new int[] { Integer.parseInt(oOp, 16) };					// Return the number into an int array
		} else { Error.fatalError(3); }										// 3 -- The file being read is corrupt

		return new int[] {-1};
	}

	/*
	 * Recieves mnemonic and opcode and returns the opcode for this pair
	 * Returns -1 if there is a problem
	 * Otherwise returns the opcode
	*/
	private int fetchOpcode(String mnem, String op) {
		String mode = "";														// Used to store the operand mode
		String derp = op;
		int opcode = 0xFF;														// Used to store the opcode
		Pattern hexadecimal = Pattern.compile("([a-f]|[0-9])");					// Used to match a hexadecimal number
		Matcher matcher;
		matcher = hexadecimal.matcher(op);										// Create the matcher to replace all hexadecimal values with the marker
		while (matcher.find()) {
			op = matcher.replaceAll("~");										// Replace all instances with the marker symbol
			matcher.reset(op);													// Reset the matcher to ensure full coverage
		}

		if (op.equals("#$~~")) mode = "IMMEDIATE";								// If the operand is immediate, return so
		else if (op.equals("$~~")) mode = "ZERO-PAGE";							// If the operand is zero-page, return so
		else if (op.equals("$~~,x")) mode = "ZERO-PAGE,X";						// If the operand is zero-page,x, return so
		else if (op.equals("$~~,y")) mode = "ZERO-PAGE,Y";						// If the operand is zero-page,y, return so
		else if (op.equals("$~~~~")) mode = "ABSOLUTE";							// If the operand is absolute, return so
		else if (op.equals("$~~~~,x")) mode = "ABSOLUTE,X";						// If the operand is absolute,x, return so
		else if (op.equals("$~~~~,y")) mode = "ABSOLUTE,Y";						// If the operand is absolute,y, return so
		else if (op.equals("($~~~~)")) mode = "INDIRECT";						// If the operand is indirect, return so
		else if (op.equals("($~~,x)")) mode = "INDIRECT,X"; 					// If the operand is indirect,x, return so
		else if (op.equals("($~~,y)")) mode = "INDIRECT,Y";						// If the operand is indirect,y, return so
		else if (op.equals("~")) mode = "ACCUMULATOR";							// If the operand is accumulator, return so
		else if (op.equals("")) mode = "IMPLIED";								// If no operand, implied addressing is used
		else if (op.equals("~~")) mode = "RELATIVE";							// If the operand is realtive, return so
		else { Error.fatalError(3); }											// 3 -- The file being read is corrupt

		int[] opArr = opcodeMap.get(mnem);										// Get the array of opcodes for the mnemonic

		if (mode.equals("ACCUMULATOR")) { opcode = opArr[0]; }					// 0	- Accumulator
		else if (mode.equals("IMMEDIATE")) { opcode = opArr[1]; }				// 1	- Immediate
		else if (mode.equals("ZERO-PAGE")) { opcode = opArr[2];	}				// 2	- Zero-Page
		else if (mode.equals("ZERO-PAGE,X")) { opcode = opArr[3]; }				// 3	- Zero-Page,x
		else if (mode.equals("ZERO-PAGE,Y")) { opcode = opArr[4]; }				// 4	- Zero-Page,y
		else if (mode.equals("ABSOLUTE")) { opcode = opArr[5]; }				// 5	- Absolute
		else if (mode.equals("ABSOLUTE,X")) { opcode = opArr[6]; }				// 6	- Absolute,x
		else if (mode.equals("ABSOLUTE,Y")) { opcode = opArr[7]; }				// 7	- Absolute,y
		else if (mode.equals("INDIRECT")) { opcode = opArr[8]; }				// 8	- Indirect
		else if (mode.equals("INDIRECT,X")) { opcode = opArr[9]; }				// 9	- Indirect,x
		else if (mode.equals("INDIRECT,Y")) { opcode = opArr[10]; }				// 10	- Indirect,y
		else if (mode.equals("RELATIVE")) { opcode = opArr[11]; }				// 11	- Relative
		else if (mode.equals("IMPLIED")) { opcode = opArr[12]; }				// 12	- Implied

		if (opcode == 0xFF) { Error.fatalError(3); }							// 3 -- The file being read is corrupt

		return opcode;
	}

	/*
	 * Takes the maximum achieved address and calculates the proper ROM size
	 * If the amount is less than 24KB, returns 24BK
	 * Otherwise returns the smallest amount (in increments of 8K) needed for the ROM
	*/
	private int ROMSize() {
		if (addressMax <= 24576) return 24576;						// The amount needed is less than/equal to 24K, return 24K
		int size = 24576;											// Start off w/ 24KB
		addressMax -= 24576;										// Remove 24KB from the overall size

		while (addressMax > 0) {
			addressMax -= 8192;										// Iterate through subtracting 8KB at a time
			size += 8192;											// Add 8KB to the ROM size
		}

		return size;												// Return the size of the ROM
	}

	/*
	 * Void function writes the data to the final .nes file
	 * Writes the iNES header if specified
	 * Writes out the body of the game
	*/
	private void writeROM() {
		DataOutputStream out = null;								// Used to write out the data to the ROM file

		try { out = new DataOutputStream(							// Create output stream used to write the ROM file
			new FileOutputStream(game)); }
		catch (FileNotFoundException e) { Error.fatalError(1); }	// 1 -- Cannot write to the designated file

		if (iNESWrite) {											// Only write the iNES header if instructed
			try { out.write(iNES, 0, iNES.length); }				// Write the iNES header to the ROM
			catch (IOException e) { Error.fatalError(1); }			// 1 -- Cannot write to the designated file
		}

		try { out.write(ROM, 0, ROM.length); }						// Write the ROM file
		catch (IOException e) { Error.fatalError(1); }				// 1 -- Cannot write to the designated file

		try { out.close(); }
		catch (IOException e) { Error.fatalError(1); }				// 1 -- Cannot write to the designated file
	}

	/* Class Variables */
	private boolean iNESWrite;										// Determines if we write the iNES header
	private int addressMax;											// Holds the maximum address reached in the code
	private File source;											// File that contains the source to be read in
	private File game;												// File that holds the final output

	private HashMap<String, int[]> opcodeMap;						// Holds all of the opcodes for the mnemonics
	private byte[] ROM;												// Holds the bytes of the .NES file
	private byte[] iNES;											// Holds the 16 byte iNES header
}