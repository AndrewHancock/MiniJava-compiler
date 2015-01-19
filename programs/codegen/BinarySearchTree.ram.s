	#General constants used for output
print_num:
	.ascii "%d \0"
newline:
	.ascii "\n\0"
	.globl _main
_main:
	#Prologue to _main
	pushl %ebp
	movl %esp, %ebp
	call ___main   #Call c library main
	#Begin println
	subl $4, %esp   #Save space for return value
	pushl %eax
	pushl %ecx
	pushl %edx
	pushl $0      # Place holder address for zero sized objects
	call test
	addl $4, %esp
	movl %eax, 12(%esp)
	popl %edx
	popl %ecx
	popl %eax
	pushl $print_num
	call _printf
	addl $8, %esp   #Pop _printf params off of stack
	#Print new line
	pushl $newline
	call _printf
	Addl $4, %esp
	#End println
	leave
	ret
printBool:
	pushl %ebp
	movl %esp, %ebp
	subl $0, %esp   # Allocate stack space for 0 varaibles
	#Begin if
	pushl 12(%ebp)
	popl %eax
	movl $1, %ebx
	cmp %ebx, %eax
	je if_true_1
	#Begin println
	pushl $0
	pushl $print_num
	call _printf
	addl $8, %esp   #Pop _printf params off of stack
	#Print new line
	pushl $newline
	call _printf
	Addl $4, %esp
	#End println
	jmp if_done_1
if_true_1:
	#Begin println
	pushl $1
	pushl $print_num
	call _printf
	addl $8, %esp   #Pop _printf params off of stack
	#Print new line
	pushl $newline
	call _printf
	Addl $4, %esp
	#End println
if_done_1:
	#End if
	pushl $0
	popl %eax
	movl %ebp, %esp
	leave
	ret
test:
	pushl %ebp
	movl %esp, %ebp
	subl $12, %esp   # Allocate stack space for 3 varaibles
	pushl $8   # Push param for malloc call onto stack
	call _malloc
	addl $4, %esp    # Clear stack from malloc call 
	pushl %eax
	lea -4(%ebp), %eax
	pushl %eax
	popl %eax
	popl (%eax)
	subl $4, %esp   #Save space for return value
	pushl %eax
	pushl %ecx
	pushl %edx
	pushl -4(%ebp)
	call init
	addl $4, %esp
	movl %eax, 12(%esp)
	popl %edx
	popl %ecx
	popl %eax
	lea -12(%ebp), %eax
	pushl %eax
	popl %eax
	popl (%eax)
	subl $4, %esp   #Save space for return value
	pushl %eax
	pushl %ecx
	pushl %edx
	pushl $8
	pushl -4(%ebp)
	call insert
	addl $8, %esp
	movl %eax, 12(%esp)
	popl %edx
	popl %ecx
	popl %eax
	lea -12(%ebp), %eax
	pushl %eax
	popl %eax
	popl (%eax)
	subl $4, %esp   #Save space for return value
	pushl %eax
	pushl %ecx
	pushl %edx
	pushl $4
	pushl -4(%ebp)
	call insert
	addl $8, %esp
	movl %eax, 12(%esp)
	popl %edx
	popl %ecx
	popl %eax
	lea -12(%ebp), %eax
	pushl %eax
	popl %eax
	popl (%eax)
	subl $4, %esp   #Save space for return value
	pushl %eax
	pushl %ecx
	pushl %edx
	pushl $12
	pushl -4(%ebp)
	call insert
	addl $8, %esp
	movl %eax, 12(%esp)
	popl %edx
	popl %ecx
	popl %eax
	lea -12(%ebp), %eax
	pushl %eax
	popl %eax
	popl (%eax)
	subl $4, %esp   #Save space for return value
	pushl %eax
	pushl %ecx
	pushl %edx
	pushl $6
	pushl -4(%ebp)
	call insert
	addl $8, %esp
	movl %eax, 12(%esp)
	popl %edx
	popl %ecx
	popl %eax
	lea -12(%ebp), %eax
	pushl %eax
	popl %eax
	popl (%eax)
	subl $4, %esp   #Save space for return value
	pushl %eax
	pushl %ecx
	pushl %edx
	pushl $2
	pushl -4(%ebp)
	call insert
	addl $8, %esp
	movl %eax, 12(%esp)
	popl %edx
	popl %ecx
	popl %eax
	lea -12(%ebp), %eax
	pushl %eax
	popl %eax
	popl (%eax)
	subl $4, %esp   #Save space for return value
	pushl %eax
	pushl %ecx
	pushl %edx
	pushl $10
	pushl -4(%ebp)
	call insert
	addl $8, %esp
	movl %eax, 12(%esp)
	popl %edx
	popl %ecx
	popl %eax
	lea -12(%ebp), %eax
	pushl %eax
	popl %eax
	popl (%eax)
	subl $4, %esp   #Save space for return value
	pushl %eax
	pushl %ecx
	pushl %edx
	pushl $14
	pushl -4(%ebp)
	call insert
	addl $8, %esp
	movl %eax, 12(%esp)
	popl %edx
	popl %ecx
	popl %eax
	lea -12(%ebp), %eax
	pushl %eax
	popl %eax
	popl (%eax)
	subl $4, %esp   #Save space for return value
	pushl %eax
	pushl %ecx
	pushl %edx
	pushl $3
	pushl -4(%ebp)
	call insert
	addl $8, %esp
	movl %eax, 12(%esp)
	popl %edx
	popl %ecx
	popl %eax
	lea -12(%ebp), %eax
	pushl %eax
	popl %eax
	popl (%eax)
	subl $4, %esp   #Save space for return value
	pushl %eax
	pushl %ecx
	pushl %edx
	pushl $1
	pushl -4(%ebp)
	call insert
	addl $8, %esp
	movl %eax, 12(%esp)
	popl %edx
	popl %ecx
	popl %eax
	lea -12(%ebp), %eax
	pushl %eax
	popl %eax
	popl (%eax)
	subl $4, %esp   #Save space for return value
	pushl %eax
	pushl %ecx
	pushl %edx
	pushl $7
	pushl -4(%ebp)
	call insert
	addl $8, %esp
	movl %eax, 12(%esp)
	popl %edx
	popl %ecx
	popl %eax
	lea -12(%ebp), %eax
	pushl %eax
	popl %eax
	popl (%eax)
	subl $4, %esp   #Save space for return value
	pushl %eax
	pushl %ecx
	pushl %edx
	pushl $5
	pushl -4(%ebp)
	call insert
	addl $8, %esp
	movl %eax, 12(%esp)
	popl %edx
	popl %ecx
	popl %eax
	lea -12(%ebp), %eax
	pushl %eax
	popl %eax
	popl (%eax)
	subl $4, %esp   #Save space for return value
	pushl %eax
	pushl %ecx
	pushl %edx
	pushl $9
	pushl -4(%ebp)
	call insert
	addl $8, %esp
	movl %eax, 12(%esp)
	popl %edx
	popl %ecx
	popl %eax
	lea -12(%ebp), %eax
	pushl %eax
	popl %eax
	popl (%eax)
	subl $4, %esp   #Save space for return value
	pushl %eax
	pushl %ecx
	pushl %edx
	pushl $11
	pushl -4(%ebp)
	call insert
	addl $8, %esp
	movl %eax, 12(%esp)
	popl %edx
	popl %ecx
	popl %eax
	lea -12(%ebp), %eax
	pushl %eax
	popl %eax
	popl (%eax)
	subl $4, %esp   #Save space for return value
	pushl %eax
	pushl %ecx
	pushl %edx
	pushl $15
	pushl -4(%ebp)
	call insert
	addl $8, %esp
	movl %eax, 12(%esp)
	popl %edx
	popl %ecx
	popl %eax
	lea -12(%ebp), %eax
	pushl %eax
	popl %eax
	popl (%eax)
	subl $4, %esp   #Save space for return value
	pushl %eax
	pushl %ecx
	pushl %edx
	pushl $13
	pushl -4(%ebp)
	call insert
	addl $8, %esp
	movl %eax, 12(%esp)
	popl %edx
	popl %ecx
	popl %eax
	lea -12(%ebp), %eax
	pushl %eax
	popl %eax
	popl (%eax)
	subl $4, %esp   #Save space for return value
	pushl %eax
	pushl %ecx
	pushl %edx
	subl $4, %esp   #Save space for return value
	pushl %eax
	pushl %ecx
	pushl %edx
	pushl $8
	pushl -4(%ebp)
	call search
	addl $8, %esp
	movl %eax, 12(%esp)
	popl %edx
	popl %ecx
	popl %eax
	pushl 8(%ebp)
	call printBool
	addl $8, %esp
	movl %eax, 12(%esp)
	popl %edx
	popl %ecx
	popl %eax
	lea -12(%ebp), %eax
	pushl %eax
	popl %eax
	popl (%eax)
	subl $4, %esp   #Save space for return value
	pushl %eax
	pushl %ecx
	pushl %edx
	subl $4, %esp   #Save space for return value
	pushl %eax
	pushl %ecx
	pushl %edx
	pushl $4
	pushl -4(%ebp)
	call search
	addl $8, %esp
	movl %eax, 12(%esp)
	popl %edx
	popl %ecx
	popl %eax
	pushl 8(%ebp)
	call printBool
	addl $8, %esp
	movl %eax, 12(%esp)
	popl %edx
	popl %ecx
	popl %eax
	lea -12(%ebp), %eax
	pushl %eax
	popl %eax
	popl (%eax)
	subl $4, %esp   #Save space for return value
	pushl %eax
	pushl %ecx
	pushl %edx
	subl $4, %esp   #Save space for return value
	pushl %eax
	pushl %ecx
	pushl %edx
	pushl $12
	pushl -4(%ebp)
	call search
	addl $8, %esp
	movl %eax, 12(%esp)
	popl %edx
	popl %ecx
	popl %eax
	pushl 8(%ebp)
	call printBool
	addl $8, %esp
	movl %eax, 12(%esp)
	popl %edx
	popl %ecx
	popl %eax
	lea -12(%ebp), %eax
	pushl %eax
	popl %eax
	popl (%eax)
	subl $4, %esp   #Save space for return value
	pushl %eax
	pushl %ecx
	pushl %edx
	subl $4, %esp   #Save space for return value
	pushl %eax
	pushl %ecx
	pushl %edx
	pushl $2
	pushl -4(%ebp)
	call search
	addl $8, %esp
	movl %eax, 12(%esp)
	popl %edx
	popl %ecx
	popl %eax
	pushl 8(%ebp)
	call printBool
	addl $8, %esp
	movl %eax, 12(%esp)
	popl %edx
	popl %ecx
	popl %eax
	lea -12(%ebp), %eax
	pushl %eax
	popl %eax
	popl (%eax)
	subl $4, %esp   #Save space for return value
	pushl %eax
	pushl %ecx
	pushl %edx
	subl $4, %esp   #Save space for return value
	pushl %eax
	pushl %ecx
	pushl %edx
	pushl $10
	pushl -4(%ebp)
	call search
	addl $8, %esp
	movl %eax, 12(%esp)
	popl %edx
	popl %ecx
	popl %eax
	pushl 8(%ebp)
	call printBool
	addl $8, %esp
	movl %eax, 12(%esp)
	popl %edx
	popl %ecx
	popl %eax
	lea -12(%ebp), %eax
	pushl %eax
	popl %eax
	popl (%eax)
	subl $4, %esp   #Save space for return value
	pushl %eax
	pushl %ecx
	pushl %edx
	subl $4, %esp   #Save space for return value
	pushl %eax
	pushl %ecx
	pushl %edx
	pushl $13
	pushl -4(%ebp)
	call search
	addl $8, %esp
	movl %eax, 12(%esp)
	popl %edx
	popl %ecx
	popl %eax
	pushl 8(%ebp)
	call printBool
	addl $8, %esp
	movl %eax, 12(%esp)
	popl %edx
	popl %ecx
	popl %eax
	lea -12(%ebp), %eax
	pushl %eax
	popl %eax
	popl (%eax)
	subl $4, %esp   #Save space for return value
	pushl %eax
	pushl %ecx
	pushl %edx
	subl $4, %esp   #Save space for return value
	pushl %eax
	pushl %ecx
	pushl %edx
	pushl $5
	pushl -4(%ebp)
	call search
	addl $8, %esp
	movl %eax, 12(%esp)
	popl %edx
	popl %ecx
	popl %eax
	pushl 8(%ebp)
	call printBool
	addl $8, %esp
	movl %eax, 12(%esp)
	popl %edx
	popl %ecx
	popl %eax
	lea -12(%ebp), %eax
	pushl %eax
	popl %eax
	popl (%eax)
	subl $4, %esp   #Save space for return value
	pushl %eax
	pushl %ecx
	pushl %edx
	subl $4, %esp   #Save space for return value
	pushl %eax
	pushl %ecx
	pushl %edx
	pushl $1
	pushl -4(%ebp)
	call search
	addl $8, %esp
	movl %eax, 12(%esp)
	popl %edx
	popl %ecx
	popl %eax
	pushl 8(%ebp)
	call printBool
	addl $8, %esp
	movl %eax, 12(%esp)
	popl %edx
	popl %ecx
	popl %eax
	lea -12(%ebp), %eax
	pushl %eax
	popl %eax
	popl (%eax)
	subl $4, %esp   #Save space for return value
	pushl %eax
	pushl %ecx
	pushl %edx
	subl $4, %esp   #Save space for return value
	pushl %eax
	pushl %ecx
	pushl %edx
	pushl $100
	pushl -4(%ebp)
	call search
	addl $8, %esp
	movl %eax, 12(%esp)
	popl %edx
	popl %ecx
	popl %eax
	pushl 8(%ebp)
	call printBool
	addl $8, %esp
	movl %eax, 12(%esp)
	popl %edx
	popl %ecx
	popl %eax
	lea -12(%ebp), %eax
	pushl %eax
	popl %eax
	popl (%eax)
	subl $4, %esp   #Save space for return value
	pushl %eax
	pushl %ecx
	pushl %edx
	subl $4, %esp   #Save space for return value
	pushl %eax
	pushl %ecx
	pushl %edx
	pushl $16
	pushl -4(%ebp)
	call search
	addl $8, %esp
	movl %eax, 12(%esp)
	popl %edx
	popl %ecx
	popl %eax
	pushl 8(%ebp)
	call printBool
	addl $8, %esp
	movl %eax, 12(%esp)
	popl %edx
	popl %ecx
	popl %eax
	lea -12(%ebp), %eax
	pushl %eax
	popl %eax
	popl (%eax)
	subl $4, %esp   #Save space for return value
	pushl %eax
	pushl %ecx
	pushl %edx
	subl $4, %esp   #Save space for return value
	pushl %eax
	pushl %ecx
	pushl %edx
	pushl $21
	pushl -4(%ebp)
	call search
	addl $8, %esp
	movl %eax, 12(%esp)
	popl %edx
	popl %ecx
	popl %eax
	pushl 8(%ebp)
	call printBool
	addl $8, %esp
	movl %eax, 12(%esp)
	popl %edx
	popl %ecx
	popl %eax
	lea -12(%ebp), %eax
	pushl %eax
	popl %eax
	popl (%eax)
	subl $4, %esp   #Save space for return value
	pushl %eax
	pushl %ecx
	pushl %edx
	pushl -4(%ebp)
	call infix_print
	addl $4, %esp
	movl %eax, 12(%esp)
	popl %edx
	popl %ecx
	popl %eax
	lea -12(%ebp), %eax
	pushl %eax
	popl %eax
	popl (%eax)
	pushl -12(%ebp)
	popl %eax
	movl %ebp, %esp
	leave
	ret
setValue:
	pushl %ebp
	movl %esp, %ebp
	subl $0, %esp   # Allocate stack space for 0 varaibles
	pushl 12(%ebp)
	movl 8(%ebp), %eax
	addl $0, %eax
	pushl %eax
	popl %eax
	popl (%eax)
	pushl $0
	popl %eax
	movl %ebp, %esp
	leave
	ret
setLeft:
	pushl %ebp
	movl %esp, %ebp
	subl $0, %esp   # Allocate stack space for 0 varaibles
	pushl 12(%ebp)
	movl 8(%ebp), %eax
	addl $4, %eax
	pushl %eax
	popl %eax
	popl (%eax)
	pushl $0
	popl %eax
	movl %ebp, %esp
	leave
	ret
setRight:
	pushl %ebp
	movl %esp, %ebp
	subl $0, %esp   # Allocate stack space for 0 varaibles
	pushl 12(%ebp)
	movl 8(%ebp), %eax
	addl $8, %eax
	pushl %eax
	popl %eax
	popl (%eax)
	pushl $0
	popl %eax
	movl %ebp, %esp
	leave
	ret
getValue:
	pushl %ebp
	movl %esp, %ebp
	subl $0, %esp   # Allocate stack space for 0 varaibles
	movl 8(%ebp), %ecx
	pushl 0(%ecx)
	popl %eax
	movl %ebp, %esp
	leave
	ret
getLeft:
	pushl %ebp
	movl %esp, %ebp
	subl $0, %esp   # Allocate stack space for 0 varaibles
	movl 8(%ebp), %ecx
	pushl 4(%ecx)
	popl %eax
	movl %ebp, %esp
	leave
	ret
getRight:
	pushl %ebp
	movl %esp, %ebp
	subl $0, %esp   # Allocate stack space for 0 varaibles
	movl 8(%ebp), %ecx
	pushl 8(%ecx)
	popl %eax
	movl %ebp, %esp
	leave
	ret
init:
	pushl %ebp
	movl %esp, %ebp
	subl $4, %esp   # Allocate stack space for 1 varaibles
	pushl $12   # Push param for malloc call onto stack
	call _malloc
	addl $4, %esp    # Clear stack from malloc call 
	pushl %eax
	movl 8(%ebp), %eax
	addl $4, %eax
	pushl %eax
	popl %eax
	popl (%eax)
	subl $4, %esp   #Save space for return value
	pushl %eax
	pushl %ecx
	pushl %edx
	pushl $0
	movl 8(%ebp), %ecx
	pushl 4(%ecx)
	call setValue
	addl $8, %esp
	movl %eax, 12(%esp)
	popl %edx
	popl %ecx
	popl %eax
	lea -4(%ebp), %eax
	pushl %eax
	popl %eax
	popl (%eax)
	subl $4, %esp   #Save space for return value
	pushl %eax
	pushl %ecx
	pushl %edx
	movl 8(%ebp), %ecx
	pushl 4(%ecx)
	movl 8(%ebp), %ecx
	pushl 4(%ecx)
	call setLeft
	addl $8, %esp
	movl %eax, 12(%esp)
	popl %edx
	popl %ecx
	popl %eax
	lea -4(%ebp), %eax
	pushl %eax
	popl %eax
	popl (%eax)
	subl $4, %esp   #Save space for return value
	pushl %eax
	pushl %ecx
	pushl %edx
	movl 8(%ebp), %ecx
	pushl 4(%ecx)
	movl 8(%ebp), %ecx
	pushl 4(%ecx)
	call setRight
	addl $8, %esp
	movl %eax, 12(%esp)
	popl %edx
	popl %ecx
	popl %eax
	lea -4(%ebp), %eax
	pushl %eax
	popl %eax
	popl (%eax)
	movl 8(%ebp), %ecx
	pushl 4(%ecx)
	movl 8(%ebp), %eax
	addl $0, %eax
	pushl %eax
	popl %eax
	popl (%eax)
	pushl $0
	popl %eax
	movl %ebp, %esp
	leave
	ret
getNewNode:
	pushl %ebp
	movl %esp, %ebp
	subl $8, %esp   # Allocate stack space for 2 varaibles
	pushl $12   # Push param for malloc call onto stack
	call _malloc
	addl $4, %esp    # Clear stack from malloc call 
	pushl %eax
	lea -8(%ebp), %eax
	pushl %eax
	popl %eax
	popl (%eax)
	subl $4, %esp   #Save space for return value
	pushl %eax
	pushl %ecx
	pushl %edx
	movl 8(%ebp), %ecx
	pushl 4(%ecx)
	pushl -8(%ebp)
	call setLeft
	addl $8, %esp
	movl %eax, 12(%esp)
	popl %edx
	popl %ecx
	popl %eax
	lea -4(%ebp), %eax
	pushl %eax
	popl %eax
	popl (%eax)
	subl $4, %esp   #Save space for return value
	pushl %eax
	pushl %ecx
	pushl %edx
	movl 8(%ebp), %ecx
	pushl 4(%ecx)
	pushl -8(%ebp)
	call setRight
	addl $8, %esp
	movl %eax, 12(%esp)
	popl %edx
	popl %ecx
	popl %eax
	lea -4(%ebp), %eax
	pushl %eax
	popl %eax
	popl (%eax)
	pushl -8(%ebp)
	popl %eax
	movl %ebp, %esp
	leave
	ret
recursiveInsert:
	pushl %ebp
	movl %esp, %ebp
	subl $8, %esp   # Allocate stack space for 2 varaibles
	#Begin if
	pushl 16(%ebp)
	subl $4, %esp   #Save space for return value
	pushl %eax
	pushl %ecx
	pushl %edx
	pushl 12(%ebp)
	call getValue
	addl $4, %esp
	movl %eax, 12(%esp)
	popl %edx
	popl %ecx
	popl %eax
	popl %ebx
	popl %eax
	cmp %ebx, %eax
	jl less_than_1
	pushl $0
	jmp less_than_done_1
less_than_1:
	pushl $1
less_than_done_1:
	popl %eax
	movl $1, %ebx
	cmp %ebx, %eax
	je if_true_2
	#Begin if
	subl $4, %esp   #Save space for return value
	pushl %eax
	pushl %ecx
	pushl %edx
	subl $4, %esp   #Save space for return value
	pushl %eax
	pushl %ecx
	pushl %edx
	pushl 12(%ebp)
	call getRight
	addl $4, %esp
	movl %eax, 12(%esp)
	popl %edx
	popl %ecx
	popl %eax
	call getValue
	addl $4, %esp
	movl %eax, 12(%esp)
	popl %edx
	popl %ecx
	popl %eax
	subl $4, %esp   #Save space for return value
	pushl %eax
	pushl %ecx
	pushl %edx
	movl 8(%ebp), %ecx
	pushl 4(%ecx)
	call getValue
	addl $4, %esp
	movl %eax, 12(%esp)
	popl %edx
	popl %ecx
	popl %eax
	pop %eax	# Result of left of eq expression
	pop %ebx  # Result of right of eq expression
	cmp %eax, %ebx # Compare the results
	je equaliy_is_equal_1
	#Not equal case
	push $0   # Push false
	jmp equality_done_1
	#Equal case
equaliy_is_equal_1:
	push $1
equality_done_1:
	popl %eax
	movl $1, %ebx
	cmp %ebx, %eax
	je if_true_3
	subl $4, %esp   #Save space for return value
	pushl %eax
	pushl %ecx
	pushl %edx
	pushl 16(%ebp)
	subl $4, %esp   #Save space for return value
	pushl %eax
	pushl %ecx
	pushl %edx
	pushl 12(%ebp)
	call getRight
	addl $4, %esp
	movl %eax, 12(%esp)
	popl %edx
	popl %ecx
	popl %eax
	pushl 8(%ebp)
	call recursiveInsert
	addl $12, %esp
	movl %eax, 12(%esp)
	popl %edx
	popl %ecx
	popl %eax
	lea -4(%ebp), %eax
	pushl %eax
	popl %eax
	popl (%eax)
	jmp if_done_3
if_true_3:
	subl $4, %esp   #Save space for return value
	pushl %eax
	pushl %ecx
	pushl %edx
	pushl 8(%ebp)
	call getNewNode
	addl $4, %esp
	movl %eax, 12(%esp)
	popl %edx
	popl %ecx
	popl %eax
	lea -8(%ebp), %eax
	pushl %eax
	popl %eax
	popl (%eax)
	subl $4, %esp   #Save space for return value
	pushl %eax
	pushl %ecx
	pushl %edx
	pushl 16(%ebp)
	pushl -8(%ebp)
	call setValue
	addl $8, %esp
	movl %eax, 12(%esp)
	popl %edx
	popl %ecx
	popl %eax
	lea -4(%ebp), %eax
	pushl %eax
	popl %eax
	popl (%eax)
	subl $4, %esp   #Save space for return value
	pushl %eax
	pushl %ecx
	pushl %edx
	pushl -8(%ebp)
	pushl 12(%ebp)
	call setRight
	addl $8, %esp
	movl %eax, 12(%esp)
	popl %edx
	popl %ecx
	popl %eax
	lea -4(%ebp), %eax
	pushl %eax
	popl %eax
	popl (%eax)
if_done_3:
	#End if
	jmp if_done_2
if_true_2:
	#Begin if
	subl $4, %esp   #Save space for return value
	pushl %eax
	pushl %ecx
	pushl %edx
	subl $4, %esp   #Save space for return value
	pushl %eax
	pushl %ecx
	pushl %edx
	pushl 12(%ebp)
	call getLeft
	addl $4, %esp
	movl %eax, 12(%esp)
	popl %edx
	popl %ecx
	popl %eax
	call getValue
	addl $4, %esp
	movl %eax, 12(%esp)
	popl %edx
	popl %ecx
	popl %eax
	subl $4, %esp   #Save space for return value
	pushl %eax
	pushl %ecx
	pushl %edx
	movl 8(%ebp), %ecx
	pushl 4(%ecx)
	call getValue
	addl $4, %esp
	movl %eax, 12(%esp)
	popl %edx
	popl %ecx
	popl %eax
	pop %eax	# Result of left of eq expression
	pop %ebx  # Result of right of eq expression
	cmp %eax, %ebx # Compare the results
	je equaliy_is_equal_2
	#Not equal case
	push $0   # Push false
	jmp equality_done_2
	#Equal case
equaliy_is_equal_2:
	push $1
equality_done_2:
	popl %eax
	movl $1, %ebx
	cmp %ebx, %eax
	je if_true_4
	subl $4, %esp   #Save space for return value
	pushl %eax
	pushl %ecx
	pushl %edx
	pushl 16(%ebp)
	subl $4, %esp   #Save space for return value
	pushl %eax
	pushl %ecx
	pushl %edx
	pushl 12(%ebp)
	call getLeft
	addl $4, %esp
	movl %eax, 12(%esp)
	popl %edx
	popl %ecx
	popl %eax
	pushl 8(%ebp)
	call recursiveInsert
	addl $12, %esp
	movl %eax, 12(%esp)
	popl %edx
	popl %ecx
	popl %eax
	lea -4(%ebp), %eax
	pushl %eax
	popl %eax
	popl (%eax)
	jmp if_done_4
if_true_4:
	subl $4, %esp   #Save space for return value
	pushl %eax
	pushl %ecx
	pushl %edx
	pushl 8(%ebp)
	call getNewNode
	addl $4, %esp
	movl %eax, 12(%esp)
	popl %edx
	popl %ecx
	popl %eax
	lea -8(%ebp), %eax
	pushl %eax
	popl %eax
	popl (%eax)
	subl $4, %esp   #Save space for return value
	pushl %eax
	pushl %ecx
	pushl %edx
	pushl 16(%ebp)
	pushl -8(%ebp)
	call setValue
	addl $8, %esp
	movl %eax, 12(%esp)
	popl %edx
	popl %ecx
	popl %eax
	lea -4(%ebp), %eax
	pushl %eax
	popl %eax
	popl (%eax)
	subl $4, %esp   #Save space for return value
	pushl %eax
	pushl %ecx
	pushl %edx
	pushl -8(%ebp)
	pushl 12(%ebp)
	call setLeft
	addl $8, %esp
	movl %eax, 12(%esp)
	popl %edx
	popl %ecx
	popl %eax
	lea -4(%ebp), %eax
	pushl %eax
	popl %eax
	popl (%eax)
if_done_4:
	#End if
if_done_2:
	#End if
	pushl $0
	popl %eax
	movl %ebp, %esp
	leave
	ret
insert:
	pushl %ebp
	movl %esp, %ebp
	subl $4, %esp   # Allocate stack space for 1 varaibles
	#Begin if
	subl $4, %esp   #Save space for return value
	pushl %eax
	pushl %ecx
	pushl %edx
	movl 8(%ebp), %ecx
	pushl 0(%ecx)
	call getValue
	addl $4, %esp
	movl %eax, 12(%esp)
	popl %edx
	popl %ecx
	popl %eax
	subl $4, %esp   #Save space for return value
	pushl %eax
	pushl %ecx
	pushl %edx
	movl 8(%ebp), %ecx
	pushl 4(%ecx)
	call getValue
	addl $4, %esp
	movl %eax, 12(%esp)
	popl %edx
	popl %ecx
	popl %eax
	pop %eax	# Result of left of eq expression
	pop %ebx  # Result of right of eq expression
	cmp %eax, %ebx # Compare the results
	je equaliy_is_equal_3
	#Not equal case
	push $0   # Push false
	jmp equality_done_3
	#Equal case
equaliy_is_equal_3:
	push $1
equality_done_3:
	popl %eax
	movl $1, %ebx
	cmp %ebx, %eax
	je if_true_5
	subl $4, %esp   #Save space for return value
	pushl %eax
	pushl %ecx
	pushl %edx
	pushl 12(%ebp)
	movl 8(%ebp), %ecx
	pushl 0(%ecx)
	pushl 8(%ebp)
	call recursiveInsert
	addl $12, %esp
	movl %eax, 12(%esp)
	popl %edx
	popl %ecx
	popl %eax
	lea -4(%ebp), %eax
	pushl %eax
	popl %eax
	popl (%eax)
	jmp if_done_5
if_true_5:
	subl $4, %esp   #Save space for return value
	pushl %eax
	pushl %ecx
	pushl %edx
	pushl 8(%ebp)
	call getNewNode
	addl $4, %esp
	movl %eax, 12(%esp)
	popl %edx
	popl %ecx
	popl %eax
	movl 8(%ebp), %eax
	addl $0, %eax
	pushl %eax
	popl %eax
	popl (%eax)
	subl $4, %esp   #Save space for return value
	pushl %eax
	pushl %ecx
	pushl %edx
	pushl 12(%ebp)
	movl 8(%ebp), %ecx
	pushl 0(%ecx)
	call setValue
	addl $8, %esp
	movl %eax, 12(%esp)
	popl %edx
	popl %ecx
	popl %eax
	lea -4(%ebp), %eax
	pushl %eax
	popl %eax
	popl (%eax)
if_done_5:
	#End if
	pushl $0
	popl %eax
	movl %ebp, %esp
	leave
	ret
recursiveSearch:
	pushl %ebp
	movl %esp, %ebp
	subl $4, %esp   # Allocate stack space for 1 varaibles
	pushl $0
	lea -4(%ebp), %eax
	pushl %eax
	popl %eax
	popl (%eax)
	#Begin if
	subl $4, %esp   #Save space for return value
	pushl %eax
	pushl %ecx
	pushl %edx
	pushl 12(%ebp)
	call getValue
	addl $4, %esp
	movl %eax, 12(%esp)
	popl %edx
	popl %ecx
	popl %eax
	subl $4, %esp   #Save space for return value
	pushl %eax
	pushl %ecx
	pushl %edx
	movl 8(%ebp), %ecx
	pushl 4(%ecx)
	call getValue
	addl $4, %esp
	movl %eax, 12(%esp)
	popl %edx
	popl %ecx
	popl %eax
	pop %eax	# Result of left of eq expression
	pop %ebx  # Result of right of eq expression
	cmp %eax, %ebx # Compare the results
	je equaliy_is_equal_4
	#Not equal case
	push $0   # Push false
	jmp equality_done_4
	#Equal case
equaliy_is_equal_4:
	push $1
equality_done_4:
	popl %eax
	movl $1, %ebx
	cmp %ebx, %eax
	je if_true_6
	#Begin if
	pushl 16(%ebp)
	subl $4, %esp   #Save space for return value
	pushl %eax
	pushl %ecx
	pushl %edx
	pushl 12(%ebp)
	call getValue
	addl $4, %esp
	movl %eax, 12(%esp)
	popl %edx
	popl %ecx
	popl %eax
	popl %ebx
	popl %eax
	cmp %ebx, %eax
	jl less_than_2
	pushl $0
	jmp less_than_done_2
less_than_2:
	pushl $1
less_than_done_2:
	popl %eax
	movl $1, %ebx
	cmp %ebx, %eax
	je if_true_7
	#Begin if
	subl $4, %esp   #Save space for return value
	pushl %eax
	pushl %ecx
	pushl %edx
	pushl 12(%ebp)
	call getValue
	addl $4, %esp
	movl %eax, 12(%esp)
	popl %edx
	popl %ecx
	popl %eax
	pushl 16(%ebp)
	pop %eax	# Result of left of eq expression
	pop %ebx  # Result of right of eq expression
	cmp %eax, %ebx # Compare the results
	je equaliy_is_equal_5
	#Not equal case
	push $0   # Push false
	jmp equality_done_5
	#Equal case
equaliy_is_equal_5:
	push $1
equality_done_5:
	popl %eax
	movl $1, %ebx
	cmp %ebx, %eax
	je if_true_8
	subl $4, %esp   #Save space for return value
	pushl %eax
	pushl %ecx
	pushl %edx
	pushl 16(%ebp)
	subl $4, %esp   #Save space for return value
	pushl %eax
	pushl %ecx
	pushl %edx
	pushl 12(%ebp)
	call getRight
	addl $4, %esp
	movl %eax, 12(%esp)
	popl %edx
	popl %ecx
	popl %eax
	pushl 8(%ebp)
	call recursiveSearch
	addl $12, %esp
	movl %eax, 12(%esp)
	popl %edx
	popl %ecx
	popl %eax
	lea -4(%ebp), %eax
	pushl %eax
	popl %eax
	popl (%eax)
	jmp if_done_8
if_true_8:
	pushl $1
	lea -4(%ebp), %eax
	pushl %eax
	popl %eax
	popl (%eax)
if_done_8:
	#End if
	jmp if_done_7
if_true_7:
	subl $4, %esp   #Save space for return value
	pushl %eax
	pushl %ecx
	pushl %edx
	pushl 16(%ebp)
	subl $4, %esp   #Save space for return value
	pushl %eax
	pushl %ecx
	pushl %edx
	pushl 12(%ebp)
	call getLeft
	addl $4, %esp
	movl %eax, 12(%esp)
	popl %edx
	popl %ecx
	popl %eax
	pushl 8(%ebp)
	call recursiveSearch
	addl $12, %esp
	movl %eax, 12(%esp)
	popl %edx
	popl %ecx
	popl %eax
	lea -4(%ebp), %eax
	pushl %eax
	popl %eax
	popl (%eax)
if_done_7:
	#End if
	jmp if_done_6
if_true_6:
	pushl $0
	lea -4(%ebp), %eax
	pushl %eax
	popl %eax
	popl (%eax)
if_done_6:
	#End if
	pushl -4(%ebp)
	popl %eax
	movl %ebp, %esp
	leave
	ret
search:
	pushl %ebp
	movl %esp, %ebp
	subl $4, %esp   # Allocate stack space for 1 varaibles
	pushl $0
	lea -4(%ebp), %eax
	pushl %eax
	popl %eax
	popl (%eax)
	#Begin if
	subl $4, %esp   #Save space for return value
	pushl %eax
	pushl %ecx
	pushl %edx
	movl 8(%ebp), %ecx
	pushl 0(%ecx)
	call getValue
	addl $4, %esp
	movl %eax, 12(%esp)
	popl %edx
	popl %ecx
	popl %eax
	subl $4, %esp   #Save space for return value
	pushl %eax
	pushl %ecx
	pushl %edx
	movl 8(%ebp), %ecx
	pushl 4(%ecx)
	call getValue
	addl $4, %esp
	movl %eax, 12(%esp)
	popl %edx
	popl %ecx
	popl %eax
	pop %eax	# Result of left of eq expression
	pop %ebx  # Result of right of eq expression
	cmp %eax, %ebx # Compare the results
	je equaliy_is_equal_6
	#Not equal case
	push $0   # Push false
	jmp equality_done_6
	#Equal case
equaliy_is_equal_6:
	push $1
equality_done_6:
	popl %eax
	movl $1, %ebx
	cmp %ebx, %eax
	je if_true_9
	subl $4, %esp   #Save space for return value
	pushl %eax
	pushl %ecx
	pushl %edx
	pushl 12(%ebp)
	movl 8(%ebp), %ecx
	pushl 0(%ecx)
	pushl 8(%ebp)
	call recursiveSearch
	addl $12, %esp
	movl %eax, 12(%esp)
	popl %edx
	popl %ecx
	popl %eax
	lea -4(%ebp), %eax
	pushl %eax
	popl %eax
	popl (%eax)
	jmp if_done_9
if_true_9:
	pushl $0
	lea -4(%ebp), %eax
	pushl %eax
	popl %eax
	popl (%eax)
if_done_9:
	#End if
	pushl -4(%ebp)
	popl %eax
	movl %ebp, %esp
	leave
	ret
recursive_print:
	pushl %ebp
	movl %esp, %ebp
	subl $4, %esp   # Allocate stack space for 1 varaibles
	#Begin if
	subl $4, %esp   #Save space for return value
	pushl %eax
	pushl %ecx
	pushl %edx
	pushl 12(%ebp)
	call getValue
	addl $4, %esp
	movl %eax, 12(%esp)
	popl %edx
	popl %ecx
	popl %eax
	subl $4, %esp   #Save space for return value
	pushl %eax
	pushl %ecx
	pushl %edx
	movl 8(%ebp), %ecx
	pushl 4(%ecx)
	call getValue
	addl $4, %esp
	movl %eax, 12(%esp)
	popl %edx
	popl %ecx
	popl %eax
	pop %eax	# Result of left of eq expression
	pop %ebx  # Result of right of eq expression
	cmp %eax, %ebx # Compare the results
	je equaliy_is_equal_7
	#Not equal case
	push $0   # Push false
	jmp equality_done_7
	#Equal case
equaliy_is_equal_7:
	push $1
equality_done_7:
	popl %eax
	movl $1, %ebx
	cmp %ebx, %eax
	je if_true_10
	subl $4, %esp   #Save space for return value
	pushl %eax
	pushl %ecx
	pushl %edx
	subl $4, %esp   #Save space for return value
	pushl %eax
	pushl %ecx
	pushl %edx
	pushl 12(%ebp)
	call getLeft
	addl $4, %esp
	movl %eax, 12(%esp)
	popl %edx
	popl %ecx
	popl %eax
	pushl 8(%ebp)
	call recursive_print
	addl $8, %esp
	movl %eax, 12(%esp)
	popl %edx
	popl %ecx
	popl %eax
	lea -4(%ebp), %eax
	pushl %eax
	popl %eax
	popl (%eax)
	#Begin println
	subl $4, %esp   #Save space for return value
	pushl %eax
	pushl %ecx
	pushl %edx
	pushl 12(%ebp)
	call getValue
	addl $4, %esp
	movl %eax, 12(%esp)
	popl %edx
	popl %ecx
	popl %eax
	pushl $print_num
	call _printf
	addl $8, %esp   #Pop _printf params off of stack
	#Print new line
	pushl $newline
	call _printf
	Addl $4, %esp
	#End println
	subl $4, %esp   #Save space for return value
	pushl %eax
	pushl %ecx
	pushl %edx
	subl $4, %esp   #Save space for return value
	pushl %eax
	pushl %ecx
	pushl %edx
	pushl 12(%ebp)
	call getRight
	addl $4, %esp
	movl %eax, 12(%esp)
	popl %edx
	popl %ecx
	popl %eax
	pushl 8(%ebp)
	call recursive_print
	addl $8, %esp
	movl %eax, 12(%esp)
	popl %edx
	popl %ecx
	popl %eax
	lea -4(%ebp), %eax
	pushl %eax
	popl %eax
	popl (%eax)
	jmp if_done_10
if_true_10:
	pushl $0
	lea -4(%ebp), %eax
	pushl %eax
	popl %eax
	popl (%eax)
if_done_10:
	#End if
	pushl -4(%ebp)
	popl %eax
	movl %ebp, %esp
	leave
	ret
infix_print:
	pushl %ebp
	movl %esp, %ebp
	subl $4, %esp   # Allocate stack space for 1 varaibles
	#Begin if
	subl $4, %esp   #Save space for return value
	pushl %eax
	pushl %ecx
	pushl %edx
	movl 8(%ebp), %ecx
	pushl 0(%ecx)
	call getValue
	addl $4, %esp
	movl %eax, 12(%esp)
	popl %edx
	popl %ecx
	popl %eax
	subl $4, %esp   #Save space for return value
	pushl %eax
	pushl %ecx
	pushl %edx
	movl 8(%ebp), %ecx
	pushl 4(%ecx)
	call getValue
	addl $4, %esp
	movl %eax, 12(%esp)
	popl %edx
	popl %ecx
	popl %eax
	pop %eax	# Result of left of eq expression
	pop %ebx  # Result of right of eq expression
	cmp %eax, %ebx # Compare the results
	je equaliy_is_equal_8
	#Not equal case
	push $0   # Push false
	jmp equality_done_8
	#Equal case
equaliy_is_equal_8:
	push $1
equality_done_8:
	popl %eax
	movl $1, %ebx
	cmp %ebx, %eax
	je if_true_11
	subl $4, %esp   #Save space for return value
	pushl %eax
	pushl %ecx
	pushl %edx
	movl 8(%ebp), %ecx
	pushl 0(%ecx)
	pushl 8(%ebp)
	call recursive_print
	addl $8, %esp
	movl %eax, 12(%esp)
	popl %edx
	popl %ecx
	popl %eax
	lea -4(%ebp), %eax
	pushl %eax
	popl %eax
	popl (%eax)
	jmp if_done_11
if_true_11:
	pushl $0
	lea -4(%ebp), %eax
	pushl %eax
	popl %eax
	popl (%eax)
if_done_11:
	#End if
	pushl -4(%ebp)
	popl %eax
	movl %ebp, %esp
	leave
	ret
