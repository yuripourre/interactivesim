package br.com.mibsim.specie;

import br.com.mibsim.building.basement.Basement;


public class BlueLurker extends Specie {

	public BlueLurker(int x, int y, Basement basement) {
		super(x, y, 66, 64, "blue_lurker.png", basement);
		
		layer.setFrames(10);
	}

}