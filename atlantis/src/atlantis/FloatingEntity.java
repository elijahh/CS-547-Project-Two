package atlantis;

import java.util.ArrayList;
import java.util.List;

import jig.Vector;
import dijkstra.engine.DijkstraAlgorithm;
import dijkstra.model.Edge;
import dijkstra.model.Graph;
import dijkstra.model.Vertex;

abstract class FloatingEntity extends AtlantisEntity {

	public FloatingEntity(final float x, final float y,
			Vector movement_direction) {
		super(x, y, movement_direction);
	}
	
	@Override
	public int compareTo(final AtlantisEntity other) {
		Float min_entity_y = getCoarseGrainedMinY();
		return min_entity_y.compareTo(other.getCoarseGrainedMinY());
	}
	
	/* -------------------------------------------------------------------- */

	/* Floating entities move on a simple grid model. */
	
	private static final int MAP_HORIZONTAL_MOVE_COST = 100; /* mS */
	private static final int MAP_VERTICAL_MOVE_COST   = 100; /* mS */
	private static final int MAP_DIAGONAL_MOVE_COST   = 141; /* mS */
	
	static {
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
				if ((0 < j) && (0 < i))
					Graph.addLane(map_edges, map_nodes, n, n - MAP_GRID_X - 1,
							MAP_DIAGONAL_MOVE_COST);
				if ((0 < j) && ((MAP_GRID_X - 1) > i))
					Graph.addLane(map_edges, map_nodes, n, n - MAP_GRID_X + 1,
							MAP_DIAGONAL_MOVE_COST);
			}
		}

		Graph graph = new Graph(map_nodes, map_edges);
		dijkstra = new DijkstraAlgorithm(graph);
	}
	
	/* -------------------------------------------------------------------- */

}
