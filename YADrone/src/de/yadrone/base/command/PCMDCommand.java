package de.yadrone.base.command;

public class PCMDCommand extends ATCommand {
	protected boolean hover;
	protected boolean combined_yaw_enabled;
	protected float left_right_tilt;
	protected float front_back_tilt;
	protected float vertical_speed;
	protected float angular_speed;
	protected int mode;

	/*
	 * TODO There is a small design flaw in here: PCMD is a generic version of Hover, Stop, and Move so it's fine to
	 * have hover and combined_yaw_enabled as parameters however, PCMDMag is an extension of PCMD that also allows
	 * absolute control (for example) Either PCMD should also generalize that concept, or PCMD becomes an abstract
	 * command that is extended by Stop, Hover and Move
	 */
	public PCMDCommand(boolean hover, boolean combined_yaw_enabled, float left_right_tilt, float front_back_tilt,
			float vertical_speed, float angular_speed) {
		super();
		this.hover = hover;
		this.combined_yaw_enabled = combined_yaw_enabled;
		this.left_right_tilt = left_right_tilt;
		this.front_back_tilt = front_back_tilt;
		this.vertical_speed = vertical_speed;
		this.angular_speed = angular_speed;
		this.mode = 0;

		if (!hover) {
			mode |= (1 << 0);
		}

		if (combined_yaw_enabled) {
			mode |= (1 << 1);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.yadrone.base.command.DroneCommand#isSticky()
	 */
	@Override
	public boolean isSticky() {
		return (mode & 1) != 0
				&& (left_right_tilt != 0f || front_back_tilt != 0f || vertical_speed != 0f || angular_speed != 0f);
	}

	@Override
	protected String getID() {
		return "PCMD";
	}

	@Override
	protected Object[] getParameters() {
		return new Object[] { mode, left_right_tilt, front_back_tilt, vertical_speed, angular_speed };
	}
}
