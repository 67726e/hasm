import java.io.*;
import java.util.HashMap;

class Pass2 {
	public Pass2(File source, int labelCount) {
		labelFile = new File(source.getName() + ".label.tmp");		// File that contains the label information
		sourceFile = new File(source.getName() + ".tmp");			// File that holds the cleaned/analyzed source

		this.labelCount = labelCount;

		labelMap = new HashMap<String, Integer>(labelCount);		// Create label address hashmap

		readLabel();												// Load the labels & addresses into the hashmap
		parseLabels();												// Process the labels and reparse the source file
		
		labelFile.delete();											// Delete unneeded temp label file
		sourceFile.delete();										// Delete unneeded temp source file
	}

	public boolean hasError() {
		return error;												// Return error status
	}

	/*
	 * Void function iterates through the cleaned source file and replaces all labels
	 * If there are problems w/ the relative addresses or labels, sets error = true
	 * Otherwise replaces all labels & numbers
	*/
	private void parseLabels() {
		BufferedReader in = null;																	// Used to read in the source file
		BufferedWriter out = null;																	// Used to write to the new file
		String line;																				// Used to hold the line that is being read

		try { in = new BufferedReader(new FileReader(sourceFile)); }								// Used to read in the source file
		catch (FileNotFoundException e) { Error.fatalError(0); }									// 0 -- Cannot find file
		try { out = new BufferedWriter(new FileWriter(sourceFile.getName() + ".fin")); }			// Used to write to the new file
		catch (IOException e) { Error.fatalError(1); }												// 1 -- Cannot write to designated file

		try {
			while ((line = in.readLine()) != null) {
				if (line.charAt(0) == '*') {					// NON-RELATIVE
					String[] tmpLine = line.split("#");												// Split off the line number & original line
					String[] tmpLine2 = tmpLine[1].split("-");										// Seperate the line number from the original line
					String[] tmpLabel = tmpLine[0].split(" ");										// Split up the mnemonic & label

					oLine = tmpLine2[1];															// Fetch the original line from the array
					oLineNumber = Integer.parseInt(tmpLine2[0]);									// Fetch the line number from the array

					String hex = getAddress(tmpLabel[2]);											// Call function to retrieve the address
					if (!hex.equals("ERROR")) {
						try { out.write(tmpLabel[1] + " $" + hex + "\r\n"); }						// Write out the mnemonic & the new address
						catch (IOException e) { Error.fatalError(1); }								// 1 -- Cannot write to designated file
					}
				} else if (line.charAt(0) == '@') {				// RELATIVE
					int labelAddress = 0;															// Holds the address that came w/ the label
					int hashAddress = 0;															// Holds the address from the hashmap

					String[] tmpLine = line.split("#");												// Split off the line number & original line
					String[] tmpLine2 = tmpLine[1].split("-");										// Seperate the line number from the original line
					String[] tmpLabel = tmpLine[0].split(" ");										// Split up the mnemonic & label
					String[] label = tmpLabel[2].split("-");										// Split off the label & the address

					oLine = tmpLine2[1];															// Grab the original line
					oLineNumber = Integer.parseInt(tmpLine2[0]);									// Grab the line number

					labelAddress = Integer.parseInt(label[1]);										// Parse the String into an integer

					if (labelMap.get(label[0]) == null) {
						Error.codeError(oLine, oLineNumber, 10);									// 10 -- The given label cannot be found
						error = true;
					} else {
						hashAddress = labelMap.get(label[0]);										// Get the address of the label
						int address = hashAddress - labelAddress;									// Calculate the relative value of the branch
						if (address <= 127 || address >= -128) {
							String hexAddress = Integer.toHexString(address);						// Convert the relative value into a hex value
							hexAddress = hexAddress.substring(hexAddress.length() - 2,				// Remove all but the last two digits
								hexAddress.length());

							try { out.write(tmpLabel[1] + " " + hexAddress + "\r\n"); }				// Write out the mnemonic & the relative value
							catch (IOException e) { Error.fatalError(1); }							// 1 -- Cannot write to designated file
						}
					}

					// validate & make sure it is an actual label
					// compare to the given address (tmp2[1])
					// report error if it is out of range
				} else {
					try { out.write(line + "\r\n"); }												// No labels, just write the line to the output file
					catch (IOException e) { Error.fatalError(1); }									// 1 -- Cannot write to designated file
				}
			}

			try { in.close(); }
			catch (IOException e) { Error.fatalError(2); }											// 2 -- Cannot read the given file
			try { out.close(); }
			catch (IOException e) { Error.fatalError(1); }											// 1 -- Cannot write to designated file
		}
		catch (IOException e) { Error.fatalError(2); }												// 2 -- Cannot read the given file
		catch (NumberFormatException e) { Error.fatalError(3); }									// 3 -- The file being read is corrupt
	}

	/*
	 * Recieves a label String as an argument and gets the address
	 * If the address does not exist, returns "ERROR"
	 * Otherwise returns the address from the label
	*/
	private String getAddress(String label) {
		if (labelMap.get(label) == null) {
			Error.codeError(oLine, oLineNumber, 10);		// 10 -- The given label cannot be found
			error = true;
		} else {
			int address = labelMap.get(label);				// Return the address of the label
			String hex = Integer.toHexString(address);		// Convert the number to a hexadecimal address
			while (hex.length() < 4) hex = "0" + hex;		// Pad hex w/ 0s until it is 4 chars long
			return hex;
		}

		return "ERROR";										// Default return "ERROR"
	}

	/*
	 * Void function used to retrieve the labels & addresses
	 * Fills the hashmap w/ all the labels
	*/
	private void readLabel() {
		try {
			BufferedReader labelIn = new BufferedReader(new FileReader(labelFile));	// Used to read in the labe & data
			String line;												// Holds the newly read line
			String[] tmpLine;											// Array used to hold the split line that was just reads

			while ((line = labelIn.readLine()) != null) {
				tmpLine = line.split("#");								// Load the label & address into a tmp array by splitting it via the '#' token
				labelMap.put(tmpLine[0], Integer.parseInt(tmpLine[1]));	// Store the label & address in the hashmap
			}
			
			labelIn.close();
		}
		catch (FileNotFoundException e) {
			Error.fatalError(0);										// 0 -- Cannot find file
		}
		catch (IOException e) {
			Error.fatalError(2);										// 2 -- Cannot read the given file
		}
		catch (NumberFormatException e) {
			Error.fatalError(3);										// 3 -- The file being read is corrupt
		}
	}

	/* Class Variables */
	private String oLine;										// Holds the original line String
	private int oLineNumber;									// Holds the line number

	private boolean error;										// Used to determine if there were errors replacing labels
	private int labelCount;										// Holds the number of lines in the label file
	private File source;										// Holds the final output of code to be compiled to binary
	private File labelFile;										// Holds the label & address information
	private File sourceFile;									// Holds the cleaned & analyzed source

	private HashMap<String, Integer> labelMap;					// Holds the addresses that each label represents
}