package pokemon.modele;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Random;
import java.util.Vector;

import pokemon.annotations.Tps;
import pokemon.launcher.PokemonCore;
import pokemon.vue.CombatV;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;

/* La classe Map permet de regrouper toutes les informations
 * concernant une map, notemment sa TiledMap, ainsi que tout les
 * objets present sur la map */

@Tps(nbhours=10)
public class Map implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5865738960359926159L;
	//Proprietes de la Map
	private TiledMap tiledMap;
	private Music music;	
	//Attributs des NPC
	private Vector<NPC> npcs;
	
	//Attributs des changements de Map
	private Vector<MapChange> mapChanges;
	//pool des pokemons qui peuvent spawn
	private int[] spawn;
	//level range
	private int[] lvlRange;
	//Constructeurs
	public Map() {
		tiledMap = new TmxMapLoader().load("map.tmx");
		npcs = new Vector<NPC>();
		mapChanges = new Vector<MapChange>();
		
		setMusique(new Music(tiledMap));
	}	
	public Map(String path) {
		TmxMapLoader loader = new TmxMapLoader();
		tiledMap = loader.load(path);
		npcs = new Vector<NPC>();
		mapChanges = new Vector<MapChange>();
		
		getTransitions();
		setMusique(new Music(tiledMap));
	}
	public Map(String path, NPCList npcList) {
		TmxMapLoader loader = new TmxMapLoader();
		tiledMap = loader.load(path);
		npcs = new Vector<NPC>();
		mapChanges = new Vector<MapChange>();
		getTransitions();
		getNPCs(npcList);
		getPokemons();
		setMusique(new Music(tiledMap));
	}
	
	private void getPokemons(){
		lvlRange=new int[2];
		XmlReader reader = new XmlReader();
		try {
			Element root = reader.parse(Gdx.files.internal("xml/spawnPool.xml"));
			lvlRange[0]=root.getChildByName("levelRange").getIntAttribute("min");
			lvlRange[1]=root.getChildByName("levelRange").getIntAttribute("max");
			Element b=root.getChildByName("pokemons");
			Array<Element> elm=b.getChildrenByName("p");
			spawn=new int[elm.size];
			int i=0;
			for(Element e:elm){
				spawn[i]=e.getIntAttribute("id");
				i++;
			}
			System.out.println(Arrays.toString(spawn));
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//Fonction privees
	private void getNPCs(NPCList npcList) {
		MapLayer objectLayer = tiledMap.getLayers().get("NPCs");
		if(objectLayer != null) {			
			for(MapObject o : objectLayer.getObjects()) {
				NPC npc = null;
				float x = Float.parseFloat(o.getProperties().get("x").toString());
				float y = Float.parseFloat(o.getProperties().get("y").toString());
				if(o.getProperties().containsKey("name")) {
					if(o.getProperties().containsKey("trainer")) {
						boolean isAggressive = o.getProperties().containsKey("aggressive");
						npc = new Dresseur(o.getProperties().get("name").toString(), new Vector2(x, y), isAggressive);
					}
					else {						
						npc = new NPC(o.getProperties().get("name").toString(), new Vector2(x, y));
					}
					if(o.getProperties().containsKey("orientation")) {
						Direction dir;
						String prop = o.getProperties().get("orientation", String.class);
						if(prop.equalsIgnoreCase("north")) {
							dir = Direction.North;
						}
						else if(prop.equalsIgnoreCase("east")) {
							dir = Direction.East;
						}
						else if(prop.equalsIgnoreCase("south")) {
							dir = Direction.South;
						}
						else if(prop.equalsIgnoreCase("west")) {
							dir = Direction.West;
						}
						else {
							dir = Direction.South;
						}
						npc.setOrientation(dir);
					}
					npcs.addElement(npc);
					npcList.addNPC(npc);
				}
				else {
					npc = new NPC(new Vector2(x, y));
					npcs.addElement(npc);
					npcList.addNPC(npc);
				}
			}
		}
	}
	private void getTransitions() {
		MapLayer objectLayer = tiledMap.getLayers().get("Regions");
		if(objectLayer != null)
		{
			for(MapObject o : objectLayer.getObjects()) {
				String destMap = o.getProperties().get("map", String.class);
				String orientationStr = o.getProperties().get("orientation", String.class);
				Direction orientation = Direction.toDirection(orientationStr);
				Vector2 pos = new Vector2();
				Vector2 dim = new Vector2();
				Vector2 targetPos = new Vector2();
				targetPos.x = Float.parseFloat(o.getProperties().get("dest_x", String.class));
				targetPos.y = Float.parseFloat(o.getProperties().get("dest_y", String.class));

				RectangleMapObject r = (RectangleMapObject) o;
				pos.x = r.getRectangle().x;
				pos.y = r.getRectangle().y;
				dim.x = r.getRectangle().width;
				dim.y = r.getRectangle().height;
				
				mapChanges.add(new MapChange(destMap, orientation, pos, dim, targetPos));
			}
		}
	}
	
	//Fonctionnalites principales
	public boolean collide(Vector2 nextPos, int spriteWidth, int spriteHeight) {
		//Collision avec les tiles
		TiledMapTileLayer layerCollision = (TiledMapTileLayer) tiledMap.getLayers().get(1);
		if(layerCollision.getCell((int)(nextPos.x/16f),(int)(nextPos.y/16f))!=null ||
				layerCollision.getCell((int)((nextPos.x+spriteWidth-5)/16f),(int)(nextPos.y/16f))!=null ||
				layerCollision.getCell((int)((nextPos.x+spriteWidth-5)/16f),(int)((nextPos.y+spriteHeight-5)/16f))!=null ||
				layerCollision.getCell((int)((nextPos.x)/16f),(int)((nextPos.y+spriteHeight-5)/16f))!=null) {
			return true;
		}
		
		//Collision avec les NPC
		Rectangle playerHitbox = new Rectangle(nextPos.x, nextPos.y, spriteWidth, spriteHeight-5);
		for(NPC npc : npcs) {
			Vector2 npcPos = npc.getPos();
			Rectangle npcHitbox = new Rectangle(npcPos.x+1, npcPos.y+16, 13, 19);
			if(playerHitbox.overlaps(npcHitbox)) {
				return true;
			}
		}
		return false;
	}
	
	public void herb(Vector2 nextPos, int spriteWidth, int spriteHeight) throws WildBattleException{
		//Collision avec les tiles
		//System.out.println(((TiledMapTileLayer) tiledMap.getLayers().get(0)).getCell((int)(nextPos.x/16f),(int)(nextPos.y/16f)).getTile().getId());

		Random r=new Random();
		TiledMapTileLayer layerCollision = (TiledMapTileLayer) tiledMap.getLayers().get(2);
		if(layerCollision.getCell((int)(nextPos.x/16f),(int)(nextPos.y/16f))!=null ||
				layerCollision.getCell((int)((nextPos.x+spriteWidth-5)/16f),(int)(nextPos.y/16f))!=null ||
				layerCollision.getCell((int)((nextPos.x+spriteWidth-5)/16f),(int)((nextPos.y+spriteHeight-5)/16f))!=null ||
				layerCollision.getCell((int)((nextPos.x)/16f),(int)((nextPos.y+spriteHeight-5)/16f))!=null) {
			if(r.nextInt(200)<2){
				//throw new WildBattleException();
				System.out.println("Lancer combat avec "+Pokedex.values()[spawn[r.nextInt(spawn.length)]+1].get().getNom()+" niveau "+(r.nextInt(lvlRange[1]-lvlRange[0])+lvlRange[0]));
			}
		}
		}
	/* Interagit avec le NPC de la position donnee
	 * Renvoie null si aucun NPC n'est present sur place.*/
//	public String interact(Joueur j, Vector2 target, NPCList npcList) throws NoMoreInstructionException, NoNPCException, MovementException {
//		for(NPC npc : npcs) {
//			Rectangle npcHitbox = new Rectangle(npc.getPos().x, npc.getPos().y+16, 16, 16);
//			if(npcHitbox.contains(target)) {
//				switch(j.orientation) {
//				case East:
//					npc.setOrientation(Direction.West);
//					break;
//				case North:
//					npc.setOrientation(Direction.South);
//					break;
//				case South:
//					npc.setOrientation(Direction.North);
//					break;
//				case West:
//					npc.setOrientation(Direction.East);
//					break;
//				default:
//					break;			
//				}
//				String res = npc.executeDialog(j, npcList);
//				if(npc.getMoveDistance() > 0) {
//					throw new MovementException();
//				}
//				return res;
//			}
//		}
//		throw new NoNPCException();
//	}
	//Renvoie le NPC devant le joueur. Si il y en a pas, renvoie null.
	public NPC getNPC(Joueur j) {
		//On determine le point cible
		Vector2 target = new Vector2();
		Vector2 center = new Vector2();
		center.x = j.getPos().x + (14/2);
		center.y = j.getPos().y + (14/2); //Car spriteHeight - 5 = spriteWidth
		int range = 12;
		switch(j.getOrientation()) {
		case East:
			target.x = center.x + range;
			target.y = center.y;
			break;
		case North:
			target.x = center.x;
			target.y = center.y + range;
			break;
		case South:
			target.x = center.x;
			target.y = center.y - range;
			break;
		case West:
			target.x = center.x - range;
			target.y = center.y;
			break;
		default:
			break;	
		}
		
		//Pour chaque NPC de la map...
		for(NPC npc : npcs) {
			//... on verifie si un NPC se trouve au point cible ...
			Rectangle npcHitbox = new Rectangle(npc.getPos().x, npc.getPos().y+16, 16, 16);
			if(npcHitbox.contains(target)) {
				//... et on renvoie le NPC en question.
				return npc;
			}
		}
		//Sinon, on renvoie null.
		return null;
	}
	public void addNPC(NPC npc, NPCList npcList) {
		npcs.addElement(npc);
		npcList.addNPC(npc);
	}
	
	//Accesseurs
	public Music getMusique() {
		return music;
	}
	public void setMusique(Music music) {
		this.music = music;
	}
	public TiledMap getTiledMap() {
		return tiledMap;
	}
	public void setTiledMap(TiledMap tiledMap) {
		this.tiledMap = tiledMap;
	}
	public Vector<MapChange> getMapChanges() {
		return mapChanges;
	}
	public void setMapChanges(Vector<MapChange> mapChanges) {
		this.mapChanges = mapChanges;
	}
	public Vector<NPC> getNpcs() {
		return npcs;
	}
	public NPC getNPCById(int i) {
		for(NPC npc : npcs) {
			if(npc.getId() == i) {
				return npc;
			}
		}
		return null;
	}
}
