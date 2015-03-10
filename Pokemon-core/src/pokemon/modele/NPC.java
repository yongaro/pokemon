package pokemon.modele;

import java.io.IOException;
import java.util.Vector;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;

/*La classe NPC permet de stocker toutes les informatiosn relatives au comportement d'un PNJ, 
 * c'est a  dire les lignes de texte que le personnage doit communiquer au joueur*/

public class NPC {
	//status determine quelle ligne de dialogue le PNJ doit reciter
	protected int id;
	protected int status;
	protected Direction orientation;
	protected Vector<Dialog> dialogs;
	
	//Constructeurs
	public NPC() {
		setStatus(0);
		setId(0);
		dialogs = new Vector<Dialog>();
	}
	public NPC(String path) {
		setStatus(0);
		dialogs = new Vector<Dialog>();
		lireXML(path);
	}
	public NPC(String path, int status) {
		setStatus(status);
		dialogs = new Vector<Dialog>();
		lireXML(path);
	}

	//Accesseurs
	public String executeDialog(NPCList npcList) {
		return dialogs.get(status).execute(npcList);
	}
	
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Direction getOrientation() {
		return orientation;
	}
	public void setOrientation(Direction orientation) {
		this.orientation = orientation;
	}
	
	//Fonctions privees
	private void lireXML(String path) {
		XmlReader reader = new XmlReader();
		Element temp = null;
		try {
			//On recupere la racine, et l'id du NPC
			Element root = reader.parse(Gdx.files.internal(path));
			id = root.getInt("id");
			
			//On parcourt chaque dialogues
			for(int i = 0;i<root.getChildCount();i++) {
				temp = root.getChild(i);
				
				Element text = temp.getChildByName("text");
				Element status = temp.getChildByName("status");
				int newStatus = 0, target = 0;
				if(status != null) {
					newStatus = status.getInt("value");
					target = status.getInt("npc");
				}
				dialogs.addElement(new Dialog(text.getText(), target, newStatus));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
