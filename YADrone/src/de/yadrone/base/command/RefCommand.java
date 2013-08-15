package de.yadrone.base.command;

public class RefCommand extends ATCommand {
	protected int value;

	protected RefCommand(boolean takeoff, boolean emergency) {
		value = (1 << 18) | (1 << 20) | (1 << 22) | (1 << 24) | (1 << 28);

		if (emergency) {
			value |= (1 << 8);
		}

		if (takeoff) {
			value |= (1 << 9);
		}
	}

	/**
	 * Defines if this command clears a previous sticky command
	 */
	@Override
	public boolean clearSticky() {
		return true;
	}

	@Override
	protected String getID() {
		return "REF";
	}

	@Override
	protected Object[] getParameters() {
		return new Object[] { value };
	}

	@Override
	public Priority getPriority() {
		return Priority.HIGH_PRIORITY;
	}
}
