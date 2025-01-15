	li a0, 42
	li a1, 0
	li a2, 0

loop:
	beq a0, a1, fin
	addi a1, a1, 1
	addi a2, a2, -1
	j loop

fin:	
	li a7, 1 # PrintInt
	mv a0, a2
	ecall
