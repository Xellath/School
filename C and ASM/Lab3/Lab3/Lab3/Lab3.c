/*
* Lab3.c
*
* Created: 01/12/2016 12:19:36
*  Author: Ludwig Ninn Alexander Johansson
*/

#include <avr/io.h>
#include <stdio.h>
#include "hmi/hmi.h"
#include "numkey/numkey.h"
#include "regulator/regulator.h"

typedef enum
{
	MOTOR_OFF,
	MOTOR_ON_FORWARD,
	MOTOR_RUNNING_FORWARD,
	MOTOR_ON_BACKWARD,
	MOTOR_RUNNING_BACKWARD
} state_t;

int main(void)
{
	// Key + regulator
	char key = NO_KEY;
	uint8_t regulator = 0;
	// States
	state_t current_state = MOTOR_OFF;
	state_t next_state = MOTOR_OFF;
	
	// Char array holds string
	char regulator_str[15];
	
	hmi_init();
	regulator_init();
	
	while(1)
	{
		// Read key and regulator
		key = numkey_read();
		regulator = regulator_read();
		
		// Switch state
		switch (current_state) {
			case MOTOR_OFF:
				if(key == '0') {
					next_state = MOTOR_OFF;
				} else if(key == '1' && regulator == 0) {
					next_state = MOTOR_ON_BACKWARD;
				} else if(key == '3' && regulator == 0) {
					next_state = MOTOR_ON_FORWARD;
				}
				
				// Format and output to lcd
				output_msg("MOTOR OFF", "", 0);
			break;
			
			case MOTOR_ON_FORWARD:
				if(key == '0') {
					next_state = MOTOR_OFF;
				} else if(regulator > 0) {
					next_state = MOTOR_RUNNING_FORWARD;
				}
				
				// Format and output to lcd
				output_msg("ON FORWARD", "", 0);
			break;
			
			case MOTOR_RUNNING_FORWARD:
				if(key == '0') {
					next_state = MOTOR_OFF;
				}
				
				// Format and output to lcd
				sprintf(regulator_str, "%u%% %s", regulator, ((regulator >= 80) ? "RIGHT" : ""));
				output_msg("RUN FORWARD", regulator_str, 0);
			break;
			
			case MOTOR_ON_BACKWARD:
				if(key == '0') {
					next_state = MOTOR_OFF;
				} else if(regulator > 0) {
					next_state = MOTOR_RUNNING_BACKWARD;
				}
			
				// Format and output to lcd
				output_msg("ON BACKWARD", "", 0);
			break;
			
			case MOTOR_RUNNING_BACKWARD:
				if(key == '0') {
					next_state = MOTOR_OFF;
				}
				
				// Format and output to lcd
				sprintf(regulator_str, "%u%% %s", regulator, ((regulator >= 80) ? "RIGHT" : ""));
				output_msg("RUN BACKWARD", regulator_str, 0);
			break;
		}
		
		// Set next state
		current_state = next_state;
	}
}