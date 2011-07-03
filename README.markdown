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

## Directives
	include
	includebin
	address
	byte
	word
	inesprg
	ineschr
	inesmir
	inesplc
	inesreg