package atlantis;

import org.newdawn.slick.Animation;

import jig.ResourceManager;
import jig.Vector;

public class Worker extends GroundEntity {
	
	private static final String FACE_U_GRAPHIC_FILE = "atlantis/resource/diver-up1.png";
	private static final String FACE_D_GRAPHIC_FILE = "atlantis/resource/diver-down1.png";
	private static final String FACE_L_GRAPHIC_FILE = "atlantis/resource/diver-left1.png";
	private static final String FACE_R_GRAPHIC_FILE = "atlantis/resource/diver-right1.png";

	private static final String MOVE_L_ANIMATION_FILE = "atlantis/resource/diver-left.png";
	private static final String MOVE_R_ANIMATION_FILE = "atlantis/resource/diver-right.png";
	private static final String MOVE_U_ANIMATION_FILE = "atlantis/resource/diver-up.png";
	private static final String MOVE_D_ANIMATION_FILE = "atlantis/resource/diver-down.png";
	
	private static final float MAX_VELOCITY = 0.07f;       /* pixels/mS */
	
	private static final int ANIMATION_FRAMES = 2;
	private static final int ANIMATION_FRAME_DURATION = 200; /* mS */
	
	private static final int ANIMATION_FRAME_WIDTH = 54; /* pixels */
	private static final int ANIMATION_FRAME_HEIGHT = 85; /* pixels */
	
	public Worker() {
		this(0, 0);
	}

	public Worker(float x, float y) {
		this(x, y, STOPPED_VECTOR);
	}

	public Worker(float x, float y, Vector move_direction) {
		super(x, y, move_direction);
	}
	
	static {
		ResourceManager.loadImage(FACE_D_GRAPHIC_FILE);
		ResourceManager.loadImage(FACE_U_GRAPHIC_FILE);
		ResourceManager.loadImage(FACE_L_GRAPHIC_FILE);
		ResourceManager.loadImage(FACE_R_GRAPHIC_FILE);
		
		ResourceManager.loadImage(MOVE_D_ANIMATION_FILE);
		ResourceManager.loadImage(MOVE_U_ANIMATION_FILE);
		ResourceManager.loadImage(MOVE_L_ANIMATION_FILE);
		ResourceManager.loadImage(MOVE_R_ANIMATION_FILE);
	}
	
	public void beginMovement(Vector direction) {
		velocity = new Vector(direction.scale(MAX_VELOCITY));
		movement_direction = direction;
	}
	
	@Override
	public void update(final int delta) {
		super.update(delta);
		
		for (GroundEntity e : getPotentialCollisions()) {
			Vector my_position = getPosition();
			Vector their_position = e.getPosition();
			
			final double angle_to_other = 
					my_position.angleTo(their_position);
			final float distance_to_other =
					their_position.distance(my_position);
			
			System.out.println("Potential collision: " + 
					distance_to_other + " " + angle_to_other);
		}
	}
	
	private final String getMovementAnimationFilename(final Vector direction) {
		String animation_filename;

		if (direction.equals(LEFT_UNIT_VECTOR)) {
			animation_filename = MOVE_L_ANIMATION_FILE;
		} else if (direction.equals(UP_UNIT_VECTOR)) {
			animation_filename = MOVE_U_ANIMATION_FILE;
		} else if (direction.equals(RIGHT_UNIT_VECTOR)) {
			animation_filename = MOVE_R_ANIMATION_FILE;
		} else {
			animation_filename = MOVE_D_ANIMATION_FILE;
		}

		return animation_filename;
	}
	
	public Animation getMovementAnimation(final Vector direction) {
		String animation_filename = getMovementAnimationFilename(direction);
		
		Animation movement_animation = new Animation(
				ResourceManager.getSpriteSheet(animation_filename,
						ANIMATION_FRAME_WIDTH, ANIMATION_FRAME_HEIGHT), 0, 0,
				ANIMATION_FRAMES - 1, 0, true, ANIMATION_FRAME_DURATION, true);
		
		return movement_animation;
	}
	
	public final String getStillImageFilename(final Vector direction) {
		String graphic_filename;

		if (direction.equals(LEFT_UNIT_VECTOR)) {
			graphic_filename = FACE_L_GRAPHIC_FILE;
		} else if (direction.equals(UP_UNIT_VECTOR)) {
			graphic_filename = FACE_U_GRAPHIC_FILE;
		} else if (direction.equals(RIGHT_UNIT_VECTOR)) {
			graphic_filename = FACE_R_GRAPHIC_FILE;
		} else {
			graphic_filename = FACE_D_GRAPHIC_FILE;
		}

		return graphic_filename;
	}
}
