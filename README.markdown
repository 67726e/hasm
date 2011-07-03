HASM
============

## License:

Dual licensed under MIT or GPL Version 2.0 by Glenn Nelson

## Description:

A 6502 assembler with basic optimization functions capable of producing NES ROMs with or without the iNES header.

## Requirements

	JDK (Tested on 6.24)
	JVM
	
## Usage:

	hasm [options] source

	This assembler accepts one source file that will be used
	to build an NES ROM file to be used by an emulator.

## Functions:
	ImageLocator(BufferedImage base, BufferedImage compare); // throws ImageLocatorSizeException
	void search();							// Raw search; no tolerance
	void search(int tolerance);				// Search w/ RGB variance tolerance
	boolean isAtLocation(int x, int y);		// Check for occourrence at x,y
	Point getFirstOccourrence();			// Get Point (Object) of first match
	Point getLastOccourrence();				// Get Point (Object) of last match (null if 0/1 match)
	int numberOfOccourrences();				// Number of matches
	