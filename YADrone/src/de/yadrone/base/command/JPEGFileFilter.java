package de.yadrone.base.command;

import org.apache.commons.net.ftp.FTPFile;

final class JPEGFileFilter extends FileFilter {

	// TODO: refactor into utility class
	public static boolean endsWithIgnoreCase(final String s, final String suffix) {
		final int l1 = s.length();
		final int l2 = suffix.length();
		final String ext = s.substring(l1 - l2);
		return suffix.equalsIgnoreCase(ext);
	}

	@Override
	public boolean accept(final FTPFile f) {
		final String nm = f.getName();
		return super.accept(f) && endsWithIgnoreCase(nm, ".jpg");
	}
}