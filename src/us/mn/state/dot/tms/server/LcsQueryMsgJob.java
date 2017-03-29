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
package us.mn.state.dot.tms.server;

import java.util.Calendar;
import java.util.Iterator;
import us.mn.state.dot.sched.Job;
import us.mn.state.dot.sched.Scheduler;
import us.mn.state.dot.tms.DeviceRequest;
import us.mn.state.dot.tms.LCSArray;
import us.mn.state.dot.tms.LCSArrayHelper;

import static us.mn.state.dot.tms.SystemAttrEnum.LCS_POLL_PERIOD_SECS;

/**
 * Job to periodically query all LCS messages.
 *
 * @author Douglas Lau
 * @author Jacob Barde
 */
public class LcsQueryMsgJob extends Job {

	/** Seconds to offset each poll from start of interval */
	static private final int OFFSET_SECS = 19;

	/** seconds to offset each pre-polling job (if needed */
	static private final int OFFSET_PRE = Math.min(
		Math.abs((OFFSET_SECS - 1)), 30);

	/** scheduler this job is attached to */
	private final Scheduler scheduler;

	/** value of setting at the time of instantiation */
	private final int lcs_poll_period_secs;

	/** Create a new job to query LCS messages */
	public LcsQueryMsgJob(Scheduler s) {
		this(s, Calendar.SECOND, LCS_POLL_PERIOD_SECS.getInt(),
		     Calendar.SECOND, OFFSET_SECS);
	}

	/** private constructor */
	private LcsQueryMsgJob(Scheduler s, int iField, int i, int oField,
		               int o) {
		super(iField, i, oField, o);
		scheduler = s;
		lcs_poll_period_secs = LCS_POLL_PERIOD_SECS.getInt();
	}

	/** Perform the job */
	@Override
	public void perform() {
		if (isValidInterval())
			queryLcsStatus();
	}

	/** Poll all LCS devices */
	private void queryLcsStatus() {
		int req = DeviceRequest.QUERY_MESSAGE.ordinal();
		Iterator<LCSArray> it = LCSArrayHelper.iterator();
		while(it.hasNext()) {
			LCSArray lcs_array = it.next();
			lcs_array.setDeviceRequest(req);
		}
	}

	/** Check if this is a repeating job */
	@Override
	public boolean isRepeating() {
		return isValidInterval();
	}

	/** is this a valid interval */
	private boolean isValidInterval() {
		return interval > 5;
	}

	/** Do this upon completion of the job */
	@Override
	public void complete() {
		LcsQueryMsgJob j;
		int in = LCS_POLL_PERIOD_SECS.getInt();

		if (lcs_poll_period_secs != in)
			removeJobsOfThisType();
		if (in > 60 || !isValidInterval()) {
			j = new LcsQueryMsgJob(scheduler, Calendar.SECOND, 30,
				               Calendar.SECOND, OFFSET_PRE) {
				public void perform() {}
				public boolean isRepeating() { return false; }
			};
			scheduler.addJob(j);
		}
		if (isValidInterval()) {
			j = new LcsQueryMsgJob(scheduler);
			scheduler.addJob(j);
		}
	}

	/** remove jobs of this type from scheduler */
	private void removeJobsOfThisType() {
		Iterator<Job> i = scheduler.getJobIterator();
		while (i.hasNext()) {
			Job j = i.next();
			if (j instanceof LcsQueryMsgJob)
				scheduler.removeJob(j);
		}
	}
}
