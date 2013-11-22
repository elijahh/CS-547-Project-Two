package atlantis.networking;

import java.io.Serializable;


/*
 * Format of simulation results that host might send to clients
 */

public class SimulationResult implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final transient int MAP = 0;
	
	public int type;
	public int frameNum;
	public String mapName;
	
	public void setMap(String mapName) {
		type = MAP;
		this.mapName = mapName;
		frameNum = 0;
	}
}
