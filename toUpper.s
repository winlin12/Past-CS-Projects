//
// AArch64 assembly program to convert
// characters in a string to upper case.
// String is NULL terminated.
//
// X3 - address of output string
// X4 - address of input string
// X5 - character being processed
// 

.data
.align 3	// align to 2^3 byte addresses (word align)
outstr:
	.space 256	// allocate 256 bytes filled with 0
format:
	.asciz "%s \n"	// store this format string in memory

.align 3	// word align code that follows

.global toUpper
toUpper:

	stp x29, x30, [sp, -16]!// Push frame pointer and link register
				// on stack and adjust stack pointer
				// Needed to return to toUpperMain.c


//<Your two instructions to initialize pointers defined in block comment.>

	ldr X3, =outstr		// X3 gets pointer to output string
	mov X4, X0		// X4 gets pointer to argv[0]


//<Your code to convert string here.>
	 
L3:	ldrb W5, [X4], #1 	// loads next argv into x5 and increments x4 
	cmp X5, #0 		// compares x5's ascii value to null or 0
	beq L5 			// branches to print if true
	cmp X5, #97 		// checks if ascii value is less than a
	blt L2 			// branch to store value if true
	cmp X5, #122 		// checks if ascii value i greater than z
	bgt L2 			// branch to store value if true
	sub X5, X5, 32		// subtracts difference of uppercase and lowercase asciis
L2:	strb W5, [X3], #1	// stores result into output string and increments it
	b L3			// loops
	
// Set parameters to call printf to print converted string
L5:
	ldr X0, =format	// printf argv[0] is format string
	ldr X1, =outstr	// printf argv[1] is pointer to converted string
	bl printf	// call printf(); printf uses the stack, hence
			// stp and ldp here protect stack for return
			// to toUpperMain.c

	ldp x29, x30, [sp], 16	// Pop frame pointer and link register
	ret			// return to toUpperMain.c
