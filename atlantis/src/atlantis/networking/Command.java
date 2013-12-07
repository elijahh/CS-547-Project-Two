package atlantis.networking;

import java.io.Serializable;

import jig.Vector;

/*
 * Need to include any form of command clients might send to host 
 */

public class Command implements Serializable {
	
	private static final long serialVersionUID = 1L;

	public static final short MOVEMENT = 0;
	public static final short ATTACK = 1;
	public static final short PURCHASE = 2;
	
	public short type;
	public int frameNum;
	public Vector target;
	
	public long entityId;
	public long attackTargetId;
	
	public Command(short type, int frameNum, Vector target, long entityId,
			long attackTargetId) {
		this.type = type;
		this.frameNum = frameNum;
		
		this.target = target;
		
		this.entityId = entityId;
		this.attackTargetId = attackTargetId;
	}
}
