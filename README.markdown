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
include		Includes another source file to be used
			  by the assembler.
			  ex: .include "source_2.asm"
	includebin	Includes a binary file to be used by the
			  assembler.
			  ex: .includebin "graphics.bin"
	address		Sets the location of the memory address
			  counter to the specified address.
			  ex: .address $0200
	byte		Allows the inclusion of arbitrary bytes
			  of data to be defined in the binary.
			  ex: .byte $45, $A0
	word		Allows the inclusion of arbitrary words
			  of data to be defined in the binary.
			  ex: .word $4500, $A000
	inesprg		Sets the PRG byte of the iNES header to
			  the specified value.
			  .inesprg #$02
	ineschr		Sets the CHR byte of the iNES header to
			  the specified value.
			  .ineschr #$02
	inesmir		Sets the MIR byte of the iNES header to
			  the specified value.
			  .inesmir #$02
	inesplc		Sets the PlayChoice byte of the iNES
			  header to the specified value.
			  .inesplc #$02
	inesreg		Sets the Region byte of the iNES header
			  to the specified value.
			  .inesreg #$02