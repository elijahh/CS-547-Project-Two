package atlantis;

import java.io.InputStream;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.tiled.TiledMap;

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
}
