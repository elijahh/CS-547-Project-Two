package atlantis;

import org.newdawn.slick.Image;

import atlantis.AtlantisEntity.Team;

import jig.Entity;
import jig.ResourceManager;
import jig.Vector;

public class Torpedo extends Entity {
	Image torpedo;
	private static final String BLUETORPEDOIMG_RSC = "atlantis/resource/soldier-torpedo-blue.png";
	private static final String REDTORPEDOIMG_RSC = "atlantis/resource/soldier-torpedo-red.png";
	
	static {
		ResourceManager.loadImage(BLUETORPEDOIMG_RSC);
		ResourceManager.loadImage(REDTORPEDOIMG_RSC);
	}
	
	public Torpedo(final float x, final float y, double theta, Team team) {
		super(x, y);
		
		if (team == Team.BLUE) { 
			torpedo = ResourceManager.getImage(BLUETORPEDOIMG_RSC).copy();
		} else {
			torpedo = ResourceManager.getImage(REDTORPEDOIMG_RSC).copy();
		}
		torpedo.setRotation((float) theta);
		
		addImage(torpedo);
	}
	
	public void update(int delta) {
		translate(Vector.getUnit(getRotation()).scale(delta / 16f));
	}
	
	@Override
	public double getRotation() {
		return torpedo.getRotation();
	}
	
	@Override
	public void setRotation(double theta) {
		torpedo.setRotation((float) theta);
	}
}
