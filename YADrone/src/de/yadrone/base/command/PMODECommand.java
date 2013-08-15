package de.yadrone.base.command;

public class PMODECommand extends ATCommand {
	protected int mode;

	public PMODECommand(int mode) {
		this.mode = mode;
	}

	@Override
	protected String getID() {
		return "PMODE";
	}

	@Override
	protected Object[] getParameters() {
		return new Object[] { mode };
	}

	@Override
	public Priority getPriority() {
		return Priority.MAX_PRIORITY;
	}

}
