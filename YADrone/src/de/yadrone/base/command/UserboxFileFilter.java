package de.yadrone.base.command;

import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilter;

final class UserboxFileFilter implements FTPFileFilter {
	@Override
	public boolean accept(FTPFile f) {
		int t = f.getType();
		String nm = f.getName();
		return t == FTPFile.DIRECTORY_TYPE && nm.startsWith("flight_") || nm.startsWith("tmp_flight_");
	}
}