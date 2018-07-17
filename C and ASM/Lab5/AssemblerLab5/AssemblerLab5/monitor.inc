/*
 * monitor.inc
 *
 *  Created: 2016-11-19 12:42:26
 *   Author: Magnus Krampell
 */ 

 ;==============================================================================
 ; Init
 ;	moni_start_str: .DB ">0000: ",0
 ;==============================================================================
 monitorstr: .DB "MONITOR",0	
 init_monitor:
	RET
;==============================================================================
; read keys from keyboard. If * - exit, if # advance address. if number - use as XH.
;==============================================================================
moni_read_keyboard:
	RCALL read_keyboard_num
	CPI		R24, 0x03					; '*'?
	BREQ monireadquit					; yes, go back
	CPI		R24, 0x0B					; '#'?
	BREQ monireadquit					; yes, go back
	CPI		R24, NO_KEY
	BRNE	moni_akey					; a key, which one?
	MOV		R3, R24						; save in case another key was pressed before this
	RJMP	moni_read_keyboard
moni_akey:								; some key is pressed
//rcall printhex	
	CP		R24, R3
	BREQ	moni_read_keyboard			; repeat key, skip...
	MOV		R3, R24						; no repeat, save new key to detect repeat next time
										; ok, accept only one key, for now....
monireadquit:
	RET

moni_print_address:
	LDI R24, '>'
	LCD_WRITE_CHR
	MOV R24, XH
	RCALL printhex
	MOV R24, XL
	RCALL printhex
	LDI R24, ':'
	LCD_WRITE_CHR
	RET
;==============================================================================
; monitor displays memory content on the format:
;	0x0100: 0xAB	The cell '0100' (hex) contains 'AB' (hex)
;	the button # means "display the next cell"
;	the button * means "go back to the main menu"
;==============================================================================
monitor:
	RCALL lcd_clear
	PRINTSTRING monitorstr
	RCALL delay_1_s
	LDI	XH,	0x00						; set start value
	LDI	XL,	0x00
moni_next:
	RCALL lcd_clear
	RCALL moni_print_address
	LD	R24, X+							; get memory value where X points...
	RCALL printhex						; print that value on LCD
	RCALL moni_read_keyboard			; returns with "a real key", not NO_KEY!
	CPI		R24, 0x03					; '*'?
	BREQ moniquit						; yes, quit
	CPI		R24, 0x0B					; '#'?
	BREQ moni_more
										; some number was returned. Use it in the X register
	RCALL convert_to_ASCII				; map key to corresponding number.....
	SUBI R24, 0x30						; convert from ASCII to number
	MOV	XH, R24							; use number as high start address
	CLR	XL								; set XL to 00
	RJMP moni_next

moni_more:								; continue and display next address
										; X is already pointing at the next address...
	LDI		R24, 200
	RCALL	delay_ms
	RJMP moni_next
	

moniquit:
	RET