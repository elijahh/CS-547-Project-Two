package atlantis;

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
	
	public Worker(float x, float y) {
		super(x, y);
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
	
	public final String getMovementAnimationFilename(final Vector direction) {
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
