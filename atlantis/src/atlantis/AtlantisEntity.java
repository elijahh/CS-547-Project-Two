package atlantis;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import jig.Entity;
import jig.ResourceManager;
import jig.Vector;
import dijkstra.engine.DijkstraAlgorithm;
import dijkstra.model.Graph;
import dijkstra.model.Vertex;

public abstract class AtlantisEntity extends Entity implements
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

	protected static final int MAP_GRID_X = 40;
	protected static final int MAP_X_NODE_DIMENSION = 32; /* Pixels */

	protected static final int MAP_GRID_Y = 40;
	protected static final int MAP_Y_NODE_DIMENSION = 32; /* Pixels */
	
	protected static final int MAP_HORIZONTAL_MOVE_COST = 100; /* mS */
	protected static final int MAP_VERTICAL_MOVE_COST   = 100; /* mS */
	protected static final int MAP_DIAGONAL_MOVE_COST   = 141; /* mS */

	private static Random random_generator = 
				new Random(System.currentTimeMillis());
	
	protected static Graph graph;

	protected Vector velocity;
	protected DijkstraAlgorithm dijkstra;
	public Vector destination_position;

	protected Vector face_direction = STOPPED_VECTOR;
	protected Vector movement_direction = STOPPED_VECTOR;
	protected Vector movement_last_direction = STOPPED_VECTOR;

	@Override
	public int compareTo(final AtlantisEntity other) {
		return identity.compareTo(other.identity);
	}

	public AtlantisEntity(final float x, final float y,
			Vector movement_direction) {
		super(x, y);
		beginMovement(movement_direction);
		identity = random_generator.nextLong();

		
		/*
		 * We need to get one shape of the entity so that the server knows the
		 * size when testing for map nodes spanned.
		 */

		still_image = ResourceManager
				.getImage(getStillImageFilename(STOPPED_VECTOR));
		if (null != still_image) {
			addImageWithBoundingBox(still_image);
		}
	}
	
	private Image still_image = null;

	private Long identity;

	public long getIdentity() {
		return identity;
	}

	public enum Team {
		RED, BLUE
	};

	private Team team = Team.RED;

	public void setTeam(Team team) {
		this.team = team;
	}
	
	public Team getTeam() {
		return this.team;
	}

	protected boolean homing = false;

	public void activateHoming() {
		homing = true;
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
		else
			return false;
	}

	public boolean isOutsideYMovementLimit() {
		if ((getY() < movement_min_y) || (getY() > movement_max_y))
			return true;
		else
			return false;
	}

	public boolean isBelowYMovementLimit() {
		if (getY() < movement_min_y)
			return true;
		else
			return false;
	}

	public boolean isOutsideMovementLimit() {
		return (isOutsideXMovementLimit() || isOutsideYMovementLimit());
	}

	public void applyMovementLimits() {
		if (getX() < movement_min_x)
			setX(movement_min_x);
		else if (getX() > movement_max_x)
			setX(movement_max_x);

		if (getY() > movement_max_y)
			setY(movement_max_y);
		else if (getY() < movement_min_y)
			setY(movement_min_y);
	}

	public static Vector getVectorForAngle(final double angle) {
		Vector angle_vector = STOPPED_VECTOR;

		if (Math.abs(angle) < 72.5) {
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

	public static List<Vector> getMapNodeCenterCoords() {
		List<Vector> node_centers = new ArrayList<Vector>(MAP_GRID_X * MAP_GRID_Y);
	 
		for (int i = 0; i < MAP_GRID_X; i++) {
			float node_center_x = 
					MAP_X_NODE_DIMENSION / 2.0f + i * MAP_X_NODE_DIMENSION;
			for (int j = 0; j < MAP_GRID_Y; j++) {
				float node_center_y = 
						MAP_Y_NODE_DIMENSION / 2.0f + j * MAP_Y_NODE_DIMENSION;
				Vector node_center = new Vector(node_center_x, node_center_y);
				node_centers.add(node_center);
			}
		}
		
	    return Collections.unmodifiableList(node_centers);
	}

	public static final int calculateMapNode(final float x, final float y) {
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
		
		calculateAndAddNodeToSetIfSane(min_x, getY(), node_number_set);
		calculateAndAddNodeToSetIfSane(max_x, getY(), node_number_set);
		
		calculateAndAddNodeToSetIfSane(getX(), min_y, node_number_set);
		calculateAndAddNodeToSetIfSane(getX(), max_y, node_number_set);
			
		// System.out.println(min_x + ", " + min_y + " :" + max_x + ", " + max_y);
		
		// System.out.println(this + ":" + node_number_set);
		
		return node_number_set;
	}

	static Map<AtlantisEntity, Set<Integer>> entity_node_map;
	static Map<Integer, Set<AtlantisEntity>> node_entity_map;

	static {
		entity_node_map = new HashMap<AtlantisEntity, Set<Integer>>();
		node_entity_map = new HashMap<Integer, Set<AtlantisEntity>>();
	}

	private void updateEntityNodeMaps() {
		Set<Integer> node_list = getCurrentMapNodesSpanned();
		Set<Integer> previous_node_list;

		/* entity/node */

		synchronized (entity_node_map) {
			previous_node_list = entity_node_map.get(this);
			entity_node_map.put(this, node_list);
		}

		/* node/entity - remove old position */

		if (null != previous_node_list) {
			synchronized (node_entity_map) {
				for (Integer n : previous_node_list) {
					if (node_list.contains(n))
						continue;

					Set<AtlantisEntity> entity_list = node_entity_map.get(n);

					if (entity_list != null)
						entity_list.remove(this);
				}
			}
		}

		/* node/entity - add new position */

		synchronized (node_entity_map) {
			for (Integer n : node_list) {
				Set<AtlantisEntity> entity_list = node_entity_map.get(n);

				boolean need_put = false;

				if (null == entity_list) {
					entity_list = new HashSet<AtlantisEntity>();
					need_put = true;
				}

				entity_list.add(this);

				if (need_put)
					node_entity_map.put(n, entity_list);
			}
		}
	}

	public final boolean isNearbyEntity(final AtlantisEntity other) {
		boolean returned_value = false;

		synchronized (entity_node_map) {
			final Set<Integer> map_nodes = entity_node_map.get(this);
			Set<Integer> their_nodes = entity_node_map.get(other);

			their_nodes.retainAll(map_nodes);

			if (0 < their_nodes.size())
				returned_value = true;
		}

		return returned_value;
	}
	
	public static final Set<AtlantisEntity> listEntitiesInMapNode(final int node) {
		Set<AtlantisEntity> entities_in_node = new HashSet<AtlantisEntity>();
		
		entities_in_node.addAll(node_entity_map.get(node));
		
		return Collections.unmodifiableSet(entities_in_node);
	}

	public final Set<AtlantisEntity> listNearbyEntities() {
		Set<AtlantisEntity> nearby_entities = new HashSet<AtlantisEntity>();
		
		Set<Integer> node_list;

		synchronized (entity_node_map) {
			node_list = entity_node_map.get(this);
		}
		
		synchronized (node_entity_map) {
			for(Integer n : node_list)
				for(AtlantisEntity e : node_entity_map.get(n))
					if(e != this)
						nearby_entities.add(e);
		}

		return Collections.unmodifiableSet(nearby_entities);
	}

	public Vector getNextMovementFromPath(final List<Vertex> path) {
		Vector movement_direction = STOPPED_VECTOR;

		final int current_map_node = getCurrentMapNode();
		String move_to_node = "";

		if (null != path)
			move_to_node = path.get(1).toString();

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

	Vector getMovementDirection() {
		return new Vector(this.velocity.unit());
	}

	/* -------------------------------------------------------------------- */

	/* Server-side processing */

	public void update(final int delta) {
		translate(velocity.scale(delta));

		updateEntityNodeMaps();
	}

	abstract void beginMovement(Vector direction);
	abstract boolean isHandlingCollision();
	
	private int target_node = -1;
	
	boolean moveTo(final Vector destination_position) {
		boolean moving = false;
		int destination_node = calculateMapNode(destination_position.getX(),
				destination_position.getY());
		
		if(null == dijkstra && null != graph)
			dijkstra = new DijkstraAlgorithm(graph);

		if ((dijkstra != null) && (destination_node != target_node)) {
			dijkstra.execute(destination_node);
			target_node = destination_node;
		}

		if (dijkstra != null) {
			List<Vertex> path = dijkstra.getPath(this.getCurrentMapNode());
					
			// TODO Remove once we're sure that entities aren't wandering into terrain
			for(int node_id : AtlantisMap.getBlockedNodes())
				if(node_id == getCurrentMapNode())
					System.out.println("HUH?!? ENTITY IN TERRAIN TILE " + node_id);
						
			Vector move_direction = this.getNextMovementFromPath(path);
			
			if (move_direction != STOPPED_VECTOR) 
				moving = true;
			
			beginMovement(move_direction);
		}

		return moving;
	}
	
	public void setDestination(final Vector dest) {
		this.destination_position = dest.copy();
	}
	
	public Vector getDestination() {
		return this.destination_position;
	}

	/* -------------------------------------------------------------------- */

	public static class Updater implements Serializable {

		long identity;

		Team team;

		Vector velocity;
		Vector position;
		
		Class entity_class;

		Updater(AtlantisEntity e) {

			position = e.getPosition();
			velocity = e.velocity;
			identity = e.identity;
			team = e.team;
			
			entity_class = e.getClass();
		}

		private static final long serialVersionUID = 234098222823485285L;

		public long getIdentity() {
			return identity;
		}

		public Class getEntityClass() {
			return entity_class;
		}
	}

	public Updater getUpdater() {
		return new Updater(this);
	}

	/* -------------------------------------------------------------------- */

	/* Client-side processing */

	abstract Animation getMovementAnimation(Vector move_direction);

	abstract String getStillImageFilename(Vector face_direction);
	abstract String getIconFilename();

	public void update(AtlantisEntity.Updater updater) {
		Vector localPosition = new Vector(updater.position.getX()
				+ PlayingState.viewportOffsetX, updater.position.getY()
				+ PlayingState.viewportOffsetY);
		
		this.setPosition(localPosition);
		
		/* Set values */
		velocity = updater.velocity;
		team = updater.team;

		/* Derived values */
		movement_direction = getMovementDirection();

		// TODO - Finish with as many variables as necessary to accurately
		// communicate entity status to client for rendering.
		
		/* Identity - very important to keep consistent */
		identity = updater.identity;
	}

	private Animation movement_animation = null;


	public void render(final Graphics g) {

		g.drawImage(ResourceManager.getImage(getIconFilename()),
				(getCoarseGrainedMinX() + getCoarseGrainedMaxX())/2f - 12,
				getCoarseGrainedMinY() - 30);
		
		/* Entity is moving. Animate appropriately. */

		if (false == movement_direction.equals(STOPPED_VECTOR)) {
			removeImage(still_image);
			still_image = null;

			if ((null == movement_animation)
					|| (false == movement_direction
							.equals(movement_last_direction))) {
				removeAnimation(movement_animation);
				movement_animation = this
						.getMovementAnimation(movement_direction);
				addAnimation(movement_animation);
			}
			face_direction = movement_direction;
		}

		movement_last_direction = movement_direction;

		/* Entity standing still. Replace animation with still image. */

		if (0 == velocity.length()) {
			removeAnimation(movement_animation);
			movement_animation = null;
		}

		if ((null == movement_animation) && (null == still_image)) {
			String graphic_filename = getStillImageFilename(face_direction);

			still_image = ResourceManager.getImage(graphic_filename);

			if (0 == this.getNumShapes()) {
				addImageWithBoundingBox(still_image);
			} else {
				addImage(still_image);
			}
		}

		super.render(g);
		
	}
}
