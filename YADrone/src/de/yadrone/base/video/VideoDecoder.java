package de.yadrone.base.video;

import java.io.InputStream;

public interface VideoDecoder {

    public void decode(InputStream is);
	public void setImageListener(ImageListener listener);

}
