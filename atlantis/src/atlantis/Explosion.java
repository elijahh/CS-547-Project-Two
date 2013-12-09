package atlantis;

import org.newdawn.slick.Animation;

import jig.Entity;
import jig.ResourceManager;
import jig.Vector;

public class Explosion extends Entity{
	
	private Animation explode;
	
	private static final String EXPLODE_SET_SMALL = "atlantis/resource/ExplosionSet_Small.png";
	private static final String EXPLODE_SET_LARGE = "atlantis/resource/ExplosionSet_Large.png";
	private static final int ANIMATION_FRAME_WIDTH_SMALL = 24;
	private static final int ANIMATION_FRAME_HEIGHT_SMALL = 24;
	private static final int ANIMATION_FRAME_WIDTH_LARGE = 35;
	private static final int ANIMATION_FRAME_HEIGHT_LARGE = 38;
	private static final int ANIMATION_FRAMES = 7 ;
	private static final int ANIMATION_FRAME_DURATION = 100;
	
	Class entity_type;
	
	static {
		ResourceManager.loadImage(EXPLODE_SET_SMALL);
		ResourceManager.loadImage(EXPLODE_SET_LARGE);
	}
	
	public Explosion(final float x, final float y, AtlantisEntity entity) {
		super(x,y);
		entity_type = entity.getClass();
		if (entity_type == Soldier.class) {
			explode = new Animation(ResourceManager.getSpriteSheet(EXPLODE_SET_SMALL, ANIMATION_FRAME_WIDTH_SMALL, ANIMATION_FRAME_HEIGHT_SMALL),
					0, 0, ANIMATION_FRAMES-1, 0, true, ANIMATION_FRAME_DURATION, true);
		} else if(entity_type == TacticalSub.class) {
			explode = new Animation(ResourceManager.getSpriteSheet(EXPLODE_SET_LARGE, ANIMATION_FRAME_WIDTH_LARGE, ANIMATION_FRAME_HEIGHT_LARGE),
					0, 0, ANIMATION_FRAMES-1, 0, true, ANIMATION_FRAME_DURATION, true);
		}
		addAnimation(explode);
		explode.setLooping(false);
	}
	
	public boolean isActive() {
		return !explode.isStopped();
	}
	
	public void play() {
		
	}
}
