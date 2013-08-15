package de.yadrone.base.command;

public class KeepAliveCommand extends ATCommand {

	@Override
	protected String getID() {
		return "COMWDG";
	}

	@Override
	protected Object[] getParameters() {
		return new Object[] {};
	}

	@Override
	public Priority getPriority() {
		return Priority.VERY_HIGH_PRIORITY;
	}
}
