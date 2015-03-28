package pokemon.modele;

import java.util.Random;

public class AtkSoin extends Atk {

	public AtkSoin(){
		super(); this.effet=Statut.Normal;
	}
	public AtkSoin(int pw,int pre,int cc,String nom,String d,Type el,int type,int pp,Statut effet,int efprc){
		super(pw,pre,cc,nom,d,el,type,pp,effet,efprc);
	}
	
	
	public void script(Pkm user,Pkm cible,Combat context){
		context.ajoutBuffer(user.nom+" utilise "+nom,false); 
		Random random=new Random();
		//test de precision
		int touche=0;
		if(random.nextInt(100)<=this.pre){touche=1;}
		//test d'esquive de l'adversaire
		int esquive=0;
		if(random.nextInt(100)<=cible.stats[9][0]){esquive=1;}
		if(touche==0 || esquive==1){
			context.ajoutBuffer(user.nom+" rate son attaque...",true);
		}
		if(touche==1 && esquive==0 && power>0){
			int soin=0;
			if(power>0){
				soin=this.atkdamage(user,cible,context,false);
				cible.infliger(soin);
				soin=(int)(soin/2);
			}
			if(random.nextInt(100)<=effetProc && this.effet!=Statut.Normal){effet.applique(cible);}
			
			//Test de capacite Passive Suintement
			if(cible.capP==CapacitePassive.Suintement){
				context.ajoutBuffer("Suitement de "+cible.nom+" fait perdre des PV a "+user.nom,true);
				//user.stats[2][0]-=soin;
				user.infliger(soin);
			}
			else{
				context.ajoutBuffer("L'attaque soigne "+user.nom,true);
				user.heal(soin);
			}
			
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
