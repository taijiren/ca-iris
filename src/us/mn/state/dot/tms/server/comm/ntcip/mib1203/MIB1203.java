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
package us.mn.state.dot.tms.server.comm.ntcip.mib1203;

import us.mn.state.dot.tms.server.comm.ntcip.MIBNode;

/**
 * MIB nodes for NTCIP 1203
 *
 * @author Douglas Lau
 */
class MIB1203 extends MIBNode {

	/** Create a node in a MIB */
	protected MIB1203(int[] n) {
		super(null, n);
	}

	static public final MIBNode nema = new MIB1203(
		new int[] { 1, 3, 6, 1, 4, 1, 1206 } );
	static public final MIBNode _private = nema.create(3);
	static public final MIBNode transportation = nema.create(4);
	static public final MIBNode devices = transportation.create(2);
	static public final MIBNode global = devices.create(6);
	static public final MIBNode globalConfiguration = global.create(1);
	static public final MIBNode globalModuleTable =
		globalConfiguration.create(3);
	static public final MIBNode moduleTableEntry =
		globalModuleTable.create(1);

	static public final MIBNode dms = devices.create(3);
	static public final MIBNode dmsSignCfg = dms.create(1);
	static public final MIBNode vmsCfg = dms.create(2);
	static public final MIBNode fontDefinition = dms.create(3);
	static public final MIBNode fontTable = fontDefinition.create(2);
	static public final MIBNode fontEntry = fontTable.create(1);
	static public final MIBNode characterTable = fontDefinition.create(4);
	static public final MIBNode characterEntry = characterTable.create(1);
	static public final MIBNode multiCfg = dms.create(4);
	static public final MIBNode dmsMessage = dms.create(5);
	static public final MIBNode dmsMessageTable = dmsMessage.create(8);
	static public final MIBNode dmsMessageEntry = dmsMessageTable.create(1);
	static public final MIBNode signControl = dms.create(6);
	static public final MIBNode illum = dms.create(7);
	static public final MIBNode dmsStatus = dms.create(9);
	static public final MIBNode statError = dmsStatus.create(7);
	static public final MIBNode pixelFailureTable = statError.create(3);
	static public final MIBNode pixelFailureEntry =
		pixelFailureTable.create(1);
	static public final MIBNode statTemp = dmsStatus.create(9);

	static public final MIBNode ledstar = _private.create(16);
	static public final MIBNode ledstarDMS = ledstar.create(1);
	static public final MIBNode ledstarSignControl = ledstarDMS.create(1);
	static public final MIBNode ledstarDiagnostics = ledstarDMS.create(2);

	static public final MIBNode skyline = _private.create(18);
	static public final MIBNode skylineDevices = skyline.create(2);
	static public final MIBNode skylineDms = skylineDevices.create(3);
	static public final MIBNode skylineDmsSignCfg = skylineDms.create(1);
	static public final MIBNode skylineDmsStatus = skylineDms.create(9);
}
