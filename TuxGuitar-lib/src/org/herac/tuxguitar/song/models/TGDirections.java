package org.herac.tuxguitar.song.models;

public class TGDirections {
	public enum Jump {
		DA_CODA("Da Coda", Target.CODA),
		DA_CAPO("Da Capo", Target.CAPO),
		DA_CAPO_AL_CODA("D.C. al Coda", Target.CAPO, DA_CODA),
		DA_DOUBLE_CODA("Da Double Coda", Target.DOUBLE_CODA),
		DA_SEGNO("Dal Segno", Target.SEGNO),
		DA_SEGNO_AL_CODA("D.S. al Coda", Target.SEGNO, DA_CODA),
		DA_SEGNO_SEGNO("Da Segno Segno", Target.SEGNO_SEGNO);

		private final String displayName;
		private final Jump nextJump;
		private final Target target;

		private Jump(String displayName, Target target) {
			this(displayName, target, null);
		}

		private Jump(String displayName, Target target, Jump nextJump) {
			this.displayName = displayName;
			this.target = target;
			this.nextJump = nextJump;
		}

		public String getDisplayName() {
			return this.displayName;
		}

		public Jump getNextJump() {
			return this.nextJump;
		}

		public Target getTarget() {
			return this.target;
		}
	}

	public enum Target {
		CAPO("Capo"),
		SEGNO("Segno"),
		CODA("Coda"),
		DOUBLE_CODA("Double Coda"),
		SEGNO_SEGNO("Segno Segno");

		private final String displayName;

		private Target(String displayName) {
			this.displayName = displayName;
		}

		public String getDisplayName() {
			return this.displayName;
		}
	}

	private Jump[] jumps = null;
	private Target[] targets = null;

	public Jump[] getJumps() {
		return this.jumps;
	}

	public void setJumps(Jump[] jumps) {
		this.jumps = jumps;
	}

	public Target[] getTargets() {
		return this.targets;
	}

	public void setTargets(Target[] targets) {
		this.targets = targets;
	}

	public void copyFrom(TGDirections other) {
		if (other.jumps != null) {
			this.jumps = other.getJumps().clone();
		}
		if (other.targets != null) {
			this.targets = other.getTargets().clone();
		}
	}
}
