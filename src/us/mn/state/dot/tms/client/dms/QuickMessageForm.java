/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2009  Minnesota Department of Transportation
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

import us.mn.state.dot.tms.QuickMessage;
import us.mn.state.dot.tms.client.Session;
import us.mn.state.dot.tms.client.proxy.ProxyTableForm;

/**
 * A form for displaying and editing quick messages.
 * @see QuickMessage, QuickMessageImpl
 *
 * @author Michael Darter
 * @author Doug Lau
 */
public class QuickMessageForm extends ProxyTableForm<QuickMessage> {

	/** Create a new quick message form.
	 * @param s Session. */
	public QuickMessageForm(Session s) {
		super("Quick Messages", new QuickMessageTableModel(s));
	}

	/** Get the row height */
	protected int getRowHeight() {
		return 20;
	}

	/** Get the visible row count */
	protected int getVisibleRowCount() {
		return 12;
	}
}

