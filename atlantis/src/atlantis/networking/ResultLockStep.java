package atlantis.networking;

import java.io.Serializable;
import java.util.ArrayList;

public class ResultLockStep implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public ArrayList<SimulationResult> frameResults;
	public int frameNum;
	
	public ResultLockStep(int frameNum) {
		this.frameNum = frameNum;
		frameResults = new ArrayList<SimulationResult>();
	}

	public void addResult(SimulationResult result) {
		frameResults.add(result);
	}
}
