package de.yadrone.base.command;

public class FlightAnimationCommand extends ConfigureCommand {

	public FlightAnimationCommand(FlightAnimation anim, int duration) {
		super("control:flight_anim", String.valueOf(anim.ordinal()) + "," + String.valueOf(duration));
	}

	public FlightAnimationCommand(FlightAnimation anim) {
		this(anim, anim.getDefaultDuration());
	}
}
