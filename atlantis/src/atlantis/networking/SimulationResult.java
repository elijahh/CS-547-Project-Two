package atlantis.networking;

import java.io.Serializable;

import atlantis.AtlantisEntity;

/*
 * Format of simulation results that host might send to clients
 */

public class SimulationResult implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final transient int MAP = 0;
	public static final transient int ENTITY_UPDATER = 1;
	public static final transient int MESSAGE = 2;
	public static final transient int GAME_OVER = 3;
	
	public int type;
	
	public String mapName;
	public String message;
	public boolean red_wins;
	public AtlantisEntity.Updater entity_updater;
	
	public void setMap(String mapName) {
		type = MAP;
		this.mapName = mapName;
	}
	
	public void setEntityUpdater(AtlantisEntity.Updater updater) {
		type = ENTITY_UPDATER;
		this.entity_updater = updater;
	}
	
	public void setMessage(String message) {
		type = MESSAGE;
		this.message = message;
	}
	
	public void setGameOver(boolean red_wins) {
		type = GAME_OVER;
		this.red_wins = red_wins;
	}
}
