package atlantis;

import java.util.HashSet;
import java.util.Set;

import jig.Entity;
import jig.Vector;

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

	protected Vector velocity;

	@Override
	public int compareTo(final AtlantisEntity other) {
		Float min_entity_y = getCoarseGrainedMinY();
		return min_entity_y.compareTo(other.getCoarseGrainedMinY());
	}

	public AtlantisEntity(final float x, final float y) {
		super(x, y);
		velocity = STOPPED_VECTOR;
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
	
	public void update(final int delta) {
		translate(velocity.scale(delta));
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
}
