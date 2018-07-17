/*
 * lab4.asm
 *
 * This program scans the keypad of the "iBridge keypad shield", and prints
 * the corresponding character of the key on a Nokia 5110 LCD, which is
 * mounted on the shield.
 * This program is written for the Arduino Mega board (ATmega2560).
 *
 * For registers in the Extended I/O space, STS must be used instead of
 * OUT, CBI, SBI. The macros SET_IO_BIT and CLR_IO_BIT is written for
 * the purpose of simplifying setting/clearing a specific bit in a I/O
 * register.
 *
 * Created by Mathias Beckius, 25 June 2015, for the course DA346A at
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
	.INCLUDE		"delay.inc"
	.INCLUDE		"lcd.inc"					; avkommenteras i moment 3
	.INCLUDE		"keyboard.inc"				; avkommenteras i moment 5
	Low_string: .DB "PRESS KEY:",0,0

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
	RCALL			lcd_init						; Initialize LCD - avkommenteras i moment 3
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
/*
	; UPPGIFT: skriv kod här för moment 1 och 2:
	; Gör så att lampan blinkar med delay
	SBI				PORTB,			7
	LDI			    R24,			200
	RCALL			delay_ms
	LDI			    R24,			200
	RCALL			delay_ms
	CBI				PORTB,			7
	LDI			    R24,			200
	RCALL			delay_ms
	LDI			    R24,			200
	RCALL			delay_ms
	RJMP			main



	; UPPGIFT: skriv kod här för moment 3
	; Skriver HELLO på första raden på LCDn, clearar efter 1 sekund, skriver ASDFG, börjar på ny rad
	LCD_WRITE_CMD	0x80 ;Set position: col 0 
	LCD_WRITE_CMD	0x40 ; row 0 
	LDI			R24,			'H' 
	LCD_WRITE_CHR 
	LDI			R24,			'E'
	LCD_WRITE_CHR 
	LDI			R24,			'L' 
	LCD_WRITE_CHR
	LDI			R24,			'L' 
	LCD_WRITE_CHR 
	LDI			R24,			'O' 
	LCD_WRITE_CHR 
	LCD_WRITE_CMD 0x80 ;Set position: col 0 
	LCD_WRITE_CMD 0x41 ; row 1 

	LDI			    R24,		1
	RCALL			delay_s
	
	RCALL			lcd_clear

	LDI			R24,			'A' 
	LCD_WRITE_CHR 
	LDI			R24,			'S'
	LCD_WRITE_CHR 
	LDI			R24,			'D' 
	LCD_WRITE_CHR
	LDI			R24,			'F' 
	LCD_WRITE_CHR 
	LDI			R24,			'G' 
	LCD_WRITE_CHR 
	LCD_WRITE_CMD 0x80 ;Set position: col 0 
	LCD_WRITE_CMD 0x41 ; row 1 
	

; Ladda hög och låg av Low_string
LDI				ZH,				HIGH(Low_string<<1)
LDI				ZL,				LOW(Low_string<<1)
*/
; Stycket clearar och skriver ut "ENTER..." på första raden, sätter cursor till andra rad
clear:
	RCALL		lcd_clear
	PRINTSTRING	Low_string
	LCD_WRITE_CMD 0x80 ;Set position: col 0 
	LCD_WRITE_CMD 0x41 

; Laddar R24, 25 med NO_KEY
LDI			R24,				NO_KEY
LDI			R25,				NO_KEY

main_loop:
	; Läser ny key
	while:
		RCALL		read_keyboard_num
		CP			R25,			R24			; Jämför R24 med R25			
		BREQ		while						; Återgå till att läsa 

	MOV			R25,				R24			; Flytta värdet av R24 till R25 för att kunna jämföra
	RCALL		convert_to_ASCII				; Konvertera till ASCII
	CPI			R24,				NO_KEY		; Kontrollera att R24 inte är NO_KEY
	BREQ		main_loop						; Återgå om sant
	LCD_WRITE_CHR								; Skriv ut char
	RJMP		main_loop						; Loop

	





	


 


