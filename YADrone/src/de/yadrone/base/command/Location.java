package de.yadrone.base.command;

public enum Location {
	INDOOR("indoor_"), CURRENT(""), OUTDOOR("outdoor_");

	private String commandPrefix;

	private Location(String commandPrefix) {
		this.commandPrefix = commandPrefix;
	}

	/**
	 * @return the commandPrefix
	 */
	public String getCommandPrefix() {
		return commandPrefix;
	}

}
