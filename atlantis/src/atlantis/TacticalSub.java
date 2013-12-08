package atlantis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.newdawn.slick.Animation;

import atlantis.AtlantisEntity.Team;
import jig.ResourceManager;
import jig.Shape;
import jig.Vector;

public class TacticalSub extends FloatingEntity {
	
	private static final String FACE_U_GRAPHIC_FILE = "atlantis/resource/tactical-up.png";
	private static final String FACE_D_GRAPHIC_FILE = "atlantis/resource/tactical-down.png";
	private static final String FACE_L_GRAPHIC_FILE = "atlantis/resource/tactical-left.png";
	private static final String FACE_R_GRAPHIC_FILE = "atlantis/resource/tactical-right.png";
	
	private static final String MOVE_L_ANIMATION_FILE = "atlantis/resource/tactical-left.png";
	private static final String MOVE_R_ANIMATION_FILE = "atlantis/resource/tactical-right.png";
	private static final String MOVE_U_ANIMATION_FILE = "atlantis/resource/tactical-up.png";
	private static final String MOVE_D_ANIMATION_FILE = "atlantis/resource/tactical-down.png";
	
	private static final String RED_ICON = "atlantis/resource/red-worker.png";
	private static final String BLUE_ICON = "atlantis/resource/blue-worker.png";
	
	private static final float MAX_VELOCITY = 0.09f;       /* pixels/mS */
	
	private static final int ANIMATION_FRAMES = 1;
	private static final int ANIMATION_FRAME_DURATION = 200; /* mS */
	
	private static int ANIMATION_FRAME_WIDTH = 100; /* pixels */
	private static int ANIMATION_FRAME_HEIGHT = 100; /* pixels */
	
	private static List<TacticalSub> tactical_subs = new LinkedList<TacticalSub>();
	
	public TacticalSub() {
		this(0,0);
	}
	
	public TacticalSub(float x, float y) {
		this(x, y, STOPPED_VECTOR);
	}

	public TacticalSub(float x, float y, Vector movement_direction) {
		super(x, y, movement_direction);
		
		MAX_HEALTH_VALUE = 500;
		health = MAX_HEALTH_VALUE;
		
		tactical_subs.add(this);
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
		
		ResourceManager.loadImage(RED_ICON);
		ResourceManager.loadImage(BLUE_ICON);
	}
	
	
	public void fire(AtlantisEntity target) {
		double theta = this.getPosition().angleTo(target.getPosition());
		
		if (getPosition().distance(target.getPosition()) < 400) {
			stopMoving();
			
			if (tacticalTorpedo == null) {
				tacticalTorpedo = new TacticalTorpedo(getX(), getY(), theta, team);
			} else {
				tacticalTorpedo.setPosition(new Vector(getX(), getY()));
				tacticalTorpedo.setRotation(theta);
			}
			torpedoTimer = 6000;

			double damage = Math.random() * 5 % 5;
			target.health -= damage;
			reward += damage * 5;
			this.health -= Math.random() * 5 % 5;

			if (target.health <= 0) isAttacking = false;
			System.out.println("target health: " + target.health);
			System.out.println("this health: " + health);
		} else {
			setDestination(target.getPosition());
		}
	}
	
	@Override
	void beginMovement(Vector direction) {
		velocity = new Vector(direction.scale(MAX_VELOCITY));
	}

	@Override
	boolean isHandlingCollision() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	Animation getMovementAnimation(Vector move_direction) {
		String animation_filename = getMovementAnimationFilename(move_direction);

		Animation movement_animation = new Animation(
				ResourceManager.getSpriteSheet(animation_filename,
						ANIMATION_FRAME_WIDTH, ANIMATION_FRAME_HEIGHT), 0, 0,
						ANIMATION_FRAMES - 1, 0, true, ANIMATION_FRAME_DURATION, true);

		return movement_animation;
	}
	
	private final String getMovementAnimationFilename(final Vector direction) {
		String animation_filename;

		if (direction.equals(LEFT_UNIT_VECTOR)) {
			animation_filename = MOVE_L_ANIMATION_FILE;		
			ANIMATION_FRAME_WIDTH = 130;
			ANIMATION_FRAME_HEIGHT = 65;
			//remove old image, add new image
			removeImage(ResourceManager.getImage(getStillImageFilename(face_direction)));
			List<Shape> shapes;
			shapes = getShapes();
			for (Shape shape: shapes)
				removeShape(shape);
			this.addImageWithBoundingBox(ResourceManager.getImage(FACE_L_GRAPHIC_FILE));
		} else if (direction.equals(UP_UNIT_VECTOR)) {
			animation_filename = MOVE_U_ANIMATION_FILE;
			ANIMATION_FRAME_WIDTH = 100;
			ANIMATION_FRAME_HEIGHT = 100;
			removeImage(ResourceManager.getImage(getStillImageFilename(face_direction)));
			List<Shape> shapes = getShapes();
			for (Shape shape: shapes)
				removeShape(shape);
			this.addImageWithBoundingBox(ResourceManager.getImage(FACE_U_GRAPHIC_FILE));
		} else if (direction.equals(RIGHT_UNIT_VECTOR)) {
			animation_filename = MOVE_R_ANIMATION_FILE;
			ANIMATION_FRAME_WIDTH = 130;
			ANIMATION_FRAME_HEIGHT = 65;
			removeImage(ResourceManager.getImage(getStillImageFilename(face_direction)));
			List<Shape> shapes = getShapes();
			for (Shape shape: shapes)
				removeShape(shape);
			this.addImageWithBoundingBox(ResourceManager.getImage(FACE_R_GRAPHIC_FILE));
		} else {
			animation_filename = MOVE_D_ANIMATION_FILE;
			ANIMATION_FRAME_WIDTH = 100;
			ANIMATION_FRAME_HEIGHT = 100;
			removeImage(ResourceManager.getImage(getStillImageFilename(face_direction)));
			List<Shape> shapes = getShapes();
			for (Shape shape: shapes)
				removeShape(shape);
			this.addImageWithBoundingBox(ResourceManager.getImage(FACE_D_GRAPHIC_FILE));
		}

		return animation_filename;
	}

	@Override
	String getStillImageFilename(Vector face_direction) {
		String graphic_filename;
		
		if (face_direction.equals(LEFT_UNIT_VECTOR)) {
			graphic_filename = FACE_L_GRAPHIC_FILE;
		} else if (face_direction.equals(UP_UNIT_VECTOR)) {
			graphic_filename = FACE_U_GRAPHIC_FILE;
		} else if (face_direction.equals(RIGHT_UNIT_VECTOR)) {
			graphic_filename = FACE_R_GRAPHIC_FILE;
		} else {
			graphic_filename = FACE_D_GRAPHIC_FILE;
		}

		return graphic_filename;
	}

	@Override
	String getIconFilename() {
		if (this.getTeam() == null) return RED_ICON;
		if (this.getTeam() == Team.BLUE) {
			return BLUE_ICON;
		} else {
			return RED_ICON;
		}
	}

	
	public void update(int delta) {
		super.update(delta);
		
		reward = 0;
		
		if (tacticalTorpedo != null) tacticalTorpedo.update(delta);
		if (torpedoTimer > 0) {
			torpedoTimer -= delta;
		} else {
			tacticalTorpedo = null;
			if (isAttacking) {
					fire(target);
			}
		}
	}
	
	public void nudgeNudge(Vector direction) {
		System.out.println("NUDGE " + direction);
	}
}
