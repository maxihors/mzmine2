/*
 * Copyright 2006-2010 The MZmine 2 Development Team
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
 * MZmine 2; if not, write to the Free Software Foundation, Inc., 51 Franklin St,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */
package net.sf.mzmine.modules.peaklistmethods.identification.mascot;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.MalformedURLException;

import net.sf.mzmine.data.ParameterSet;
import net.sf.mzmine.data.PeakList;
import net.sf.mzmine.data.RawDataFile;
import net.sf.mzmine.data.impl.SimpleParameterSet;
import net.sf.mzmine.desktop.Desktop;
import net.sf.mzmine.desktop.MZmineMenu;
import net.sf.mzmine.main.MZmineCore;
import net.sf.mzmine.modules.batchmode.BatchStep;
import net.sf.mzmine.modules.batchmode.BatchStepCategory;
import net.sf.mzmine.taskcontrol.Task;
import net.sf.mzmine.util.dialogs.ExitCode;
import net.sf.mzmine.util.dialogs.ParameterSetupDialog;

/**
 *
 */
public class MascotSearch implements BatchStep, ActionListener {

	public static final String MODULE_NAME = "Mascot MS/MS Ion Search (experimental)";

	private Desktop desktop;
	private MascotSearchParameters parameters;

	/**
	 * @see net.sf.mzmine.main.MZmineModule#getParameterSet()
	 */
	public ParameterSet getParameterSet() {
		return parameters;
	}

	/**
	 * @see net.sf.mzmine.main.MZmineModule#initModule(net.sf.mzmine.main.MZmineCore)
	 */
	public void initModule() {
		this.desktop = MZmineCore.getDesktop();

//		try {
//			parameters = new MascotSearchParameters();
//		} catch (MalformedURLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

		desktop
				.addMenuItem(
						MZmineMenu.IDENTIFICATION,
						MODULE_NAME,
						"Identification based on raw MS/MS data from one or more peptides",
						KeyEvent.VK_R, false, this, null);

	}

	/**
	 * @see net.sf.mzmine.main.MZmineModule#setParameters(net.sf.mzmine.data.ParameterSet)
	 */
	public void setParameters(ParameterSet parameterValues) {
		this.parameters = (MascotSearchParameters) parameterValues;
	}

	/**
	 * @see net.sf.mzmine.modules.batchmode.BatchStep#getBatchStepCategory()
	 */
	public BatchStepCategory getBatchStepCategory() {
		return BatchStepCategory.IDENTIFICATION;
	}

	/**
	 * @see net.sf.mzmine.modules.batchmode.BatchStep#setupParameters(net.sf.mzmine.data.ParameterSet)
	 */
	public ExitCode setupParameters(ParameterSet parameters) {
		ParameterSetupDialog dialog = new ParameterSetupDialog(
				"Please set parameter values for " + toString(),
				(SimpleParameterSet) parameters);
		dialog.setVisible(true);




		return dialog.getExitCode();
	}

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {

		PeakList[] peakLists = desktop.getSelectedPeakLists();

		if (peakLists.length == 0) {
			desktop
					.displayErrorMessage("Please select a peak lists to process");
			return;
		}
		try {
			parameters = new MascotSearchParameters();
		} catch (MalformedURLException ee) {
			// TODO Auto-generated catch block
			ee.printStackTrace();
		} catch (IOException ee) {
			// TODO Auto-generated catch block
			ee.printStackTrace();
		}


		for (int i = 0; i < peakLists.length; i++) {
			if (peakLists[i].getNumberOfRawDataFiles() > 1) {
				desktop
						.displayErrorMessage("Mascot search can only be performed on peak lists which have a single column");
				return;
			}
		}

		ExitCode exitCode = setupParameters(parameters);
		if (exitCode != ExitCode.OK)
			return;

		runModule(null, peakLists, parameters.clone());

	}

	/**
	 * @see net.sf.mzmine.modules.batchmode.BatchStep#runModule(net.sf.mzmine.data.RawDataFile[],
	 *      net.sf.mzmine.data.PeakList[], net.sf.mzmine.data.ParameterSet,
	 *      net.sf.mzmine.taskcontrol.Task[]Listener)
	 */
	public Task[] runModule(RawDataFile[] dataFiles, PeakList[] peakLists,
			ParameterSet parameters) {
		if (peakLists == null) {
			throw new IllegalArgumentException(
					"Cannot run identification without a peak list");
		}

		// prepare a new sequence of tasks
		Task tasks[] = new MascotSearchTask[peakLists.length];
		for (int i = 0; i < peakLists.length; i++) {
			tasks[i] = new MascotSearchTask(
					(MascotSearchParameters) parameters, peakLists[i]);
		}

		// execute the sequence
		MZmineCore.getTaskController().addTasks(tasks);

		return tasks;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return MODULE_NAME;
	}

}