/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2009-2013  Minnesota Department of Transportation
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

import junit.framework.TestCase;
import us.mn.state.dot.tms.DmsPgTime;
import us.mn.state.dot.tms.units.Interval;

/**
 * DmsPgTime test cases
 *
 * @author Michael Darter
 * @author Douglas Lau
 */
public class DmsPgTimeTest extends TestCase {

	/** constructor */
	public DmsPgTimeTest(String name) {
		super(name);
	}

	public void testValidateOnInterval() {
		assertTrue(DmsPgTime.validateOnInterval(new Interval(0), true).
			equals(DmsPgTime.defaultPageOnInterval(true)));
		assertTrue(DmsPgTime.validateOnInterval(new Interval(0), false).
			equals(DmsPgTime.minPageOnInterval()));
	}

	public void testValidateValue() {
		// single page
		assertTrue(new Interval(0).equals(
			DmsPgTime.validateValue(new Interval(-3), true,
			new Interval(.5), new Interval(10.0))));
		assertTrue(new Interval(0).equals(
			DmsPgTime.validateValue(new Interval(-3), true,
			new Interval(.5), new Interval(10.0))));
		assertTrue(new Interval(0).equals(
			DmsPgTime.validateValue(new Interval(0), true,
			new Interval(.5), new Interval(10.0))));
		assertTrue(new Interval(0).equals(
			DmsPgTime.validateValue(new Interval(.4), true,
			new Interval(.5), new Interval(10.0))));
		assertTrue(new Interval(2.6).equals(
			DmsPgTime.validateValue(new Interval(2.6), true,
			new Interval(.5), new Interval(10.0))));
		assertTrue(new Interval(10.0).equals(
			DmsPgTime.validateValue(new Interval(12.0), true,
			new Interval(.5), new Interval(10.0))));

		// multi page
		assertTrue(new Interval(.5).equals(
			DmsPgTime.validateValue(new Interval(-3.3), false,
			new Interval(.5), new Interval(10.0))));
		assertTrue(new Interval(.5).equals(
			DmsPgTime.validateValue(new Interval(0), false,
			new Interval(.5), new Interval(10.0))));
		assertTrue(new Interval(.5).equals(
			DmsPgTime.validateValue(new Interval(.4), false,
			new Interval(.5), new Interval(10.0))));
		assertTrue(new Interval(2.6).equals(
			DmsPgTime.validateValue(new Interval(2.6), false,
			new Interval(.5), new Interval(10.0))));
		assertTrue(new Interval(10.0).equals(
			DmsPgTime.validateValue(new Interval(12.0), false,
			new Interval(.5), new Interval(10.0))));
	}
}
