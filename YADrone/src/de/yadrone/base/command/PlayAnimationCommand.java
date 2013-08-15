package de.yadrone.base.command;


/**
 * TODO what happened to this command? It's not described in the manual.
 * Seems to be replaced by Configuration command control:flight_anim
 */
public class PlayAnimationCommand extends ATCommand
{
    protected int   animation_no;
    protected int   duration;

    public PlayAnimationCommand(int animation_no, int duration)
    {
        this.animation_no = animation_no;
        this.duration     = duration;
    }

    @Override
    protected String getID()
    {
        return "ANIM";
    }

    @Override
    protected Object[] getParameters()
    {
        return new Object[] { animation_no, duration };
    }
}
