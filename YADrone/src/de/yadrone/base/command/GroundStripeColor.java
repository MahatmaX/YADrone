package de.yadrone.base.command;

public enum GroundStripeColor {
	ORANGE_GREEN(0x10), YELLOW_BLUE(0x11);
	private int value;

	private GroundStripeColor(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}