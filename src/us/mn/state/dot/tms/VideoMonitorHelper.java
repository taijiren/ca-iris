/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2009-2012  Minnesota Department of Transportation
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
package us.mn.state.dot.tms;

import java.util.Iterator;

/**
 * Video monitor helper methods.
 *
 * @author Douglas Lau
 */
public class VideoMonitorHelper extends BaseHelper {

	/** Disallow instantiation */
	private VideoMonitorHelper() {
		assert false;
	}

	/** Get a video monitor iterator */
	static public Iterator<VideoMonitor> iterator() {
		return new IteratorWrapper<VideoMonitor>(namespace.iterator(
			VideoMonitor.SONAR_TYPE));
	}
}
