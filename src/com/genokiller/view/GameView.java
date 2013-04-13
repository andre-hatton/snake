package com.genokiller.view;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.genokiller.snake.MainActivity;
import com.genokiller.snake.R;
import com.genokiller.utils.Angle;
import com.genokiller.utils.Doublon;


/**
 * 
 * @author André Hatton
 * @since 21/12/2012
 * @date 12/04/2013 Ajout de boite de dialogue fin du jeu avec l'option rejouer
 *       ou arrêter
 * @brief dessine un serpent et controle sont déplacement selon le mouvement sur
 *        l'ecran
 * 
 */
public class GameView extends SurfaceView implements Runnable, SurfaceHolder.Callback
{
	/**
	 * nom du tag pour le debugage
	 */
	public static final String	tag					= "SNAKE";
	/**
	 * Lien vers l'activité
	 */
	private MainActivity mainActivity;
	/**
	 * Holdre de la surface
	 */
	private SurfaceHolder	holder;
	/**
	 * Thread du jeu
	 */
	private Thread				gameThread			= new Thread(this);
	/**
	 * Vérifie si une surface est active
	 */
	private boolean			hasSurface;
	/**
	 * Position actuel de la tete
	 */
	private int					x, y;
	/**
	 * La peinture
	 */
	private Paint			paint	= new Paint();
	/**
	 * direction du serpent (0, 1, 2 ,3 / haut, droite, bas, gauche)
	 */
	private int				direction	= -1;
	/**
	 * hauteur de la vue
	 */
	private int					height;
	/**
	 * largeur de la vue
	 */
	private int					width;
	/**
	 * vérifie si le jeu a démarrer (serpent bouge ou non)
	 */
	public boolean start = false;
	/**
	 * liste des coordonnees du serpent
	 */
	private ArrayList<Doublon>	snake;
	/**
	 * Taille d'un bloc de jeu
	 */
	private final int			SIZE		= 30;
	/**
	 * Canvas sur lequel le jeu va etre affiche
	 */
	private Canvas				canvas		= null;
	/**
	 * coordonnee aléatoire du fruit et de l'objet chance (ou pas)
	 */
	private int					ran_x, ran_y, lucky_x, lucky_y;
	/**
	 * Vérifie si un fruit est affiché (si il viens d'être mangé faut le recréer)
	 */
	public boolean				has_fruit =  false;
	/**
	 * verifie si le jeu est fini
	 */
	private boolean				lost;
	/**
	 * image à afficher (pomme, ciseau, moins, plus)
	 */
	private Bitmap				apple, cut, less, more;
	/**
	 * preference de l'utilisateur pour la vitesse de deplacement et le joystick)
	 */
	private int					vitesse, joystick;
	/**
	 * coordonnée sur touch down pour verifier le sens d'un mouvement
	 */
	private int					g_x, g_y;
	/*
	 * si un objet chance doit apparaitre (redeeviens false des que les coordonnées sont choisi) (false)
	 */
	private boolean				lucky		= false;
	/**
	 * precise qu'n objet chance est en cours (false)
	 */
	private boolean				lucky_ok	= false;
	/**
	 * temps pendant lequel l'objet chance est affiché (30)
	 */
	private final int TIME = 10;
	/**
	 * variable remettant à 0 les entiers necessaire
	 */
	private final int ZEROS = 0;
	/**
	 * Score du jeu
	 */
	private int score = ZEROS;
	/**
	/**
	 * nombre sur laquel la probabilité d'avoir un jocker (1 chance sur luck)
	 */
	private final int			luck				= 200;
	/**
	 * nombre sur lequel tombé pour avoir le jocker (si le ran tombe sur 23 c'est gagné)
	 */
	private int					nb_find		= 23;
	/**
	 * nombre permettant graçe au sleep du thread de compté le temps d'une seconde
	 */
	private int					tmp_luck	= ZEROS;
	/**
	 * durée restante d'un jocker
	 */
	private int					end_lucky	= TIME;
	/**
	 * Nombre de bonus possible
	 */
	private final int type_bonus = 3;
	/**
	 * bonus en cours
	 */
	private int bonus = ZEROS;
	/**
	 * Nombre de fruit à manger avant la fin du bonus
	 */
	private int reste_bonus = 3;
	/**
	 * previens que le bonus plus ou moins est actif
	 */
	private boolean bonus_plus_moins = false;
	/**
	 * Objet pour boite de dialogue
	 */
	AlertDialog.Builder			d;
	/**
	 * Boite de dialogue
	 */
	AlertDialog					dl;
	/**
	 * construit le vue sur un contexte
	 * @param context Le contexte dans laquel est la vue
	 */
	public GameView(Context context)
	{
		super(context);
		init();
	}

	/**
	 * Constructeur de la vue avec des attribut en plus
	 * @param context
	 * @param attrs
	 */
	public GameView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init();
	}
	/**
	 * Constructeur de la vue avec des attributs et un style donné
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public GameView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init();
		// TODO Auto-generated constructor stub
	}

	/**
	 * initialisation du jeu
	 */
	public void init()
	{
		holder = getHolder();
		holder.addCallback(this);
		start = false;
		/* Initialisation des images du jeu */
		apple = BitmapFactory.decodeResource(getResources(), R.drawable.apple);
		cut = BitmapFactory.decodeResource(getResources(), R.drawable.cut);
		less = BitmapFactory.decodeResource(getResources(), R.drawable.less);
		more = BitmapFactory.decodeResource(getResources(), R.drawable.more);
		/* Initialisation des variables de base */
		lost = false;						// pas perdu encore
		hasSurface = false; // pas de surface
		direction = 1;						// direction droite de base
		resume(); // appel de la methode de resumé
		x = 0;								// tete du zerpent à x=0
		y = 0;								// tete du serpent à y=0
		score = ZEROS;						// initialise du score à 0
		snake = new ArrayList<Doublon>();
		snake.add(new Doublon(x, y));		// initialisation du snake à 0,0
		lucky = false;						// pas d'objet bonus
		lucky_ok = false;					// pas d'objet chance en cours
		bonus_plus_moins = false;			// pas de bonus plus ou moins
		end_lucky = TIME;					// durée de l'apparition du bonus initialiser
		tmp_luck = ZEROS;					// compteur du temps d'apparition du bonus
		bonus = ZEROS;						// type du bonus en cours
		d = new AlertDialog.Builder(getContext());
		/* Si le thread est démarrer mais en attente on le démarre */
		if (gameThread != null && gameThread.getState() == Thread.State.WAITING)
		{
			try
			{
				gameThread.start();
			}
			catch (IllegalThreadStateException e)
			{
				e.printStackTrace();
			}
		}
		/*
		 * si le thread n'est pas null et qu'il n'est pas en attente on créer la
		 * boucle en mode infini
		 */
		else if (gameThread != null)
		{
			done = false;
		}
	}

	/**
	 * Démarre un thread si null ou terminé
	 */
	public void resume()
	{
		done = false;
		if (gameThread == null || gameThread.getState() == Thread.State.TERMINATED)
		{
			Thread.State st = gameThread.getState();
			gameThread = new Thread(this);
			if (hasSurface || st == Thread.State.TERMINATED)
			{
				gameThread.start();

			}
		}
	}

	/**
	 * Supprime un thread, vide le serpent, fini le jeu
	 */
	public void pause()
	{
		lost = false;
		hasSurface = false;
		snake.removeAll(snake);
		if (gameThread != null)
		{

			gameThread = null;
		}
	}
	
	public boolean isSnakeNull()
	{
		if(snake == null || snake.size() == 0)
			return true;
		return false;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
	{
		if (gameThread != null)
		{
			start = false;
			has_fruit = false;
			this.width = width;
			this.height = height;
			init();
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder)
	{
		height = getHeight();
		width = getWidth();
		hasSurface = true;
		holder.addCallback(this);
		if (gameThread != null)
		{
			gameThread.start();
			done = false;
		}

	}

	/**
	 * fini le jeu (supprime le threead)
	 */
	public void finish()
	{
		surfaceDestroyed(holder);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder)
	{
		hasSurface = false;
		pause();
	}

	/**
	 * @param event evenement sur le clavier tactile
	 * @return true l'evenement à été effectué
	 */
	public boolean onTouchEvent(MotionEvent event)
	{
		super.onTouchEvent(event);
		// action sur le pave tactile
		int action = event.getAction();
		/* Si c'est le premier touché sur l'ecran alors le serpent doit démarrer */
		if(!start)
		{
			if (action == MotionEvent.ACTION_UP)
			{
				init();
				start = true;
			}
		}
		else
		{
	
			// position x et y du touché
			int ptx = (int) event.getX();
			int pty = (int) event.getY();
	
			// centre x et y du rectangle formant la vue
			int centerX = width / 2;
			int centerY = height / 2;
	
			// calcul de la difference de longueur entre x et y (pour faire une
			// gille 1/1)

			/* memorise l'endroit ou l'utilisateur à appuyer sur l'ecran */
			if (action == MotionEvent.ACTION_DOWN)
			{
				g_x = ptx;
				g_y = pty;
			}
			Angle a = new Angle();

			/* selon le type du joystick on change la façon de bouger le serpent */
			/* Cas ou on utilise les 4 coins */
			if (action == MotionEvent.ACTION_UP && getJoystick() == 1)
			{
				double angle = a.getAngleRad(new Doublon(centerX, centerY), new Doublon(ptx, pty));
				if ((angle > 315 || angle <= 45) && direction != 1)
					direction = 3;
				else if (angle <= 315 && angle > 225 && direction != 2)
					direction = 0;
				else if (angle > 45 && angle <= 135 && direction != 0)
					direction = 2;
				else if (angle > 135 && angle <= 225 && direction != 3)
					direction = 1;
			}
			
			else if (action == MotionEvent.ACTION_UP && getJoystick() == 4)
			{
				double angle = a.getAngleRad(new Doublon(x + SIZE / 2, y + SIZE / 2), new Doublon(ptx, pty));
				if ((angle > 315 || angle <= 45) && direction != 1)
					direction = 3;
				else if (angle <= 315 && angle > 225 && direction != 2)
					direction = 0;
				else if (angle > 45 && angle <= 135 && direction != 0)
					direction = 2;
				else if (angle > 135 && angle <= 225 && direction != 3)
					direction = 1;
			}
			
			/* cas où on utilise le coté droite et gauche */
			else if (action == MotionEvent.ACTION_UP && getJoystick() == 2)
			{
				if (ptx < centerX && direction == 0)
					direction = 3;
				else if (ptx < centerX && direction == 1)
					direction = 0;
				else if (ptx < centerX && direction == 2)
					direction = 1;
				else if (ptx < centerX && direction == 3)
					direction = 2;
				else if (ptx > centerX && direction == 0)
					direction = 1;
				else if (ptx > centerX && direction == 1)
					direction = 2;
				else if (ptx > centerX && direction == 2)
					direction = 3;
				else if (ptx > centerX && direction == 3)
					direction = 0;
			}
			/* si on utilise le mouvement on recupert le point lors de la fin du mouvement */
			else if (action == MotionEvent.ACTION_UP && getJoystick() == 3)
			{
				/* longueur du mouvement en x et y
				 * si la distance de x est plus grande que y le mouvement à été réalisé en x
				 * selon la longueur on regarde le sens du glissement 
				 */
				int d_x = g_x - ptx;
				int d_y = g_y - pty;
				if(Math.abs(d_x) > Math.abs(d_y))
				{
					if(d_x < 0 && direction != 3)
						direction = 1;
					else if(d_x > 0 && direction != 1)
						direction = 3;
				}
				else
				{
					if (d_y < 0 && direction != 0)
						direction = 2;
					else if (d_y > 0 && direction != 2)
						direction = 0;
				}
			}
		}
		return true;
	}

	/**
	 * methode de dessin
	 * 
	 * @param canvas
	 *        canvas dans lequel on dessine
	 */
	public void doDraw(Canvas canvas)
	{
		/* Si le canvas n'existe pas on ne dessine rien */
		if(canvas == null)
			return;
		/* si aucun bonus n'existe on tante la chance d'en avoir un */
		if ((int) (Math.random() * luck + 1) == nb_find && !lucky_ok && snake.size() > 4 && !bonus_plus_moins)
		{
			lucky = true;
			lucky_ok = true;
			bonus = (int)(Math.random() * type_bonus) + 1;
		}
		/* si aucun fruit n'existe on en créé un nouveau */
		if (has_fruit == false)
		{
			do
			{
				ran_x = (int) (Math.random() * ((width - SIZE - width % SIZE) / SIZE)) * SIZE;
				ran_y = (int) (Math.random() * ((height - SIZE - height % SIZE) / SIZE)) * SIZE;
			}while(inSnake(ran_x, ran_y, snake));
			has_fruit = true;
		}

		/* si bonus placement aleatoire */
		if (lucky)
		{
			lucky = false;
			do
			{
				lucky_x = (int) (Math.random() * ((width - SIZE - width % SIZE) / SIZE)) * SIZE;
				lucky_y = (int) (Math.random() * ((height - SIZE - height % SIZE) / SIZE)) * SIZE;
			}
			while (lucky_x == ran_x && lucky_y == ran_y && inSnake(lucky_x, lucky_y, snake));
		}
		/* Si le démarrage n'a pas encore eu lieu (serpent immobile) on fait l'affichage par defaut et on arrete le dessin */
		if (!start)
		{
			canvas.drawColor(Color.BLACK);
			paint.setColor(Color.LTGRAY);
			canvas.drawLine(0, 0, width - width % SIZE - 0.1f, 0, paint);
			canvas.drawLine(width - width % SIZE - 0.1f, 0, width - width % SIZE - 0.1f, height - height % SIZE - 0.1f, paint);
			canvas.drawLine(0, 0, 0, height - height % SIZE - 0.1f, paint);
			canvas.drawLine(0, height - height % SIZE - 0.1f, width - width % SIZE - 0.1f, height - height % SIZE - 0.1f, paint);
			
			paint.setColor(Color.argb(255, 0, 255, 255));
			if (snake == null)
			{
				snake = new ArrayList<Doublon>();
				snake.add(new Doublon(x, y));
			}
			else
			{
				try
				{
					snake.set(0, new Doublon(x, y));
				}
				catch (IndexOutOfBoundsException e)
				{
					e.printStackTrace();
					return;
				}
			}
			canvas.drawRect((float) snake.get(0).getX(), (float) snake.get(0).getY(), (float) snake.get(0).getX() + SIZE, (float) snake.get(0).getY() + SIZE, paint);paint.setColor(Color.RED);


			canvas.drawBitmap(apple, ran_x, ran_y, paint);
			paint.setColor(Color.BLUE);
			paint.setTextSize(getResources().getDimension(R.dimen.myFontSize));
			canvas.drawText(String.valueOf(score), 1, height - SIZE, paint);
			return;
		}
		/* selon la direction on déplacement le serpent d'une case */
		switch (direction)
		{
			case 0:
				if (y > 0)
					y -= SIZE;
				else
					lost = true;
				break;
			case 1:
				if (x < width - SIZE - width % SIZE)
					x += SIZE;
				else
					lost = true;
				break;
			case 2:
				if (y < height - SIZE - height % SIZE)
					y += SIZE;
				else
					lost = true;
				break;
			case 3:
				if (x > 0)
					x -= SIZE;
				else
					lost = true;
				break;
		}
		/* si le jeu est fini on affiche le dernier mouvement fait */
		if (lost)
		{
			canvas.drawColor(Color.BLACK);
			paint.setColor(Color.RED);
			canvas.drawBitmap(apple, ran_x, ran_y, paint);
			paint.setColor(Color.LTGRAY);
			canvas.drawLine(0, 0, width - width % SIZE - 0.1f, 0, paint);
			canvas.drawLine(width - width % SIZE - 0.1f, 0, width - width % SIZE - 0.1f, height - height % SIZE - 0.1f, paint);
			canvas.drawLine(0, 0, 0, height - height % SIZE - 0.1f, paint);
			canvas.drawLine(0, height - height % SIZE - 0.1f, width - width % SIZE - 0.1f, height - height % SIZE - 0.1f, paint);

			for (int i = 0; i < snake.size(); i++)
			{
				if(i==0)
				{
					paint.setColor(Color.argb(255, 0, 255, 255));
					canvas.drawRect((float) snake.get(i).getX(), (float) snake.get(i).getY(), (float) snake.get(i).getX() + SIZE, (float) snake.get(i).getY() + SIZE, paint);
					paint.setColor(Color.LTGRAY);
				}
				else
					canvas.drawRect((float) snake.get(i).getX(), (float) snake.get(i).getY(), (float) snake.get(i).getX() + SIZE, (float) snake.get(i).getY() + SIZE, paint);

			}
			paint.setColor(Color.BLUE);
			paint.setTextSize(getResources().getDimension(R.dimen.myFontSize));
			canvas.drawText(String.valueOf(score), 1, height - SIZE, paint);
		}
		/* si on joue on recalcule les coordonnée du serpent et test les bonus */
		else
		{
			/* initialisation de la map */
			// font de l'ecran noire
			canvas.drawColor(Color.BLACK);
			// affichage du fruit
			paint.setColor(Color.RED);
			canvas.drawBitmap(apple, ran_x, ran_y, paint);
			// affichage des contour de la map
			paint.setColor(Color.LTGRAY);
			canvas.drawLine(0, 0, width - width % SIZE - 0.1f, 0, paint);
			canvas.drawLine(width - width % SIZE - 0.1f, 0, width - width % SIZE - 0.1f, height - height % SIZE - 0.1f, paint);
			canvas.drawLine(0, 0, 0, height - height % SIZE - 0.1f, paint);
			canvas.drawLine(0, height - height % SIZE - 0.1f, width - width % SIZE - 0.1f, height - height % SIZE - 0.1f, paint);
			// si un bonus a été lancé on l'affiche selon celui a apparaitre
			if (lucky_ok)
			{
				switch(bonus)
				{
				case 1:
					canvas.drawBitmap(cut, lucky_x, lucky_y, paint);
					break;
				case 2:
					canvas.drawBitmap(less, lucky_x, lucky_y, paint);
					break;
				case 3:
					canvas.drawBitmap(more, lucky_x, lucky_y, paint);
					break;
				}
			}
			if(snake == null)
				snake = new ArrayList<Doublon>();
			// si aucun fruit n'a encore été mangé
			if (snake.size() == 0)
			{
				// on met la position x, y comme la tete
				try
				{
					snake.set(0, new Doublon(x, y));
				}
				catch (IndexOutOfBoundsException e)
				{
					e.printStackTrace();
					return;
				}
				paint.setColor(Color.argb(255, 0, 255, 255));
				canvas.drawRect((float) snake.get(0).getX(), (float) snake.get(0).getY(), (float) snake.get(0).getX() + SIZE, (float) snake.get(0).getY() + SIZE, paint);
				// la tete mange un fruit, score++ et nouveau fruit
				if (snake.get(0).getX() == ran_x && snake.get(0).getY() == ran_y)
				{
					has_fruit = false;
					switch(vitesse)
					{
					case 1:
						score += 10;
						break;
					case 2:
						score += 15;
						break;
					case 3:
						score += 20;
						break;
					case 4:
						score += 30;
						break;
					}
					// ajoute une case ua serpent
					switch (direction)
					{
						case 0:
							snake.add(new Doublon(snake.get(0).getX(), snake.get(0).getY() + SIZE));
							break;
						case 1:
							snake.add(new Doublon(snake.get(0).getX() - SIZE, snake.get(0).getY()));
							break;
						case 2:
							snake.add(new Doublon(snake.get(0).getX(), snake.get(0).getY() - SIZE));
							break;
						case 3:
							snake.add(new Doublon(snake.get(0).getX() + SIZE, snake.get(0).getY()));
							break;
					}
				}
			}
			/* serpent d'au moins deux cases */
			else
			{
				/* parcours le serpent et reposition les coronné à la case précédente */
				for (int i = snake.size() - 1; i > 0; i--)
				{
					snake.set(i, new Doublon(snake.get(i - 1).getX(), snake.get(i - 1).getY()));
				}
				/* met à jour la tete du serpent avec les nouvelles coordonnée de x,y */
				try
				{
					snake.set(0, new Doublon(x, y));
				}
				catch (IndexOutOfBoundsException e)
				{
					e.printStackTrace();
					return;
				}

				/* boucle sur le serpent */
				src: for (int i = 0; i < snake.size(); i++)
				{
					// si on affiche la tete on met une couleur bleu sinon couleur gris
					if(i == 0)
					{
						paint.setColor(Color.argb(255, 0, 255, 255));
						canvas.drawRect((float) snake.get(i).getX(), (float) snake.get(i).getY(), (float) snake.get(i).getX() + SIZE, (float) snake.get(i).getY() + SIZE, paint);
						paint.setColor(Color.LTGRAY);
					}
					else
						canvas.drawRect((float) snake.get(i).getX(), (float) snake.get(i).getY(), (float) snake.get(i).getX() + SIZE, (float) snake.get(i).getY() + SIZE, paint);
					// si la tete du serpent mange un fruit
					if (snake.get(0).getX() == ran_x && snake.get(0).getY() == ran_y)
					{

						for (int j = 0; j < snake.size(); j++)
						{
							canvas.drawRect((float) snake.get(j).getX(), (float) snake.get(j).getY(), (float) snake.get(j).getX() + SIZE, (float) snake.get(j).getY() + SIZE, paint);
						}
						has_fruit = false;
						/* on change  le score selon la vitesse du serpent */
						switch(vitesse)
						{
						case 1:
							if(bonus_plus_moins && bonus == 2)
								score += 5;
							else if(bonus_plus_moins && bonus == 3)
								score += 20;
							else
								score += 10;
							break;
						case 2:
							if(bonus_plus_moins && bonus == 2)
								score += 10;
							else if(bonus_plus_moins && bonus == 3)
								score += 25;
							else
								score += 15;
							break;
						case 3:
							if(bonus_plus_moins && bonus == 2)
								score += 10;
							else if(bonus_plus_moins && bonus == 3)
								score += 30;
							else
								score += 20;
							break;
						case 4:
							if(bonus_plus_moins && bonus == 2)
								score += 20;
							else if(bonus_plus_moins && bonus == 3)
								score += 50;
							else
								score += 30;
							break;
						}
						/* si un bonus plus ou moins est actif on enleve 1 au nombre de pomme mangé avant la fin du bonus */
						if(bonus_plus_moins)
							reste_bonus--;
						/* si le bonus est achevé on reinitialise ce bonus */
						if(reste_bonus <= 0)
						{
							bonus_plus_moins = false;
							reste_bonus = 3;
						}
						/* ajout d'un morceau au serpent selon la direction */
						switch (direction)
						{
							case 0:
								snake.add(new Doublon(snake.get(snake.size() - 1).getX(), snake.get(snake.size() - 1).getY() + SIZE));
								break;
							case 1:
								snake.add(new Doublon(snake.get(snake.size() - 1).getX() - SIZE, snake.get(snake.size() - 1).getY()));
								break;
							case 2:
								snake.add(new Doublon(snake.get(snake.size() - 1).getX(), snake.get(snake.size() - 1).getY() - SIZE));
								break;
							case 3:
								snake.add(new Doublon(snake.get(snake.size() - 1).getX() + SIZE, snake.get(snake.size() - 1).getY()));
								break;
						}
						break src;
					}
					/* bonus en cours et bonus de type coupage */
					if (lucky_ok && bonus == 1)
					{
						/* si la tete touche le ciseau */
						if (snake.get(0).getX() == lucky_x && snake.get(0).getY() == lucky_y)
						{
							/* on coupe la queue du serpent */
							lucky_ok = false;
							end_lucky = TIME;
							tmp_luck = ZEROS;
							int index_1 = snake.size() - 1;
							int index_2 = snake.size() - 2;
							int index_3 = snake.size() - 3;
							int index_4 = snake.size() - 4;
							int index_5 = snake.size() - 5;
							int index_6 = snake.size() - 6;
							boolean more_gain = false;
							if ((int) (Math.random() * 5) == 3 && snake.size() > 10)
							{
								more_gain = true;
								
							}
							snake.remove(index_1);
							snake.remove(index_2);
							snake.remove(index_3);
							if(more_gain)
							{
								snake.remove(index_4);
								snake.remove(index_5);
								snake.remove(index_6);								
							}
						}
					}
					/* si le bonus est plus ou moins */
					else if(lucky_ok && (bonus == 2 || bonus == 3))
					{
						/* si on mange un des deux bonus on previens de lancer les effets du bonus */
						if (snake.get(0).getX() == lucky_x && snake.get(0).getY() == lucky_y)
						{
							lucky_ok = false;
							end_lucky = TIME;
							tmp_luck = ZEROS;
							bonus_plus_moins = true;
						}
					}
				}
			}

			/* si le serpent ce mord c'est perdu */
			if (mord(snake))
			{
				lost = true;
			}
		}
		/* affichage du score */
		paint.setColor(Color.BLUE);
		paint.setTextSize(getResources().getDimension(R.dimen.myFontSize));
		canvas.drawText(String.valueOf(score), 1, height - SIZE, paint);

	}

	/**
	 * test si le serpent se mord
	 * @param snake liste des coordonnées du serpent
	 * @return true le serpent se mord sinon false
	 */
	private boolean mord(ArrayList<Doublon> snake)
	{
		Doublon d = new Doublon(snake.get(0).getX(), snake.get(0).getY());

		for (int j = 1; j < snake.size(); j++)
		{
			if (snake.get(j).getX() == d.getX() && snake.get(j).getY() == d.getY())
				return true;
		}
		return false;
	}

	/**
	 * @return the vitesse
	 */
	public int getVitesse()
	{
		return vitesse;
	}

	/**
	 * @param vitesse
	 *        the vitesse to set
	 */
	public void setVitesse(int vitesse)
	{
		this.vitesse = vitesse;
	}

	/**
	 * @return the joystick
	 */
	public int getJoystick()
	{
		return joystick;
	}

	/**
	 * @param joystick
	 *        the joystick to set
	 */
	public void setJoystick(int joystick)
	{
		this.joystick = joystick;
	}
	
	public boolean inSnake(int x, int y, ArrayList<Doublon> snake)
	{
		for(int i = 0; i < snake.size(); i++)
		{
			if(snake.get(i).getX() == x && snake.get(i).getY() == y)
				return true;
		}
		return false;
	}

	/**
	 * @return the mainActivity
	 */
	public MainActivity getMainActivity() {
		return mainActivity;
	}

	/**
	 * @param mainActivity the mainActivity to set
	 */
	public void setMainActivity(MainActivity mainActivity) {
		this.mainActivity = mainActivity;
	}

	private Handler	handler	= new Handler()
							{
								public void handleMessage(Message message)
								{
									String msg = "Vous avez perdu !!!";
									int bestScore = getMainActivity().getBestScore();
									if(bestScore >= score)
										msg = "Dommage vous n'avez que " + score + " points, le meilleur score est " + bestScore;
									else if (bestScore < score)
									{
										// nouveau record
										msg = "Vous avez battu le record de " + bestScore + " avec un score de " + score + ". Bravo";
										getMainActivity().setBestScore(score);
									}
									dl = d.setMessage(msg).setNegativeButton("Arrêter", new DialogInterface.OnClickListener()
									{

										@Override
										public void onClick(DialogInterface dialog, int which)
										{
											getMainActivity().finish();

										}
									}).setPositiveButton("Rejouer", new DialogInterface.OnClickListener()
									{

										@Override
										public void onClick(DialogInterface dialog, int which)
										{
											// rejouer
											dialog.dismiss();
											has_fruit = false;
											init();
										}
									}).show();
								}
							};
	/*
	 * variable permettant de créer et d'arreté la boucle infini
	 */
	private boolean	done	= false;

	@Override
	public void run()
	{
		/* boucle infini */
		while (!done)
		{

			/* si on a pas perdu on dessine le serpent */
			if (!lost)
			{
				canvas = getHolder().lockCanvas();
				if (canvas != null)
				{
					doDraw(canvas);
					getHolder().unlockCanvasAndPost(canvas);
				}
			}
			else
			{
				handler.sendEmptyMessage(0);
				done = true;
			}
			/*
			 * on essai d'endormir le thread pour controler la vitesse du
			 * serpent
			 */
			try
			{
				if(!lost)
				{
					/*
					 * selon la vitesse on du serpent on change le sommeil du
					 * serpent
					 */
					switch (getVitesse())
					{
						case 1:
							if (lucky_ok)
								tmp_luck++;
							if (bonus_plus_moins && bonus == 2)
								Thread.sleep(250);
							else if (bonus_plus_moins && bonus == 3)
								Thread.sleep(100);
							else
								Thread.sleep(200);
							break;
						case 2:
							if (lucky_ok)
								tmp_luck++;
							if (bonus_plus_moins && bonus == 2)
								Thread.sleep(200);
							else if (bonus_plus_moins && bonus == 3)
								Thread.sleep(100);
							else
								Thread.sleep(150);
							break;
						case 3:
							if (lucky_ok)
								tmp_luck++;
							if (bonus_plus_moins && bonus == 2)
								Thread.sleep(150);
							else if (bonus_plus_moins && bonus == 3)
								Thread.sleep(50);
							else
								Thread.sleep(100);
							break;
						case 4:
							if (lucky_ok)
								tmp_luck++;
							if (bonus_plus_moins && bonus == 2)
								Thread.sleep(125);
							else if (bonus_plus_moins && bonus == 3)
								Thread.sleep(25);
							else
								Thread.sleep(50);
							break;
					}
					if (lucky_ok)
					{
						switch (getVitesse())
						{
							case 1:
								if (tmp_luck >= 5)
								{
									end_lucky--;
									tmp_luck = 0;
								}
								break;
							case 2:
								if (tmp_luck >= 7)
								{
									end_lucky--;
									tmp_luck = 0;
								}
								break;
							case 3:
								if (tmp_luck >= 10)
								{
									end_lucky--;
									tmp_luck = 0;
								}
								break;
							case 4:
								if (tmp_luck >= 20)
								{
									end_lucky--;
									tmp_luck = 0;
								}
								break;
						}
						if (end_lucky <= 0)
						{
							lucky_ok = false;
							end_lucky = TIME;
						}
					}
				}
			}
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
}
