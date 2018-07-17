/*
 * AssemblerLab5.asm
 *
 *  Created: 15/12/2016 14:19:10
 *  Author: Ludwig Ninn, Alexander Johansson
 *
 * Orignaly created by Mathias Beckius, 25 June 2015, for the course DA346A at
 * Malmo University.
 */
 
;==============================================================================
; Definitions of registers, etc. ("constants")
;==============================================================================
	.EQU			RESET		 =	0x0000			; reset vector
	.EQU			PM_START	 =	0x0072			; start of program

	; Bit masks, to be used together with SET_IO_BIT and CLR_IO_BIT
	.EQU			BIT3_HIGH	 =	0x08	; 0b00001000
	.EQU			BIT3_LOW	 =	0xF7	; 0b11110111
	.EQU			BIT4_HIGH	 =	0x10	; 0b00010000
	.EQU			BIT4_LOW	 =	0xEF	; 0b11101111
	.EQU			BIT5_HIGH	 =	0x20	; 0b00100000
	.EQU			BIT5_LOW	 =	0xDF	; 0b11011111
	.EQU			BIT6_HIGH	 =	0x40	; 0b01000000
	.EQU			BIT6_LOW	 =	0xBF	; 0b10111111

;==============================================================================
; Macro for setting a bit in I/O register, located in the extended I/O space.
; Uses registers:
;	R24				Store I/O data
;
; Example: Set bit 6 in PORTH
;	SET_IO_BIT		PORTH,			BIT6_HIGH
;==============================================================================
	.MACRO			SET_IO_BIT
	 LDS			R24,			@0
	 ORI			R24,			@1
	 STS			@0,				R24

	.ENDMACRO

;==============================================================================
; Macro for clearing a bit in I/O register, located in the extended I/O space.
; Uses registers:
;	R24				Store I/O data
;
; Example: Clear bit 6 in PORTH
;	CLR_IO_BIT		PORTH,			BIT6_LOW
;==============================================================================
	.MACRO			CLR_IO_BIT

	LDS				R24,			@0
	ANDI			R24,			@1
	STS				@0,				R24
	.ENDMACRO

;==============================================================================
; Start of program
;==============================================================================
	.CSEG
	.ORG			RESET
	RJMP			init

	.ORG			PM_START
	welcome_string: .DB "WELCOME!",0,0
	rolling_string: .DB "ROLLING...",0,0
	value_string: .DB "VALUE:",0,0
	roll_string: .DB "PRESS 2 TO ROLL!",0,0
	.INCLUDE		"delay.inc"
	.INCLUDE		"lcd.inc"					; avkommenteras i moment 3
	.INCLUDE		"keyboard.inc"				; avkommenteras i moment 5
	.INCLUDE		"Tarning.inc"
	.INCLUDE		"stats.inc"
	.INCLUDE		"monitor.inc"
	.INCLUDE		"stat_data.inc"

;==============================================================================
; Basic initializations of stack pointer, I/O pins, etc.
;==============================================================================
init:
	LDI				R16,			LOW(RAMEND)		; Set stack pointer
	OUT				SPL,			R16				; at the end of RAM.
	LDI				R16,			HIGH(RAMEND)
	OUT				SPH,			R16
	RCALL			init_pins						; Initialize pins
	RCALL			init_pins_led
	RCALL			lcd_init						; Initialize LCD 
	RCALL			init_monitor					; Initialize Monitor 
	RCALL			init_stat						; Initialize stat
	RJMP			main							; Jump to main

;==============================================================================
; Initialize I/O pins
;==============================================================================
init_pins:
	; PORT B
	; output:	4, 5 and 6 (LCD command/character, reset, CS/SS)
	LDI				R16,			0x70
	OUT				DDRB,			R16
	; PORT H
	; output:	3 and 4 (keypad col 2 and 3)
	;			5 and 6 (LCD clock and data)
	LDI				R16,			0x78
	STS				DDRH,			R16
	; PORT E
	; output:	3 (keypad col 1)
	; input:	4 and 5 (keypad row 2 and 3)
	LDI				R16,			0x08
	OUT				DDRE,			R16
	; PORT F
	; input:	4 and 5 (keypad row 1 and 0)
	LDI				R16,			0x00
	OUT				DDRF,			R16
	; PORT G
	; output:	5 (keypad col 0)
	LDI				R16,			0x20
	OUT				DDRG,			R16
	RET

init_pins_led:
	SBI				DDRB,			7			; enable LED as output
	RET

;==============================================================================
; Main part of program
; Uses registers:
;	R20				temporary storage of pressed key
;	R24				input / output values
;==============================================================================
main:
	RCALL			lcd_clear					; Clear
	PRINTSTRING		welcome_string				; Print
	RCALL			clear_stat
	LDI				R24,				2		; Delay 2 sekunder
	RCALL			delay_s
	RCALL			lcd_clear					; Clear
	PRINTSTRING		roll_string					; Print
	RJMP			main_loop

;==============================================================================
;	checkkey- checks what key is being pressed
; 
;	Uses registers:
;	R24				input / output values
;==============================================================================
checkkey:
	RCALL			read_keyboard_num				; Read number
	CPI				R24,				4			; Check if 2
	BREQ			rolling

	CPI				R24,				6			; Check if 8
	BREQ			clear__stat

	CPI				R24,				8			; Check if 3
	BREQ			show__stat

	CPI				R24,				10			; Check if 9
	BREQ			monitor__
	RJMP			main_loop	

;==============================================================================
;	rolling - if key 2 was a dice will get rolled.
; 
;	Uses registers:
;	R24				input / output values
;   R24				Temp
;==============================================================================
rolling:
	RCALL			lcd_clear	
	PRINTSTRING		rolling_string
	RCALL			roll_dice						; Roll dice
	RCALL			lcd_clear						; Clear lcd
	PRINTSTRING		value_string					; Print value
	MOV				R24,				R25			; Move value to R24 before printing 
	SUBI			R24,				-0x30		; Convert to ASCII
	LCD_WRITE_CHR									; Print
	MOV				R24,				R25			; Move to R24 again for storing
	RCALL			store_stat						; Store data
	RCALL			roll_again						; Print roll string again
	RJMP			main_loop

clear__stat:
	RCALL			clearstat						; Call routine in stats.inc
	RCALL			roll_again						; Print roll string again
	RJMP			main_loop

monitor__:	
	RCALL			monitor							; Call routine in monitor.inc
	RCALL			roll_again						; Print roll string again
	RJMP			main_loop	
				
show__stat:
	RCALL			showstat						; Call routine in stats.inc
	RCALL			roll_again						; Print roll string again
	RJMP			main_loop

roll_again:
	LDI				R24,				1			; Delay 1 sekund
	RCALL			delay_s
	RCALL			lcd_clear						; Clear
	PRINTSTRING		roll_string						; Print
	RET

;==============================================================================
;	Main loop - polling for key.
; 
;	Uses registers:
;	R25				temporary storage of pressed key
;	R24				input / output values
;==============================================================================
main_loop:
	RJMP			checkkey						; Check key "method"
	RJMP			main_loop						; Loop