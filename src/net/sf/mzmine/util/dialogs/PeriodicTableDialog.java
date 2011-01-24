/*
 * Copyright 2006-2011 The MZmine 2 Development Team
 * 
 * This file is part of MZmine 2.
 * 
 * MZmine 2 is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * MZmine 2 is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * MZmine 2; if not, write to the Free Software Foundation, Inc., 51 Franklin
 * St, Fifth Floor, Boston, MA 02110-1301 USA
 */

package net.sf.mzmine.util.dialogs;

import java.awt.BorderLayout;
import java.util.EventObject;

import javax.swing.JDialog;

import net.sf.mzmine.main.MZmineCore;

import org.openscience.cdk.event.ICDKChangeListener;
import org.openscience.jchempaint.dialog.PeriodicTablePanel;

public class PeriodicTableDialog extends JDialog implements ICDKChangeListener {

	private PeriodicTablePanel periodicTable;
	private String elementSymbol;

	public PeriodicTableDialog() {
		
		super(MZmineCore.getDesktop().getMainFrame(), "Choose an element...", true);
		
		setLayout(new BorderLayout());
		
		periodicTable = new PeriodicTablePanel();
		periodicTable.addCDKChangeListener(this);
		add(BorderLayout.CENTER, periodicTable);

		pack();
		
		setLocationRelativeTo(MZmineCore.getDesktop().getMainFrame());
	}

	public void stateChanged(EventObject event) {

		if (event.getSource() == periodicTable) {
			try {
				elementSymbol = periodicTable.getSelectedElement();
			} catch (Exception e) {
				e.printStackTrace();
			}
			dispose();
		}
	}
	
	public String getSelectedElement() {
		return elementSymbol;
	}

}
