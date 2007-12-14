/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2000-2007  Minnesota Department of Transportation
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
package us.mn.state.dot.tms.client.incidents;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import us.mn.state.dot.map.MapObject;
import us.mn.state.dot.map.Outline;
import us.mn.state.dot.map.Style;
import us.mn.state.dot.map.StyledTheme;
import us.mn.state.dot.sonar.client.TypeCache;
import us.mn.state.dot.tms.SystemPolicy;
import us.mn.state.dot.trafmap.IncidentLayer;

/**
 * Theme for directional (arrow) incidents
 *
 * @author Erik Engstrom
 * @author Douglas Lau
 */
public class DirectionalIncidentTheme extends StyledTheme {

	/** Number of meters per mile */
	static protected final float METERS_PER_MILE = 1609.344f;

	/** Default ring distances (miles) */
	static protected final int[] RING_DEFAULTS = { 2, 5, 10 };

	/** Stroke for rings */
	static protected final Stroke STROKE = new BasicStroke(25);

	/** Create ellipses for the given ring array */
	static protected Ellipse2D[] createEllipses(int[] r) {
		Ellipse2D[] e = new Ellipse2D[r.length];
		for(int i = 0; i < r.length; i++) {
			double m = r[i] * METERS_PER_MILE;
			e[i] = new Ellipse2D.Double(-m, -m, 2 * m, 2 * m);
		}
		return e;
	}

	/** Style for rendering incidents */
	static protected final Style STYLE = new Style("Incident",
		Outline.createSolid(Color.BLACK, 20), Color.MAGENTA);

	/** Get the style to render incidents */
	public Style getStyle(MapObject o) {
		return STYLE;
	}

	/** Ellipses to paint selection rings */
	protected final Ellipse2D[] ellipses;

	/** Get the value of the named policy */
	static protected int getPolicyValue(TypeCache<SystemPolicy> c,
		String p)
	{
		SystemPolicy sp = c.getObject(p);
		if(sp != null)
			return sp.getValue();
		else
			return 0;
	}

	/** Create a new directional incident theme */
	public DirectionalIncidentTheme(TypeCache<SystemPolicy> c) {
		super("Incidents", IncidentLayer.TWO_WAY);
		int[] r = new int[4];
		r[0] = getPolicyValue(c, SystemPolicy.RING_RADIUS_0);
		r[1] = getPolicyValue(c, SystemPolicy.RING_RADIUS_1);
		r[2] = getPolicyValue(c, SystemPolicy.RING_RADIUS_2);
		r[3] = getPolicyValue(c, SystemPolicy.RING_RADIUS_3);
		ellipses = createEllipses(r);
		addStyle(STYLE);
	}

	/** Draw a selected incident */
	public void drawSelected(Graphics2D g, MapObject o) {
		super.drawSelected(g, o);
		g.setColor(Color.RED);
		g.setStroke(STROKE);
		for(Ellipse2D e: ellipses)
			g.draw(e);
	}
}
