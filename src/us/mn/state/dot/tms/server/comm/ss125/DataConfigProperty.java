/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2009-2012  Minnesota Department of Transportation
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
package us.mn.state.dot.tms.server.comm.ss125;

import java.io.IOException;
import us.mn.state.dot.tms.server.comm.ParsingException;

/**
 * Data Configuration Property.
 *
 * @author Douglas Lau
 */
public class DataConfigProperty extends SS125Property {

	/** Message ID for data config request */
	protected int msgId() {
		return MSG_ID_DATA_CONFIG;
	}

	/** Format a QUERY request */
	protected byte[] formatQuery() throws IOException {
		byte[] body = new byte[3];
		format8(body, OFF_MSG_ID, msgId());
		format8(body, OFF_MSG_SUB_ID, msgSubId());
		formatBool(body, OFF_READ_WRITE, false);
		return body;
	}

	/** Format a STORE request */
	protected byte[] formatStore() throws IOException {
		byte[] body = new byte[28];
		format8(body, OFF_MSG_ID, msgId());
		format8(body, OFF_MSG_SUB_ID, msgSubId());
		formatBool(body, OFF_READ_WRITE, true);
		format16(body, 3, interval);
		format8(body, 5, mode.ordinal());
		event_push.format(body, 6);
		interval_push.format(body, 12);
		presence_push.format(body, 18);
		format16Fixed(body, 24, default_sep);
		format16Fixed(body, 26, default_size);
		return body;
	}

	/** Parse a QUERY response */
	protected void parseQuery(byte[] rbody) throws IOException {
		if(rbody.length != 28)
			throw new ParsingException("BODY LENGTH");
		interval = parse16(rbody, 3);
		mode = StorageMode.fromOrdinal(rbody[5]);
		event_push = PushConfig.parse(rbody, 6);
		interval_push = PushConfig.parse(rbody, 12);
		presence_push = PushConfig.parse(rbody, 18);
		default_sep = parse16Fixed(rbody, 24);
		default_size = parse16Fixed(rbody, 26);
	}

	/** Data interval (seconds) */
	protected int interval;

	/** Get the data interval (seconds) */
	public int getInterval() {
		return interval;
	}

	/** Set the data interval (seconds) */
	public void setInterval(int i) {
		interval = i;
	}

	/** Data storage mode */
	static public enum StorageMode {
		DISABLED, CIRCULAR, FILL_ONCE;
		static public StorageMode fromOrdinal(int o) {
			for(StorageMode sm: StorageMode.values()) {
				if(sm.ordinal() == o)
					return sm;
			}
			return DISABLED;
		}
	}

	/** Interval storage mode */
	protected StorageMode mode = StorageMode.DISABLED;

	/** Get the interval storage mode */
	public StorageMode getMode() {
		return mode;
	}

	/** Set the interval storage mode */
	public void setMode(StorageMode m) {
		mode = m;
	}

	/** Data push configuration */
	static public class PushConfig {
		public PushPort port = PushPort.RS485;
		public PushProtocol protocol = PushProtocol.Z1;
		public boolean enable;
		public int dest_sub_id;
		public int dest_id;

		static protected PushConfig parse(byte[] rbody, int pos)
			throws IOException
		{
			PushConfig pc = new PushConfig();
			pc.port = PushPort.fromOrdinal(parse8(rbody, pos));
			pc.protocol = PushProtocol.fromOrdinal(parse8(rbody,
				pos + 1));
			pc.enable = parseBool(rbody, pos + 2);
			pc.dest_sub_id = parse8(rbody, pos + 3);
			pc.dest_id = parse16(rbody, pos + 4);
			return pc;
		}

		void format(byte[] body, int pos) {
			format8(body, pos, port.ordinal());
			format8(body, pos + 1, protocol.ordinal());
			formatBool(body, pos + 2, enable);
			format8(body, pos + 3, dest_sub_id);
			format16(body, pos + 4, dest_id);
		}
	}

	/** Config for event data push */
	protected PushConfig event_push;

	/** Get event push config */
	public PushConfig getEventPush() {
		return event_push;
	}

	/** Config for interval data push */
	protected PushConfig interval_push;

	/** Get the interval push config */
	public PushConfig getIntervalPush() {
		return interval_push;
	}

	/** Config for presence data push */
	protected PushConfig presence_push;

	/** Get the presence push config */
	public PushConfig getPresencePush() {
		return presence_push;
	}

	/** Port to send push data */
	static public enum PushPort {
		RS485, RS232, EXP1, EXP2;
		static public PushPort fromOrdinal(int o) {
			for(PushPort p: PushPort.values()) {
				if(p.ordinal() == o)
					return p;
			}
			return RS485;
		}
	}

	/** Protocol to send push data */
	static public enum PushProtocol {
		Z1, SS105, SS105_MULTI, RTMS;
		static public PushProtocol fromOrdinal(int o) {
			for(PushProtocol p: PushProtocol.values()) {
				if(p.ordinal() == o)
					return p;
			}
			return Z1;
		}
	}

	/** Default loop separation */
	protected float default_sep;

	/** Get the default loop separation */
	public float getDefaultSeparation() {
		return default_sep;
	}

	/** Default loop size */
	protected float default_size;

	/** Get the default loop size */
	public float getDefaultSize() {
		return default_size;
	}
}
