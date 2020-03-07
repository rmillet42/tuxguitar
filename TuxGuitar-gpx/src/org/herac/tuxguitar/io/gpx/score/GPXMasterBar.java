package org.herac.tuxguitar.io.gpx.score;

import java.util.ArrayList;
import java.util.List;

public class GPXMasterBar {
	
	private int[] barIds;
	private int[] time;
	
	private int repeatCount;
	private boolean repeatStart;
	private int accidentalCount;
	private String mode;
	private String tripletFeel;
	private int alternateEndings;
	private List<String> directionsTargets = new ArrayList<String>();
	private List<String> directionsJumps = new ArrayList<String>();

	public GPXMasterBar(){
		this.accidentalCount = 0;
		this.alternateEndings = 0;
		this.mode = null;
	}
	
	public int[] getBarIds() {
		return this.barIds;
	}
	
	public void setBarIds(int[] barIds) {
		this.barIds = barIds;
	}
	
	public int[] getTime() {
		return time;
	}
	
	public void setTime(int[] time) {
		this.time = time;
	}

	public int getRepeatCount() {
		return repeatCount;
	}

	public void setRepeatCount(int repeatCount) {
		this.repeatCount = repeatCount;
	}

	public boolean isRepeatStart() {
		return repeatStart;
	}

	public void setRepeatStart(boolean repeatStart) {
		this.repeatStart = repeatStart;
	}

	public int getAccidentalCount() {
		return accidentalCount;
	}

	public void setAccidentalCount(int accidentalCount) {
		this.accidentalCount = accidentalCount;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getTripletFeel() {
		return this.tripletFeel;
	}
	
	public void setTripletFeel(String tripletFeel) {
		this.tripletFeel = tripletFeel;
	}

	public int getAlternateEndings() {
		return alternateEndings;
	}

	public void setAlternateEndings(int alternateEndings) {
		this.alternateEndings = alternateEndings;
	}

	public List<String> getDirectionsTargets() {
		return this.directionsTargets;
	}

	public List<String> getDirectionsJumps() {
		return this.directionsJumps;
	}
}
