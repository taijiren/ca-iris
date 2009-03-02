/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2000-2009  Minnesota Department of Transportation
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
package us.mn.state.dot.tms.client.toast;

import java.awt.Color;
import java.rmi.RemoteException;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import us.mn.state.dot.sched.ActionJob;
import us.mn.state.dot.tms.Camera;
import us.mn.state.dot.tms.Controller;
import us.mn.state.dot.tms.TrafficDevice;
import us.mn.state.dot.tms.client.TmsConnection;

/**
 * TrafficDeviceForm is a form for entering and editing Traffic device records
 *
 * @author Douglas Lau
 */
abstract public class TrafficDeviceForm extends TMSObjectForm {

	/** Device ID */
	protected final String id;

	/** Remote traffic device object */
	protected TrafficDevice device;

	/** Tabbed pane */
	protected final JTabbedPane tab = new JTabbedPane(JTabbedPane.TOP);

	/** Location panel */
	protected LocationPanel location;

	/** Controller button */
	protected final JButton controller = new JButton("Controller");

	/** Notes text area */
	protected final JTextArea notes = new JTextArea(3, 24);

	/** Apply button */
	protected final JButton apply = new JButton("Apply Changes");

	/** Create a new traffic device form */
	protected TrafficDeviceForm(String t, TmsConnection tc, String _id) {
		super(t, tc);
		id = _id;
	}

	/** Set the traffic device */
	protected void setDevice(TrafficDevice d) {
		device = d;
		obj = d;
	}

	/** Initialize the widgets on the form */
	protected void initialize() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		try {
			location = new LocationPanel(admin, device.getGeoLoc(),
				connection.getSonarState());
		}
		catch(RemoteException e) {
			e.printStackTrace();
			return;
		}
		location.initialize();
		location.addRow("Notes", notes);
		location.setCenter();
		location.addRow(controller);
		new ActionJob(this, controller) {
			public void perform() throws Exception {
				controllerPressed();
			}
		};
		tab.add("Location", location);
		add(tab);
		if(admin) {
			add(Box.createVerticalStrut(VGAP));
			new ActionJob(this, apply) {
				public void perform() throws Exception {
					applyPressed();
				}
			};
			add(apply);
			apply.setEnabled(admin);
		}
		setBackground(Color.LIGHT_GRAY);
		super.initialize();
	}

	/** Controller lookup button pressed */
	protected void controllerPressed() {
		String cont;
		try {
			cont = device.getController();
		}
		catch(RemoteException e) {
			e.printStackTrace();
			return;
		}
		Controller c = connection.getSonarState().lookupController(
			cont);
		if(c == null)
			controller.setEnabled(false);
		else {
			connection.getDesktop().show(
				new ControllerForm(connection, c));
		}
	}

	/** Dispose of the form */
	protected void dispose() {
		location.dispose();
		super.dispose();
	}

	/** Update the form with the current state of the device */
	protected void doUpdate() throws RemoteException {
		notes.setText(device.getNotes());
		controller.setEnabled(device.getController() != null);
	}

	/** Apply button is pressed */
	protected void applyPressed() throws Exception {
		device.setNotes(notes.getText());
	}

	/** Get the name of a camera */
	protected String getCameraName(Camera c) {
		if(c == null)
			return "";
		else
			return c.getName();
	}
}
