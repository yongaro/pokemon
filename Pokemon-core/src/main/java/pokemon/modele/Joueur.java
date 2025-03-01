package pokemon.modele;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Vector;

//import javax.swing.JOptionPane;

import pokemon.annotations.Tps;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

//import pokemon.launcher.PokemonCore;

/* La classe Joueur regroupe toute les informations concernant le joueur, son �quipe
 * de Pokemon ainsi que son etat (argent, badges...). 
 * Contient aussi la base de donnees afin de stocker les pokemons */

@Tps(nbhours=5)
public class Joueur implements CombatInfos, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1207455231877983503L;
	//Attributs du dresseur
	protected int ID;
	protected String nom;
	protected int argent;
	protected int[] badges;
	protected Vector<Stockage<Objet>> inventaire; //  0 Medicaments | 1 Objets Rares | 2 CT/CM | 3 Pokeball | 4 Objets
	
	//Attributs de l'equipe de Pokemon
	protected Pkm[] team;
	protected int teamsize;
	protected Vector<Pkm> boites;
	
	//Attributs concernant la position du joueur
	protected int spriteWidth = 14;
	protected int spriteHeight = 19;
	protected Vector2 pos;
	protected boolean canMove;
	protected Direction moveDirection;
	protected Direction orientation;
	protected Map currentMap;
	protected Vector2 speed;

	public Joueur(){
		ID=0; nom="TODO"; argent=2000; badges=new int[8];
		team=new Pkm[6]; teamsize=0;
		boites=new Vector<Pkm>();
		inventaire=new Vector<Stockage<Objet>>();
		for(int i=0;i<5;i++){ inventaire.add(i,new Stockage<Objet>(30));}
		
		pos = new Vector2(400f, 400f);
		canMove = true;
		speed=new Vector2(0,0);
		moveDirection = Direction.South;
		orientation = Direction.South;
		currentMap = null;
	}
	public Joueur(int id,String nom){
		ID=id; this.nom=nom; argent=2000; badges=new int[8];
		team=new Pkm[6]; teamsize=0;
		boites=new Vector<Pkm>();
		inventaire=new Vector<Stockage<Objet>>();
		for(int i=0;i<5;i++){ inventaire.add(new Stockage<Objet>(30));}
		
		pos = new Vector2(0f, 0f);
		canMove = true;
		moveDirection = Direction.South;
		orientation = Direction.South;
		currentMap = null;
	}
	
	public void Sauvegarder(){
		try{
		FileOutputStream fout = new FileOutputStream("PkmSauvegarde");
		ObjectOutputStream oos = new ObjectOutputStream(fout);
		oos.writeObject(this);
		oos.close();
		}
		catch(IOException ex){
			System.out.println(ex.toString());
			//JOptionPane.showMessageDialog(null, "Echec de la sauvegarde", "Sauvegarde",JOptionPane.ERROR_MESSAGE);
		}
		
	}
	
	public void Charger() throws IOException{
		Joueur temp = null;
		try{
		FileInputStream fin = new FileInputStream("PkmSauvegarde");
		ObjectInputStream ois = new ObjectInputStream(fin);
		temp = (Joueur) ois.readObject();
		this.argent=temp.argent;
		this.badges=temp.badges;
		this.boites=temp.boites;
		this.ID=temp.ID;
		this.inventaire=temp.inventaire;
		this.nom=temp.nom;
		for(Pkm p: temp.team){ 
			for(UniteStockage<Capacite> uc:p.cap){
				uc.cible=bddCapacite.getByID(uc.cibleID);
				//System.out.println("-- "+uc.cibleID+" "+uc.getNom()+" "+uc.getQte()+"/"+uc.getQteMax());
			}
		}
		this.team=temp.team;
		this.teamsize=temp.teamsize;
		}
	/*	catch(IOException ex){
			System.out.println(ex.toString());
			JOptionPane.showMessageDialog(null, "Echec du chargement", "Chargement de Partie",JOptionPane.ERROR_MESSAGE);
		}*/ catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}
	
	
	//Fonction privees
	private boolean changeMap() {
		Vector<MapChange> mapChanges = currentMap.getMapChanges();
		for(MapChange mc : mapChanges) {
			//On check si le joueur est entierment dans la zone de changement
			if((mc.getPos().x <= pos.x && pos.x <= (mc.getPos().x + mc.getDim().x) - spriteWidth) 
					&& (mc.getPos().y <= pos.y && pos.y <= (mc.getPos().y + mc.getDim().y) - spriteHeight)) {
				
				currentMap = new Map(mc.getDestMap());
				pos.x = mc.getTargetPos().x;
				pos.y = mc.getTargetPos().y;
				moveDirection = mc.getOrientation();
				
				return true;
			}
		}
		return false;
	}
	
	//Accesseurs
	public Vector2 getPos() {
		return pos;
	}
	public void setPos(Vector2 pos) {
		this.pos.x=pos.x;
		this.pos.y=pos.y;
	}

	public Map getCurrentMap() {
		return currentMap;
	}
	public void setCurrentMap(Map currentMap) {
		//Sauvegarder();
		this.currentMap = currentMap;
	}
	public Pkm[] getTeam(){ return team; }
	
	public Stockage<Objet> getPocket(int i) {
		return inventaire.get(i);
	}
	public int teamSize(){ return teamsize;}
	
	//Ajoute le pokemon cible a l'equipe du dresseur
	public void add(Pkm cible){
		if(teamsize<6){
			team[teamsize]=cible;
			teamsize++;
		}
	}
	
	//Ajoute un objet dans la poche choisie
	public void add(int poche,Objet obj,int qte){
		if(inventaire.elementAt(poche).contains(obj)){
			int indice=inventaire.elementAt(poche).indiceOf(obj);
			inventaire.elementAt(poche).contenu.elementAt(indice).ajoutQte(qte);
		}
		else{
			inventaire.elementAt(poche).add(new UniteStockage<Objet>(obj,qte));
		}
	}
	
	public Stockage<Objet> getPoche(int poche){ return inventaire.elementAt(poche); }
	
	//enleve le pokemon cible a l'equipe du dresseur
	public void remove(Pkm cible){
		int ind=-1;
		for(int i=0;i<teamsize;i++){
			if(team[i]==cible){
				ind=i; teamsize--;
				break;
			}
		}
		if(ind!=-1){
			for(int i=ind;i<teamsize;i++){
				team[i]=team[i++];
			}
			team[teamsize+1]=null;
		}
	}
	
	public void teamSet(Pkm cible,int ind){ 
		team[ind]=cible;
	}
	
	//Methodes de mouvement
	public void move(Direction orientation) {	
		
		this.moveDirection = orientation;
		switch(this.moveDirection){
		case South:
			this.orientation = orientation;
			speed.y=-60;
			speed.x=0;
			break;
		case North:
			this.orientation = orientation;
			speed.y=60;
			speed.x=0;
			break;
		case East:
			this.orientation = orientation;
			speed.y=0;
			speed.x=60;
			break;
		case West:
			this.orientation = orientation;
			speed.y=0;
			speed.x=-60;
			break;
		case Standing:
			speed.y=0;
			speed.x=0;
		}
	}
	public void updatePosition() throws ChangeMapException,WildBattleException {
		Vector2 nextPos = new Vector2(getPos());

		nextPos.y+=Gdx.graphics.getDeltaTime()*getSpeed().y;
		nextPos.x+=Gdx.graphics.getDeltaTime()*getSpeed().x;
		
		//System.out.println("Nextpos: "+nextPos);
		/*Verif si non debordement de map*/

		if(nextPos.x>800-spriteWidth || nextPos.x<0 ) 
		{setSpeedX(0);nextPos = getPos();}
		if( nextPos.y>800-spriteHeight || nextPos.y<0)
		{setSpeedY(0);nextPos = getPos();}
		/*verif si collision avec decors*/
		if(currentMap.collide(nextPos, spriteWidth, spriteHeight))
		{
			if(getSpeed().x!=0)
				setSpeedX(0);
			if(getSpeed().y!=0)
				setSpeedY(0);
//			System.out.println("Collision DETECTE A " + nextPos);
			//System.out.println("Speed:" +getSpeed());
			nextPos = getPos();
		}
		if(!getPos().epsilonEquals(nextPos,0.5f)){
			currentMap.herb(nextPos, spriteWidth, spriteHeight);
		}
		//System.out.println("GOING TO NEXT POS");
		//speed.x+=10*delta;
		setPos(nextPos);//);=nextPos.x;
		//posy=nextPos.y;
		
		//On retourne vrai si le joueur a change de Map
		if(changeMap())
		{
			throw new ChangeMapException();
		}
		
	}
	
//	public String interact(NPCList npcList) throws NoMoreInstructionException, NoNPCException, MovementException {
//		Vector2 target = new Vector2();
//		Vector2 center = new Vector2();
//		center.x = pos.x + (spriteWidth/2);
//		center.y = pos.y + (spriteWidth/2); //Car spriteHeight - 5 = spriteWidth
//		int range = 12;
//		switch(this.orientation) {
//		case East:
//			target.x = center.x + range;
//			target.y = center.y;
//			break;
//		case North:
//			target.x = center.x;
//			target.y = center.y + range;
//			break;
//		case South:
//			target.x = center.x;
//			target.y = center.y - range;
//			break;
//		case West:
//			target.x = center.x - range;
//			target.y = center.y;
//			break;
//		default:
//			break;	
//		}
//		NPC npc = null;
//		npc = currentMap.getNPC(target);
//		if(npc == null) {
//			throw new NoNPCException();
//		}
//		return currentMap.interact(this, npc, npcList);
//	}
	
	public Vector2 getSpeed() {
		return speed;
	}
	public void setSpeedX(float sx)
	{
		speed.x=sx;
	}
	public void setSpeedY(float sy)
	{
		speed.y=sy;
	}
	public boolean isMoving(){
		return !speed.isZero();
	}
	public boolean canMove() {
		return canMove;
	}
	public void setMove(boolean canMove) {
		this.canMove = canMove;
	}
	public Direction getOrientation() {
		return orientation;
	}
	public void setOrientation(Direction orientation) {
		this.orientation = orientation;
	}
	public Vector2 getDimensions()
	{
		return new Vector2(spriteWidth, spriteHeight);
	}
	
	public Pkm teamAt(int ind){ return team[ind]; }
	public Pkm[] Team(){ return team; }
	public String getName(){ return this.nom;}
	public void stop() {
		speed = new Vector2();
	}
}
