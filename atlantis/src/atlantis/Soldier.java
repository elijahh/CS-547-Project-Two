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
import dijkstra.engine.DijkstraAlgorithm;
import dijkstra.model.Graph;
import dijkstra.model.Vertex;

public class Soldier extends GroundEntity {
	
	private static final float MIN_SOLDIER_SOLDIER_DISTANCE = 36;
	
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
	
	public Soldier() {
		this(0, 0);
	}

	public Soldier(float x, float y) {
		this(x, y, STOPPED_VECTOR);
	}

	public Soldier(float x, float y, Vector move_direction) {
		super(x, y, move_direction);
		health = 100;
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
	
	private List<Soldier> handling_collisions_with_these_soldiers = 
			new LinkedList<Soldier>();
	private Map<Soldier, Integer> collision_avoidance_countdown =
			new HashMap<Soldier, Integer>();
		
	public boolean isHandlingCollision() {
		if(0 < handling_collisions_with_these_soldiers.size())
			return true;
		
		return false;
	}
	
	public void beginMovement(final Vector direction) {
		velocity = new Vector(direction.scale(MAX_VELOCITY));
	}
	
	private void enforceSoldierSoldierDistance(Soldier other,
			final Vector their_position, final Vector my_position) {
		//final double angle_to_other = my_position.angleTo(their_position);

		if (false == handling_collisions_with_these_soldiers.contains(other)) {
			handling_collisions_with_these_soldiers.add(other);

			Integer countdown = new Integer(1500);
			collision_avoidance_countdown.put(other, countdown);

			System.out.println(this + " HANDLING SOLDIER-SOLDIER COLLISION "
					+ countdown);
		}
	}
	
	private void manageCountdownForCollision(final Soldier other, final int delta) {
		Integer countdown = collision_avoidance_countdown.get(other);
		
		if(null != countdown) {
			countdown -= delta;
			if (0 > countdown) {
				handling_collisions_with_these_soldiers.remove(other);
			}
			collision_avoidance_countdown.put(other, countdown);
		}
	}
	
	private void swapOutDijkstraIfNecessary() {
		if (0 == handling_collisions_with_these_soldiers.size()) {
			// System.out.println("NULLING DIJKSTRA");
			dijkstra = null;
		} else {
			dijkstra = new DijkstraAlgorithm(group_dijkstra);
			
			// System.out.println("AT " + this.getCurrentMapNode());
			
			for(Soldier soldier : handling_collisions_with_these_soldiers)
				for(Integer node_id : soldier.getCurrentMapNodesSpanned())
					if(node_id != getCurrentMapNode())
						dijkstra.removeNode(node_id);
			
			dijkstra.execute(target_node);
		}
	}

	@Override
	public void update(final int delta) {
		super.update(delta);
		
		reward = 0;
			
		if (torpedo != null) {
			torpedo.update(delta);
			if(torpedo.collides(target)!=null) System.out.println("hits target"); // The if condition never turns true
		}
		
		if (torpedoTimer > 0) {
			torpedoTimer -= delta;
		} else {
			torpedo = null;
			if (isAttacking) {
				fire(target);
			}
		}
				
		
		int managing_collisisons_count = 
				handling_collisions_with_these_soldiers.size();
				
		Set<GroundEntity> potential_collisions = 
				new HashSet<GroundEntity>(getPotentialCollisions());
		potential_collisions.addAll(handling_collisions_with_these_soldiers);
		
		for (GroundEntity e : potential_collisions) {
			Vector my_position = getPosition();
			Vector their_position = e.getPosition();

			final float distance_to_other = their_position
					.distance(my_position);

			if (e instanceof Soldier
					&& distance_to_other < MIN_SOLDIER_SOLDIER_DISTANCE)
				enforceSoldierSoldierDistance((Soldier)e, their_position, my_position);
			else
				manageCountdownForCollision((Soldier)e, delta);
		}

		if(managing_collisisons_count != 
				handling_collisions_with_these_soldiers.size())
			swapOutDijkstraIfNecessary();
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
	
	/* ------------------------------------------------------------------------ */
		
	public void fire(AtlantisEntity target) {
		double theta = this.getPosition().angleTo(target.getPosition());
		
		//moveTo(target.getPosition());
		
		if (getPosition().distance(target.getPosition()) < 200) {
			stopMoving();
			
			if (torpedo == null) {
				torpedo = new Torpedo(getX(), getY(), theta, team);
			} else {
				torpedo.setPosition(new Vector(getX(), getY()));
				torpedo.setRotation(theta);
			}
			torpedoTimer = 2000;

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
	
}
