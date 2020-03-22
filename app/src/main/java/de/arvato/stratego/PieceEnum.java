package de.arvato.stratego;

/**
 * 40 en total
 */
public enum PieceEnum {
	FLAG ("FLAG", 0, -1, false),	/* 1 */
	BOMB ("BOMB", 1, 12, false),	/* 6 */
	SPY ("SPY",2,  1, true),		/* 1 */
	SCOUT ("SCOUT", 3, 2, true),	/* 8 */
	MINER ("MINER", 4, 3, true),	/* 5 */
	SERGEANT ("SERGEANT", 5, 4, true),	/* 4 */
	LIEUTENANT ("LIEUTENANT", 6, 5, true),	/* 4 */
	CAPITAN ("CAPITAN", 7, 6, true),  	/* 4 */
	MAJOR ("MAJOR", 8, 7, true),  /* 3 */
	COLONEL ("COLONEL", 9, 8, true),	  	/* 2 */
	GENERAL ("GENERAL", 10, 9, true),		/* 1 */
	MARSHALL ("MARSHALL", 11, 10, true); 	/* 1 */

	private final String name;
	private final int point;
	private final int id;
	private boolean allowMovement;

	PieceEnum (final String name, final int id, final int points, final boolean allowMovement) {
		this.name = name;
		this.point = points;
		this.id = id;
		this.allowMovement = allowMovement;
	}

	public static PieceEnum fromPosition (final int position) {
		return PieceEnum.values()[position];
	}

	public String getName() {
		return name;
	}
	public int getPoints() {
		return point;
	}
	public int getId () {return id; }
	public boolean isAllowMovement() {
		return allowMovement;
	}
}
