	.address $0000
LABEL4:
	BEQ LABEL4
	.address $0200
LABEL:
	LDA 	#$00			; Line commment mo'fucka
	STA $20				/* block comment
	CMP LABEL
	*/LABEL2:
	CLC					/* dis is a block comment ;asdfkljas;dkfj */ /* Imma assho */
	.include "nesasm_include.asm"	; Include a source file
	CLC
	SEC					; /* lolwut?
	SEC
	LDA 		($44,y)
	LDA LABEL2
	BEQ LABEL
	.includebin "includebin_test.bin"
	.byte $45, $45, $10, $FF, $00
	.word $FFFE, $000F