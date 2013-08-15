package de.yadrone.base.command;

public enum EnemyColor {
	ORANGE_GREEN(1), ORANGE_YELLOW(2), ORANGE_BLUE(3), ARRACE_FINISH_LINE(0x10), ARRACE_DONUT(0x11);
	private int value;

	private EnemyColor(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}