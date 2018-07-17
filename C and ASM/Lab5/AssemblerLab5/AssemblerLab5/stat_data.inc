/*
 * IncFile1.inc
 *
 *  Created: 15/12/2016 17:17:51
 *   Author: ludwig & Alexander
 */ 


;==============================================================================
;Space in the RAM to save the results from dice throws.
;The following functions are provided:
;
;store_stat (R24 contains the dice value) The function will increment the total number of throws and the
;number of throws with results equals R24. 
;
;get_stat (R24 gives the value for which the result is retrieved. If R24 = 0, the total number of throws shall be returned.
;The result is returned in R24 clear_stat (no parameters in nor out) Clears all data.
;==============================================================================		

.DSEG								; The following applies to the RAM:
.ORG		 0x200					; Set starting point
									; address of data
									; segment to 0x200
throws_nbr: .BYTE 7					; Create space
.CSEG

;==============================================================================
;	store_stat 
;	The function will increment the Total number of throws and the
;	number of throws with results equals R24.
;
;	Uses registers:
;	R24			contains the dice value
;==============================================================================								
store_stat: 
	LDI			ZH,				HIGH(throws_nbr)			; Get Z pointers
    LDI			ZL,				LOW(throws_nbr)
	LD			R25,			Z							; Load R25 with Z value
	INC			R25											; Increment
	ST			Z,				R25							; Set value
	ADD			ZL,				R24							; Increase pointer with value of dice
	LD			R25,			Z							; Load from next Z-pointer
	INC			R25											; Increment
	ST			Z,				R25							; Store R25 in Z register
	
	RET
	
;==============================================================================
;	get_stat
; 
;	If R24 = 0, the total number of throws shall be returned.
;	The result is returned in R24.
; 
;	Uses registers:
;	R24			R24 gives the value for which the result is retrieved
;==============================================================================	
get_stat:
	LDI			ZH,				HIGH(throws_nbr)			; Get Z pointers
    LDI			ZL,				LOW(throws_nbr)
	ADD			ZL,				R24							; Add position to Z-pointer
	LD			R24,			Z							; Load
	RET

;==============================================================================
;	clear_stat -
; 
;	Uses registers:
;	no parameters in nor out
;==============================================================================	
clear_stat: 
	LDI			R25,			0							; Load R25 with 0
	STS			throws_nbr,		R25
	STS			throws_nbr+1,	R25
	STS			throws_nbr+2,	R25		
	STS			throws_nbr+3,	R25
	STS			throws_nbr+4,	R25
	STS			throws_nbr+5,	R25
	STS			throws_nbr+6,	R25

	RET