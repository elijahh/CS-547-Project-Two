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

import jig.ResourceManager;
import jig.Vector;

public class Worker extends GroundEntity {
	
	private static final float MIN_WORKER_WORKER_DISTANCE = 36;
	
	private static final String FACE_U_GRAPHIC_FILE = "atlantis/resource/diver-up1.png";
	private static final String FACE_D_GRAPHIC_FILE = "atlantis/resource/diver-down1.png";
	private static final String FACE_L_GRAPHIC_FILE = "atlantis/resource/diver-left1.png";
	private static final String FACE_R_GRAPHIC_FILE = "atlantis/resource/diver-right1.png";

	private static final String MOVE_L_ANIMATION_FILE = "atlantis/resource/diver-left.png";
	private static final String MOVE_R_ANIMATION_FILE = "atlantis/resource/diver-right.png";
	private static final String MOVE_U_ANIMATION_FILE = "atlantis/resource/diver-up.png";
	private static final String MOVE_D_ANIMATION_FILE = "atlantis/resource/diver-down.png";
	
	private static final String RED_ICON = "atlantis/resource/red-worker.png";
	private static final String BLUE_ICON = "atlantis/resource/blue-worker.png";
	
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
		
		ResourceManager.loadImage(RED_ICON);
		ResourceManager.loadImage(BLUE_ICON);
	}
	
	private static Random random_generator = 
			new Random(System.currentTimeMillis());
	
	private List<Worker> handling_collisions_with_these_workers = 
			new LinkedList<Worker>();
	private Map<Worker, Integer> collision_avoidance_countdown =
			new HashMap<Worker, Integer>();
	
	public boolean isHandlingCollision() {
		if(0 < handling_collisions_with_these_workers.size())
			return true;
		
		return false;
	}
	
	public boolean isHandlingCollisionWith(Worker other) {
		return handling_collisions_with_these_workers.contains(other);
	}
	
	public void beginMovement(final Vector direction) {
		velocity = new Vector(direction.scale(MAX_VELOCITY));
	}
	
	private void enforceWorkerWorkerDistance(Worker other,
			final Vector their_position, final Vector my_position) {
		//final double angle_to_other = my_position.angleTo(their_position);
		
		if (other.isHandlingCollisionWith(this))
			return;

		if (false == handling_collisions_with_these_workers.contains(other)) {
			handling_collisions_with_these_workers.add(other);
			velocity = velocity.negate();

			Integer countdown = new Integer(100 + random_generator.nextInt(150));
			collision_avoidance_countdown.put(other, countdown);

			System.out.println(this + " HANDLING WORKER-WORKER COLLISION "
					+ countdown);
		}
	}
	
	private void manageCountdownForCollision(Worker other, int delta) {
		Integer countdown = collision_avoidance_countdown.get(other);
		
		if(null != countdown) {
			countdown -= delta;
			if (0 > countdown)
				handling_collisions_with_these_workers.remove(other);
			collision_avoidance_countdown.put(other, countdown);
		}
	}
	
	@Override
	public void update(final int delta) {
		super.update(delta);
		
		Set<GroundEntity> potential_collisions = 
				new HashSet<GroundEntity>(getPotentialCollisions());
		potential_collisions.addAll(handling_collisions_with_these_workers);
				
		for (GroundEntity e : potential_collisions) {
			Vector my_position = getPosition();
			Vector their_position = e.getPosition();

			final float distance_to_other = their_position
					.distance(my_position);

			if (e instanceof Worker
					&& distance_to_other < MIN_WORKER_WORKER_DISTANCE)
				enforceWorkerWorkerDistance((Worker)e, their_position, my_position);
			else 
				manageCountdownForCollision((Worker)e, delta);
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

	@Override
	public final String getIconFilename() {
		if (this.getTeam() == null) return RED_ICON;
		if (this.getTeam() == Team.BLUE) {
			return BLUE_ICON;
		} else {
			return RED_ICON;
		}
	}
}
