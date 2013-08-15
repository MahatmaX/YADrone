package de.yadrone.base.command;

public class ManualTrimCommand extends ATCommand {
	private float pitch;
	private float roll;
	private float yaw;


	public ManualTrimCommand(float pitch, float roll, float yaw) {
		super();
		this.pitch = pitch;
		this.roll = roll;
		this.yaw = yaw;
	}

	@Override
	protected String getID() {
		// TODO Auto-generated method stub
		return "MTRIM";
	}

	@Override
	protected Object[] getParameters() {
		// TODO Auto-generated method stub
		return new Object[] {pitch, roll, yaw};
	}

}
