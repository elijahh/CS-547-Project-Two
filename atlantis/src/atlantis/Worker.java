package atlantis;

import org.newdawn.slick.Animation;

import jig.ResourceManager;
import jig.Vector;

public class Worker extends GroundEntity {
	
	private static final String FACE_U_GRAPHIC_FILE = "atlantis/resource/worker_u.png";
	private static final String FACE_D_GRAPHIC_FILE = "atlantis/resource/worker_d.png";
	private static final String FACE_L_GRAPHIC_FILE = "atlantis/resource/worker_l.png";
	private static final String FACE_R_GRAPHIC_FILE = "atlantis/resource/worker_r.png";
	private static final String FACE_UL_GRAPHIC_FILE = "atlantis/resource/worker_ul.png";
	private static final String FACE_UR_GRAPHIC_FILE = "atlantis/resource/worker_ur.png";
	private static final String FACE_DL_GRAPHIC_FILE = "atlantis/resource/worker_dl.png";
	private static final String FACE_DR_GRAPHIC_FILE = "atlantis/resource/worker_dr.png";

	private static final String MOVE_L_ANIMATION_FILE = "atlantis/resource/worker_l_move.png";
	private static final String MOVE_R_ANIMATION_FILE = "atlantis/resource/worker_r_move.png";
	private static final String MOVE_U_ANIMATION_FILE = "atlantis/resource/worker_u_move.png";
	private static final String MOVE_UR_ANIMATION_FILE = "atlantis/resource/worker_ur_move.png";
	private static final String MOVE_UL_ANIMATION_FILE = "atlantis/resource/worker_ul_move.png";
	private static final String MOVE_D_ANIMATION_FILE = "atlantis/resource/worker_d_move.png";
	private static final String MOVE_DL_ANIMATION_FILE = "atlantis/resource/worker_dl_move.png";
	private static final String MOVE_DR_ANIMATION_FILE = "atlantis/resource/worker_dr_move.png";
	
	private static final float MAX_VELOCITY = 0.10f;       /* pixels/mS */
	
	private static final int ANIMATION_FRAMES = 2;
	private static final int ANIMATION_FRAME_DURATION = 10; /* mS */
	
	private static final int ANIMATION_FRAME_WIDTH = 48; /* pixels */
	private static final int ANIMATION_FRAME_HEIGHT = 48; /* pixels */
	
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
//		ResourceManager.loadImage(FACE_U_GRAPHIC_FILE);
//		ResourceManager.loadImage(FACE_L_GRAPHIC_FILE);
//		ResourceManager.loadImage(FACE_R_GRAPHIC_FILE);
//		ResourceManager.loadImage(FACE_DL_GRAPHIC_FILE);
//		ResourceManager.loadImage(FACE_DR_GRAPHIC_FILE);
//		ResourceManager.loadImage(FACE_UL_GRAPHIC_FILE);
//		ResourceManager.loadImage(FACE_UR_GRAPHIC_FILE);
		
//		ResourceManager.loadImage(MOVE_D_ANIMATION_FILE);
//		ResourceManager.loadImage(MOVE_U_ANIMATION_FILE);
		ResourceManager.loadImage(MOVE_L_ANIMATION_FILE);
		ResourceManager.loadImage(MOVE_R_ANIMATION_FILE);
//		ResourceManager.loadImage(MOVE_DL_ANIMATION_FILE);
//		ResourceManager.loadImage(MOVE_DR_ANIMATION_FILE);
//		ResourceManager.loadImage(MOVE_UL_ANIMATION_FILE);
//		ResourceManager.loadImage(MOVE_UR_ANIMATION_FILE);
	}
	
	public void beginMovement(Vector direction) {
		velocity = new Vector(direction.scale(MAX_VELOCITY));
		movement_direction = direction;
	}
	
	private final String getMovementAnimationFilename(final Vector direction) {
		String animation_filename;

		if (direction.equals(LEFT_UNIT_VECTOR)) {
			animation_filename = MOVE_L_ANIMATION_FILE;
		} else if (direction.equals(UP_UNIT_VECTOR)) {
			animation_filename = MOVE_U_ANIMATION_FILE;
		} else if (direction.equals(UP_RIGHT_UNIT_VECTOR)) {
			animation_filename = MOVE_UR_ANIMATION_FILE;
		} else if (direction.equals(UP_LEFT_UNIT_VECTOR)) {
			animation_filename = MOVE_UL_ANIMATION_FILE;
		} else if (direction.equals(RIGHT_UNIT_VECTOR)) {
			animation_filename = MOVE_R_ANIMATION_FILE;
		} else if (direction.equals(DOWN_RIGHT_UNIT_VECTOR)) {
			animation_filename = MOVE_DR_ANIMATION_FILE;
		} else if (direction.equals(DOWN_LEFT_UNIT_VECTOR)) {
			animation_filename = MOVE_DL_ANIMATION_FILE;
		} else /* Move right */{
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
		} else if (direction.equals(UP_RIGHT_UNIT_VECTOR)) {
			graphic_filename = FACE_UR_GRAPHIC_FILE;
		} else if (direction.equals(UP_LEFT_UNIT_VECTOR)) {
			graphic_filename = FACE_UL_GRAPHIC_FILE;
		} else if (direction.equals(RIGHT_UNIT_VECTOR)) {
			graphic_filename = FACE_R_GRAPHIC_FILE;
		} else if (direction.equals(DOWN_RIGHT_UNIT_VECTOR)) {
			graphic_filename = FACE_DR_GRAPHIC_FILE;
		} else if (direction.equals(DOWN_LEFT_UNIT_VECTOR)) {
			graphic_filename = FACE_DL_GRAPHIC_FILE;
		} else {
			graphic_filename = FACE_D_GRAPHIC_FILE;
		}

		return graphic_filename;
	}
}
