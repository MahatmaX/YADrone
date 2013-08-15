package de.yadrone.base.command;

public class PCMDMagCommand extends PCMDCommand {
	protected float magneto_psi;
	protected float magneto_psi_accuracy;

	public PCMDMagCommand(boolean hover, boolean combined_yaw_enabled, boolean absolutecontrol, float left_right_tilt,
			float front_back_tilt, float vertical_speed, float angular_speed, float magneto_psi,
			float magneto_psi_accuracy) {
		super(hover, combined_yaw_enabled, left_right_tilt, front_back_tilt, vertical_speed, angular_speed);
		this.magneto_psi = magneto_psi;
		this.magneto_psi_accuracy = magneto_psi_accuracy;
		if (absolutecontrol) {
			mode |= (1 << 2);
		}

	}

	@Override
	protected String getID() {
		return "PCMD_MAG";
	}

	@Override
	protected Object[] getParameters() {
		return new Object[] { mode, left_right_tilt, front_back_tilt, vertical_speed, angular_speed, magneto_psi,
				magneto_psi_accuracy };
	}
}