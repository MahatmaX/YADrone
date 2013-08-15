
package de.yadrone.base.command;

import java.io.UnsupportedEncodingException;

public abstract class ATCommand extends DroneCommand
{
    private String encodeParameter(Object p)
    {
        if(p instanceof Integer)
            return p.toString();

        if(p instanceof Float)
            return Integer.toString(Float.floatToIntBits((Float) p));

        if(p instanceof String)
            return "\"" + p + "\"";

        throw new IllegalArgumentException("Unsupported parameter type: " + p.getClass().getName() + " " + p);
    }

    public String getCommandString(int seq)
    {
        return "AT*" + getID() + "=" + seq + getParametersString() + "\r";
    }

    protected abstract String getID();

    public byte[] getPacket(int seq)
    {
        try
        {
            return getCommandString(seq).getBytes("ASCII");
        } catch(UnsupportedEncodingException e)
        {
            // never happens
            return null;
        }
    }

    protected abstract Object[] getParameters();

    private String getParametersString()
    {
        StringBuffer sb = new StringBuffer();
        for(Object p : getParameters())
        {
            sb.append(',').append(encodeParameter(p));
        }

        return sb.toString();
    }

    @Override
    public Priority getPriority()
    {
        return Priority.MIN_PRIORITY;
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append(getClass().getSimpleName());
        builder.append(" [ID=");
        builder.append(getID());
        builder.append(", param=");
        builder.append(getParametersString());
        builder.append("]");
        return builder.toString();
    }

    public boolean equals(Object obj)
    {
        if(obj == null || !(obj instanceof ATCommand))
            return false;
        ATCommand o = (ATCommand) obj;
        return o.getCommandString(0).equals(getCommandString(0));
    }

	public boolean needControlAck() {
		return false;
	}
	
}
