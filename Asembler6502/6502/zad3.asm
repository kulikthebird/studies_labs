
                opt f-g-h+l+o+
                org $1000

start           equ *
				
				ldx #$ff
				ldy #$ff
				jsr conv
				lda <bcd5
                ldx >bcd5
                jsr $ff80

				jsr conv2
				
				lda <number
                ldx >number
                jsr $ff80

				brk


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;   ascii -> bin
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


conv2			lda #0
				sta counter
				clc
				lda bcd1
				sub #'0'
				sta bcd1

				lda bcd2
				sub #'0'
				sta bcd2

				lda bcd3
				sub #'0'
				sta bcd3

				lda bcd4
				sub #'0'
				sta bcd4

				lda bcd5
				sub #'0'
				sta bcd5


				;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
				;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; shift right output bytes 

loop2			ldy #0
				ldx bytelow
				jsr rightbyte
				;sty 
				stx bytelow

				ldy bytelow
				ldx bytehigh
				jsr rightbyte
				sty bytelow
				stx bytehigh

				ldy bytehigh
				ldx bcd1
				jsr rightbyte
				sty bytehigh
				stx bcd1

				;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
				;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; shift right bcd registers


				ldy bcd1
				ldx bcd2
				jsr rightbcd
				sty bcd1
				stx bcd2

				ldy bcd2
				ldx bcd3
				jsr rightbcd
				sty bcd2
				stx bcd3

				ldy bcd3
				ldx bcd4 
				jsr rightbcd
				sty bcd3
				stx bcd4

				ldy bcd4
				ldx bcd5 
				jsr rightbcd
				sty bcd4
				stx bcd5
				

				;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
				;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; conditions of loop ending
				lda counter
				cmp #15
				beq end2
				inc counter


				;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
				;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; add 3 to bcd registers, which values are less than 5

				ldx bcd1
				jsr sub3
				stx bcd1

				ldx bcd2
				jsr sub3
				stx bcd2

				ldx bcd3
				jsr sub3
				stx bcd3

				ldx bcd4
				jsr sub3
				stx bcd4

				ldx bcd5
				jsr sub3
				stx bcd5

				jmp loop2

end2			rts				; end of conv2 method
				

				;;;;;;;;;;;;;;	przesuniêcie w prawo rejestru bcd
rightbcd		txa
				lsr @
				tax
				bcc more4bits2
				tya
				ora #%1000
				tay
more4bits2		rts
				
				


rightbyte		txa
				lsr @
				tax
				bcc dontinc2
				tya
				ora #%10000000		; dodanie do poprzedniego bajtu 1 z przodu
				tay
dontinc2		rts 


				
sub3			txa				; odejmowanie 3 do liczb bcd >= 8 
				cmp #8
				bcc less5b2
				clc
				sub #3
less5b2			tax
				rts


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; bin -> ascii
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;




conv			stx bytelow
				sty bytehigh


				;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
				;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; shift left bcd registers
loop			ldy #0
				ldx bcd5 
				jsr leftbcd
				;sty
				stx bcd5

				ldy bcd5
				ldx bcd4 
				jsr leftbcd
				sty bcd5
				stx bcd4

				ldy bcd4
				ldx bcd3
				jsr leftbcd
				sty bcd4
				stx bcd3

				ldy bcd3
				ldx bcd2 
				jsr leftbcd
				sty bcd3
				stx bcd2

				ldy bcd2
				ldx bcd1 
				jsr leftbcd
				sty bcd2
				stx bcd1

				;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
				;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; shift left input bytes 

				ldy bcd1
				ldx bytehigh
				jsr leftbyte
				sty bcd1
				stx bytehigh

				ldy bytehigh
				ldx bytelow
				jsr leftbyte
				sty bytehigh
				stx bytelow

				;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
				;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; conditions of loop ending
				lda counter
				cmp #15
				beq end
				inc counter

				;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
				;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; add 3 to bcd registers, which values are less than 5

				ldx bcd1
				jsr add3
				stx bcd1

				ldx bcd2
				jsr add3
				stx bcd2

				ldx bcd3
				jsr add3
				stx bcd3

				ldx bcd4
				jsr add3
				stx bcd4

				ldx bcd5
				jsr add3
				stx bcd5

				jmp loop

end				;;;;;;;;;;;;;;;;;;;;;;;;;;
				;;;;;;;;;;;;;;;;;;;;;;;;;; Add '0' value to whole bcd registers
				clc
				lda #'0'
				adc bcd1
				sta bcd1

				lda #'0'
				adc bcd2
				sta bcd2

				lda #'0'
				adc bcd3
				sta bcd3
				
				lda #'0'
				adc bcd4
				sta bcd4
				
				lda #'0'
				adc bcd5
				sta bcd5
				
				rts					; end of conv method
				

				;;;;;;;;;;;;;;	przesuniêcie w lewo rejestru bcd
leftbcd			txa
				asl @
				cmp #16
				bcc more4bits
				iny
				and #%1111
more4bits		tax
				rts
				


leftbyte		txa
				asl @
				bcc dontinc
				iny				; dodanie do nastepnego bajtu 1
dontinc			tax
				rts 


				
add3			txa				; dodawanie 3 do liczb bcd >= 5 
				cmp #5
				bcc less5b1
				clc
				adc #3
less5b1			tax
				rts
				


                org $2000
bytehigh		dta b(0)
bytelow			dta b(0)
counter			dta b(0)

number			equ *
bcd5			dta b(0)
bcd4			dta b(0)
bcd3			dta b(0)
bcd2			dta b(0)
bcd1			dta b(0),b(10),b(0)     

                org $2E0
                dta a(start)

                end of file
