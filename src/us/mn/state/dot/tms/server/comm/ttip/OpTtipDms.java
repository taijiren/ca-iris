/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2016 California Department of Transportation
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
package us.mn.state.dot.tms.server.comm.ttip;

import us.mn.state.dot.tms.server.DMSImpl;
import us.mn.state.dot.tms.server.comm.OpDevice;
import us.mn.state.dot.tms.server.comm.PriorityLevel;

/**
 * Operation to be performed on an TTIP DMS.
 *
 * @author Dan Rossiter
 */
abstract public class OpTtipDms extends OpDevice {

    /** DMS to operate */
    protected final DMSImpl dms;

    /** Create a new TTIP DMS operation */
    protected OpTtipDms(PriorityLevel p, DMSImpl d) {
        super(p, d);
        dms = d;
    }

    /** Cleanup the operation */
    @Override
    public void cleanup() {
        if (isSuccess())
            dms.requestConfigure();
        else
            dms.setConfigure(false);
        super.cleanup();
    }
}
