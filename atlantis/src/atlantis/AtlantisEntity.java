package atlantis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import jig.Entity;
import jig.ResourceManager;
import jig.Vector;

import dijkstra.engine.DijkstraAlgorithm;

import dijkstra.model.Vertex;

abstract class AtlantisEntity extends Entity implements
		Comparable<AtlantisEntity> {
	
	protected static final Vector DOWN_UNIT_VECTOR = new Vector(0, 1);
	protected static final Vector DOWN_RIGHT_UNIT_VECTOR = new Vector(1, 1);
	protected static final Vector DOWN_LEFT_UNIT_VECTOR = new Vector(-1, 1);			
	protected static final Vector UP_UNIT_VECTOR = new Vector(0, -1);
	protected static final Vector UP_RIGHT_UNIT_VECTOR = new Vector(1, -1);
	protected static final Vector UP_LEFT_UNIT_VECTOR = new Vector(-1, -1);
	protected static final Vector LEFT_UNIT_VECTOR = new Vector(-1, 0);
	protected static final Vector RIGHT_UNIT_VECTOR = new Vector(1, 0);
	protected static final Vector STOPPED_VECTOR = new Vector(0, 0);
	
	protected static final int MAP_GRID_X = 16; 
	protected static final int MAP_X_NODE_DIMENSION = 50; /* Pixels */
	
	protected static final int MAP_GRID_Y = 12;
	protected static final int MAP_Y_NODE_DIMENSION = 50; /* Pixels */
	
	private static Random random_generator = new Random(); 
	
	protected Vector velocity;
	protected static DijkstraAlgorithm dijkstra;
	
	@Override
	public int compareTo(final AtlantisEntity other) {
		return identity.compareTo(other.identity);
	}
	
	public AtlantisEntity(final float x, final float y) {
		super(x, y);
		velocity = STOPPED_VECTOR;
		identity = random_generator.nextLong();
	}
	
	private Long identity;

	public long getIdentity() { return identity; }
	
	protected boolean homing = false;
	
	public void activateHoming() { homing = true; }
	
	public void update(final int delta) {
		translate(velocity.scale(delta));
	}
	
	public void update(final AtlantisEntity other) {
		
	}
	
	protected static float movement_min_x = -Float.MAX_VALUE; 
	protected static float movement_max_x = Float.MAX_VALUE;
	protected static float movement_min_y = -Float.MAX_VALUE; 
	protected static float movement_max_y = Float.MAX_VALUE;
	
	public static void setMovementLimits(final float min_x_arg,
			final float max_x_arg, final float min_y_arg, final float max_y_arg) {
		movement_min_x = min_x_arg;
		movement_max_x = max_x_arg;
		movement_min_y = min_y_arg;
		movement_max_y = max_y_arg;
	}
	
	public boolean isOutsideXMovementLimit() {
		if ((getX() < movement_min_x) || (getX() > movement_max_x)) 
			return true;
		else return false;
	}
	
	public boolean isOutsideYMovementLimit() {
		if ((getY() < movement_min_y) || (getY() > movement_max_y)) 
			return true;
		else return false;
	}
	
	public boolean isBelowYMovementLimit() {
		if(getY() < movement_min_y) 
			return true;
		else return false;
	}
	
	public boolean isOutsideMovementLimit() {
		return (isOutsideXMovementLimit() || isOutsideYMovementLimit());
	}
	
	public void applyMovementLimits() {
		if (getX() < movement_min_x) setX(movement_min_x);
		else if (getX() > movement_max_x) setX(movement_max_x);

		if (getY() > movement_max_y)	setY(movement_max_y);
		else if (getY() < movement_min_y) setY(movement_min_y);
	}
	
	public static Vector getVectorForAngle(final double angle) {
		Vector angle_vector = STOPPED_VECTOR;
				
		if(Math.abs(angle) < 72.5) {
			angle_vector = RIGHT_UNIT_VECTOR;
		} else if (Math.abs(angle) > 112.5) {
			angle_vector = LEFT_UNIT_VECTOR;
		}
		
		if ((angle < -22.5) && (angle > -157.5)) {
			angle_vector = angle_vector.add(UP_UNIT_VECTOR);
		} else if ((angle > 22.5) && (angle < 157.5)) {
			angle_vector = angle_vector.add(DOWN_UNIT_VECTOR);
		}

		return angle_vector;
	}
	
	private final int calculateMapNode(final float x, final float y) {
		final int g_x = (int) (x) / MAP_X_NODE_DIMENSION;
		final int g_y = (int) (y) / MAP_Y_NODE_DIMENSION;

		return (g_y * MAP_GRID_X + g_x);
	}
	
	public int getCurrentMapNode() {
		return calculateMapNode(getX(), getY());
	}
	
	private final void calculateAndAddNodeToSetIfSane(final float x,
			final float y, Set<Integer> set) {
		final int node_number = calculateMapNode(x, y);
		if (node_number < (MAP_X_NODE_DIMENSION * MAP_Y_NODE_DIMENSION))
			set.add(node_number);
	}

	public Set<Integer> getCurrentMapNodesSpanned() {
		HashSet<Integer> node_number_set = new HashSet<Integer>();

		node_number_set.add(getCurrentMapNode());

		final float max_x = getCoarseGrainedMaxX();
		final float max_y = getCoarseGrainedMaxY();

		calculateAndAddNodeToSetIfSane(max_x, max_y, node_number_set);
		
		final float min_x = getCoarseGrainedMinX();

		calculateAndAddNodeToSetIfSane(min_x, max_y, node_number_set);

		final float min_y = getCoarseGrainedMinY();

		calculateAndAddNodeToSetIfSane(min_x, min_y, node_number_set);
		calculateAndAddNodeToSetIfSane(max_x, min_y, node_number_set);

		return node_number_set;
	}
		
	public final boolean isNearbyEntity(final AtlantisEntity g) {
		final Set<Integer> map_nodes = this.getCurrentMapNodesSpanned();
		Set<Integer> their_nodes = g.getCurrentMapNodesSpanned();

		their_nodes.retainAll(map_nodes);

		if (0 < their_nodes.size())
			return true;

		return false;
	}

	public final List<AtlantisEntity> listNearbyEntities() {
		ArrayList<AtlantisEntity> nearby_entities = new ArrayList<AtlantisEntity>();

		for (AtlantisEntity e : nearby_entities) {
			if (e == this)
				continue;

			if (isNearbyEntity(e))
				nearby_entities.add(e);
		}

		return Collections.unmodifiableList(nearby_entities);
	}
	
	public Vector getNextMovementFromPath(final List<Vertex> path) {
		Vector movement_direction = STOPPED_VECTOR;
		
		final int current_map_node = getCurrentMapNode();
		String move_to_node = "";
		
		if(null != path) move_to_node = path.get(1).toString();
				
		if (move_to_node.equals("Node_" + (current_map_node + MAP_GRID_X))) {
			movement_direction = movement_direction.add(DOWN_UNIT_VECTOR);
		} else if (move_to_node.equals("Node_"
				+ (current_map_node + MAP_GRID_X - 1))) {
			movement_direction = movement_direction.add(DOWN_UNIT_VECTOR);
			movement_direction = movement_direction.add(LEFT_UNIT_VECTOR);
		} else if (move_to_node.equals("Node_"
				+ (current_map_node + MAP_GRID_X + 1))) {
			movement_direction = movement_direction.add(DOWN_UNIT_VECTOR);
			movement_direction = movement_direction.add(RIGHT_UNIT_VECTOR);
		} else if (move_to_node.equals("Node_"
				+ (current_map_node - MAP_GRID_X))) {
			movement_direction = movement_direction.add(UP_UNIT_VECTOR);
		} else if (move_to_node.equals("Node_"
				+ (current_map_node - MAP_GRID_X - 1))) {
			movement_direction = movement_direction.add(UP_UNIT_VECTOR);
			movement_direction = movement_direction.add(LEFT_UNIT_VECTOR);
		} else if (move_to_node.equals("Node_"
				+ (current_map_node - MAP_GRID_X + 1))) {
			movement_direction = movement_direction.add(UP_UNIT_VECTOR);
			movement_direction = movement_direction.add(RIGHT_UNIT_VECTOR);
		} else if (move_to_node.equals("Node_" + (current_map_node - 1))) {
			movement_direction = movement_direction.add(LEFT_UNIT_VECTOR);
		} else if (move_to_node.equals("Node_" + (current_map_node + 1))) {
			movement_direction = movement_direction.add(RIGHT_UNIT_VECTOR);
		}
		
		return movement_direction;
	}

	/* -------------------------------------------------------------------- */
	
	protected Vector face_direction = STOPPED_VECTOR;
	protected Vector movement_last_direction = STOPPED_VECTOR;

	abstract String getMovementAnimationFilename(Vector movement_direction);
	abstract String getStillImageFilename(Vector face_direction);
		
	@Override
	public void render(final Graphics g) {
		
		/* TEMPORARY FOR issue7 */
		
		if(0 == this.getNumImages()) {
			String graphic_filename = getStillImageFilename(face_direction);

			Image still_image = ResourceManager.getImage(graphic_filename);
			
			if (0 == this.getNumShapes()) 
				addImageWithBoundingBox(still_image);
		}
		
		super.render(g);
	}
}
