package net.sf.mzmine.modules.visualization.oldtwod;

import java.awt.Color;
import java.awt.Graphics;
import java.text.NumberFormat;

import javax.swing.JPanel;

import net.sf.mzmine.main.MZmineCore;
import net.sf.mzmine.userinterface.Desktop;

public class OldTwoDXAxis extends JPanel {

	private final int leftMargin = 100;
	private final int rightMargin = 5;

	private OldTwoDDataSet dataset;

	public OldTwoDXAxis(OldTwoDDataSet dataset) {
		super();
		this.dataset = dataset;
	}

	public void paint(Graphics g) {


		super.paint(g);

		Desktop desktop = MZmineCore.getDesktop();
        NumberFormat rtFormat = desktop.getRTFormat();

        int minX = 0;
        int maxX = 0;
        if (desktop!=null) {
        	minX = dataset.getCurrentMinScan();
        	maxX = dataset.getCurrentMaxScan();
        }
        
		int w = getWidth();
		double h = getHeight();

		int numofscans = maxX-minX+1;
		double pixelsperscan = (double)(w-leftMargin-rightMargin) / (double)numofscans;
		if (pixelsperscan<=0) { return; }

		int scanspertic = 1;
		while ( (scanspertic * pixelsperscan) < 60 ) { scanspertic++;}

		double pixelspertic = (double)scanspertic * pixelsperscan;
		int numoftics = (int)java.lang.Math.floor((double)numofscans / (double)scanspertic);


		// Draw axis
		this.setForeground(Color.black);
		g.drawLine((int)leftMargin,0,(int)(w-rightMargin),0);

		// Draw tics and numbers
		String tmps;
		double xpos = leftMargin;
		int xval = minX;
		for (int t=0; t<numoftics; t++) {
			// if (t==(numoftics-1)) { this.setForeground(Color.red); }

			tmps = null;
			tmps = rtFormat.format(xval); 
				
			g.drawLine((int)java.lang.Math.round(xpos), 0, (int)java.lang.Math.round(xpos), (int)(h/4));
			g.drawBytes(tmps.getBytes(), 0, tmps.length(), (int)java.lang.Math.round(xpos),(int)(3*h/4));

			xval += scanspertic;
			xpos += pixelspertic;
		}

	}

}


