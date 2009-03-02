/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2008-2009  Minnesota Department of Transportation
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

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import us.mn.state.dot.sched.ActionJob;
import us.mn.state.dot.sched.ListSelectionJob;
import us.mn.state.dot.sonar.client.TypeCache;
import us.mn.state.dot.tms.Alarm;

/**
 * A form for displaying and editing alarms
 *
 * @author Douglas Lau
 */
public class AlarmForm extends AbstractForm {

	/** Frame title */
	static protected final String TITLE = "Alarms";

	/** Table model for alarms */
	protected AlarmModel model;

	/** Table to hold the alarm list */
	protected final ZTable table = new ZTable();

	/** Button to delete the selected alarm */
	protected final JButton del_button = new JButton("Delete");

	/** Alarm type cache */
	protected final TypeCache<Alarm> cache;

	/** Create a new alarm form */
	public AlarmForm(TypeCache<Alarm> c) {
		super(TITLE);
		cache = c;
	}

	/** Initializze the widgets in the form */
	protected void initialize() {
		model = new AlarmModel(cache);
		add(createAlarmPanel());
	}

	/** Dispose of the form */
	protected void dispose() {
		model.dispose();
	}

	/** Create alarm panel */
	protected JPanel createAlarmPanel() {
		final ListSelectionModel s = table.getSelectionModel();
		s.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		new ListSelectionJob(this, s) {
			public void perform() {
				if(!event.getValueIsAdjusting())
					selectAlarm();
			}
		};
		new ActionJob(this, del_button) {
			public void perform() throws Exception {
				int row = s.getMinSelectionIndex();
				if(row >= 0)
					model.deleteRow(row);
			}
		};
		table.setModel(model);
		table.setAutoCreateColumnsFromModel(false);
		table.setColumnModel(model.createColumnModel());
		table.setVisibleRowCount(16);
		FormPanel panel = new FormPanel(true);
		panel.addRow(table);
		panel.addRow(del_button);
		del_button.setEnabled(false);
		return panel;
	}

	/** Change the selected alarm */
	protected void selectAlarm() {
		int row = table.getSelectedRow();
		del_button.setEnabled(row >= 0 && !model.isLastRow(row));
	}
}
