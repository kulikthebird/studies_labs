
                opt f-g-h+l+o+
                org $1000

start           equ *

                lda <text
                sta $80
                lda >text
                sta $81
                ldy #0
                lda #$cc
                jsr hex

                lda <text
                ldx >text
                jsr $ff80
                brk

hex				sta byte
				sta temp
				lda #%11110000
				and temp
				sta temp
				;lda byte

				lsr temp
				lsr temp
				lsr temp
				lsr temp
				
				lda temp
				tay
				lda char,Y
				ldy #0
				sta ($80),y

				;;;;;;;;;;;;;;;;;;;;;;; druga czesc bajtu

				lda #%00001111
				sta temp
				lda byte
				and temp
				tay
				lda char,Y
				ldy #1
				sta ($80),y


				lda temp
                

                rts



                org $2000
byte            dta b(0)
temp			dta b(0)
text            equ *
                dta b(0),b(0)
                dta b(10) ; '\n'
                dta b(0)
char			dta c'0123456789ABCDEF'


                org $2E0
                dta a(start)

                end of file