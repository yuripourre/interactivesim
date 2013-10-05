package application;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import map.GeoMap;
import br.com.etyllica.core.application.Application;
import br.com.etyllica.core.event.GUIEvent;
import br.com.etyllica.core.event.KeyEvent;
import br.com.etyllica.core.event.PointerEvent;
import br.com.etyllica.core.input.mouse.MouseButton;
import br.com.etyllica.core.video.Graphic;
import concept.Concept;
import concept.Fountain;
import concept.being.Being;
import concept.being.creature.BlueCreature;
import concept.being.creature.RedCreature;
import concept.being.creature.YellowCreature;
import concept.nutrient.FoodFountain;
import concept.nutrient.WaterFountain;

public class MibSimApplication extends Application{

	private final int MAP_WIDTH = 10;
	private final int MAP_HEIGHT = 10;
	
	private int mapX = 20;
	private int mapY = 5;

	private int width = 60;
	private int height = 60;
	
	private int TILE_SIZE = Fountain.TILE_SIZE;
	
	private boolean drawRadius = true;
	
	private boolean paused = false; 

	private List<Being> beings;

	private GeoMap geomap;

	private List<Concept> fountains = new ArrayList<Concept>();	

	public MibSimApplication(int w, int h) {
		super(w, h);
	}

	@Override
	public void load() {

		geomap = new GeoMap();

		generateRiver();
		generateFood();
		geomap.addAll(fountains);

		beings = new ArrayList<Being>();
		beings.add(new RedCreature(16,6));
		beings.add(new RedCreature(19,7));
		beings.add(new YellowCreature(38,19));
		beings.add(new YellowCreature(35,22));
		beings.add(new YellowCreature(36,28));
		beings.add(new BlueCreature(17,9));

		Random rand = new Random();

		for(Being being: beings){
			being.getGeomap().add(fountains.get(rand.nextInt(fountains.size())));
		}

		updateAtFixedRate(700);

		mapX = -mapX;
		mapY = -mapY;
		offsetMap();
		
		loading = 100;
	}

	private void generateRiver(){

		fountains.add(new WaterFountain(10,3));
		fountains.add(new WaterFountain(11,3));
		fountains.add(new WaterFountain(12,3));
		fountains.add(new WaterFountain(13,3));
		fountains.add(new WaterFountain(14,3));
		fountains.add(new WaterFountain(15,3));
		fountains.add(new WaterFountain(15,5));

	}

	private void generateFood(){

		fountains.add(new FoodFountain(12,9));
		fountains.add(new FoodFountain(13,9));
		fountains.add(new FoodFountain(14,9));

		fountains.add(new FoodFountain(12,10));
		fountains.add(new FoodFountain(13,10));
		fountains.add(new FoodFountain(14,10));		

	}

	@Override
	public void timeUpdate(){

		if(!paused){

			for(Being being: beings){
				being.react();
			}
		}
	}

	@Override
	public void draw(Graphic g) {

		g.setColor(Color.GREEN);
		for(int j=0;j<height;j++){
			for(int i=0;i<width;i++){
				g.fillRect(i*TILE_SIZE, j*TILE_SIZE, TILE_SIZE, TILE_SIZE);
			}
		}

		g.setColor(Color.BLACK);
		for(int j=0;j<height;j++){
			for(int i=0;i<width;i++){			
				g.drawRect(i*TILE_SIZE, j*TILE_SIZE, TILE_SIZE, TILE_SIZE);
			}
		}

		drawMap(g);
		drawBeings(g);

	}

	private void drawMap(Graphic g){
		geomap.draw(g);
	}	

	private void drawBeings(Graphic g){
		if(drawRadius){
			for(Being being: beings){
				being.drawInteractionRadius(g);
			}
		}
		for(Being being: beings){
			being.draw(g);
		}
	}

	@Override
	public GUIEvent updateKeyboard(KeyEvent event) {

		if(event.onKeyDown(KeyEvent.TSK_SETA_DIREITA)){
			//if(mapX>0){
				mapX--;
				offsetMap();
			//}			
		}
		
		if(event.onKeyDown(KeyEvent.TSK_SETA_ESQUERDA)){
			//if(mapX<MAP_WIDTH){
				mapX++;
				offsetMap();
			//}
			
		}
		
		if(event.onKeyDown(KeyEvent.TSK_SETA_BAIXO)){
			//if(mapY>0){
				mapY--;
				offsetMap();
			//}
			
		}
		
		if(event.onKeyDown(KeyEvent.TSK_SETA_CIMA)){
			//if(mapY<MAP_HEIGHT){
				mapY++;
				offsetMap();
			//}
			
		}
		
		if(event.onKeyDown(KeyEvent.TSK_H)){
			drawRadius = !drawRadius;
		}
		
		if(event.onKeyDown(KeyEvent.TSK_P)){
			paused = !paused;
		}

		return null;
	}
	
	private void offsetMap(){
		
		for(Being being: beings){
			being.setMapX(mapX);
			being.setMapY(mapY);
		}
		
		for(Concept fountain: fountains){
			fountain.setMapX(mapX);
			fountain.setMapY(mapY);
		}
		
	}

	int dragX = 0;
	int dragY = 0;
	
	@Override
	public GUIEvent updateMouse(PointerEvent event) {
		
		if(event.onButtonDown(MouseButton.MOUSE_BUTTON_LEFT)){
			
		}
		
		for(Being being: beings){
			if(being.onMouse(event)){
				being.setOnMouse(true);
			}else{
				being.setOnMouse(false);
			}
		}
		
		// TODO Auto-generated method stub
		return null;
	}

}

