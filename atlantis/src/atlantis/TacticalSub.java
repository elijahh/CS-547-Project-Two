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

import dijkstra.engine.DijkstraAlgorithm;
import dijkstra.model.Edge;
import dijkstra.model.Graph;
import dijkstra.model.Vertex;
import atlantis.AtlantisEntity.Team;
import jig.Collision;
import jig.ResourceManager;
import jig.Shape;
import jig.Vector;

public class TacticalSub extends FloatingEntity {
	
	private static final String FACE_U_GRAPHIC_FILE = "atlantis/resource/tactical-up.png";
	private static final String FACE_UL_GRAPHIC_FILE = "atlantis/resource/tactical-upleft.png";
	private static final String FACE_UR_GRAPHIC_FILE = "atlantis/resource/tactical-upright.png";
	private static final String FACE_D_GRAPHIC_FILE = "atlantis/resource/tactical-down.png";
	private static final String FACE_DL_GRAPHIC_FILE = "atlantis/resource/tactical-downleft.png";
	private static final String FACE_DR_GRAPHIC_FILE = "atlantis/resource/tactical-downright.png";
	private static final String FACE_L_GRAPHIC_FILE = "atlantis/resource/tactical-left.png";
	private static final String FACE_R_GRAPHIC_FILE = "atlantis/resource/tactical-right.png";
	
	private static final String MOVE_L_ANIMATION_FILE = "atlantis/resource/tactical-left.png";
	private static final String MOVE_R_ANIMATION_FILE = "atlantis/resource/tactical-right.png";
	private static final String MOVE_U_ANIMATION_FILE = "atlantis/resource/tactical-up.png";
	private static final String MOVE_UL_ANIMATION_FILE = "atlantis/resource/tactical-upleft.png";
	private static final String MOVE_UR_ANIMATION_FILE = "atlantis/resource/tactical-upright.png";
	private static final String MOVE_D_ANIMATION_FILE = "atlantis/resource/tactical-down.png";
	private static final String MOVE_DL_ANIMATION_FILE = "atlantis/resource/tactical-downleft.png";
	private static final String MOVE_DR_ANIMATION_FILE = "atlantis/resource/tactical-downright.png";
	
	private static final String RED_ICON = "atlantis/resource/red-worker.png";
	private static final String BLUE_ICON = "atlantis/resource/blue-worker.png";
	
	private static final float MAX_VELOCITY = 0.09f;       /* pixels/mS */
	
	private static final int ANIMATION_FRAMES = 1;
	private static final int ANIMATION_FRAME_DURATION = 200; /* mS */
	
	private static int ANIMATION_FRAME_WIDTH = 100; /* pixels */
	private static int ANIMATION_FRAME_HEIGHT = 100; /* pixels */
	
	private static List<TacticalSub> tactical_subs = new LinkedList<TacticalSub>();
	
	private static final String HIT_SOUND = "atlantis/resource/hit.wav";
	
	public TacticalSub() {
		this(0,0);
	}
	
	public TacticalSub(float x, float y) {
		this(x, y, STOPPED_VECTOR);
	}

	public TacticalSub(float x, float y, Vector movement_direction) {
		super(x, y, movement_direction);
		
		MAX_HEALTH_VALUE = 600;
		health = MAX_HEALTH_VALUE;
		eyesight = 500;
		
		tactical_subs.add(this);
		
		dijkstra = new DijkstraAlgorithm(graph_with_diagonals);
	}
	
	static private Graph graph_with_diagonals;
	
	static {
		List<Edge> map_edges = new ArrayList<Edge>();
		List<Vertex> map_nodes = new ArrayList<Vertex>();
		
		for (int j = 0; j < MAP_GRID_Y; j++) {
			for (int i = 0; i < MAP_GRID_X; i++) {
				int n = j * MAP_GRID_X + i;

				Vertex location = new Vertex("Node_" + n);
				map_nodes.add(location);

				if (0 < i)
					Graph.addLane(map_edges, map_nodes, n, n - 1,
							MAP_HORIZONTAL_MOVE_COST);
				if (0 < j)
					Graph.addLane(map_edges, map_nodes, n, n - MAP_GRID_X,
							MAP_VERTICAL_MOVE_COST);
				if ((0 < j) && (0 < i))
					Graph.addLane(map_edges, map_nodes, n, n - MAP_GRID_X - 1,
							MAP_DIAGONAL_MOVE_COST);
				if ((0 < j) && ((MAP_GRID_X - 1) > i))
					Graph.addLane(map_edges, map_nodes, n, n - MAP_GRID_X + 1,
							MAP_DIAGONAL_MOVE_COST);
			}
		}

		graph_with_diagonals = new Graph(map_nodes, map_edges);
	}
	
	static {
		ResourceManager.loadImage(FACE_D_GRAPHIC_FILE);
		ResourceManager.loadImage(FACE_DL_GRAPHIC_FILE);
		ResourceManager.loadImage(FACE_DR_GRAPHIC_FILE);
		ResourceManager.loadImage(FACE_U_GRAPHIC_FILE);
		ResourceManager.loadImage(FACE_UL_GRAPHIC_FILE);
		ResourceManager.loadImage(FACE_UR_GRAPHIC_FILE);
		ResourceManager.loadImage(FACE_L_GRAPHIC_FILE);
		ResourceManager.loadImage(FACE_R_GRAPHIC_FILE);	
		
		ResourceManager.loadImage(MOVE_D_ANIMATION_FILE);
		ResourceManager.loadImage(MOVE_DL_ANIMATION_FILE);
		ResourceManager.loadImage(MOVE_DR_ANIMATION_FILE);
		ResourceManager.loadImage(MOVE_U_ANIMATION_FILE);
		ResourceManager.loadImage(MOVE_UL_ANIMATION_FILE);
		ResourceManager.loadImage(MOVE_UR_ANIMATION_FILE);
		ResourceManager.loadImage(MOVE_L_ANIMATION_FILE);
		ResourceManager.loadImage(MOVE_R_ANIMATION_FILE);
		
		ResourceManager.loadImage(RED_ICON);
		ResourceManager.loadImage(BLUE_ICON);
		
		ResourceManager.loadSound(HIT_SOUND);
	}
	
	
	public void fire(AtlantisEntity target) {
		double theta = this.getPosition().angleTo(target.getPosition());
		
		if (getPosition().distance(target.getPosition()) < 400) {
			stopMoving();
			
			if (tacticalTorpedo == null) {
				System.out.println("new torpedo");
				tacticalTorpedo = new TacticalTorpedo(getX(), getY(), theta, team);
			} else {
				tacticalTorpedo.setPosition(new Vector(getX(), getY()));
				tacticalTorpedo.setRotation(theta);
			}
			torpedoTimer = 5000;

			if (target.health <= 0) isAttacking = false;
			System.out.println("target health: " + target.health);
			System.out.println("this health: " + health);
		} else if(target.visibleToOpponent){		
			setDestination(target.getPosition());
		}
	}
	
	@Override
	void beginMovement(Vector direction) {
		velocity = new Vector(direction.scale(MAX_VELOCITY));
	}

	@Override
	boolean isHandlingCollision() {
		if(this.handling_mother_ship_collision)
			return true;
		
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
		
	private Vector parseDirection(final Vector dir) {
		Vector direction = new Vector(0, 0);
		
		if(dir.getX() > 0)
			direction = direction.add(RIGHT_UNIT_VECTOR);
		else if(dir.getX() < 0)
			direction = direction.add(LEFT_UNIT_VECTOR);
		
		if(dir.getY() > 0)
			direction = direction.add(DOWN_UNIT_VECTOR);
		else if(dir.getY() < 0)
			direction = direction.add(UP_UNIT_VECTOR);
		
		return direction;
	}
	
	private final String getMovementAnimationFilename(final Vector unit_direction) {
		String animation_filename;
		
		Vector direction = parseDirection(unit_direction);
				
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
		} else if (direction.equals(UP_LEFT_UNIT_VECTOR)) {
			animation_filename = MOVE_UL_ANIMATION_FILE;
			ANIMATION_FRAME_WIDTH = 100;
			ANIMATION_FRAME_HEIGHT = 81;
			removeImage(ResourceManager.getImage(getStillImageFilename(face_direction)));
			List<Shape> shapes = getShapes();
			for (Shape shape: shapes)
				removeShape(shape);
			this.addImageWithBoundingBox(ResourceManager.getImage(FACE_UL_GRAPHIC_FILE));
		} else if (direction.equals(UP_RIGHT_UNIT_VECTOR)) {
			animation_filename = MOVE_UR_ANIMATION_FILE;
			ANIMATION_FRAME_WIDTH = 100;
			ANIMATION_FRAME_HEIGHT = 81;
			removeImage(ResourceManager.getImage(getStillImageFilename(face_direction)));
			List<Shape> shapes = getShapes();
			for (Shape shape: shapes)
				removeShape(shape);
			this.addImageWithBoundingBox(ResourceManager.getImage(FACE_UR_GRAPHIC_FILE));
		} else if (direction.equals(RIGHT_UNIT_VECTOR)) {
			animation_filename = MOVE_R_ANIMATION_FILE;
			ANIMATION_FRAME_WIDTH = 130;
			ANIMATION_FRAME_HEIGHT = 65;
			removeImage(ResourceManager.getImage(getStillImageFilename(face_direction)));
			List<Shape> shapes = getShapes();
			for (Shape shape: shapes)
				removeShape(shape);
			this.addImageWithBoundingBox(ResourceManager.getImage(FACE_R_GRAPHIC_FILE));
		} else if (direction.equals(DOWN_LEFT_UNIT_VECTOR)) {
			animation_filename = MOVE_DL_ANIMATION_FILE;
			ANIMATION_FRAME_WIDTH = 105;
			ANIMATION_FRAME_HEIGHT = 80;
			removeImage(ResourceManager.getImage(getStillImageFilename(face_direction)));
			List<Shape> shapes = getShapes();
			for (Shape shape: shapes)
				removeShape(shape);
			this.addImageWithBoundingBox(ResourceManager.getImage(FACE_DL_GRAPHIC_FILE));
		} else if (direction.equals(DOWN_RIGHT_UNIT_VECTOR)) {
			animation_filename = MOVE_DR_ANIMATION_FILE;
			ANIMATION_FRAME_WIDTH = 105;
			ANIMATION_FRAME_HEIGHT = 80;
			removeImage(ResourceManager.getImage(getStillImageFilename(face_direction)));
			List<Shape> shapes = getShapes();
			for (Shape shape: shapes)
				removeShape(shape);
			this.addImageWithBoundingBox(ResourceManager.getImage(FACE_DR_GRAPHIC_FILE));
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
	String getStillImageFilename(Vector direction) {
		String graphic_filename;
		
		Vector face_direction = parseDirection(direction);
		
		if (face_direction.equals(LEFT_UNIT_VECTOR)) {
			graphic_filename = FACE_L_GRAPHIC_FILE;
		} else if (face_direction.equals(UP_UNIT_VECTOR)) {
			graphic_filename = FACE_U_GRAPHIC_FILE;
		} else if (face_direction.equals(UP_LEFT_UNIT_VECTOR)) {
			graphic_filename = FACE_UL_GRAPHIC_FILE;
		} else if (face_direction.equals(UP_RIGHT_UNIT_VECTOR)) {
			graphic_filename = FACE_UR_GRAPHIC_FILE;
		} else if (face_direction.equals(RIGHT_UNIT_VECTOR)) {
			graphic_filename = FACE_R_GRAPHIC_FILE;
		} else if (face_direction.equals(DOWN_LEFT_UNIT_VECTOR)) {
			graphic_filename = FACE_DL_GRAPHIC_FILE;
		} else if (face_direction.equals(DOWN_RIGHT_UNIT_VECTOR)) {
			graphic_filename = FACE_DR_GRAPHIC_FILE;
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

	private boolean handling_mother_ship_collision = false;
	int mother_ship_collision_countdown;
	
	private void enforceTacticalSubMotherShipDistance(final MotherShip e,
			final int delta) {
		
		if(0 < mother_ship_collision_countdown) {
			mother_ship_collision_countdown -= delta;
		} else if (handling_mother_ship_collision){
			handling_mother_ship_collision = false;
			this.destination_position = this.getPosition();
		}
		
		Collision collision = this.collides(e);
				
		if ((null != collision) && (handling_mother_ship_collision == false)) {
			handling_mother_ship_collision = true;
			mother_ship_collision_countdown = 150;
			velocity = velocity.negate();
		}
	}
	
	private void enforceTacticalSubTacticalSubDistance(final TacticalSub e,
			final int delta) {
		
	}
	
	public void update(int delta) {
		super.update(delta);
		
		for(FloatingEntity e : this.getPotentialCollisions()) {
			
			/* MotherShip to MotherShip collision */
			
			if(e instanceof MotherShip)
				this.enforceTacticalSubMotherShipDistance((MotherShip)e, delta);
			else /* assume TacticalSub */
				this.enforceTacticalSubTacticalSubDistance((TacticalSub)e, delta);
		}
		
		reward = 0;
		hitTarget = false;
		
		if (tacticalTorpedo != null) {
			tacticalTorpedo.update(delta);
			if(tacticalTorpedo.collides(target)!=null) {
				hitTarget = true;
				ResourceManager.getSound(HIT_SOUND).play();
				double damage = Math.random() * 50 % 50 + 100 ;
				target.health -= damage;
				reward += damage;
				attackPosition = new Vector(tacticalTorpedo.getX(), tacticalTorpedo.getY());
				tacticalTorpedo = null;
			}
		}
		if (torpedoTimer > 0) {
			torpedoTimer -= delta;
		} else {
			tacticalTorpedo = null;
			if (isAttacking && target.health > 0 && this.health > 0) {
					fire(target);
			}
		}
	}

	
	ArrayList<Soldier> soldiers = new ArrayList<Soldier>();
	public void load(Soldier soldier) {
		soldiers.add(soldier);
	}
	
	public void unload() {
		for (Soldier soldier : soldiers) {
			soldier.setPosition(getPosition());
			soldier.visible = true;
		}
		soldiers.clear();
	}
}
