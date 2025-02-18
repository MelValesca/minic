.global printint
.global println
.global printbool

printint:
	li a7, 1 # PrintInt
	ecall
	ret
	
println:
	li a0, '\n'
	li a7, 11 # PrintChar
	ecall
	ret

printbool:
	beqz a0, pb1
	la a0, trueS
	j pb2
pb1:
	la a0, falseS
pb2:
	li a7, 4 # PrintString
	ecall
	ret

.data
trueS:	.string "true"
falseS:	.string "false"
