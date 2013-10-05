package test.concept.being.creature;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import concept.being.creature.RedCreature;
import concept.nutrient.Nutrient;
import concept.nutrient.WaterFountain;

public class RedCreatureTest {

	protected RedCreature being;
	
	@Before
	public void setUp() throws Exception {
		
		being = new RedCreature(22,33);
		
	}
	
	@Test
	public void testPosition() {
		
		assertEquals(22,being.getX());
		assertEquals(33,being.getY());

	}
	
	@Test
	public void testNeeds(){
		
		assertTrue(being.haveNeed(Nutrient.WATER));		
		assertFalse(being.haveNeed(Nutrient.FOOD));
		
	}

	@Test
	public void testReaction(){
		
		being.getGeomap().add(new WaterFountain(20, 33));
		being.react();
		assertTrue(being.getX()<22);
	}
	
}
