package br.com.mibsim.specie;

import br.com.etyllica.layer.ImageLayer;
import br.com.mibsim.building.basement.Basement;


public class GreenUltralisk extends Speciemen {

	public GreenUltralisk(int x, int y, Basement basement) {
		super(x, y, 100, 108, "green_ultralisk.png", basement);
		
		deadLayer = new ImageLayer("green_ultralisk_dead.png");
	}

}
