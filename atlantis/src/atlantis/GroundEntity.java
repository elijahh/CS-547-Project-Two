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
	 * TiledMap data. */
	
	public static void populateTerrainMap(TiledMap map) {
		List<Edge> map_edges = new ArrayList<Edge>();
		List<Vertex> map_nodes = new ArrayList<Vertex>();

		// TODO

		Graph graph = new Graph(map_nodes, map_edges);
		dijkstra = new DijkstraAlgorithm(graph);
	}
	
	/* -------------------------------------------------------------------- */

}