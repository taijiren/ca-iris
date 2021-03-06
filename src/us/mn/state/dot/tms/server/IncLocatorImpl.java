/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2016  Minnesota Department of Transportation
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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import us.mn.state.dot.tms.ChangeVetoException;
import us.mn.state.dot.tms.IncLocator;
import us.mn.state.dot.tms.SignGroup;
import us.mn.state.dot.tms.TMSException;
import us.mn.state.dot.tms.utils.MultiString;

/**
 * An incident locator is part of a message to deploy on a DMS, matching
 * incident attributes.
 *
 * @author Douglas Lau
 */
public class IncLocatorImpl extends BaseObjectImpl implements IncLocator {

	/** Load all the incident locators */
	static protected void loadAll() throws TMSException {
		namespace.registerType(SONAR_TYPE, IncLocatorImpl.class);
		store.query("SELECT name, sign_group, range, branched, " +
			"pickable, multi FROM iris." + SONAR_TYPE + ";",
			new ResultFactory()
		{
			public void create(ResultSet row) throws Exception {
				namespace.addObject(new IncLocatorImpl(row));
			}
		});
	}

	/** Get a mapping of the columns */
	@Override
	public Map<String, Object> getColumns() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("name", name);
		map.put("sign_group", sign_group);
		map.put("range", range);
		map.put("branched", branched);
		map.put("pickable", pickable);
		map.put("multi", multi);
		return map;
	}

	/** Get the database table name */
	@Override
	public String getTable() {
		return "iris." + SONAR_TYPE;
	}

	/** Get the SONAR type name */
	@Override
	public String getTypeName() {
		return SONAR_TYPE;
	}

	/** Create a new incident locator */
	private IncLocatorImpl(ResultSet row) throws SQLException {
		this(row.getString(1),		// name
		     row.getString(2),		// sign_group
		     row.getInt(3),		// range
		     row.getBoolean(4),		// branched
		     row.getBoolean(5),		// pickable
		     row.getString(6)		// multi
		);
	}

	/** Create a new incident locator */
	private IncLocatorImpl(String n, String sg, int r, boolean b, boolean p,
		String m)
	{
		super(n);
		sign_group = lookupSignGroup(sg);
		range = r;
		branched = b;
		pickable = p;
		multi = m;
	}

	/** Create a new incident locator */
	public IncLocatorImpl(String n) {
		super(n);
	}

	/** Sign group */
	private SignGroup sign_group;

	/** Set the sign group */
	@Override
	public void setSignGroup(SignGroup sg) {
		sign_group = sg;
	}

	/** Set the sign group */
	public void doSetSignGroup(SignGroup sg) throws TMSException {
		if (sg != sign_group) {
			store.update(this, "sign_group", sg);
			setSignGroup(sg);
		}
	}

	/** Get the sign group */
	@Override
	public SignGroup getSignGroup() {
		return sign_group;
	}

	/** Range ordinal */
	private int range;

	/** Set the range */
	@Override
	public void setRange(int r) {
		range = r;
	}

	/** Set the range */
	public void doSetRange(int r) throws TMSException {
		if (r != range) {
			store.update(this, "range", r);
			setRange(r);
		}
	}

	/** Get the range */
	@Override
	public int getRange() {
		return range;
	}

	/** Branched flag */
	private boolean branched;

	/** Set the branched flag */
	@Override
	public void setBranched(boolean b) {
		branched = b;
	}

	/** Set the branched flag */
	public void doSetBranched(boolean b) throws TMSException {
		if (b != branched) {
			store.update(this, "branched", b);
			setBranched(b);
		}
	}

	/** Get the branched flag */
	@Override
	public boolean getBranched() {
		return branched;
	}

	/** Pickable flag */
	private boolean pickable;

	/** Set the pickable flag */
	@Override
	public void setPickable(boolean p) {
		pickable = p;
	}

	/** Set the pickable flag */
	public void doSetPickable(boolean p) throws TMSException {
		if (p != pickable) {
			store.update(this, "pickable", p);
			setPickable(p);
		}
	}

	/** Get the pickable flag */
	@Override
	public boolean getPickable() {
		return pickable;
	}

	/** MULTI string */
	private String multi = "";

	/** Set the MULTI string */
	@Override
	public void setMulti(String m) {
		multi = m;
	}

	/** Set the MULTI string */
	public void doSetMulti(String m) throws TMSException {
		// FIXME: allow true MULTI tags plus locator tags
		if (!new MultiString(m).isValid())
			throw new ChangeVetoException("Invalid MULTI: " + m);
		if (!m.equals(multi)) {
			store.update(this, "multi", m);
			setMulti(m);
		}
	}

	/** Get the MULTI string */
	@Override
	public String getMulti() {
		return multi;
	}
}
