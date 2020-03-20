package de.arvato.stratego;

/**
 * 40 en total
 */
public enum PieceEnum {
	FLAG ("FLAG", 0, -1),	/* 1 */
	BOMB ("BOMB", 1, -1),	/* 6 */
	SPY ("SPY",2,  1),		/* 1 */
	SCOUT ("SCOUT", 3, 2),	/* 8 */
	MINER ("MINER", 4, 3),	/* 5 */
	SERGEANT ("SERGEANT", 5, 4),	/* 4 */
	LIEUTENANT ("LIEUTENANT", 6, 5),	/* 4 */
	CAPITAN ("CAPITAN", 7, 6),  	/* 4 */
	MAJOR ("MAJOR", 8, 7),  /* 3 */
	COLONEL ("COLONEL", 9, 8),	  	/* 2 */
	GENERAL ("GENERAL", 10, 9),		/* 1 */
	MARSHALL ("MARSHALL", 11, 10); 	/* 1 */

	private final String name;
	private final int point;
	private final int position;
	PieceEnum (final String name, final int position, final int points) {
		this.name = name;
		this.point = points;
		this.position = position;
	}
	public String getName() {
		return name;
	}
	public int getPoints() {
		return point;
	}
	public int getPosition () {return position; }
}
