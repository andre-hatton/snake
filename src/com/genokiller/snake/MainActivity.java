package com.genokiller.snake;

import android.R.color;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;

import com.genokiller.view.GameView;
import com.samsung.spen.lib.multiwindow.SMultiWindowManager;

@SuppressLint("DefaultLocale")
public class MainActivity extends Activity
{
	/**
	 * variable donnant le nom du stockage du meilleur score
	 */
	public static final String PREF_NAME = "score";
	public static final String PREF_BEST_SCORE_NAME = "best_score";
	private SharedPreferences pref;
	private SharedPreferences.Editor edit;
	/**
	 * Gestion de multi fenetrage pour les android gérant le multi fenetrage
	 */
	SMultiWindowManager mMWM;
	/**
	 * lien vers la vue du jeu
	 */
	private GameView	game;
	/**
	 * difficulté choisis dans les preferences
	 */
	private String		difficulty;
	/**
	 * vitesse du serpent selon la difficulté
	 */
	private int			vitesse	= 2;
	/**
	 * type de touché selon les préférences
	 */
	private String		joystick_str;
	/**
	 * les direction choisis selon le touché
	 */
	private int			joystick	= 1;

	@SuppressLint("DefaultLocale")
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		try
		{
			mMWM = new SMultiWindowManager(this);
		}
		catch (NoClassDefFoundError e)
		{
			e.printStackTrace();
		}

		game = (GameView) findViewById(R.id.gameView);
		game.setBackgroundColor(color.background_dark);

		/* recuperation des preferences */
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		difficulty = preferences.getString("difficulty", "Normal");
		joystick_str = preferences.getString("joystick", "4 coins");
		if (difficulty.equals(getResources().getString(R.string.lent)))
			vitesse = 1;
		else if (difficulty.equals(getResources().getString(R.string.normal)))
			vitesse = 2;
		else if (difficulty.equals(getResources().getString(R.string.rapide)))
			vitesse = 3;
		else
			vitesse = 4;
		if (joystick_str.toLowerCase().equals("droite et gauche"))
		{
			joystick = 2;
		}
		else if (joystick_str.toLowerCase().equals("par glissement"))
		{
			joystick = 3;
		}
		else if (joystick_str.toLowerCase().equals("autour"))
		{
			joystick = 4;
		}
		else
		{
			joystick = 1;
		}
		game.setVitesse(vitesse);
		game.setJoystick(joystick);
		game.setMainActivity(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	/**
	 * option du menu
	 */
	public boolean onMenuItemSelected(int featureId, MenuItem item)
	{
		super.onMenuItemSelected(featureId, item);
		if (item.getItemId() == R.id.rejouer)
		{
			restart();
		}
		if (item.getItemId() == R.id.pref)
		{
			Intent intent = new Intent(this, MyPreferenceActivity.class);
			startActivity(intent);
			game.start = false;
			game.has_fruit = false;
			game.init();
			finish();
		}
		return true;
	}

	public void restart()
	{
		game.has_fruit = false;
		game.init();
	}

	public void onBackPressed()
	{
		super.onBackPressed();
		game.finish();
		finish();
	}

	/**
	 * @return retourne le meilleur score
	 */
	public int getBestScore() {
		return getSharedPreferences(PREF_NAME, 0).getInt(PREF_BEST_SCORE_NAME, 0);
	}
	
	public void setBestScore(int score)
	{
		pref = getSharedPreferences(PREF_NAME, 0);
		edit = pref.edit();
		edit.putInt(PREF_BEST_SCORE_NAME, score);
		edit.commit();
	}


}
