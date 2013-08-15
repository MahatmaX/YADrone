package de.yadrone.base.command;

public class ConfigureCommand extends ATCommand {
	protected String name;
	protected String value;

	public ConfigureCommand(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public ConfigureCommand(String name, int value) {
		this(name, String.valueOf(value));
	}

	public ConfigureCommand(String name, long l) {
		this(name, String.valueOf(l));
	}

	public ConfigureCommand(String name, double d) {
		this(name, Double.doubleToLongBits(d));
	}

	public ConfigureCommand(String name, boolean b) {
		this(name, String.valueOf(b));
	}

	@Override
	protected String getID() {
		return "CONFIG";
	}

	@Override
	protected Object[] getParameters() {
		return new Object[] { name, value };
	}

	@Override
	public boolean needControlAck() {
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ConfigureCommand [name=");
		builder.append(name);
		builder.append(", value=");
		builder.append(value);
		builder.append(", qorder=");
		builder.append(qorder);
		builder.append("]");
		return builder.toString();
	}


}
