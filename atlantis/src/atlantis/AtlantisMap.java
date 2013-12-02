package atlantis;

import java.io.InputStream;
import java.util.List;

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

	public void processMovementCostsIntoEdges(List<Vertex> nodes,
			List<Edge> edges) {
		
		for(int i = 0; i < this.getWidth(); i++)
			for(int j = 0; j < this.getHeight(); j++) {
				int tile_id = this.getTileId(i, j, 0);
				
				/* Avoid non-zero tiles for now. Refinement will be possible once
				 * we can pull the terrain type. */
				
				if(tile_id != 0) {
					int map_node_id = j * AtlantisEntity.MAP_GRID_X + j;
					Vertex map_node = nodes.get(map_node_id);
					
					
				}
			}
	}
}
