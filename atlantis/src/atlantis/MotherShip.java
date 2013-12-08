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
import jig.Collision;
import jig.ResourceManager;
import jig.Shape;
import jig.Vector;

public class MotherShip extends FloatingEntity {
	
	private static final float MIN_MOTHERSHIP_MOTHERSHIP_DISTANCE = 36;
	
	private static final String FACE_U_GRAPHIC_FILE = "atlantis/resource/submarine-up.png";
	private static final String FACE_D_GRAPHIC_FILE = "atlantis/resource/submarine-down.png";
	private static final String FACE_L_GRAPHIC_FILE = "atlantis/resource/submarine-left.png";
	private static final String FACE_R_GRAPHIC_FILE = "atlantis/resource/submarine-right.png";
	
	private static final String MOVE_L_ANIMATION_FILE = "atlantis/resource/submarine-left.png";
	private static final String MOVE_R_ANIMATION_FILE = "atlantis/resource/submarine-right.png";
	private static final String MOVE_U_ANIMATION_FILE = "atlantis/resource/submarine-up.png";
	private static final String MOVE_D_ANIMATION_FILE = "atlantis/resource/submarine-down.png";
	
	private static final String RED_ICON = "atlantis/resource/red-worker.png";
	private static final String BLUE_ICON = "atlantis/resource/blue-worker.png";
	
	private static final float MAX_VELOCITY = 0.03f;       /* pixels/mS */
	
	private static final int ANIMATION_FRAMES = 1;
	private static final int ANIMATION_FRAME_DURATION = 200; /* mS */
	
	private static int ANIMATION_FRAME_WIDTH = 50; /* pixels */
	private static int ANIMATION_FRAME_HEIGHT = 200; /* pixels */
	
	private static int MAX_HEALTH_VALUE = 5000;
	
	private static List<MotherShip> mother_ships = new LinkedList<MotherShip>();
	
	public MotherShip() {
		this(0,0);
	}
	
	public MotherShip(float x, float y) {
		this(x, y, STOPPED_VECTOR);
	}

	public MotherShip(float x, float y, Vector movement_direction) {
		super(x, y, movement_direction);
		
		health = MAX_HEALTH_VALUE;
		
		mother_ships.add(this);
	}
	
	/* GAME IS OVER WHEN ONE MOTHER SHIP IS DESTROYED */
	
	public static boolean areBothAlive() {
		boolean both_alive = true;
		
		synchronized (mother_ships) {
			for (MotherShip ship : mother_ships)
				if (ship.health <= 0) {

					both_alive = false;
				}

			if (false == both_alive)
				mother_ships.clear();
		}
		
		return both_alive;
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
			ANIMATION_FRAME_WIDTH = 200;
			ANIMATION_FRAME_HEIGHT = 50;
			//remove old image, add new image
			removeImage(ResourceManager.getImage(getStillImageFilename(face_direction)));
			List<Shape> shapes;
			shapes = getShapes();
			for (Shape shape: shapes)
				removeShape(shape);
			this.addImageWithBoundingBox(ResourceManager.getImage(FACE_L_GRAPHIC_FILE));
		} else if (direction.equals(UP_UNIT_VECTOR)) {
			animation_filename = MOVE_U_ANIMATION_FILE;
			ANIMATION_FRAME_WIDTH = 50;
			ANIMATION_FRAME_HEIGHT = 200;
			removeImage(ResourceManager.getImage(getStillImageFilename(face_direction)));
			List<Shape> shapes = getShapes();
			for (Shape shape: shapes)
				removeShape(shape);
			this.addImageWithBoundingBox(ResourceManager.getImage(FACE_U_GRAPHIC_FILE));
		} else if (direction.equals(RIGHT_UNIT_VECTOR)) {
			animation_filename = MOVE_R_ANIMATION_FILE;
			ANIMATION_FRAME_WIDTH = 200;
			ANIMATION_FRAME_HEIGHT = 50;
			removeImage(ResourceManager.getImage(getStillImageFilename(face_direction)));
			List<Shape> shapes = getShapes();
			for (Shape shape: shapes)
				removeShape(shape);
			this.addImageWithBoundingBox(ResourceManager.getImage(FACE_R_GRAPHIC_FILE));
		} else {
			animation_filename = MOVE_D_ANIMATION_FILE;
			ANIMATION_FRAME_WIDTH = 50;
			ANIMATION_FRAME_HEIGHT = 200;
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
	
	private void enforceMotherShipMotherShipDistance(FloatingEntity e) {
		Collision collision = this.collides(e);
		
		if(null != collision) {
			System.out.println(collision);
		}
	}
	
	@Override
	public void update(final int delta) {
		super.update(delta);		

		
		for(FloatingEntity e : this.getPotentialCollisions()) {
			
			/* MotherShip to MotherShip collision */

			if(e instanceof MotherShip)
				enforceMotherShipMotherShipDistance(e);
			else
				// TODO MotherShip TacticalSub collision
				;
		}
	}
}
