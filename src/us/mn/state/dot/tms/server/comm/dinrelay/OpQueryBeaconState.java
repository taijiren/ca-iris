/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2014  Minnesota Department of Transportation
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
package us.mn.state.dot.tms.server.comm.dinrelay;

import java.io.IOException;
import us.mn.state.dot.tms.server.BeaconImpl;
import us.mn.state.dot.tms.server.comm.CommMessage;
import us.mn.state.dot.tms.server.comm.OpDevice;
import us.mn.state.dot.tms.server.comm.PriorityLevel;
import static us.mn.state.dot.tms.server.comm.dinrelay.OpDinRelay.DIN_LOG;

/**
 * Query the state of a beacon
 *
 * @author Douglas Lau
 */
public class OpQueryBeaconState extends OpDevice<DinRelayProperty> {

	/** Beacon device */
	private final BeaconImpl beacon;

	/** Log a property query */
	protected void logQuery(DinRelayProperty prop) {
		if(DIN_LOG.isOpen())
			DIN_LOG.log(controller.getName() + ": " + prop);
	}

	/** Create a new query beacon state operation */
	public OpQueryBeaconState(BeaconImpl b) {
		super(PriorityLevel.DATA_30_SEC, b);
		beacon = b;
	}

	/** Create the second phase of the operation */
	@Override
	protected Phase<DinRelayProperty> phaseTwo() {
		return new QueryBeacon();
	}

	/** Phase to query the beacon status */
	private class QueryBeacon extends Phase<DinRelayProperty> {

		/** Query the beacon status */
		protected Phase<DinRelayProperty> poll(
			CommMessage<DinRelayProperty> mess) throws IOException
		{
			OutletProperty property = new OutletProperty();
			mess.add(property);
			mess.queryProps();
			logQuery(property);
			boolean[] states = property.getOutletState();
			int p = beacon.getPin();
			if(p > 0 && p <= states.length)
				beacon.setFlashingNotify(states[p - 1]);
			else
				setErrorStatus("Invalid pin");
			return null;
		}
	}
}