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
package us.mn.state.dot.tms.client.widget;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.BindException;
import java.text.ParseException;
import javax.mail.MessagingException;
import javax.naming.AuthenticationException;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import us.mn.state.dot.sonar.SonarException;
import us.mn.state.dot.sonar.client.SonarShowException;
import us.mn.state.dot.sonar.client.PermissionException;
import us.mn.state.dot.tms.ChangeVetoException;
import us.mn.state.dot.tms.InvalidMessageException;
import us.mn.state.dot.tms.SystemAttrEnum;
import us.mn.state.dot.tms.utils.SEmail;
import us.mn.state.dot.tms.utils.I18N;

/**
 * A swing dialog for displaying exception stack traces.
 *
 * @author Douglas Lau
 */
public class ExceptionDialog extends JDialog {

	/** Vertical box for components in the exception dialog */
	protected final Box box;

	/** Owner of any new exception dialogs */
	static protected Frame owner;

	/** Set the owner to use for new exception dialogs */
	static public void setOwner(Frame f) {
		owner = f;
	}

	/** Flag for fatal exceptions */
	protected boolean fatal = false;

	/** Set the fatal status */
	protected void setFatal(boolean f) {
		fatal = f;
		if(fatal)
			setTitle("Program error");
		else
			setTitle("Warning");
	}

	/** Create a new ExceptionDialog */
	public ExceptionDialog(final Exception e) {
		super(owner, true);
		setFatal(false);
		e.printStackTrace();
		box = Box.createVerticalBox();
		box.add(Box.createVerticalGlue());
		try { throw e; }
		catch(AuthenticationException ee) {
			addText("Authentication failed:");
			addText(ee.getMessage());
			box.add(Box.createVerticalStrut(6));
			addText("Please make sure your user");
			addText("name is correct, then");
			addText("type your password again.");
		}
		catch(BindException ee) {
			setFatal(true);
			addText("Another copy of this program is");
			addText("already running on this computer.");
			addText("Please shut it down before");
			addText("starting this program again.");
			addAssistanceMessage();
		}
		catch(ChangeVetoException ee) {
			addText("The change has been prevented");
			addText("for the following reason:");
			box.add(Box.createVerticalStrut(6));
			addText(ee.getMessage());
		}
		catch(PermissionException ee) {
			addText("Permission denied:");
			box.add(Box.createVerticalStrut(6));
			addText(ee.getMessage());
		}
		catch(SonarShowException ee) {
			addText("The following message was");
			addText("received from the IRIS server:");
			box.add(Box.createVerticalStrut(6));
			addText(ee.getMessage());
		}
		catch(SonarException ee) {
			setFatal(true);
			addText("This program has encountered");
			addText("a problem while communicating");
			addText("with the IRIS server.");
			box.add(Box.createVerticalStrut(6));
			addText(ee.getMessage());
		}
		catch(NumberFormatException ee) {
			addText("Number formatting error");
			box.add(Box.createVerticalStrut(6));
			addText("Please check all numeric");
			addText("fields and try again.");
		}
		catch(InvalidMessageException ee) {
			addText("Invalid message");
			box.add(Box.createVerticalStrut(6));
			addText("The sign is unable to display");
			addText("the following message:");
			addText(ee.getMessage());
			addText("Please select a different message");
		}
		catch(ParseException ee) {
			addText("Parsing error");
			box.add(Box.createVerticalStrut(6));
			addText("Please try again.");
		}
		catch(Exception ee) {
			sendEmailAlert(e);
			setFatal(true);
			addText("This program has encountered");
			addText("a serious problem.");
			addAssistanceMessage();
		}
		box.add(Box.createVerticalStrut(6));
		String lastLine = I18N.get("ExceptionForm.LastLine");
		if(lastLine != null)
			box.add(new CenteredLabel(lastLine));
		box.add(Box.createVerticalGlue());
		box.add(Box.createVerticalStrut(6));
		Box hbox = Box.createHorizontalBox();
		hbox.add(Box.createHorizontalGlue());
		JButton button = new JButton("OK");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) {
				if(fatal)
					System.exit(-1);
				setVisible(false);
				dispose();
			}
		});
		hbox.add(button);
		hbox.add(Box.createHorizontalStrut(10));
		button = new JButton("Detail");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) {
				new DetailDialog(e).setVisible(true);
			}
		});
		hbox.add(button);
		hbox.add(Box.createHorizontalGlue());
		box.add(hbox);
		box.add(Box.createVerticalStrut(6));
		JPanel panel = new JPanel();
		panel.add(box);
		Dimension size = panel.getPreferredSize();
		size.height += 24;
		size.width += 16;
		getContentPane().add(panel);
		setSize(size);
		Screen.centerOnCurrent(this);
	}

	/** Add a line of text to the exception dialog */
	protected void addText(String text) {
		box.add(new CenteredLabel(text));
	}

	/** Add a message about what to do for assistance */
	protected void addAssistanceMessage() {
		box.add(Box.createVerticalStrut(6));
		addText("For assistance, contact an");
		addText("IRIS system administrator.");
	}

	/** Send an e-mail alert to the system administrators */
	protected void sendEmailAlert(Exception e) {
		String sender = SystemAttrEnum.EMAIL_SENDER_CLIENT.getString();
		String recipient =
			SystemAttrEnum.EMAIL_RECIPIENT_BUGS.getString();
		if(sender != null && recipient != null) {
			try {
				sendEmailAlert(sender, recipient, e);
				box.add(Box.createVerticalStrut(6));
				addText("A detailed error report");
				addText("has been emailed to:");
				addText(recipient);
			}
			catch(MessagingException ex) {
				ex.printStackTrace();
				box.add(Box.createVerticalStrut(6));
				addText("Unable to send error");
				addText("report to:");
				addText(recipient);
			}
		}
	}

	/** Send an email alert to the specified recipient */
	protected void sendEmailAlert(String sender, String recipient,
		Exception e) throws MessagingException
	{
		StringWriter writer = new StringWriter(200);
		e.printStackTrace(new PrintWriter(writer));
		SEmail email = new SEmail(sender, recipient, "IRIS Exception",
			writer.toString());
		email.send();
	}

	/** Centered label component */
	static protected class CenteredLabel extends Box {
		CenteredLabel(String s) {
			super(BoxLayout.X_AXIS);
			add(Box.createHorizontalGlue());
			add(new JLabel(s));
			add(Box.createHorizontalGlue());
		}
	}

	/** Exception detail dialog */
	static protected class DetailDialog extends JDialog {
		protected DetailDialog(Exception e) {
			setTitle("Exception detail");
			setModal(true);
			Box box = Box.createVerticalBox();
			JTextArea area = new JTextArea();
			StringWriter writer = new StringWriter(200);
			e.printStackTrace(new PrintWriter(writer));
			area.append(writer.toString());
			JScrollPane scroll = new JScrollPane(area);
			box.add(scroll);
			box.add(Box.createVerticalStrut(6));
			JButton ok = new JButton("OK");
			ok.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent a) {
					setVisible(false);
					dispose();
				}
			});
			box.add(ok);
			box.add(Box.createVerticalStrut(6));
			Dimension size = box.getPreferredSize();
			size.height += 32;
			size.width += 16;
			getContentPane().add(box);
			setSize(size);
			Screen.centerOnCurrent(this);
		}
	}
}
