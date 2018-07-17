/*
 * IncFile1.inc
 *
 *  Created: 15/12/2016 14:19:10
 *  Author: Ludwig Ninn, Alexander Johansson
 */

;==============================================================================
; Tarning.inc
; 
; Parameters IN: 
;				R24: contains number.
;				R16 contains the dice value on return
;Parameters OUT: 
;				R24: Value of dicce
;
;==============================================================================


roll_dice:
	
	LDI			R25, 6							;dice have 6 values
	test:		NOP
				NOP
	RCALL		read_keyboard_num	
	CPI			R24,		 4 
	BREQ roll											
	RET								;no, key is released
	roll:		
	DEC			R25
	BREQ		roll_dice						;R16 is zero, start agn at 6
	RJMP		test							;no, keep rolling