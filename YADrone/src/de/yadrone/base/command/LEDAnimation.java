package de.yadrone.base.command;

public enum LEDAnimation {
	BLINK_GREEN_RED, BLINK_GREEN, BLINK_RED, BLINK_ORANGE, SNAKE_GREEN_RED, FIRE, STANDARD, RED, GREEN, RED_SNAKE, BLANK, RIGHT_MISSILE, LEFT_MISSILE, DOUBLE_MISSILE, FRONT_LEFT_GREEN_OTHERS_RED, FRONT_RIGHT_GREEN_OTHERS_RED, REAR_RIGHT_GREEN_OTHERS_RED, REAR_LEFT_GREEN_OTHERS_RED, LEFT_GREEN_RIGHT_RED, LEFT_RED_RIGHT_GREEN, BLINK_STANDARD;

	// TODO: refactor to common super class if possible or utility class
	public LEDAnimation getNext() {
		LEDAnimation[] values = LEDAnimation.values();
		return values[(ordinal() + 1) % values.length];
	}
}
