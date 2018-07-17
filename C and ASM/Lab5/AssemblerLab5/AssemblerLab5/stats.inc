/*
 * stats.inc
 *
 *  Created: 2016-11-16 20:03:40
 *   Author: Magnus Krampell
 */ 


clearstring:
	.DB "CLEARING...",0 
totalstring:
	.DB "THROWS:",0
countones:
	.DB "NBR OF 1'S:",0
counttwos:
	.DB "NBR OF 2'S:",0
countthrees:
	.DB "NBR OF 3'S:",0
countfours:
	.DB "NBR OF 4'S:",0
countfives:
	.DB "NBR OF 5'S:",0
countsixs:
	.DB "NBR OF 6'S:",0

;==============================================================================
; init statistics module
;==============================================================================
init_stat:
	RCALL clear_stat	; clear data structure
	RET
hexconv: .DB "0123456789ABCDEF"

;==============================================================================
; Print content of R24 on display
; Uses registers:
;	R24			Saves content
;	R5			Retrieves content
;	R6			Handles carry
;==============================================================================
printhex:
	LDI		ZH, HIGH(hexconv<<1)
	LDI		ZL, LOW(hexconv<<1)
	MOV		R5, R24		; save content
	SWAP	R24			; 
	ANDI	R24, 0x0F		; mask out low nibble
	ADD		ZL, R24
	CLR		R6
	ADC		ZH, R6			; handle carry, if needed		
	LPM		R24, Z			; convert number to ASCII
	LCD_WRITE_CHR			; write to LCD
	MOV		R24, R5		; retrieve content
	ANDI	R24, 0x0F		; mask out low nibble
	LDI		ZH, HIGH(hexconv<<1)
	LDI		ZL, LOW(hexconv<<1) ; reset ZL to start
	ADD		ZL, R24
	CLR		R6
	ADC		ZH, R6			; handle carry, if needed		
	LPM		R24, Z			; convert number to ASCII
	LCD_WRITE_CHR			; write to LCD
	RET
;==============================================================================
;	Shows stats of all dices throws
;	Uses registers:
;	  R24			Saves dice trows
;==============================================================================
showstat:
	RCALL lcd_clear
	PRINTSTRING totalstring
	LDI R24, 0
	RCALL get_stat			; get total number of throws	
	RCALL printhex			; print as 2 hex numbers
	RCALL delay_1_s

	RCALL lcd_clear
	PRINTSTRING countones
	LDI R24, 1
	RCALL get_stat			; get total number of throws	
	RCALL printhex			; print as 2 hex numbers
	RCALL delay_1_s

	RCALL lcd_clear
	PRINTSTRING counttwos
	LDI R24, 2
	RCALL get_stat			; get total number of throws	
	RCALL printhex			; print as 2 hex numbers
	RCALL delay_1_s

	RCALL lcd_clear
	PRINTSTRING countthrees
	LDI R24, 3
	RCALL get_stat			; get total number of throws	
	RCALL printhex			; print as 2 hex numbers
	RCALL delay_1_s

	RCALL lcd_clear
	PRINTSTRING countfours
	LDI R24, 4
	RCALL get_stat			; get total number of throws	
	RCALL printhex			; print as 2 hex numbers
	RCALL delay_1_s

	RCALL lcd_clear
	PRINTSTRING countfives
	LDI R24, 5
	RCALL get_stat			; get total number of throws	
	RCALL printhex			; print as 2 hex numbers
	RCALL delay_1_s

	RCALL lcd_clear
	PRINTSTRING countsixs
	LDI R24, 6
	RCALL get_stat			; get total number of throws	
	RCALL printhex			; print as 2 hex numbers
	RCALL delay_1_s

	RCALL lcd_clear
	RET
;==============================================================================
; Clears all stats
;==============================================================================
clearstat:
	RCALL lcd_clear			; clear display
	PRINTSTRING clearstring	; print string
	RCALL clear_stat		; clear stats
	RET
