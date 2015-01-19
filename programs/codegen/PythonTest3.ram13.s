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
call main  #call the starting frame
leave
ret

main:
pushl %ebp
movl %esp, %ebp
subl $12 , %esp   #Reserve spsace for locals and temporaries.
pop %eax
push %eax  #Store results of call onto stack
pop %ebx
movl %ebx, (%eax)
pop %eax
pushl $print_num
call _printf
addl $8, %esp   # Pop _printf params off of stack
pop %ebx
movl %ebx, (%eax)
movl %ebp, %esp
leave
ret


runTest:
pushl %ebp
movl %esp, %ebp
subl $36 , %esp   #Reserve spsace for locals and temporaries.
pop %eax
push %eax  #Store results of call onto stack
pop %ebx
movl %ebx, (%eax)
pop %eax
pushl $print_num
call _printf
addl $8, %esp   # Pop _printf params off of stack
pop %ebx
movl %ebx, (%eax)
pop %eax
push %eax  #Store results of call onto stack
pop %ebx
movl %ebx, (%eax)
pop %eax
pushl $print_num
call _printf
addl $8, %esp   # Pop _printf params off of stack
pop %ebx
movl %ebx, (%eax)
pop %eax
push %eax  #Store results of call onto stack
pop %ebx
movl %ebx, (%eax)
pop %eax
pushl $print_num
call _printf
addl $8, %esp   # Pop _printf params off of stack
pop %ebx
movl %ebx, (%eax)
movl %ebp, %esp
leave
ret


testMath:
pushl %ebp
movl %esp, %ebp
subl $20 , %esp   #Reserve spsace for locals and temporaries.
pushl $3
pushl $5
popl %ebx
popl %eax
imull %ebx, %eax
pushl %eax
pushl $2
popl %ebx
popl %eax
addl %ebx, %eax
pushl %eax
pushl $1
pushl $2
popl %ebx
popl %eax
imull %ebx, %eax
pushl %eax
pushl $8
popl %ebx
popl %eax
imull %ebx, %eax
pushl %eax
popl %ebx
popl %eax
subl %ebx, %eax
pushl %eax
movl %ebp, %esp
leave
ret


testBool:
pushl %ebp
movl %esp, %ebp
subl $28 , %esp   #Reserve spsace for locals and temporaries.
pushl $2
pushl $2
popl %ebx
popl %eax
null %ebx, %eax
pushl %eax
pushl $2
pushl $2
popl %ebx
popl %eax
null %ebx, %eax
pushl %eax
pop %eax
pushl $1
pop %ebx
movl %ebx, (%eax)
pop %eax
pushl $0
pop %ebx
movl %ebx, (%eax)
pushl $0
pushl $0
popl %ebx
popl %eax
null %ebx, %eax
pushl %eax
push -4(%ebp)
pop %eax
pushl $0
pop %ebx
movl %ebx, (%eax)
pushl $0
pushl $0
popl %ebx
popl %eax
null %ebx, %eax
pushl %eax
pushl $0
pushl $2
popl %ebx
popl %eax
imull %ebx, %eax
pushl %eax
pop %eax
pop %ebx
movl %ebx, (%eax)
pop %eax
pushl $99
pop %ebx
movl %ebx, (%eax)
movl %ebp, %esp
leave
ret


testLength:
pushl %ebp
movl %esp, %ebp
subl $8 , %esp   #Reserve spsace for locals and temporaries.
pop %eax
#Allocate new array
pushl $5
pop %eax
mull $4, %eax
push %eax
call _malloc
push %eax
pop %ebx
movl %ebx, (%eax)
movl %ebp, %esp
leave
ret

