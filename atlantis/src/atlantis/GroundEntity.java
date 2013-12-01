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
	
		for (int j = 0; j < MAP_GRID_Y; j++) {
			for (int i = 0; i < MAP_GRID_X; i++) {
				int n = j * MAP_GRID_X + i;

				Vertex location = new Vertex("Node_" + n);
				map_nodes.add(location);

				if (0 < i)
					Graph.addLane(map_edges, map_nodes, n, n - 1,
							MAP_HORIZONTAL_MOVE_COST);
				if (0 < j)
					Graph.addLane(map_edges, map_nodes, n, n - MAP_GRID_X,
							MAP_VERTICAL_MOVE_COST);
			}
		}
		
		if(null != map) {

			/* Process TiledMap into modified nodes/edges. issue17 */
			

		}

		graph = new Graph(map_nodes, map_edges);
	}
	
	/* -------------------------------------------------------------------- */

	/* Collision avoidance - ground entities should avoid each other. 
	 * Overriding update to handle task. */
	
	private List<GroundEntity> potential_collisions =
			new ArrayList<GroundEntity>();
	
	public List<GroundEntity> getPotentialCollisions() {
		return potential_collisions;
	}
	
	@Override
	public void update(final int delta) {
		super.update(delta);
		
		potential_collisions.clear();
		
		for(AtlantisEntity e : listNearbyEntities()) {
			if(e instanceof GroundEntity) 
				potential_collisions.add((GroundEntity)e);
		}
	}
}