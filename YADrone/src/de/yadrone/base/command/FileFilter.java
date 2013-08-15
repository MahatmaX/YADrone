package de.yadrone.base.command;

import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilter;

class FileFilter implements FTPFileFilter {

	@Override
	public boolean accept(final FTPFile f) {
		return f.getType() == FTPFile.FILE_TYPE;
	}
}