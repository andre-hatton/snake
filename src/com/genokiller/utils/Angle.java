package com.genokiller.utils;

/**
 * 
 * @author André Hatton
 * @since 12/04/2013
 * @category Utils
 * @brief Decrit la classe angle
 * 
 */
public class Angle
{

	/**
	 * 
	 * Le points d'origine permet de calculé deux segments à partir du meme
	 * points Le premier segment et sur l'axe des abscisses à -x de l'origine où
	 * x est les coordonées x de l'origine Soit le premier segment est un
	 * vecteur (-x, 0) Le deuxième point de coordonnées (xb, yb) avec l'origine
	 * (xa, ya) donne un vecteur (xb - xa, ya - yb)
	 * 
	 * @param origine
	 *        coordonnées x,y de l'origine
	 * @param touch
	 *        coordonnées x,y du deuxième points
	 * @return double angle entre l'origine et le second points
	 */
	public double getAngleRad(Doublon origine, Doublon touch)
	{
		// calcul des 2 vecteurs xa, ya et xb, yb
		double xa = -origine.getX();
		double ya = 0;
		double xb = touch.getX() - origine.getX();
		double yb = origine.getY() - touch.getY();
		double na = Math.sqrt(xa * xa + ya * ya);
		double nb = Math.sqrt(xb * xb + yb * yb);
		double c = (xa * xb + ya * yb) / (na * nb);
		double s = xa * yb - ya * xb;
		// calcul de l'angle en radian
		double angle = Math.signum(s) * Math.acos(c);
		// conversion de l'angle en degré
		angle = Math.toDegrees(angle);
		// calcul l'angle entre 0 et 360
		if (angle < 0)
			angle = 180 + (180 + angle);
		return angle;
	}

}
