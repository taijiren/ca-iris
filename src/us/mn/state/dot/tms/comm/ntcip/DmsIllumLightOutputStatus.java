/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2000-2009  Minnesota Department of Transportation
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
package us.mn.state.dot.tms.comm.ntcip;

/**
 * Ntcip DmsIllumLightOutputStatus object
 *
 * @author Douglas Lau
 */
public class DmsIllumLightOutputStatus extends Illum implements ASN1Integer {

	/** Create a new DmsIllumLightOutputStatus object */
	public DmsIllumLightOutputStatus() {
		super(9);
	}

	/** Get the object name */
	protected String getName() {
		return "dmsIllumLightOutputStatus";
	}

	/** Light output status */
	protected int light;

	/** Set the integer value */
	public void setInteger(int value) {
		light = value;
	}

	/** Get the integer value */
	public int getInteger() {
		return light;
	}

	/** Get the object value */
	public String getValue() {
		return String.valueOf(light);
	}

	/** Get the light output as percent */
	public int getPercent() {
		return Math.round(light / 655.35f);
	}
}
