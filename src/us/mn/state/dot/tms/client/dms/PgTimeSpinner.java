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
package us.mn.state.dot.tms.client.dms;

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import us.mn.state.dot.tms.DmsPgTime;
import us.mn.state.dot.tms.MultiString;
import us.mn.state.dot.tms.SystemAttrEnum;
import us.mn.state.dot.tms.utils.I18N;
import us.mn.state.dot.tms.utils.Log;

/**
 * A spinner for the DMS message page time.
 * @see DmsPgTime, SignMessageComposer, SystemAttributeForm
 * @author Michael Darter
 */
public class PgTimeSpinner extends JSpinner
{
	public static final float INC_ONTIME_SECS = .1f;

	/** constructor */
	public PgTimeSpinner() {
		setModel(new SpinnerNumberModel(
			DmsPgTime.getDefaultOn().toSecs(), 
			DmsPgTime.MIN_ONTIME.toSecs(), 
			DmsPgTime.MAX_ONTIME.toSecs(), 
			INC_ONTIME_SECS));
		setToolTipText(I18N.get("PgOnTimeSpinner.ToolTip"));
		setMaximumSize(getMinimumSize());
	}

	/** is this control IRIS enabled? */
	public static boolean getIEnabled() {
		return SystemAttrEnum.
			DMS_PGONTIME_SELECTION_ENABLE.getBoolean();
	}

	/** enable or disable */
	public void setEnabled(boolean b) {
		super.setEnabled(b);
		// if disabled, reset value to default
		if(!b)
			setValue(DmsPgTime.getDefaultOn().toSecs());
	}

	/** Set value using seconds. */
	public void setValue(float secs) {
		super.setValue(new DmsPgTime(secs).toSecs());
	}

	/** Set value using seconds. */
	public void setValue(DmsPgTime t) {
		super.setValue(t.toSecs());
	}

	/** Get the current value */
	public DmsPgTime getValuePgTime() {
		Object v = super.getValue();
		if(v instanceof Number)
			return new DmsPgTime(((Number)v).floatValue());
		assert false;
		return DmsPgTime.getDefaultOn();
	}

	/** Set value using the page-on time specified in the 1st page 
	 *  of the MULTI string.
	 *  @param smulti A MULTI string, containing possible page times. */
	public void setValue(String smulti) {
		MultiString m = new MultiString(smulti);
		int[] ponts = m.getPageOnTime(DmsPgTime.getDefaultOn().toTenths());
		setValue(ponts.length > 0 ? new DmsPgTime(ponts[0]) : 
			DmsPgTime.getDefaultOn());
	}

	/** dispose */
	public void dispose() {
	}
}
