package br.com.mibsim.specie;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.com.etyllica.core.graphics.Graphic;
import br.com.etyllica.core.graphics.SVGColor;
import br.com.etyllica.layer.AnimatedLayer;
import br.com.etyllica.layer.ImageLayer;
import br.com.etyllica.linear.Point2D;
import br.com.etyllica.linear.PointInt2D;
import br.com.etyllica.util.kdtree.KDTree;
import br.com.etyllica.util.kdtree.KeyDuplicateException;
import br.com.etyllica.util.kdtree.KeySizeException;
import br.com.mibsim.building.basement.Basement;
import br.com.mibsim.fx.Dialog;
import br.com.mibsim.model.fountain.Fountain;
import br.com.mibsim.model.fountain.Nutrient;
import br.com.mibsim.planning.PlanningAction;
import br.com.mibsim.planning.PlanningTask;
import br.com.tide.action.player.ActionPlayer;

public class Speciemen extends ActionPlayer {

	protected int health = 10000;
	protected int currentHealth = health;
	protected int hungryThreshold = health/3;
	protected int metabolism = 2;

	protected int breathEnergy = 1;
	protected int walkEnergy = 5;
	protected int reportEnergy = 10;
	protected int turnEnergy = 1;
	protected int sensorRadius = 100;

	protected Nutrient nutrient = Nutrient.WATER;

	protected boolean dead = false;
	protected boolean hungry = false;

	protected AnimatedLayer layer;
	protected ImageLayer deadLayer;

	private Dialog dialog = new Dialog();

	protected Basement basement;

	private PlanningTask lastTask;

	private Fountain nearest = null;

	protected List<PlanningTask> tasks = new ArrayList<PlanningTask>();

	protected Set<Fountain> found = new HashSet<Fountain>();

	protected Set<Fountain> knownFountains = new HashSet<Fountain>();

	protected KDTree<Fountain> fountainsTree = new KDTree<Fountain>(2);

	public Speciemen(int x, int y, int tileW, int tileH, String path, Basement basement) {
		super(x, y);
		
		startAngle = 0;

		layer = new AnimatedLayer(x, y, tileW, tileH, path);
		layer.setAnimateHorizontally(false);
		layer.setSpeed(100);
		layer.setFrames(7);

		if(basement != null) {
			this.basement = basement;

			tasks.add(new PlanningTask(PlanningAction.REPORT, basement.getCenter()));
		}
	}

	@Override
	public void update(long now) {
		super.update(now);

		if(dead)
			return;

		act();

		if(isWalking()) {
			walk(now);
			loseEnergy(walkEnergy);
		} else if(isTurning()) {
			layer.setAngle(angle);
			loseEnergy(turnEnergy);
		} else {
			loseEnergy(breathEnergy);	
		}

		if(currentHealth <= 0) {
			die(now);			
		}

		if(!hungry) {
			if(isHungry()) {
				hungry = true;
				dialog.showHungryDialog();
			}
		}
	}

	private void walk(long now) {
		layer.animate(now);
		layer.setCoordinates(x, y);

		dialog.setCoordinates(x, y);
	}

	private void die(long now) {
		dead = true;

		deadLayer.centralize(layer);
	}

	private void loseEnergy(int energy) {
		currentHealth -= energy*metabolism;
	}

	private void act() {

		if(tasks.isEmpty())
			return;

		PlanningTask currentTask = currentTask();

		if(isHungry()) {
			searchForFood();
		}

		walkToTarget(currentTask);
	}

	private void walkToTarget(PlanningTask currentTask) {

		PointInt2D target = currentTask.getTarget();

		if(currentTask != lastTask) {
			lastTask = currentTask;
			turnToTarget(target);
		}

		if(!reachTarget(target)) {

			if(!isWalking()) {
				walkForward();
			}

		} else {
			stopWalk();

			if(!currentTask.isCompleted()) {
				completeTask(currentTask);
				tasks.remove(currentTask);
			}
		}

	}

	private void completeTask(PlanningTask task) {

		task.setCompleted(true);

		switch (task.getAction()) {

		case REPORT:
			dialog.showReportDialog();
			askDesignation(task);
			break;

		case EXPLORE:
			dialog.showExploreDialog();
			reportBasement(task);
			break;

		case FEED:
			drainNutrients(nearest);
			break;
		}
	}

	private void searchForFood() {

		if(fountainsTree.isEmpty())
			return;

		try {

			nearest = fountainsTree.nearest(getPositionAsArray());

		} catch (KeySizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		searchForFood(nearest);
	}

	private void searchForFood(Fountain fountain) {

		PlanningTask feed = new PlanningTask(PlanningAction.FEED, fountain.getCenter());

		tasks.add(feed);
	}

	private void drainNutrients(Fountain fountain) {
		int quantity = fountain.drain(this);

		currentHealth += quantity;

		if(currentHealth >= health) {
			currentHealth = health;
			return;
		} else {
			searchForFood(fountain);
		}
	}

	private void askDesignation(PlanningTask report) {
		if(basement == null)
			return;

		PlanningTask designation = basement.askForDesignation(report);

		tasks.add(designation);
	}

	private void reportBasement(PlanningTask exploreTask) {
		if(basement == null)
			return;

		tasks.add(basement.reportToBasement(exploreTask));

		//Share knowledge
		if(!found.isEmpty()) {

			for(Fountain fountain: found) {
				basement.reportFountain(fountain);
			}

			found.clear();
		}

		//Clear database
		knownFountains.clear();
		fountainsTree = new KDTree<Fountain>(2);

		//Update database
		for(Fountain fountain: basement.getFountainsSet()) {
			addFountain(fountain);
			knownFountains.add(fountain);
		}

	}

	private boolean isHungry() {
		return currentHealth<hungryThreshold;
	}

	private void turnToTarget(PointInt2D target) {

		int cx = layer.getX()+layer.utilWidth()/2;
		int cy = layer.getY()+layer.utilHeight()/2;

		double angle = Point2D.angle(cx, cy, target.getX(), target.getY());

		this.setStartAngle(angle+90);

		//Compensate sprite rotation
		layer.setAngle(angle+90);
	}

	private boolean reachTarget(PointInt2D target) {

		int cx = layer.getX()+layer.utilWidth()/2;
		int cy = layer.getY()+layer.utilHeight()/2;

		double distance = Point2D.distance(cx, cy, target.getX(), target.getY());

		return distance < 10;		
	}

	private PlanningTask currentTask() {
		return tasks.get(tasks.size()-1);
	}

	public void addTask(PlanningTask task) {
		tasks.add(task);
	}

	public Basement getBasement() {
		return basement;
	}

	public void setBasement(Basement basement) {
		this.basement = basement;
	}

	public void draw(Graphic g, int x, int y) {

		if(!dead) {
			layer.draw(g, x, y);
			dialog.draw(g, x, y);
		} else {
			deadLayer.draw(g, x, y);
		}
	}

	public void drawSensors(Graphic g, int x, int y) {
		g.setColor(Color.BLACK);
		g.setAlpha(50);
		g.fillCircle(layer.getX()+layer.utilWidth()/2+x, layer.getY()+layer.utilHeight()/2+y, sensorRadius);
		g.resetOpacity();
	}

	public void drawHealthBar(Graphic g, int x, int y) {

		int border = 1;

		g.setColor(Color.BLACK);

		g.fillRect(layer.getX()+x, layer.getY()+y, layer.utilWidth(), 4*border);

		g.setColor(healthColor());

		int width = layer.utilWidth()*currentHealth/health;

		g.fillRect(layer.getX()+x+border, layer.getY()+y+border, width-2*border, 2*border);
	}

	private Color healthColor() {
		if(isHungry())
			return Color.RED;

		return SVGColor.LIME_GREEN;
	}

	public double[] getPositionAsArray() {

		double[] position = new double[2];
		position[0] = getDx();
		position[1] = getDy();

		return position;
	}

	public int getSensorRadius() {
		return sensorRadius;
	}

	public void discovered(Fountain fountain) {

		if(fountain.getNutrient() != nutrient || knownFountains.contains(fountain))
			return;

		found.add(fountain);
		knownFountains.add(fountain);
		addFountain(fountain);
	}

	private void addFountain(Fountain fountain) {
		try {
			fountainsTree.insert(fountain.getPositionAsArray(), fountain);
		} catch (KeySizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyDuplicateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

}
