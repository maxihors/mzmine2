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

package net.sf.mzmine.modules.batchmode;

import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.JList;

import net.sf.mzmine.data.ParameterSet;

public class BatchListComponent extends JList {

    BatchListComponent(Vector<BatchStepWrapper> batchSteps) {
        super(batchSteps);
    }

    public String getToolTipText(MouseEvent e) {

        int index = locationToIndex(e.getPoint());
        if (index > -1) {
            ParameterSet parameters = ((BatchStepWrapper) getModel().getElementAt(
                    index)).getParameters();
            return parameters.toString().replace(", ", "\n");
        }
        return null;
    }

}
