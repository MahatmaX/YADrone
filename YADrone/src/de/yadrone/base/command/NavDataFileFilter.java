package de.yadrone.base.command;

import org.apache.commons.net.ftp.FTPFile;

public class NavDataFileFilter extends FileFilter {

	@Override
	public boolean accept(FTPFile f) {
		final String nm = f.getName();
		return super.accept(f) && nm.startsWith("userbox_");
	}

}
