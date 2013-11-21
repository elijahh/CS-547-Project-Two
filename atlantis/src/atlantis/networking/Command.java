package atlantis.networking;

/*
 * Need to include any form of command clients might send to host 
 */

public class Command {

	public static final int MOVEMENT = 0;
	public static final int ATTACK = 1;
	
	public int type;
	public int frame;
	
	
}
