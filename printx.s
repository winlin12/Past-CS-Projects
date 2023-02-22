// printx - processes %x conversions.

// w0 - used to pass in 32-bit string and, later, to operate on it
// w2 - holds a constant used in decision making
// w3 - holds remaining bits to be converted of the input argument
// w4 - holds a count of ASCII chars in the stack
//

.global printx
format:
	.string "%d\n"
printx:
	stp x29, x30, [sp, #-16]!// Push frame pointer and link register

        mov w3, w0		// copy arg from call to printx( )

//<your code here; comment each line added>
	mov w4, #0		// initiallize w4
loop:	mov w3, w0
	and w3, w3, #15 	// mask w3 with 0000....1111	
	lsr w0, w0, #4 		// discards those bits from original arg 
	add w3, w3, #48		// turns bitwise integers to ascii representation 
	cmp w3, #57		// if ascii is 0-9, then
	ble L2			// skip next line
	add w3, w3, #39		// add 39 to ascii otherwise to equal a-f
L2:	strb w3, [sp, #-1]!	// push to stack
	add w4, w4, #1		// add 1 to counter
	cmp w4, #8		// see if 8 charactesr are pushed
	bne loop		// loops if not

L3:	ldrb w0, [sp], #1	// loads from stack
	sub w4, w4, #1		// subtracts from count
	cmp w4, #0		// compares count to 0
	beq L4 			// branches if true
	cmp w0, #48		// sees if char is 0
	beq L3			// skips print if so 
	bl putchar		// prints char
loop2:	ldrb w0, [sp], #1	// final iteration that removes char is 0 check
	sub w4, w4, #1		// subtracts from count
L4:	bl putchar		// prints char
	cmp w4, #0		// compares to see if there are still elements in stack
	bne loop2		// loops again if so
	ldp x29, x30, [sp], #16	// Pop frame pointer and link register
	ret			// return
