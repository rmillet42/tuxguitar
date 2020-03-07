package org.herac.tuxguitar.player.base;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.herac.tuxguitar.song.models.TGDirections;
import org.herac.tuxguitar.song.models.TGDirections.Target;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.song.models.TGSong;

public class MidiRepeatController {
	
	private TGSong song;
	private int count;
	private int index;
	private int lastIndex;
	private boolean shouldPlay;
	private boolean repeatOpen;
	private long repeatStart;
	private long repeatEnd;
	private long repeatMove;
	private int repeatStartIndex;
	private int repeatNumber;
	private int repeatClose; /* Number of times the sequence is repeated */
	private int repeatAlternative;
	private int repeatAlternativeNumber; /* Current loop in a repeatAlternative*/
	private int sHeader;
	private int eHeader;

	private final Map<TGDirections.Jump, Integer> jumps = new HashMap<TGDirections.Jump, Integer>();
	private final Map<TGDirections.Target, Integer> targets = new HashMap<TGDirections.Target, Integer>();
	/* Indicate the next jump to take when we followed a jump "... al ..." */
	private TGDirections.Jump nextJump = null;

	public MidiRepeatController(TGSong song, int sHeader , int eHeader){
		this.song = song;
		this.sHeader = sHeader;
		this.eHeader = eHeader;
		this.count = song.countMeasureHeaders();
		this.index = 0;
		this.lastIndex = -1;
		this.shouldPlay = true;
		this.repeatOpen = true;
		this.repeatAlternative = 0;
		this.repeatStart = TGDuration.QUARTER_TIME;
		this.repeatEnd = 0;
		this.repeatMove = 0;
		this.repeatStartIndex = 0;
		this.repeatNumber = 0;
		this.repeatClose = 0;

		for (int i = 0; i < this.count; ++i) {
			TGMeasureHeader h = this.song.getMeasureHeader(i);
			if (h.getDirections().getJumps() != null) {
				for (TGDirections.Jump j : h.getDirections().getJumps()) {
					this.jumps.put(j, i);

					if (j.getTarget() == Target.CAPO) {
						this.targets.put(Target.CAPO, 0);
					}
				}
			}

			if (h.getDirections().getTargets() != null) {
				for (TGDirections.Target j : h.getDirections().getTargets()) {
					this.targets.put(j, i);
				}
			}
		}
	}

	public void process(){
		TGMeasureHeader header = this.song.getMeasureHeader(this.index);

		//Verifica si el compas esta dentro del rango.
		if( (this.sHeader != -1 && header.getNumber() < this.sHeader) || ( this.eHeader != -1 && header.getNumber() > this.eHeader ) ){
			this.shouldPlay = false;
			this.index ++;
			return;
		}

		//Abro repeticion siempre para el primer compas.
		if( (this.sHeader != -1 && header.getNumber() == this.sHeader ) || header.getNumber() == 1 ){
			this.repeatStartIndex = this.index;
			this.repeatStart = header.getStart();
			this.repeatOpen = true;
		}

		//Por defecto el compas deberia sonar
		this.shouldPlay = true;

		//En caso de existir una repeticion nueva,
		//guardo el indice de el compas donde empieza una repeticion
		if (header.isRepeatOpen()) {
			if (this.index != this.repeatStartIndex) {
				this.repeatAlternativeNumber = 0;
			}
			this.repeatStartIndex = this.index;
			this.repeatStart = header.getStart();
			this.repeatOpen = true;

			//Si es la primer vez que paso por este compas
			//Pongo numero de repeticion y final alternativo en cero
			if(this.index > this.lastIndex){
				this.repeatClose = 0;
				this.repeatNumber = 0;
				this.repeatAlternative = 0;
			}
		}
		else{
			//verifico si hay un final alternativo abierto
			if(this.repeatAlternative == 0){
				this.repeatAlternative = header.getRepeatAlternative();
			}

			//Si estoy en un final alternativo.
			//el compas solo puede sonar si el numero de repeticion coincide con el numero de final alternativo.
			if ((this.repeatAlternative > 0) && ((this.repeatAlternative & (1 << (this.repeatAlternativeNumber))) == 0)){
				this.repeatMove -= header.getLength();
				if (header.getRepeatClose() >0){
					this.repeatAlternative = 0;
				}
				this.shouldPlay = false;
				this.index ++;
				return;
			}
		}

		//antes de ejecutar una posible repeticion
		//guardo el indice del ultimo compas tocado 
		this.lastIndex = Math.max(this.lastIndex,this.index);

		// Handle Jumps / Taget (Da Coda, etc…)
		TGDirections.Jump[] curJumps = header.getDirections().getJumps();
		if (curJumps != null && curJumps.length > 0) {
			for (TGDirections.Jump j : curJumps) {
				/* We have not yet took this jump */
				if (this.jumps.containsKey(j)) {
					if (this.nextJump == null) {
						/* Ensure no other jump will need to reach this one "... al ..." */
						boolean isLinked = false;
						for (TGDirections.Jump jj : this.jumps.keySet()) {
							if (jj.getNextJump() == j) {
								isLinked = true;
								break;
							}
						}

						if (isLinked) {
							continue;
						}
					} else if (this.nextJump != j) {
						/* We are trying to reach a specific jump, ignore this one */
						continue;
					}

					/* Looking if there is another jump between this one and the next section */
					int endSection = -1;
					for (Entry<Target, Integer> t : this.targets.entrySet()) {
						if (t.getValue() >= this.index && (endSection == -1 || t.getValue() < endSection)) {
							endSection = t.getValue();
						}
					}

					if (endSection >= 0) {
						boolean skip = false;
						for (int t : this.jumps.values()) {
							if (t > this.index && t < endSection) {
								/* There is another jump between this one and the next section */
								skip = true;
								break;
							}
						}
						if (skip) {
							continue;
						}
					}

					boolean doJump = false;
					if (!this.repeatOpen || (this.repeatClose > 0 && this.repeatNumber == this.repeatClose) || this.nextJump == j) {
						doJump = true;
					} else {
						/* Looking for repeatBar or new Section */
						int endLookup = this.count;
						for (Entry<Target, Integer> t : this.targets.entrySet()) {
							if (t.getValue() >= this.index && t.getValue() < endLookup) {
								endLookup = t.getValue();
							}
						}

						doJump = true;
						for (int i2 = this.index; i2 < endLookup; ++i2) {
							if (this.song.getMeasureHeader(i2).getRepeatClose() > 0) {
								doJump = false;
								break;
							}
						}
					}

					if (doJump) {
						Integer targetIndex = this.targets.get(j.getTarget());
						this.jumps.remove(j);
						if (targetIndex == null) {
							System.err.println("Target " + j.getTarget() + " not found when jumping from " + j + ", index " + this.index);
						} else {
							this.nextJump = j.getNextJump();
							this.repeatClose = 0;
							this.repeatEnd = header.getStart() + header.getLength();
							this.repeatMove += this.repeatEnd - this.song.getMeasureHeader(targetIndex).getStart();
							this.index = targetIndex;
							this.lastIndex = -1;
							this.repeatNumber = 0;
							this.repeatStart = 0;
							this.repeatOpen = false;
							this.repeatAlternative = 0;
							return;
						}
					}
				}
			}
		}

		//si hay una repeticion la hago
		if (header.getRepeatClose() > 0) {
			if (this.repeatAlternative > 0) {
				this.repeatClose = header.getRepeatClose();
				this.repeatEnd = header.getStart() + header.getLength();
				this.repeatMove += this.repeatEnd - this.repeatStart;
				this.index = this.repeatStartIndex - 1;
				this.repeatNumber = 0; /* Reset repeat section */
				this.repeatAlternativeNumber++;
			} else if (this.repeatOpen && this.repeatNumber < header.getRepeatClose()) {
				this.repeatClose = header.getRepeatClose();
				this.repeatEnd = header.getStart() + header.getLength();
				this.repeatMove += this.repeatEnd - this.repeatStart;
				this.index = this.repeatStartIndex - 1;
				this.repeatNumber++;
			} else {
				this.repeatClose = 0;
				this.repeatNumber = 0;
				this.repeatEnd = 0;
				this.repeatOpen = false;
			}
			this.repeatAlternative = 0;
		}

		this.index ++;
	}

	public boolean finished(){
		return (this.index >= this.count);
	}
	
	public boolean shouldPlay(){
		return this.shouldPlay;
	}
	
	public int getIndex(){
		return this.index;
	}
	
	public long getRepeatMove(){
		return this.repeatMove;
	}
}
