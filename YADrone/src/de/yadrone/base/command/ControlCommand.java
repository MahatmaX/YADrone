package de.yadrone.base.command;

/* Very little is documented about the CTRL command and modes
 * See https://github.com/bklang/ARbDrone/wiki/UndocumentedCommands
 * 
 * TODO: ATCommand could support enum types; however, how do we distinguish between ordinal() and getValue()
 * To be checked: could be true that all occurences here use ordinal
 */
public class ControlCommand extends ATCommand {
	protected ControlMode mode;
	protected int arg2;

	public ControlCommand(ControlMode mode, int arg2) {
		this.mode = mode;
		this.arg2 = arg2;
	}

	@Override
	protected String getID() {
		return "CTRL";
	}

	@Override
	protected Object[] getParameters() {
		return new Object[] { mode.ordinal(), arg2 };
	}

}
