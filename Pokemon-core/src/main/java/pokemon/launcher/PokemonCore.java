package pokemon.launcher;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;

import pokemon.annotations.Tps;
import pokemon.controle.MenuListener;
import pokemon.modele.*;
import pokemon.vue.Musics;
import pokemon.vue.StartScreen;


@Tps(nbhours=2)
public class PokemonCore extends Game {
	SpriteBatch batch;
	Texture img;
	TiledMap tiledMap;
    OrthographicCamera camera;
    TiledMapRenderer tiledMapRenderer;
    
    public static NPCList npcList = new NPCList();
    public static Minimap m=new Minimap();
    public static Joueur Jtest=new Joueur();
    public static Joueur Jtest2=new Joueur();
    public static Pkm[] Ptest=new Pkm[6];
    public static Pkm[] Ptest2=new Pkm[6];
   
 
	@Override
	public void create () {
		PokemonCore.initStatic();
		Musics.loadContent();
		//Jtest.Sauvegarder();
		//Jtest.Charger();
		Jtest.setCurrentMap(new Map("maps/bigmap.tmx", npcList));
		MapScreen mapS=new MapScreen(this);
		//MenuListener menu=new MenuListener(this,mapS);
		//this.setScreen(new StartScreen(this));
		//TestMap2 test = new TestMap2();
		this.setScreen(mapS);
		//Combat test=new Combat(Jtest, Jtest2);
		//test.start();
		//this.setScreen(new CombatV(test,this));
	}
	
	
	
	//@Tps(nbhours=2)
	 public static void initStatic(){
	    	Ptest[0]=new Pkm(Pokedex.Reptincel.get(),25); Ptest2[0]=new Pkm(Pokedex.Reptincel.get(),25);
	    	Ptest[1]=new Pkm(Pokedex.Herbizarre.get(),25); Ptest2[1]=new Pkm(Pokedex.Herbizarre.get(),25);
	    	Ptest[2]=new Pkm(Pokedex.Carabaffe.get(),25); Ptest2[2]=new Pkm(Pokedex.Carabaffe.get(),25);
	    	Ptest[3]=new Pkm(Pokedex.Roucoups.get(),25); Ptest2[3]=new Pkm(Pokedex.Roucoups.get(),25);
	    	Ptest[4]=new Pkm(Pokedex.Rattatac.get(),25); Ptest2[4]=new Pkm(Pokedex.Rattatac.get(),25);
	    	Ptest[5]=new Pkm(Pokedex.Pikachu.get(),25); Ptest2[5]=new Pkm(Pokedex.Pikachu.get(),25);
	    	Ptest[0].setAttacks(12045);
	    	System.out.println("Attribut Attacks de Reptincel : "+Ptest[0].getAttacks());
	    	System.out.println("ID de l'attaque Charge : "+bddCapacite.Charge.get().getID());
	    	System.out.println(Ptest[0].getNom()+" peut il apprendre Charge ? " +  bddCapacite.Charge.get().peutApprendre(Ptest[0]));
	    	//Ptest[0].infliger(56);
	    	Ptest[1].infliger(20);
	    	//Ptest[4].infliger(999);
	    	for(int i=0;i<6;i++){
	    		//Ptest[i].give(Medicament.baieTest); Ptest2[i].give(Medicament.baieTest);
	    		if(i==0){
	    			Ptest[i].add(bddCapacite.PoingEclair.get()); Ptest2[i].add(bddCapacite.PoingEclair.get());
	    			Ptest[i].add(bddCapacite.JetDeSable.get()); Ptest2[i].add(bddCapacite.JetDeSable.get());
	    			Ptest[i].add(bddCapacite.PoingDeFeu.get()); Ptest2[i].add(bddCapacite.PoingDeFeu.get());
	    			Ptest[i].add(bddCapacite.Zenith.get()); Ptest2[i].add(bddCapacite.Zenith.get());
	    			Ptest[i].give(Medicament.baieTest);
	    		}
	    		if(i==1){
	    			Ptest[i].add(bddCapacite.Charge.get()); Ptest2[i].add(bddCapacite.Charge.get());
	    			Ptest[i].add(bddCapacite.JetDeSable.get()); Ptest2[i].add(bddCapacite.JetDeSable.get());
	    			Ptest[i].add(bddCapacite.Seisme.get()); Ptest2[i].add(bddCapacite.Seisme.get());
	    			Ptest[i].add(bddCapacite.MegaSangsue.get()); Ptest2[i].add(bddCapacite.MegaSangsue.get());
	    			Ptest[i].setStatut(Statut.Gele);  //Ptest2[i].setStatut(Statut.Empoisonne);
	    		}
	    		if(i==2){
	    			Ptest[i].add(bddCapacite.Surf.get()); Ptest2[i].add(bddCapacite.Surf.get());
	    			Ptest[i].add(bddCapacite.Grele.get()); Ptest2[i].add(bddCapacite.Grele.get());
	    			Ptest[i].add(bddCapacite.PoingGlace.get()); Ptest2[i].add(bddCapacite.PoingGlace.get());
	    			Ptest[i].add(bddCapacite.DansePluie.get()); Ptest2[i].add(bddCapacite.DansePluie.get());
	    		}
	    		if(i==3){
	    			Ptest[i].add(bddCapacite.CruAile.get()); Ptest2[i].add(bddCapacite.CruAile.get());
	    			Ptest[i].add(bddCapacite.JetDeSable.get()); Ptest2[i].add(bddCapacite.JetDeSable.get());
	    			Ptest[i].add(bddCapacite.JetPierres.get()); Ptest2[i].add(bddCapacite.JetPierres.get());
	    			Ptest[i].add(bddCapacite.TempeteDeSable.get()); Ptest2[i].add(bddCapacite.TempeteDeSable.get());
	    		}
	    		if(i==4){
	    			Ptest[i].add(bddCapacite.CrocDeMort.get()); Ptest2[i].add(bddCapacite.CrocDeMort.get());
	    			Ptest[i].add(bddCapacite.JetDeSable.get()); Ptest2[i].add(bddCapacite.JetDeSable.get());
	    			Ptest[i].add(bddCapacite.CrochetVenin.get()); Ptest2[i].add(bddCapacite.CrochetVenin.get());
	    			Ptest[i].add(bddCapacite.TempeteDeSable.get()); Ptest2[i].add(bddCapacite.TempeteDeSable.get());
	    		}
	    		if(i==5){
	    			Ptest[i].add(bddCapacite.Tonnerre.get()); Ptest2[i].add(bddCapacite.Tonnerre.get());
	    			Ptest[i].add(bddCapacite.JetDeSable.get()); Ptest2[i].add(bddCapacite.JetDeSable.get());
	    			Ptest[i].add(bddCapacite.PoingGlace.get()); Ptest2[i].add(bddCapacite.PoingGlace.get());
	    			Ptest[i].add(bddCapacite.DansePluie.get()); Ptest2[i].add(bddCapacite.DansePluie.get());
	    		}
	    		Jtest.add(Ptest[i]); Jtest2.add(Ptest2[i]);
	    	}
	    	Pkm[] temp=Jtest2.getTeam();
	    	temp[0]=Ptest2[2]; temp[2]=Ptest2[0];
	    	//temp=Jtest.getTeam();
	    	//temp[1]=Ptest[0]; temp[0]=Ptest[1];
	    	
	    	
	    	//ajout d'objets dans l'inventaire
	    	Jtest.add(0,Medicament.baieTest,6);  Jtest2.add(0,Medicament.baieTest,6);
	    	Jtest.add(0,Medicament.rappel,3);  Jtest2.add(0,Medicament.rappel,3);
	    	Jtest.add(1,ObjetRare.Bicyclette,1);  Jtest2.add(1,ObjetRare.Bicyclette,1);
	    	Jtest.add(1,ObjetRare.Machette,1);  Jtest2.add(1,ObjetRare.Machette,1);
	    	Jtest.add(1,ObjetRare.Lampe,1);  Jtest2.add(1,ObjetRare.Lampe,1);
	    	Jtest.add(2,CT.surf,1);  Jtest2.add(2,CT.surf,1);
	    	Jtest.add(2,CT.tonnerre,1);  Jtest2.add(2,CT.tonnerre,1);
	    	Jtest.add(3,Pokeball.pokeball,25);  Jtest2.add(3,Pokeball.pokeball,25);
	    	Jtest.add(3,Pokeball.superball,10);  Jtest2.add(3,Pokeball.superball,10);
	    	Jtest.add(3,Pokeball.hyperball,5);  Jtest2.add(3,Pokeball.hyperball,5);
	    	Jtest.add(3,Pokeball.masterball,1);  Jtest2.add(3,Pokeball.masterball,1);
	    	Jtest.add(4,Objet.graineMiracle,1);  Jtest2.add(4,Objet.graineMiracle,1);
	    	Jtest.add(4,Objet.aimant,1);  Jtest2.add(4,Objet.aimant,1);
	    	
	    }
}
