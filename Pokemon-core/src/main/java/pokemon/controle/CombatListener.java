package pokemon.controle;

import java.util.Arrays;

import pokemon.launcher.MapScreen;
import pokemon.launcher.PokemonCore;
import pokemon.modele.AtkMeteo;
import pokemon.modele.Combat;
//import pokemon.vue.BattleSoundManager;
import pokemon.vue.CombatV;
import pokemon.vue.Musics;

import com.badlogic.gdx.Gdx;


public class CombatListener extends GameInput{

	CombatV combatV;
	Combat c;
	String[] retval;
	int width=640;
	CombatMenuPokemon mpokemon;
	PokemonCore PokemonCore;
	int flag;
	int textinc;
	MapScreen ms;
	
	public CombatListener(PokemonCore PokemonCore,CombatV combatV,Combat c,MapScreen ms) {
		this.combatV=combatV;
		this.c=c;
		this.PokemonCore=PokemonCore;
		this.ms=ms;
		Gdx.input.setInputProcessor(this);

	}

	public void getBuffer(String s){
		retval=s.split("\n");
		System.out.println("RETVAL"+Arrays.toString(retval));
		combatV.getDbox().setWidth(width);
		combatV.getDbox().setMessage(retval[0]);
		textinc=1;
		if(c.getFini()!=0){
			//BattleSoundManager.end();
			Musics.stop();
			PokemonCore.setScreen(ms);
		}
		if(c.isMeteo()){
			
			combatV.setState(10);
		}
		else
			combatV.setState(5);
	}
	
	@Override
	public boolean keyDown(int arg0) {
		//if(c.getPCourant()!=null)
		
		
		if(!combatV.healthbarLocked()){
			if(this.PokemonCore.getScreen()==combatV){
				return super.keyDown(arg0);
			}

	
		}
		return false;
	}
	public boolean touchDown(int x, int y, int pointer, int button) {			
		if(combatV==PokemonCore.getScreen()){
			combatV.setTouched(true);
			v.set(x, y);
			combatV.getStage().screenToStageCoordinates(v);
			return super.touchUp(v);
			}
		else
			return false;
		
	}



	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if(combatV==PokemonCore.getScreen()){
			combatV.setTouched(false);
			return true;}
		else
			return false;
	}
	
	
	

	public int getTextinc() {
		// TODO Auto-generated method stub
		return textinc;
	}

	@Override
	void handleA() {
		System.out.println("A PRESSED textinc: "+textinc+" STATE: "+combatV.getState());
		// TODO Auto-generated method stub
		if(combatV.getState()==-1){
			combatV.ennemiLaunch();//set state to 1
			return;
		}
		if(combatV.getState()==0){
			combatV.battleBegin();//set state to 1
			return;
		}
		if(combatV.getState()==1){
			
			c.setfreeze(false);
			combatV.getDbox().setWidth(width/2);
			combatV.getDbox().setMessage("Que faire ?");
			//state++;
			combatV.setState(combatV.getState()+1);
			combatV.setSelector(0);
			return;

		}
		if(combatV.getState()==2){ //selection action
			if(combatV.getSelector()==3) {
				System.exit(0);
			}

			if(combatV.getSelector()==2)
			{
				//state=2;
				//combatV.setState(2);
				mpokemon=new CombatMenuPokemon(PokemonCore,combatV);
			}
			else{
				textinc=0;
				//state++;
				combatV.setState(combatV.getState()+1);
			}
			flag=combatV.getSelector();
			combatV.setSelector(0);	
			return;

		}
		if(combatV.getState()==3){ //selection atq
			System.out.println("ACTIONS SELECTED");
			c.setAct(flag, combatV.getSelector());
			//
			//selector=0;
			combatV.setSelector(0);
		}
		if(combatV.getState()==5)//lecture du buffer
		{//si qqchose a lire
			if(textinc==1){
				
				if(c.getCapCur() instanceof AtkMeteo){
					combatV.weather();
					}
				else{
					if(c.getCapCur()!=null && c.getCapCur()!=null)
					combatV.playAttackAnimations();
				}

			}
			//state++;
			combatV.setState(combatV.getState()+1);
			return;

		}

		if(combatV.getState()==6){

			if(textinc<retval.length)
			{
				//if(!combatV.healthbarLocked())
					combatV.getDbox().setMessage(retval[textinc++]);
					
				if(textinc==2){
					combatV.animateHealthBars();
				}
			}
			else{
				//textinc=1;
				if(combatV.hideDeadIA())
				{
					return;
				}
				
				if(combatV.getPkm().get(2)==0){
					mpokemon=new CombatMenuPokemon(PokemonCore,combatV);
				}

				if(c.getPCourant().getPkm()==combatV.getPkm())
				{
					combatV.setState(2);
					combatV.getDbox().setWidth(width/2);
				
				combatV.getDbox().setMessage("Que faire ?");
				}
				//
				c.setfreeze(false);
			}
			return;

		}

		if(combatV.getState()==7){ //pokemon change par le joueur
			c.setfreeze(false);
			combatV.getDbox().setWidth(width/2);
			combatV.getDbox().setMessage("Que faire ?");

			combatV.setState(2);
			return;

		}
		if(combatV.getState()==8){ //swapIA
			combatV.swapIA();
			return;

		}
		if(combatV.getState()==10){
			combatV.weather();
			combatV.setState(11);
			combatV.animateHealthBars();
			return;

		}
		if(combatV.getState()==11){
			c.setfreeze(false);
			combatV.getDbox().setWidth(width/2);
			combatV.getDbox().setMessage("Que faire ?");
			combatV.setState(2);
			return;

		}
	}

	@Override
	void handleB() {
		// TODO Auto-generated method stub
		
	}

	@Override
	void handleLeft() {
		// TODO Auto-generated method stub
		if(combatV.getSelector()>1)
			combatV.setSelector(combatV.getSelector()-2);
	}

	@Override
	void handleRight() {
		// TODO Auto-generated method stub
		if(combatV.getSelector()<2)
			combatV.setSelector(combatV.getSelector()+2);
	}

	@Override
	void handleUp() {
		// TODO Auto-generated method stub
		if(combatV.getSelector()!=0 && combatV.getSelector()!=2)
			combatV.setSelector(combatV.getSelector()-1);
	}

	@Override
	void handleDown() {
		// TODO Auto-generated method stub
		if(combatV.getSelector()==0 || combatV.getSelector()==2)
			combatV.setSelector(combatV.getSelector()+1);
	}

	@Override
	void handleSelect() {
		// TODO Auto-generated method stub
		
	}

	@Override
	void handleStart() {
		// TODO Auto-generated method stub
		
	}
}
