
                opt f-g-h+l+o+
                org $1000

start           equ *
				
				lda #$cc			; przekazanie liczby do przekonwertowania
				jsr conv

				lda #0
				adc #0

				lda #48
				adc bcd1
				sta bcd1
				lda #48
				adc bcd2
				sta bcd2
				lda #48
				adc bcd3
				sta bcd3

				lda <bcd3
                ldx >bcd3

                jsr $ff80

				brk


conv			sta byte
				sta temp
				

				;;;;;;;;;;;;;;;;;;	przesuniêcie w lewo rejestrów bcd
left			asl bcd3
				asl bcd2
				lda bcd2
				cmp #16
				bcc e1
				and #%1111
				sta bcd2
				inc bcd3

e1				asl bcd1
				lda bcd1
				cmp #16
				bcc e2
				and #%1111
				sta bcd1
				inc bcd2

e2				asl temp				; przesuniecie w lewo bajtu
				bcc bit0			
				inc bcd1

bit0			lda counter				; warunek zakonczenia petli
				cmp #7
				beq end
				inc counter
				
				lda bcd1				; dodawanie 3 do liczb bcd >= 5 
				cmp #5
				bcc less5b1  

				clc
				lda bcd1
				adc #3
				sta bcd1
				
less5b1			lda bcd2
				cmp #5
				bcc less5b2
				
				clc
				lda bcd2
				adc #3
				sta bcd2
				
less5b2			lda bcd3
				cmp #5
				bcc less5b3
				
				clc
				lda bcd3
				adc #3
				sta bcd3

less5b3			jmp left
				
end				rts


                org $2000
counter			dta b(0)
byte			dta b(0)
temp			dta b(0)
bcd3			dta b(0)
bcd2			dta b(0)
bcd1			dta b(0)
sadf			dta b(0)
sadfwer			dta b(10)                

                org $2E0
                dta a(start)

                end of file
