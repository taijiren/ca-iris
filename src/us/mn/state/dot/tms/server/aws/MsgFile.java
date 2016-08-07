/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2011-2015  AHMCT, University of California
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package us.mn.state.dot.tms.server.aws;

import java.net.URL;

import us.mn.state.dot.tms.DMSHelper;
import us.mn.state.dot.tms.SystemAttrEnum;
import us.mn.state.dot.tms.server.DMSImpl;
import us.mn.state.dot.tms.utils.MultiString;
import us.mn.state.dot.tms.utils.SFile;
import us.mn.state.dot.tms.utils.STime;

/**
 * This class contains convenience methods for writing the real-time
 * message file, which is used by the client to display the AWS messages
 * generated by the server.
 * The long-term solution would be to use SONAR for this instead.
 *
 * @author Michael Darter
 * @author Travis Swanston
 */
public class MsgFile {

	static private final String BASEPATH = "/var/www/html";

	/** Private constructor -- do not instantiate. */
	private MsgFile() {
	}

	/**
	 * Get the AWS message file name, which is specified in a system
	 * attribute as a URL.
	 * @return the file name for AWS messages, or the empty string on error.
	 */
	static protected String getAwsMsgFileName() {
		String surl = SystemAttrEnum.DMS_AWS_MSG_FILE_URL.getString();
		if (surl == null || surl.isEmpty())
			return "";
		URL url;
		try {
			url = new URL(surl);
			String fname = url.getFile();
			return (fname.isEmpty() ? ""
				: (BASEPATH + "/" + url.getFile()));
		}
		catch(Exception ex) {
			return "";
		}
	}

	/**
	 * Update the real-time AWS message file.
	 * @param di the associated DMSimpl
	 * @param multi the AWS MULTI string
	 */
	static protected void writeToMsgFile(DMSImpl di, MultiString multi) {
		String fname = getAwsMsgFileName();
		if (fname.isEmpty())
			return;
		String msg = STime.getCurTimeShortString() + ";" + di + ";";
		if (multi.isBlank())
			msg += ";;;;;;";
		else {
			//FIXME CA-MN-MERGE changed here, see if correct
			String[] lines = new String[0];
			if (multi != null)
				lines = multi.getLines(
					DMSHelper.getLineCount(di), "");
			for (int i = 0; i < 6; ++i) {
				String l = (i < lines.length ? lines[i] : "");
				msg += l + ";";
			}
		}
		if (!SFile.writeLineToFile(fname, msg, true))
			AwsJob.logsevere("Unable to write to file \"" + fname + "\"");
	}

	/** Delete the current message file */
	static protected void clean() {
		String fname = getAwsMsgFileName();
		SFile.delete(fname);
	}

}
