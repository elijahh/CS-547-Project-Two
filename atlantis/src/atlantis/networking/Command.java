package atlantis.networking;

import java.io.Serializable;

/*
 * Need to include any form of command clients might send to host 
 */

public class Command implements Serializable {
	
	private static final long serialVersionUID = 1L;

	public static final int MOVEMENT = 0;
	public static final int ATTACK = 1;
	
	public int type;
	public int frameNum;
}
