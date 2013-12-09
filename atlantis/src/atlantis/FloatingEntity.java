package atlantis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import jig.Vector;
import dijkstra.engine.DijkstraAlgorithm;
import dijkstra.model.Edge;
import dijkstra.model.Graph;
import dijkstra.model.Vertex;

abstract class FloatingEntity extends AtlantisEntity {

	private static LinkedList<FloatingEntity> floating_entities = 
			new LinkedList<FloatingEntity>();
	
	public FloatingEntity(final float x, final float y,
			Vector movement_direction) {
		super(x, y, movement_direction);
		
		floating_entities.add(this);
	}
	
	@Override
	public int compareTo(final AtlantisEntity other) {
		Float min_entity_y = getCoarseGrainedMinY();
		return min_entity_y.compareTo(other.getCoarseGrainedMinY());
	}
	
	/* -------------------------------------------------------------------- */

	/* Floating entities move on a simple grid model. */
	
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
				
//				if ((0 < j) && (0 < i))
//					Graph.addLane(map_edges, map_nodes, n, n - MAP_GRID_X - 1,
//							MAP_DIAGONAL_MOVE_COST);
//				if ((0 < j) && ((MAP_GRID_X - 1) > i))
//					Graph.addLane(map_edges, map_nodes, n, n - MAP_GRID_X + 1,
//							MAP_DIAGONAL_MOVE_COST);
			}
		}

		graph = new Graph(map_nodes, map_edges);
	}
	
	/* -------------------------------------------------------------------- */
	
	private List<FloatingEntity> potential_collisions =
			new ArrayList<FloatingEntity>();
	
	public List<FloatingEntity> getPotentialCollisions() {
		return Collections.unmodifiableList(potential_collisions);
	}
	
	@Override
	public void update(final int delta) {
		super.update(delta);
		
		/* Handle collisions between FloatingEntities if moving */
		
		if (velocity.length() > 0.0) {
			potential_collisions.clear();

			for (AtlantisEntity e : listNearbyEntities()) {
				if (e instanceof FloatingEntity)
					potential_collisions.add((FloatingEntity) e);
			}
		}
	}
}
