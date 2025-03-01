package pokemon.modele;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.*;
import java.util.Random;

public class Atk extends Capacite implements Serializable {
	/**
	 * 
	 */
	
	protected static Atk confDmg=new Atk(Type.Neutre);
	
	
	private static final long serialVersionUID = 5046958113917347481L;
	protected Statut effet;
	protected int effetProc;
	
	public Atk(){
		super(); this.effet=Statut.Normal;
	}
	public Atk(Type elt){super(elt); this.type=3; this.power=40; this.pre=100; this.CC=0;} 
	public Atk(int pw,int pre,int cc,String nom,String d,Type el,int type,int pp,Statut effet, int efprc){
		super(pw,pre,cc,nom,d,el,type,pp); this.effet=effet; effetProc=efprc;
	}
	
	public void script(Pkm user,Pkm cible,Combat context){
		context.ajoutBuffer(user.nom+" utilise "+nom);
		 Random random=new Random();
		//test de precision
		int touche=0;
		if(random.nextInt(100)<=this.pre){touche=1;}
		//test d'esquive de l'adversaire
		int esquive=0;
		if(random.nextInt(100)<=cible.stats[9][0]){esquive=1;}
		if(touche==0 || esquive==1){
			context.ajoutBuffer(user.nom+" rate son attaque...");
		}
		if(touche==1 && esquive==0){
			if(power>0){cible.infliger(this.atkdamage(user,cible,context,false));}
			if(random.nextInt(100)<=effetProc && this.effet!=Statut.Normal){
				effet.applique(cible,context);}
		
			//Traitement capacite passive
			if(user.capP.flag==3 || (user.capP.flag==1 && type==3) || (user.capP.flag==2 && type==5) ){
				try{
					CapacitePassive.class.getMethod(cible.capP.name(),Pkm.class,Pkm.class).invoke(null,user,cible);
				}
				catch(Exception ex){System.out.println("DAMNIT JAVA\n"+ex);}
			}
		}
	}

}
