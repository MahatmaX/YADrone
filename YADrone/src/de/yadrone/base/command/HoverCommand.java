package de.yadrone.base.command;

public class HoverCommand extends PCMDCommand {
	public HoverCommand() {
		super(true, false, 0f, 0f, 0f, 0f);
	}

	/**
	 * Defines if this command clears a previous sticky command
	 */
	@Override
	public boolean clearSticky() {
		return true;
	}

}
