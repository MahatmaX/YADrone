package de.yadrone.base.command;

//TODO: find out of there are undocumented devices that can be calibrated
public class CalibrationCommand extends ATCommand {
	protected Device device;

	/**
	 * This command asks the drone to calibrate the drone magnetometer. This command MUST be sent when the AR.Drone is
	 * flying. When receiving this command, the drone will automatically calibrate its magnetometer by spinning around
	 * itself for some time.
	 * 
	 * @param device
	 *            The device to calibrate
	 */
	public CalibrationCommand(Device device) {
		this.device = device;
	}

	@Override
	protected String getID() {
		return "CALIB";
	}

	@Override
	protected Object[] getParameters() {
		return new Object[] { device.ordinal() };
	}
}
