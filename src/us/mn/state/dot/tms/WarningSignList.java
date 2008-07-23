/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2004-2008  Minnesota Department of Transportation
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
import java.rmi.RemoteException;
import us.mn.state.dot.vault.ObjectVaultException;

/**
 * List of warning signs in the traffic management system
 *
 * @author Douglas Lau
 */
public class WarningSignList extends SortedListImpl {

	/** Create a new warning sign list */
	public WarningSignList() throws RemoteException {
		super();
	}

	/** Add a warning sign to the list */
	public synchronized TMSObject add(String key) throws TMSException,
		RemoteException
	{
		WarningSignImpl sign = (WarningSignImpl)map.get(key);
		if(sign != null) return sign;
		sign = new WarningSignImpl(key);
		try { vault.save(sign, getUserName()); }
		catch(ObjectVaultException e) {
			throw new TMSException(e);
		}
		map.put(key, sign);
		Iterator iterator = map.keySet().iterator();
		for(int index = 0; iterator.hasNext(); index++) {
			String search = (String)iterator.next();
			if(key.equals(search)) {
				notifyAdd(index, key);
				break;
			}
		}
		return sign;
	}

	/** Remove a warning sign from the list */
	public synchronized void remove(String key) throws TMSException {
		GeoLocImpl geo_loc = lookupDeviceLoc(key);
		super.remove(key);
		if(geo_loc != null)
			MainServer.server.removeObject(geo_loc);
		deviceList.remove(key);
	}
}
