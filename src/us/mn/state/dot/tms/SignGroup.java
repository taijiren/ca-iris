/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2008-2015  Minnesota Department of Transportation
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

import us.mn.state.dot.sonar.SonarObject;

/**
 * A sign group is an arbitrary group of dynamic message signs (DMS).
 *
 * @author Douglas Lau
 */
public interface SignGroup extends SonarObject {

	/** SONAR type name */
	String SONAR_TYPE = "sign_group";

	/** Is the group local to one sign? */
	boolean getLocal();

	/** Set the hidden flag */
	void setHidden(boolean h);

	/** Get the hidden flag */
	boolean getHidden();
}
