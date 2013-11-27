package atlantis;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.tiled.TiledMap;

import jig.Vector;
import dijkstra.engine.DijkstraAlgorithm;
import dijkstra.model.Edge;
import dijkstra.model.Graph;
import dijkstra.model.Vertex;

abstract class GroundEntity extends AtlantisEntity {
	
	public GroundEntity(final float x, final float y,
			Vector movement_direction) {
		super(x, y, movement_direction);
	}
	
	@Override
	public int compareTo(final AtlantisEntity other) {
		Float min_entity_y = getCoarseGrainedMinY();
		return min_entity_y.compareTo(other.getCoarseGrainedMinY());
	}

	/* -------------------------------------------------------------------- */

	/* Ground entities have to move according to the map. Populate from 
	 * TiledMap data. Initialize with an empty graph. */
	
	static { populateTerrainMap(null); }
	
	public static void populateTerrainMap(TiledMap map) {
		List<Edge> map_edges = new ArrayList<Edge>();
		List<Vertex> map_nodes = new ArrayList<Vertex>();

		if(null != map) {
			
			/* Process TiledMap into nodes/edges. */
		}

		Graph graph = new Graph(map_nodes, map_edges);
		dijkstra = new DijkstraAlgorithm(graph);
	}
	
	/* -------------------------------------------------------------------- */

	/* Collision avoidance - ground entities should avoid each other. 
	 * Overriding update to handle task. */
	
	@Override
	public void update(final int delta) {
		super.update(delta);
		
		for(AtlantisEntity e : listNearbyEntities()) {
			if(false == (e instanceof GroundEntity)) 
				continue;
			
			
		}
	}
}