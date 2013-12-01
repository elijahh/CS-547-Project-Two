package atlantis;

import java.io.InputStream;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.tiled.TileSet;
import org.newdawn.slick.tiled.TiledMap;

public class AtlantisMap extends TiledMap {

	public AtlantisMap(String ref) throws SlickException {
		super(ref);
		
		System.out.println("objectGroups.size() = "+ this.objectGroups.size());
		System.out.println("tileSets.size() = " + this.tileSets.size());
		
		TileSet tileset = getTileSet(0);
		
		System.out.println(tileset);
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
}
