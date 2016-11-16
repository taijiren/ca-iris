/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2014-2015  AHMCT, University of California
 * Copyright (C) 2016       California Department of Transportation
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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * SiteDataHelper has static methods for dealing with SiteData entities.
 * @author Travis Swanston
 * @author Dan Rossiter
 * @author Jacob Barde
 */
public class SiteDataHelper extends BaseHelper {

	public final static String TAG_RDFULL = "\\[RDFULL\\]";         // full road name
	public final static String TAG_RDABBR = "\\[RDABBR\\]";         // road abbreviation
	public final static String TAG_RD = "\\[RD\\]";                 // same as [RDABBR] if exists, else [RDFULL]
	public final static String TAG_RDDIR = "\\[RDDIR\\]";           // road direction
	public final static String TAG_RDDIRFULL = "\\[RDDIRFULL\\]";   // road direction abbreviation
	public final static String TAG_XRDFULL = "\\[XRDFULL\\]";       // full road name of cross-road
	public final static String TAG_XRDABBR = "\\[XRDABBR\\]";       // road abbreviation of cross-road
	public final static String TAG_XRD = "\\[XRD\\]";               // same as [XRDABBR] if exists, else [XRDFULL]
	public final static String TAG_XRDDIR = "\\[XRDDIR\\]";         // road direction abbreviation of cross-road
	public final static String TAG_XRDDIRFULL = "\\[XRDDIRFULL\\]"; // road direction of cross-road
	public final static String TAG_XMOD = "\\[XMOD\\]";             // prepositional relation of road to cross-road
	public final static String TAG_MILE = "\\[MILE\\]";             // milepoint
	public final static String TAG_CTY = "\\[CTY\\]";               // county code
	public final static String TAG_CTYFULL = "\\[CTYFULL\\]";       // county name
	public final static String TAG_GLNAME = "\\[GLNAME\\]";         // GeoLoc name

	static public final String DESCFMT_DEFAULT = "[RDFULL] [RDDIR] [XMOD] [XRDFULL] [XRDDIR]";

	private final static Map<String, String> geoLocToSD_name = new HashMap<>();
	private final static Map<String, String> geoLocToSD_site_name = new HashMap<>();
	private final static Map<String, String> siteName2geoLoc = new HashMap<>();
	private final static Object hashLock = new Object();

	static private String getFormatString(GeoLoc gl) {
		// return format from SiteData if present
		SiteData sd = lookupByGeoLoc(gl);
		if (sd != null) {
			String fmt = sd.getFormat();
			if (!("".equals(sanitize(fmt))))
				return fmt.trim();
		}
		// return format from system attributes if present
		String lf = SystemAttrEnum.LOCATION_FORMAT.getString();
		if (!"".equals(sanitize(lf)))
			return lf.trim();
		// return default
		return DESCFMT_DEFAULT;
	}

	/** Constructor (do not instantiate). */
	private SiteDataHelper() {
		assert false;
	}

	/** Get a SiteData iterator */
	static public Iterator<SiteData> iterator() {
		return new IteratorWrapper<>(namespace.iterator(SiteData.SONAR_TYPE));
	}

	/** Lookup a SiteData entity */
	static public SiteData lookup(String name) {
		if (name == null)
			return null;
		return (SiteData) namespace.lookupObject(SiteData.SONAR_TYPE, name);
	}

	/** Lookup a SiteData entity */
	static public SiteData lookupBySiteName(String site_name) {
		if (site_name == null)
			return null;

		String geoloc_name;
		String name = getCachedNameBySiteName(site_name);
		SiteData sd = validateCached(name, null, site_name);

		if (sd == null) {
			geoloc_name = null;
			name = null;
			Iterator<SiteData> it = iterator();
			while(it.hasNext()) {
				SiteData tmp = it.next();
				if(site_name.equals(tmp.getSiteName())) {
					sd = tmp;
					geoloc_name = sd.getGeoLoc();
					name = sd.getName();
					break;
				}
			}

			populateCache(geoloc_name, name, site_name);
		}

		return sd;
	}

	/** get the cache name for a site-name */
	static private String getCachedNameBySiteName(String site_name) {
		synchronized (hashLock) {
			String geoloc_name = siteName2geoLoc.get(site_name);
			if (geoloc_name != null)
				return geoLocToSD_name.get(geoloc_name);
		}
		return null;
	}

	/** Lookup a SiteData entity by GeoLoc */
	static public SiteData lookupByGeoLoc(GeoLoc gl) {
		return gl != null ? lookupByGeoLoc(gl.getName()) : null;
	}

	/** Lookup a site data. if it is not in the cache, query sonar for it. */
	static private SiteData lookupByGeoLoc(String geoloc_name) {
		if (geoloc_name == null)
			return null;

		System.out.println("lookupByGeoLoc('" + geoloc_name + "')");

		// try to find cached value first
		String site_name;
		String name = getCachedNameByGeoLoc(geoloc_name);
		SiteData sd = validateCached(name, geoloc_name, null);

		// perform the expensive operation (and cache the result)
		if (sd == null) {
			Iterator<SiteData> it = iterator();
			name = null;
			site_name = null;
			while (it.hasNext()) {
				SiteData tmp = it.next();
				if (geoloc_name.equals(tmp.getGeoLoc())) {
					sd = tmp;
					name = sd.getName();
					site_name = sd.getSiteName();
					break;
				}
			}

			populateCache(geoloc_name, name, site_name);
		}

		return sd;
	}

	/** get the name by geoloc name */
	static private String getCachedNameByGeoLoc(String geoloc_name) {
		synchronized (hashLock) {
			return geoLocToSD_name.get(geoloc_name);
		}
	}

	/** populate the cache with the values */
	static private void populateCache(String geoloc_name, String name, String site_name) {
		synchronized (hashLock) {
			System.out.println("++ geoLocToSD_name.putting '" + geoloc_name + "' => '" + (name==null?"<<null>>":name) + "'");
			geoLocToSD_name.put(geoloc_name, name);
			System.out.println("   geoLocToSD_name.size=" + geoLocToSD_name.size());

			System.out.println("++ geoLocToSD_site_name.putting '" + geoloc_name + "' => '" + (site_name==null?"<<null>>":site_name) + "'");
			geoLocToSD_site_name.put(geoloc_name, site_name);
			System.out.println("   geoLocToSD_site_name.size=" + geoLocToSD_site_name.size());

			if (null != site_name) {
				System.out.println("++ siteName2geoLoc.putting '" + site_name + "' => '" + geoloc_name + "'");
				siteName2geoLoc.put(site_name, geoloc_name);
				System.out.println("  siteName2geoLoc.size=" + siteName2geoLoc.size());
			}
		}

	}
	/**
	 * validate a cached value set
	 *
	 * @param name          SiteData name string (required)
	 * @param geoloc_name   SiteData geoloc string
	 * @param site_name     SiteData site-name string
	 * @return              corresponding valid SiteData object or null if invalid
	 */
	static private SiteData validateCached(String name, String geoloc_name, String site_name) {
		if (name == null || (geoloc_name == null && site_name == null))
			return null;

		String sn = null;
		String gn = null;

		// verify hasn't changed since cached value was added
		SiteData sd = lookup(name);
		if (geoloc_name != null) {
			if (sd == null || !geoloc_name.equals(sd.getGeoLoc())) {
				synchronized (hashLock) {
					geoLocToSD_name.remove(geoloc_name);
					sn = geoLocToSD_site_name.remove(geoloc_name);
					if (sn != null)
						siteName2geoLoc.remove(sn);
				}
				sd = null;
			}
		} else if (site_name != null) {
			if (sd == null || !site_name.equals(sd.getSiteName())) {
				synchronized (hashLock) {
					gn = siteName2geoLoc.remove(site_name);
					if (gn != null) {
						geoLocToSD_name.remove(gn);
						geoLocToSD_site_name.remove(gn);
					}
				}
				sd = null;
			}
		}
		return sd;
	}

	/**
	 * Build a site name string for a SiteData entity.
	 * @param geoloc_name The GeoLoc name corresponding to the SiteData entity.
	 *
	 * @return A site name string, or null if entity not found or if entity doesn't contain a site name
	 */
	static public String getSiteName(String geoloc_name) {
		if (geoloc_name == null)
			return null;

		SiteData sd = null;
		String site_name = null;
		boolean exists;
		synchronized (hashLock) {
			exists = geoLocToSD_site_name.containsKey(geoloc_name);
			site_name = geoLocToSD_site_name.get(geoloc_name);
		}

		// TODO: need to validate via validateCached
//		String name = getCachedNameByGeoLoc(geoloc_name);
//		sd = validateCached(name, geoloc_name, null);
//		if (sd == null) {
		if (site_name == null && !exists) {
			sd = lookupByGeoLoc(geoloc_name);
			site_name = sd != null ? sd.getSiteName() : null;
		}
		return !"".equals(sanitize(site_name)) ? site_name : null;
	}

	/**
	 * Find a GeoLoc name by site name.
	 * @param sn The site name corresponding to the GeoLoc entity.
	 *
	 * @return A GeoLoc name string, or null if site name not found.
	 */
	static public String getGeoLocNameBySiteName(String sn) {
		SiteData sd = lookupBySiteName(sn);
		String gl = sd != null ? sd.getGeoLoc() : null;

		return !"".equals(sanitize(gl)) ? gl : null;
	}

	/** Build a string to describe a GeoLoc */
	static public String getLocDescription(GeoLoc gl, String connect) {
		if (gl == null)
			return "Unknown location";
		String fmt = getFormatString(gl);
		Road r = gl.getRoadway();
		Road x = gl.getCrossStreet();
		// build [RD.*] values
		String rdfull = null;
		String rdabbr = null;
		String rd = null;
		String rddir = null;
		String rddirfull = null;
		if (r != null) {
			rdfull = r.getName();
			rdabbr = r.getAbbrev();
			rddir = Direction.fromOrdinal(gl.getRoadDir()).abbrev;
			rddirfull = Direction.fromOrdinal(gl.getRoadDir()).toString();
			rd = "".equals(sanitize(rdabbr)) ? rdfull : rdabbr;
		}
		// build [X.*] values
		String xrdfull = null;
		String xrdabbr = null;
		String xrd = null;
		String xrddir = null;
		String xrddirfull = null;
		String xmod = null;
		if (x != null) {
			xrdfull = x.getName();
			xrdabbr = x.getAbbrev();
			xrddir = Direction.fromOrdinal(gl.getRoadDir()).abbrev;
			xrddirfull = Direction.fromOrdinal(gl.getRoadDir()).toString();
			xrd = "".equals(sanitize(xrdabbr)) ? xrdfull : xrdabbr;
			xmod = connect != null ? connect : sanitize(GeoLocHelper.getModifier(gl));
		}
		// build the rest
		xmod = xmod != null ? xmod : "";
		String mile = gl.getMilepoint();
		String cty = null;
		String ctyfull = null;
		SiteData sd = lookupByGeoLoc(gl);
		if (sd != null) {
			County c = County.lookup(sd.getCounty());
			if (c != null) {
				ctyfull = c.name;
				cty = c.code;
			}
		}
		fmt = fmt.replaceAll(TAG_RD, sanitize(rd));
		fmt = fmt.replaceAll(TAG_RDFULL, sanitize(rdfull));
		fmt = fmt.replaceAll(TAG_RDABBR, sanitize(rdabbr));
		fmt = fmt.replaceAll(TAG_RDDIR, sanitize(rddir));
		fmt = fmt.replaceAll(TAG_RDDIRFULL, sanitize(rddirfull));
		fmt = fmt.replaceAll(TAG_XRD, sanitize(xrd));
		fmt = fmt.replaceAll(TAG_XRDFULL, sanitize(xrdfull));
		fmt = fmt.replaceAll(TAG_XRDABBR, sanitize(xrdabbr));
		fmt = fmt.replaceAll(TAG_XRDDIR, sanitize(xrddir));
		fmt = fmt.replaceAll(TAG_XRDDIRFULL, sanitize(xrddirfull));
		fmt = fmt.replaceAll(TAG_XMOD, xmod);        // don't sanitize
		fmt = fmt.replaceAll(TAG_MILE, sanitize(mile));
		fmt = fmt.replaceAll(TAG_CTY, sanitize(cty));
		fmt = fmt.replaceAll(TAG_CTYFULL, sanitize(ctyfull));
		fmt = fmt.replaceAll(TAG_GLNAME, sanitize(gl.getName()));
		fmt = fmt.replaceAll("\\s+", " ");
		fmt = fmt.trim();
		return fmt;
	}

	/** Trim a string, or convert it to an empty string if null */
	static private String sanitize(String s) {
		return (s != null) ? s.trim() : "";
	}

}

