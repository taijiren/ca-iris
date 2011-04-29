/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2009-2011  Minnesota Department of Transportation
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
package us.mn.state.dot.tms.server;

import java.io.PrintWriter;
import us.mn.state.dot.sonar.Checker;
import us.mn.state.dot.tms.Camera;
import us.mn.state.dot.tms.CameraHelper;

/**
 * This class writes out the current camera configuration to an XML file.
 *
 * @author Tim Johnson
 * @author Douglas Lau
 */
public class CameraXmlWriter {

	/** Print the body of the camera list XML file */
	public void print(final PrintWriter out) {
		CameraHelper.find(new Checker<Camera>() {
			public boolean check(Camera cam) {
				if(cam instanceof CameraImpl)
					((CameraImpl)cam).printXml(out);
				return false;
			}
		});
	}
}
