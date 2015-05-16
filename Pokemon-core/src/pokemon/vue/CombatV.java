package pokemon.vue;



import java.util.Arrays;
import java.util.Vector;

import pokemon.controle.CombatMenuPokemon;
import pokemon.launcher.MyGdxGame;
import pokemon.modele.Capacite;
import pokemon.modele.Combat;
import pokemon.modele.Pkm;
import pokemon.modele.PokemonCombat;
import pokemon.modele.UniteStockage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class CombatV extends GameScreen implements InputProcessor{
	MyGdxGame mygdxgame;
	PokemonSprite e1;//=new PokemonSprite(PokemonSprite.e1,"Sprites/10.png");
	//PokemonSprite e2=new PokemonSprite(PokemonSprite.e2,"Sprites/99.png");
	Vector<BattleGroup> ennemies=new Vector<BattleGroup>();
	Vector<BattleGroup> friends=new Vector<BattleGroup>();
	BattleGroup a=new BattleGroup(new PokemonSprite(PokemonSprite.a1,"trainerS.png"));
	Texture fond=new Texture(Gdx.files.internal("battlebackground.png"));
	//Pkm[] pkms=MyGdxGame.Jtest.getTeam();
	Pkm pkm=MyGdxGame.Jtest.getTeam()[0];
	boolean attackanimation;
	int atknumber=0;
	PokemonSprite p1;
	DialogBox dbox;
	float timer;
	int state=0;
	int offset;
	int selector=0;
	int flag;
	Combat c;
	String[] actions = {"Attaque","Objets","Pkm","Fuite"};
	String text;
	String[] retval;
	int textinc=1;
	CombatMenuPokemon mpokemon;
	Music music;
	ParticleEffect e;
	ParticleEffect boom;
	int swap;
	public CombatV(Combat c,MyGdxGame mygdxgame){
		this.mygdxgame=mygdxgame;
		dbox=new DialogBox(new Vector2(640,100),true);
		dbox.setMessage("Un pokemon sauvage apparait");
		this.c=c;
		for( int i=0;i<c.getPkmListe().length;i++){
			System.out.println(c.getPkmListe()[i].isIA());
			if(this.c.getPkmListe()[i].isIA())
			{
				ennemies.add(new BattleGroup(PokemonSprite.e1,c,this,c.getPkmListe()[i]));
			}
			else{
				friends.add(new BattleGroup(PokemonSprite.a1,c,this,c.getPkmListe()[i]));
			}
		}
		stage.addActor(a);
		for(BattleGroup g:ennemies)
		{
			stage.addActor(g);
			g.getpSprite().addSlideAction();
			//stage.addActor(g.getHud());
		}
		a.getpSprite().addSlideAction();
		stage.addActor(dbox);
		/*for(BattleHud eH:ennemiesHUD)
		{
			stage.addActor(eH);
		}*/

		//Demarrage de la musique
		music = Gdx.audio.newMusic(Gdx.files.internal("musics/battlestart.ogg"));
		music.setOnCompletionListener(new Music.OnCompletionListener() {
			
			@Override
			public void onCompletion(Music arg0) {
				music.stop();
				music = Gdx.audio.newMusic(Gdx.files.internal("musics/battleintro.ogg"));
				music.setVolume(0.2f);
				music.play();
				music.setLooping(true);
			}
		});
		music.setVolume(0.2f);
		music.play();
		attackanimation=true;
		e=new ParticleEffect();
		boom=new ParticleEffect();
		//e.load(Gdx.files.internal("effect/TenebreSpecialLeft"), Gdx.files.internal("effect"));
		//e.setPosition(170,150);
	//	boom.load(Gdx.files.internal("effect/psyexplosion.p"), Gdx.files.internal("effect"));
		//boom.setPosition(500,270);
	}


	@Override
	public void dispose() {


	}
	@Override
	public void hide() {
		// TODO Auto-generated method stub

	}
	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}
	@Override
	public void render(float arg0) {
		if(!c.bufferIsEmpty() && state>1){
			text=c.readBuffer();
			c.resetBuffer();
			retval=text.split("\n");
			System.out.println("---"+text+"---");
			System.out.println("RETVAL"+Arrays.toString(retval));
			System.out.println("RETVALEND");
			dbox.setWidth(width);
			dbox.setMessage(retval[0]);
			textinc=1;
			state=5;
			attackanimation=true;
		}

		Gdx.gl.glClearColor(0f, 0f, 0f, 0.0f);
		// Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT| GL20.GL_DEPTH_BUFFER_BIT);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		shapeRenderer.setProjectionMatrix(this.getStage().getViewport().getCamera().combined);
		stage.getBatch().begin();

		stage.getBatch().draw(fond,0,0);


		stage.getBatch().end();

		switch(state){
		case 2:
			drawPanel();
			offset=0;
			stage.getBatch().begin();

			f.setColor(0.58f, 0.59f, 0.57f, 1);
			for(int i=0;i<actions.length;i++)//affichage des attaques
			{
				//f.draw(stage.getBatch(),cap.get().getNom(),220,113-offset);
				if(i<2)
					f.draw(stage.getBatch(),actions[i],330,85-offset);
				if(i==2)
					offset=0;
				if(i>=2)
					f.draw(stage.getBatch(),actions[i],490,85-offset);
				//f.draw(stage.getBatch(),cap.getQte()+"/"+cap.getQteMax(),385-f.getBounds(cap.getQte()+" / "+cap.getQteMax()).width,113-offset);
				offset+=50;
			}
			stage.getBatch().end();
			break;
		case 3:
			drawPanel();
			stage.getBatch().begin();
			offset=0;
			f.setColor(0.58f, 0.59f, 0.57f, 1);
			for(int i=0;i<pkm.getCap().size();i++)//affichage des attaques
			{
				//f.draw(stage.getBatch(),cap.get().getNom(),220,113-offset);
				if(i<2)
					f.draw(stage.getBatch(),pkm.getCap().at(i).getNom(),330,85-offset);
				if(i==2)
					offset=0;
				if(i>=2)
					f.draw(stage.getBatch(),pkm.getCap().at(i).getNom(),490,85-offset);
				//f.draw(stage.getBatch(),cap.getQte()+"/"+cap.getQteMax(),385-f.getBounds(cap.getQte()+" / "+cap.getQteMax()).width,113-offset);
				offset+=50;
			}
			stage.getBatch().end();
			dbox.setMessage(descGen(pkm.getCap().elementAt(selector)));
			break;

		}
		stage.act(arg0);
		stage.draw();
		stage.getBatch().begin();
		e.draw(stage.getBatch(), arg0);
		boom.draw(stage.getBatch(), arg0);
		
		stage.getBatch().end();		
		Gdx.gl.glDisable(GL20.GL_BLEND);
		//	System.out.println("PARTICLE PLAYING"+e.isComplete());
		
	}
	@Override
	public void resize(int arg0, int arg1) {
		super.resize(arg0, arg1);

	}
	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}
	@Override
	public void show() {

		//stage.addActor(e1);
		//stage.addActor(e2);
		Gdx.input.setInputProcessor(this);


	}///////


	@Override
	public boolean keyDown(int arg0) {
		switch(arg0){
		
		case Keys.ENTER:
		{
			if(state==0 && a.getpSprite().getActions().size==0){
				music.setLooping(false);
				music.setOnCompletionListener(new Music.OnCompletionListener() {
					
					@Override
					public void onCompletion(Music m) {
						m.stop();
						m=Gdx.audio.newMusic(Gdx.files.internal("musics/battlemain.ogg"));
						m.setVolume(0.2f);
						music=m;
						music.play();
						music.setOnCompletionListener(new Music.OnCompletionListener() {
							
							@Override
							public void onCompletion(Music m) {
								System.out.println("ENTERRING MAIN LOOP");
								music.stop();
								music=Gdx.audio.newMusic(Gdx.files.internal("musics/battlemainloop.ogg"));
								music.setVolume(0.2f);
								music.setLooping(true);
								music.play();
							}
						});
					}
				});
				a.getpSprite().hideTrainer();
				dbox.setMessage("En avant "+pkm.getNom());
				for(BattleGroup g:friends){
					stage.getActors().insert(0, g);
					g.getpSprite().popPokemon();					
				}
			/*	for(BattleHud fr:friendHUD)
				{
					stage.addActor(fr);
				}*/
				state++;
				break;
			}
			if(state==1){
				System.out.println("UNLOCKING THREAD STATE 1");
				c.setfreeze(false);
				dbox.setWidth(width/2);
				dbox.setMessage("Que faire ?");
				state++;
				selector=0;
				break;
			}
			if(state==2){ //selection action
				if(selector==3) {					
					System.exit(0);
				}

				if(selector==2)
				{
					state=2;
					mpokemon=new CombatMenuPokemon(mygdxgame,this);
				}
				else{
					textinc=0;
					state++;
				}
				flag=selector;
				selector=0;
				break;
			}
			if(state==3){ //selection atq
				attackanimation=true;
				c.setAct(flag, selector);
				System.out.println(" SETACT "+flag+","+selector);
				selector=0;
				break;
			}
			if(state==5)//lecture du buffer
			{//si qqchose a lire
				if(textinc==1){
					System.out.println("LAUNCHING ANIMATIONS");
					for(BattleGroup g:ennemies)
					{
						g.getpSprite().hurt();
						g.getpSprite().attack();
					}
					for(BattleGroup g:friends)
					{
						g.getpSprite().hurt();
						g.getpSprite().attack();
					}
				}
				state++;
				break;
			}

			if(state==6){

				if(textinc<retval.length)
				{
					if(!healthbarLocked())
						dbox.setMessage(retval[textinc++]);
				}
				else{
					//retval=null;
					textinc=1;
					for(int i=0;i<c.getPkmListe().length;i++){//acteurs des ennemis
						//System.out.println(ennemies.get(i).getP().getNom()+ "=/SWAP/=" + ennemies.get(i).getP().getSwap());
						//System.out.println("COMBAT-"+c.getPkmListe()[i].getNom()+ "=/SWAP/=" + c.getPkmListe()[i].getSwap());
					//	System.out.println("COMBAT-"+c.getPkmListe()[i+1].getNom()+ "=/SWAP/=" + c.getPkmListe()[i+1].getSwap());
						if(ennemies.get(i).getpCombat().getSwap()!=-1){
							state=8; //swapping ennemies
							dbox.setMessage("Le pokemon ennemi est KO");
							ennemies.get(i).getpSprite().die();
							ennemies.get(i).getHud().hide();
							swap=ennemies.get(i).getpCombat().getSwap();
							//ennemies.get(i).setP(c.getEquipe2()[swap]);
							//ennemies.get(i).getHud().hideRight();
						}
						i++;
					}
					if(pkm.get(2)==0){
						mpokemon=new CombatMenuPokemon(mygdxgame,this);
						break;
					}

					if(c.getPCourant().getPkm()!=pkm)
					{System.out.println("GNE");state=2;dbox.setWidth(width/2);
					dbox.setMessage("Que faire ?");
					}
					System.out.println("UNLOCKING THREAD STATE6");
					c.setfreeze(false);
				}
				break;
			}

			if(state==7){ //pokemon change par le joueur
				c.setfreeze(false);
				dbox.setWidth(width/2);
				dbox.setMessage("Que faire ?");
				state=2;
				break;
			}
			if(state==8){

				ennemies.get(0).getpCombat().setSwap(-1);
				ennemies.get(0).setpCombat(c.getEquipe2()[swap]);
				//ennemiesHUD.get(0).setP(c.getEquipe2()[swap]);
				dbox.setWidth(width/2);
				dbox.setMessage("Que faire ?");
				state=2;
				c.setfreeze(false);
				break;
			}
		}


		case Keys.DOWN:
		{	
			if(selector==0 || selector==2)
				selector++;
			break;
		}
		case Keys.UP:
		{	
			if(selector!=0 && selector!=2)
				selector--;
			break;
		}
		case Keys.LEFT:
		{	
			if(selector>1)
				selector-=2;
			break;
		}
		case Keys.RIGHT:
		{	
			if(selector<2)
				selector+=2;
			break;
		}
		case Keys.O:
			pkm.heal(20);
			break;
		case Keys.P:
			pkm.infliger(20);
			break;

		}
		System.out.println("TEXT INC :"+this.getTextinc());


		return false;//
	}

	private String descGen(UniteStockage<Capacite> element) {
		String str="Type: "+element.get().getElement().name();
		str+="\n\n          PP: ";
		str+=element.getQte()+"/"+element.getQteMax();
		//str+="Puissance: "+element.get().getPower();

		return str;
	}

	private void drawPanel(){
		shapeRenderer.begin(ShapeType.Filled);
		Gdx.gl.glEnable(GL20.GL_BLEND);
		shapeRenderer.setColor(1f, 1f, 1f, 0.8f);
		shapeRenderer.rect(310, 0,320,100);
		offset=50;
		if(selector<2)
			offset+=offset*selector;
		else
			offset+=offset*(selector-2);
		shapeRenderer.setColor(1f, 0, 0f, 0.2f);
		if(selector<2)
			shapeRenderer.rect(310,100-offset, 160, 50);
		else
			shapeRenderer.rect(470,100-(offset), 160, 50);
		shapeRenderer.end();
		Gdx.gl.glDisable(GL20.GL_BLEND);
	}

	void playEffect(){
		e.start();
		//e.scaleEffect(1.2f);
		boom.start();
		
		//boom.scaleEffect(1.2f);
	}
	@Override
	public boolean keyTyped(char arg0) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean keyUp(int arg0) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean mouseMoved(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean scrolled(int arg0) {
		// TODO Auto-generated method stub
		return false;
	}


	public boolean isAttackanimation() {
		return attackanimation;
	}
	public void setAttackanimation(boolean attack) {
		attackanimation=attack;
	}

	@Override
	public boolean touchDown(int arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean touchDragged(int arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean touchUp(int arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		return false;
	}
	public int getTextinc() {
		return textinc;
	}

	public boolean healthbarLocked(){
		for(int i=0;i<friends.size();i++){
			if(friends.get(i).isLocked())
				return true;
		}
		for(int i=0;i<ennemies.size();i++){
			if(ennemies.get(i).isLocked())
				return true;
		}
		return false;
	}


	public void swapPokemon(Pkm p,int indice){
		int i=0;
		state=7;
		dbox.setWidth(width);
		dbox.setMessage("En avant "+p.getNom()+" !");
		for(i=0;i<c.getEquipe1().length;i++) //recherche du pokemon dnas l'equipe
		{
			if(p==c.getEquipe1()[i].getPkm())
			{
				System.out.println("Found"+c.getEquipe1()[i].getNom());
				break;
			}
		}

		if(pkm.get(2)!=0){
			//c.setfreeze(true);

			//	friends.get(0).getP().setSwap(indice);

			//System.out.println(c.getEquipe1()[i].getPkm());
			c.setAct(2,indice);
		}
		else{
			int j=0;
			System.out.println("DEAD SWAP");
			for(j=0;j<c.getPkmListe().length;j++) //recherche du pokemon a changer dans la liste des pokemons
			{
				if(pkm==c.getPkmListe()[j].getPkm()){ //quand j'ai trouve
					System.out.println("set act "+c.getPkmListe()[j].getNom()+" a "+indice);
					c.getPkmListe()[j].setSwap(indice);
				}
			}
		}
		pkm=p;
		friends.get(0).setpCombat(c.getEquipe1()[i]);
		//friendHUD.get(0).setP(c.getEquipe1()[i]);
	}

}
