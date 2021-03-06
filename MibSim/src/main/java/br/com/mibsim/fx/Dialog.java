package br.com.mibsim.fx;

import br.com.etyllica.animation.AnimationHandler;
import br.com.etyllica.animation.listener.OnAnimationFinishListener;
import br.com.etyllica.animation.scripts.OpacityAnimation;
import br.com.etyllica.core.graphics.Graphic;
import br.com.etyllica.layer.ImageLayer;
import br.com.etyllica.layer.StaticLayer;

public class Dialog implements OnAnimationFinishListener {

	private StaticLayer reportLabel;
	private StaticLayer exploreLabel;
	private StaticLayer hungryLabel;
	private StaticLayer cannibalLabel;
	
	private ImageLayer label;
	
	private ImageLayer dialogEffect;
	
	private OpacityAnimation opacityEffect;
	
	public Dialog() {
		super();
				
		dialogEffect = new ImageLayer("ui/dialog/ballon.png");
		dialogEffect.setVisible(false);

		opacityEffect = new OpacityAnimation(dialogEffect, 800);
		opacityEffect.setEndDelay(3000);
		opacityEffect.setListener(this);
		
		hungryLabel = new StaticLayer("ui/dialog/hamburger.png");
		exploreLabel = new StaticLayer("ui/dialog/map.png");
		reportLabel = new StaticLayer("ui/dialog/sheet.png");
		cannibalLabel = new StaticLayer("ui/dialog/evil.png");
		
		label = new ImageLayer();
		label.cloneLayer(hungryLabel);
	}
	
	public void showHungryDialog() {
		label.cloneLayer(hungryLabel);
		showDialog();
	}
	
	public void showExploreDialog() {
		label.cloneLayer(exploreLabel);
		showDialog();
	}
	
	public void showCannibalDialog() {
		label.cloneLayer(cannibalLabel);
		showDialog();
	}
	
	public void showReportDialog() {
		label.cloneLayer(reportLabel);
		showDialog();
	}
	
	private void showDialog() {
		dialogEffect.setVisible(true);
		opacityEffect.restart();
		AnimationHandler.getInstance().add(opacityEffect);
	}
	
	public void draw(Graphic g, int x, int y) {
		if(!dialogEffect.isVisible()) {
			return;
		}
		
		dialogEffect.draw(g, x, y);
		g.setOpacity(dialogEffect.getOpacity());
		label.draw(g, x, y);
		g.resetOpacity();
	}

	@Override
	public void onAnimationFinish(long now) {
		dialogEffect.setVisible(false);
	}

	public void setCoordinates(int x, int y) {
		dialogEffect.setCoordinates(x-16, y-16);
		label.centralize(dialogEffect);
	}
	
}
