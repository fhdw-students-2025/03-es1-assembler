; add.asm - adds the values at addresses F0 and F1 and stores the sum at F2
; B = mem[F0]
LOAD F0
SWAP
; A = mem[F1] + B
LOAD F1
ADD
; mem[F2] = A
SAVE F2
; Halt
JUMP 0B
