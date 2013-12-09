package atlantis;

import org.newdawn.slick.Animation;

import jig.Entity;
import jig.ResourceManager;
import jig.Vector;

public class ShipExplosion extends Entity{
	
	private Animation explode;
	
	private static final String EXPLODE_SET = "atlantis/resource/ship_explosion_set.png";
	private static final int ANIMATION_FRAME_WIDTH = 100;
	private static final int ANIMATION_FRAME_HEIGHT = 101;

	private static final int ANIMATION_FRAMES = 16 ;
	private static final int ANIMATION_FRAME_DURATION = 200;
	
	static {
		ResourceManager.loadImage(EXPLODE_SET);
	}
	
	public ShipExplosion(final float x, final float y) {
		super(x,y);
		explode = new Animation(ResourceManager.getSpriteSheet(EXPLODE_SET, ANIMATION_FRAME_WIDTH, ANIMATION_FRAME_HEIGHT),
					0, 0, ANIMATION_FRAMES-1, 0, true, ANIMATION_FRAME_DURATION, true);
	
		addAnimation(explode);
		explode.setLooping(false);
	}
	
	public boolean isActive() {
		return !explode.isStopped();
	}
}
