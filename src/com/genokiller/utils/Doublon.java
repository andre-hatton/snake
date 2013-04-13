package com.genokiller.utils;

/**
 * 
 * @author genokiller 
 * 
 * @brief permet d'avoir un objet contenant un doublon d'entier (x, y) pour l'utilisation de coordonnées)
 *
 */
public class Doublon {
	private int x, y;
	/**
	 * Création d'un doublon x, y
	 * @param x int coordonnée x
	 * @param y int coordonnée y
	 */
	public Doublon(int x, int y)
	{
		this.setX(x);
		this.setY(y);
	}
	
	/**
	 * Constructeur par defaut
	 */
	public Doublon(){}
	
	/**
	 * Ajote un doublon x, y
	 * @param x int coordonnée x
	 * @param y int coordonnée y
	 */
	public void setDoublons(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	
	/**
	 * retourne un doublon qui peut ensuite etre utilisé pour  recuperer le x et le y
	 * @return Doublon retourne un doublon d'entier
	 */
	public Doublon getDoublon()
	{
		return new Doublon(x, y);
	}
	
	/**
	 * @return int the x
	 */
	public int getX() {
		return x;
	}
	/**
	 * @param int x the x to set
	 */
	public void setX(int x) {
		this.x = x;
	}
	/**
	 * @return int the y
	 */
	public int getY() {
		return y;
	}
	/**
	 * @param int y the y to set
	 */
	public void setY(int y) {
		this.y = y;
	}
}
