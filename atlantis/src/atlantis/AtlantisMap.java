package atlantis;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jig.Vector;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.tiled.TileSet;
import org.newdawn.slick.tiled.TiledMap;

import dijkstra.model.Edge;
import dijkstra.model.Vertex;

public class AtlantisMap extends TiledMap {

	public AtlantisMap(String ref) throws SlickException {
		super(ref);
	}

	public AtlantisMap(InputStream in) throws SlickException {
		super(in);
	}

	public AtlantisMap(String ref, boolean loadTileSets) throws SlickException {
		super(ref, loadTileSets);
	}

	public AtlantisMap(String ref, String tileSetsLocation)
			throws SlickException {
		super(ref, tileSetsLocation);
	}

	public AtlantisMap(InputStream in, String tileSetsLocation)
			throws SlickException {
		super(in, tileSetsLocation);
	}
	
	static private List<Integer> blocked_nodes;
	
	static public List<Integer> getBlockedNodes() {
		return Collections.unmodifiableList(blocked_nodes);
	}
	
	static public boolean isPositionVectorInsideTerrainTile(Vector position) {
		
		return false;
	}
	
	public void processMovementCostsIntoEdges(final List<Vertex> nodes,
			List<Edge> edges) {
		List<Edge> remove_edges = new ArrayList<Edge>(edges.size());
		List<Edge> add_edges = new ArrayList<Edge>(edges.size());
		
		// System.out.println(edges.size());
		
		blocked_nodes = new ArrayList<Integer>(nodes.size());
				
		for (int i = 0; i < getWidth(); i++)
			for (int j = 0; j < getHeight(); j++) {
				int tile_id = getTileId(i, j, 0);

				/*
				 * Avoid non-zero tiles for now. Refinement will be possible
				 * once we can pull the terrain type.
				 */

				if (tile_id != 0) {
					int map_node_id = j * AtlantisEntity.MAP_GRID_X + i;
					Vertex map_node = nodes.get(map_node_id);
					
					blocked_nodes.add(map_node_id);
															 
					for (Edge edge : edges) {
						if (edge.getDestination() == map_node) {
							remove_edges.add(edge);
							Edge new_edge = new Edge(edge.getId(),
									edge.getSource(), edge.getDestination(),
									Integer.MAX_VALUE/2);
							add_edges.add(new_edge);
						}
					}
				}
			}
				
		edges.removeAll(remove_edges);
		
		// System.out.println(remove_edges.size());
		// System.out.println(edges.size());
		
		edges.addAll(add_edges);
		
		// System.out.println(add_edges.size());
		// System.out.println(edges.size());
	}
}
