/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2014-2015  AHMCT, University of California
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
package us.mn.state.dot.tms.server.comm.carwis;

import java.io.IOException;
import java.util.HashMap;
import us.mn.state.dot.tms.server.WeatherSensorImpl;
import us.mn.state.dot.tms.server.comm.CommMessage;
import us.mn.state.dot.tms.server.comm.OpDevice;
import us.mn.state.dot.tms.server.comm.PriorityLevel;

/**
 * Operation to read the feed.
 * Based on Doug Lau's ssi.OpRead.
 *
 * @author Travis Swanston
 */
public class OpRead extends OpDevice {

	/** Weather sensor to read */
	private final WeatherSensorImpl sensor;

	/** RWIS site ID */
	private final String site_id;

	/** Mapping of site_id to most recent RWIS records */
	private final HashMap<String, RwisRec> records;

	/** Constructor. */
	protected OpRead(WeatherSensorImpl ws, HashMap<String, RwisRec> recs) {
		super(PriorityLevel.DATA_30_SEC, ws);
		records = recs;
		sensor = ws;
		site_id = sensor.getName();
	}

	/** Create the second phase of the operation. */
	@Override
	protected Phase phaseTwo() {
		return new PhaseCheck();
	}

	/** Phase to check records mapping. */
	private class PhaseCheck extends Phase {
		protected Phase poll(CommMessage mess) {
			RwisRec rec = records.get(site_id);
			if (rec == null || rec.isExpired()) {
				// Add a null mapping for site_id
				records.put(site_id, null);
				return new PhaseRead();
			}
			else
				return new PhaseUpdate();
		}
	}

	/** Phase to read the file */
	private class PhaseRead extends Phase {
		protected Phase poll(CommMessage mess) throws IOException {
			mess.add(new CaRwisProperty(records));
			mess.queryProps();
			return new PhaseUpdate();
		}
	}

	/** Phase to update the sensor */
	private class PhaseUpdate extends Phase {
		protected Phase poll(CommMessage mess) throws IOException {
			RwisRec rec = records.get(site_id);
			if (rec == null || rec.isExpired()) {
				rec = new RwisRec(site_id);
// need?  are we doing it this way?:
				records.put(site_id, rec);
			}
			CaRwisPoller.log("storing for " + sensor);
			rec.store(sensor);
			return null;
		}
	}

}
