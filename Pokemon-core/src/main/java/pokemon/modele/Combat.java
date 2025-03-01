package pokemon.modele;

import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import java.util.Stack;
import java.util.Vector;

import pokemon.annotations.Tps;
import pokemon.launcher.PokemonCore;

@Tps(nbhours=17)
public class Combat extends Thread {
	protected Terrain terrain;
	protected Climat climat;
	protected PokemonCombat[] equipe1;
	protected PokemonCombat[] equipe2;
	protected PokemonCombat[] pkmListe;

	protected boolean meteo;

	protected boolean endOfTurn;
	protected  String buffer;
	protected boolean bufferReady;
	protected PokemonCombat pCourant; 
	protected PokemonCombat cibleCourante;
	protected Capacite capCur;
	protected int actflag;
	protected int act;
	protected int ind;
	protected boolean freeze;
	protected int fini;
	protected boolean dresseur;
	
	
	//0 niveau 1 XP 2 PV 3 ATT 4 DEF 5 ATTSP 6 DEFSP 7 VIT 8 Precision (100) 9 Esquive (5% de base)
	
	public Combat(){
		terrain=Terrain.Plaine; climat=Climat.Normal;
		buffer=""; bufferReady=false;
		actflag=-1; act=-1; ind=-1;
		freeze=false;
		meteo=false;
		endOfTurn=false;
		fini=0;
		dresseur=false;
	}


	public Combat(Joueur j1,Joueur j2){ this(); this.initSolo(j1,j2); } 
	public Combat(Joueur j,Dresseur d){ this(); this.initSolo(j,d); }
	
	public int gagnant(){
		int nbko1=0; int nbko2=0;
		
		for(int i=0;i<equipe1.length;i++){
			if(equipe1[i].pkm.stats[2][0]<=0){ nbko1++; }
		}
		
		for(int i=0;i<equipe2.length;i++){
			if(equipe2[i].pkm.stats[2][0]<=0){ nbko2++; }
		}
		if(nbko2==equipe2.length){ return 1; }
		if(nbko1==equipe1.length){ return 2; }
		return 0;
	}
		public PokemonCombat[] getPkmListe() {
		return pkmListe;
	}
	public void run(){ this.combatsolo();
	}
	
	
	public void initSolo(Joueur j1,Joueur j2){
		equipe1=new PokemonCombat[j1.teamsize];
		equipe2=new PokemonCombat[j2.teamsize];
		pkmListe=new PokemonCombat[2];
		
		for(int i=0;i<j1.teamsize;i++){
			equipe1[i]=new PokemonCombat(j1.team[i],false,j1); equipe1[i].equipe=equipe1;
			if(equipe1[i].pkm.statut!=Statut.KO && pkmListe[0]==null){
				pkmListe[0]=new PokemonCombat(equipe1[i]);
				pkmListe[0].listeIndice=i;
			}
		}
		for(int i=0;i<j2.teamsize;i++){
			equipe2[i]=new PokemonCombat(j2.team[i],true,j2); equipe2[i].equipe=equipe2;
			if(equipe2[i].pkm.statut!=Statut.KO && pkmListe[1]==null){
				pkmListe[1]=new PokemonCombat(equipe2[i]);
				pkmListe[1].listeIndice=i;
			}
		}
		pkmListe[0].adv[0]=pkmListe[1]; pkmListe[0].XpStack.add(pkmListe[0].adv[0].pkm);
		pkmListe[1].adv[0]=pkmListe[0]; pkmListe[1].XpStack.add(pkmListe[1].adv[0].pkm);
	}
	

	
	
	public void initSolo(Joueur j,Dresseur d){
		dresseur=true;
		equipe1=new PokemonCombat[j.teamsize];
		equipe2=new PokemonCombat[d.getTeam().size()];
		pkmListe=new PokemonCombat[2];
		
		for(int i=0;i<j.teamsize;i++){
			equipe1[i]=new PokemonCombat(j.team[i],false,j); equipe1[i].equipe=equipe1;
			if(equipe1[i].pkm.statut!=Statut.KO && pkmListe[0]==null){
				pkmListe[0]=new PokemonCombat(equipe1[i]);
				pkmListe[0].listeIndice=i;
			}
		}
		for(int i=0;i<d.getTeam().size();i++){
			equipe2[i]=new PokemonCombat(d.getTeam().elementAt(i),true,d); equipe2[i].equipe=equipe2;
			if(equipe2[i].pkm.statut!=Statut.KO && pkmListe[1]==null){
				pkmListe[1]=new PokemonCombat(equipe2[i]);
				pkmListe[1].listeIndice=i;
			}
		}
		pkmListe[0].adv[0]=pkmListe[1]; pkmListe[0].XpStack.add(pkmListe[0].adv[0].pkm);
		pkmListe[1].adv[0]=pkmListe[0]; pkmListe[1].XpStack.add(pkmListe[1].adv[0].pkm);
	}
	
	
	public int combatsolo(){
		 this.setfreeze(true); 
		while(this.gagnant()==0){
			Arrays.sort(pkmListe);
			for(int i=0;i<pkmListe.length;i++){
				
				if(endOfTurn){endOfTurn=false;}
				this.resetAct();
				this.setBufferState(false);
				pCourant=pkmListe[i];
				this.capCur=null; this.cibleCourante=null;
				pkmListe[i].action(pkmListe[i].adv[0],this);
				//
			}
			if(this.climat != Climat.Normal){
				//Application des effets de meteo
				meteo=true;
				System.out.println(this.climat.text());
				this.ajoutBuffer(climat.text());
				for(PokemonCombat p:pkmListe){
					this.climat.effet(p.pkm);
				}
				this.setfreeze(true);
				meteo=false;
			}
			//this.setfreeze(true);
			//Application des degats sur la duree
			endOfTurn=true;
			for(PokemonCombat p:pkmListe){
				System.out.println("Traitement des statuts de "+p.pkm.nom);
				if(p.pkm.statut==Statut.Empoisonne || p.pkm.statut==Statut.Brule || p.pkm.statut==Statut.Toxic ){
					System.out.println(p.pkm.statut);
					this.pCourant=p;
					this.capCur=p.pkm.statut.dummy;
					this.cibleCourante=p;
					p.pkm.statut.StatEffect(p.pkm,p.adv[0].pkm,1,this);
					this.setfreeze(true);
				}
				if(p.pkm.stats[2][0]>0){
					for(Statut s: p.pkm.supTemp){
						if(s.infligedegats){
							this.pCourant=p;
							this.capCur=s.dummy;
							this.cibleCourante=p;
							s.StatEffect(p.pkm,p.adv[0].pkm,1,this);
							this.setfreeze(true);
							if(p.pkm.stats[2][0]<=0){ break; }
						}
					}
				}
				//if(p.pkm.stats[2][0]<=0){ break; }
				this.chercherKO();
			}
			endOfTurn=false;
			
		}
		
		//on enleve tous les statuts temporaires
		for(PokemonCombat p:equipe1){
			p.pkm.supTemp.clear();
		}
		
		fini=this.gagnant();
		this.ajoutBuffer("   ");
		return fini;
	}
	
	
	public boolean isMeteo() {
		return meteo;
	}
	
	public void action(PokemonCombat user,PokemonCombat cible){
		int isdone=0; int i=0; int ch1=0; int ch2=1;
		while(isdone==0){
			System.out.println("Debut du tour du joueur");
			
			//this.pCourant=user;
			//this.capCur=Statut.Peur.dummy;
			//this.cibleCourante=cible;
			
			this.getAct();
			
			switch(actflag){
			case 0:
				
				
				//Application des statuts pouvant empecher l'action
				ch1=user.pkm.statut.StatEffect(user.pkm,user.adv[0].pkm,0,this);
				if(ch1==1){
					for(Statut s: user.pkm.supTemp){
						if(s.StatEffect(user.pkm,user.adv[0].pkm,0,this)==0){
							this.endOfTurn=true;
							ch2=0;
							this.pCourant=user;
							this.capCur=s.dummy;
							this.cibleCourante=user;
							//this.setBufferState(true);
						}
					}
				}
				else{
					this.endOfTurn=true;
					this.pCourant=user;
					this.capCur=user.pkm.statut.dummy;
					this.cibleCourante=user;
					//this.ajoutBuffer(" ");
					//this.setBufferState(true);
				}
				if(ch1==1 && ch2==1){
					this.capCur=user.pkm.cap.elementAt(act).cible;
					this.cibleCourante=cible;
					user.pkm.cap.utiliser(act,user.pkm,cible.pkm,this);
				}
				//Consequences de l'action
				
				this.chercherKO();
				
				if(user.pkm.stats[2][0]<=(int)(user.pkm.stats[2][1]/2) && cible.pkm.statut!=Statut.KO){
					if(user.pkm.objTenu instanceof Medicament && cible.pkm.objTenu!=null){
						Medicament m=(Medicament)user.pkm.objTenu;
						this.ajoutBuffer(user.pkm.nom+" utilise sa baie");
						if(m.baie){ m.script(user.pkm,this); user.pkm.objTenu=null; }
					}
				}
				if(cible.pkm.stats[2][0]<=(int)(cible.pkm.stats[2][1]/2) && cible.pkm.statut!=Statut.KO){
					if(cible.pkm.objTenu instanceof Medicament && cible.pkm.objTenu!=null){
						Medicament m=(Medicament)cible.pkm.objTenu;
						this.ajoutBuffer(cible.pkm.nom+" utilise sa baie");
						if(m.baie){ m.script(cible.pkm,this); cible.pkm.objTenu=null; }
					}
				}
				isdone=1;
				this.setBufferState(true);
				break;
			case 1:
				//Inventaire
				break;
			case 2:
				pokeswap(user,false);
				//traitement capacite passive ici
				isdone=1;
				this.setBufferState(true);
				break;
			case 3:
				System.out.println("FUITE");
				break;
			default :
				System.out.println("mauvaise input dans "+user.pkm.nom+" action");
				break;
			}
		}
		
	}
	
	public void ajoutXpStack(PokemonCombat pkmc){
		for(PokemonCombat p:pkmListe){
			if(p.equipe!=pkmc.equipe && !p.XpStack.contains(pkmc.pkm)){
				p.XpStack.push(pkmc.pkm);
			}
		}
	}
	
	
	public void chercherKO(){
		for(PokemonCombat p: pkmListe){
			if(p.pkm.stats[2][0]<=0){ p.XPreward(this); pokeswap(p,true); }
		}
	}
	
	public void pokeswap(PokemonCombat user,boolean ko){
		/*int i=0; int act=0;*/ int done=0; int select=0;// Pkm pkmRef; Stack<Pkm> stackRef; 
		if(!user.isIA){
			while(done==0){
				if(ko){ user.waitPlswap(); select=user.swap;}
				else{ select=this.act; }
				
				if(user.equipe[select].pkm.statut!=Statut.KO){
					System.out.println(user.equipe[select].pkm.nom+" remplace "+user.pkm.nom);
					user.swapImport(user.equipe[select]);
					//pkmRef=user.pkm; stackRef=user.XpStack;
					//user.pkm=user.equipe[select].pkm; user.XpStack=user.equipe[select].XpStack;
					//user.equipe[act].pkm=pkmRef; user.equipe[act].XpStack=stackRef;
					ajoutXpStack(user);
					done=1;
				}
				else{
					//System.out.println("Vous ne pouvez pas envoyer un Pokemon K.O au combat !");
				}
			}
		}
		else{
			for(int i=0;i<user.equipe.length;i++){
				if(user.equipe[i].pkm.statut!=Statut.KO){
					this.ajoutBuffer(user.prop.getName()+" envoie "+user.equipe[i].pkm.nom+" au combat");
					user.setSwap(i);
					user.waitIAswap();
					//pkmRef=user.pkm; stackRef=user.XpStack;
					//user.pkm=user.equipe[i].pkm; user.XpStack=user.equipe[i].XpStack;
					user.swapImport(user.equipe[i]);
					//p.pkm=pkmRef; p.XpStack=stackRef;
					ajoutXpStack(user);
					break;
				}
			}
		}
	}
	public Terrain getTerrain(){ return terrain; }
	public Climat getClimat(){ return climat; }
	public PokemonCombat[] getEquipe1(){ return equipe1;}
	public PokemonCombat[] getEquipe2(){ return equipe2;}
	
	
	
	public Capacite getCapCur() {
		return capCur;
	}


	// Fonctions de manipulation des objets synchronis�s entre modele et vue
	public synchronized void ajoutBuffer(String s){ 
		this.buffer+=s+"\n";
		notify();
	}
	
	public synchronized boolean bufferIsEmpty(){ return this.buffer.compareTo("")==0; }
	public synchronized boolean bufferIsReady(){ return bufferReady; }
	
	public synchronized String readBuffer(){
		while(this.buffer.compareTo("")==0){
			try { wait(); } 
			catch(InterruptedException ie) { ie.printStackTrace(); }
		}
		return this.buffer;
	}
	public synchronized void resetBuffer(){ this.buffer=""; setBufferState(false);}
	public synchronized void setBufferState(boolean st){
		bufferReady=st;
		if(bufferReady){ notify(); }
	}
	
	public synchronized void getAct(){
		while(actflag==-1 && act==-1){
			try { wait(); } 
			catch(InterruptedException ie) { ie.printStackTrace(); }
		}
	}
	
	
	public synchronized void setAct(int aflag,int act){
		actflag=aflag; this.act=act; notify();
	}
	
	
	public synchronized void resetAct(){ this.actflag=-1; this.act=-1; notify(); }

	public synchronized void setfreeze(boolean f){ 
		freeze=f;
		if(!freeze){  notify();}
		else{
			while(freeze){
				try { System.out.println("SETFREEZE "+f);  this.wait(); } 
				catch(InterruptedException ie) { ie.printStackTrace(); }
			}
		}
	}
	
	public synchronized PokemonCombat getPCourant(){ return pCourant;}
	public synchronized PokemonCombat getCibleCourante() {
		return cibleCourante;
	}
	public synchronized void setCible(PokemonCombat cible){ this.cibleCourante=cible; }
	public synchronized boolean getendOfTurn(){ return endOfTurn;}
	public synchronized int getFini(){ return fini; }
	public synchronized boolean getDresseur(){ return dresseur; }
	
	
	
}


