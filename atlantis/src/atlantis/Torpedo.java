package atlantis;

import org.newdawn.slick.Image;

import atlantis.AtlantisEntity.Team;

import jig.Entity;
import jig.ResourceManager;
import jig.Vector;
import jig.Shape;
	
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.newdawn.slick.Animation;

public class Torpedo extends Entity {
	
	private static final String FACE_U_GRAPHIC_FILE = "atlantis/resource/Torpedo-Up.png";
	private static final String FACE_D_GRAPHIC_FILE = "atlantis/resource/Torpedo-down.png";
	private static final String FACE_L_GRAPHIC_FILE = "atlantis/resource/Torpedo-left.png";
	private static final String FACE_R_GRAPHIC_FILE = "atlantis/resource/Torpedo-right.png";
	
	private static final String MOVE_L_ANIMATION_FILE = "atlantis/resource/Torpedo-Left.png";
	private static final String MOVE_R_ANIMATION_FILE = "atlantis/resource/Torpedo-Right.png";
	private static final String MOVE_U_ANIMATION_FILE = "atlantis/resource/Torpedo-Up.png";
	private static final String MOVE_D_ANIMATION_FILE = "atlantis/resource/Torpedo-Down.png";
	
//	private static final String RED_ICON = "atlantis/resource/red-worker.png";
//	private static final String BLUE_ICON = "atlantis/resource/blue-worker.png";
	
	private static final float MAX_VELOCITY = 0.12f;       /* pixels/mS */
	
	private static final int ANIMATION_FRAMES = 1;
	private static final int ANIMATION_FRAME_DURATION = 200; /* mS */
	
	private static int ANIMATION_FRAME_WIDTH = 50; /* pixels */
	private static int ANIMATION_FRAME_HEIGHT = 200; /* pixels */
	
	Image torpedo;
	private static final String BLUETORPEDOIMG_RSC = "atlantis/resource/soldier-torpedo-blue.png";
	private static final String REDTORPEDOIMG_RSC = "atlantis/resource/soldier-torpedo-red.png";
	
	static {
		ResourceManager.loadImage(BLUETORPEDOIMG_RSC);
		ResourceManager.loadImage(REDTORPEDOIMG_RSC);
	}
	
	public Torpedo(final float x, final float y, double theta, Team team) {
		super(x, y);
		
		if (team == Team.BLUE) { 
			torpedo = ResourceManager.getImage(BLUETORPEDOIMG_RSC).copy();
		} else {
			torpedo = ResourceManager.getImage(REDTORPEDOIMG_RSC).copy();
		}
		torpedo.setRotation((float) theta);
		
		addImageWithBoundingBox(torpedo);
	}
	
	public void update(int delta) {
		translate(Vector.getUnit(getRotation()).scale(delta / 16f));
	}
	
	@Override
	public double getRotation() {
		return torpedo.getRotation();
	}
	
	@Override
	public void setRotation(double theta) {
		torpedo.setRotation((float) theta);
	}
}
