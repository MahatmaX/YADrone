package de.yadrone.base.command;

public class RawCaptureCommand extends ATCommand {

	private int picture;
	private int video;

	public RawCaptureCommand(boolean picture, boolean video) {
		super();
		this.picture = picture ? 1 : 0;
		this.video = video ? 1 : 0;
	}

	@Override
	protected String getID() {
		return "CAP";
	}

	@Override
	protected Object[] getParameters() {
		return new Object[] { picture, video };
	}

}
