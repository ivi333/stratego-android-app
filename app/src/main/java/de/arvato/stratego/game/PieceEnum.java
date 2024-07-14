package de.arvato.stratego.game;

import de.arvato.stratego.StrategoConstants;

public enum PieceEnum {
    FLAG 		("FLAG",	 	0,	-1, false),	/* 1 */
    BOMB 		("BOMB",	 	1, 	12, false),	/* 6 */
    SPY 		("SPY",		 	2,  1, 	true),	/* 1 */
    SCOUT 		("SCOUT",	 	3, 	2, 	true),	/* 8 */
    MINER 		("MINER",	 	4, 	3, 	true),	/* 5 */
    SERGEANT 	("SERGEANT", 	5, 	4, 	true),	/* 4 */
    LIEUTENANT 	("LIEUTENANT", 	6, 	5, 	true),	/* 4 */
    CAPTAIN 	("CAPTAIN", 	7, 	6, 	true),  /* 4 */
    MAJOR 		("MAJOR", 		8, 	7, 	true),  /* 3 */
    COLONEL 	("COLONEL", 	9, 	8, 	true),	/* 2 */
    GENERAL 	("GENERAL", 	10, 9, 	true),	/* 1 */
    MARSHALL 	("MARSHALL", 	11, 10, true); 	/* 1 */

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

    public static PieceEnum fromToken(String token) {
        if ("MIN".equals(token)) {
            return PieceEnum.MINER;
        } else if ("FLA".equals(token)) {
            return PieceEnum.FLAG;
        } else if ("BOM".equals(token)) {
            return PieceEnum.BOMB;
        } else if ("SER".equals(token)) {
            return PieceEnum.SERGEANT;
        } else if ("SCO".equals(token)) {
            return PieceEnum.SCOUT;
        } else if ("CAP".equals(token)) {
            return PieceEnum.CAPTAIN;
        } else if ("COL".equals(token)) {
            return PieceEnum.COLONEL;
        } else if ("GEN".equals(token)) {
            return PieceEnum.GENERAL;
        } else if ("LIE".equals(token)) {
            return PieceEnum.LIEUTENANT;
        } else if ("MAJ".equals(token)) {
            return PieceEnum.MAJOR;
        } else if ("MAR".equals(token)) {
            return PieceEnum.MARSHALL;
        } else if ("SPY".equals(token)) {
            return PieceEnum.SPY;
        }
        return null;
    }

    public int maxLives () {
        if (PieceEnum.MINER == this) {
            return StrategoConstants.MIN_MAX;
        } else if (PieceEnum.BOMB == this) {
            return StrategoConstants.BOM_MAX;
        } else if (PieceEnum.FLAG == this) {
            return StrategoConstants.BAN_MAX;
        } else if (PieceEnum.SPY == this) {
            return StrategoConstants.ESP_MAX;
        } else if (PieceEnum.GENERAL == this) {
            return StrategoConstants.GEN_MAX;
        } else if (PieceEnum.COLONEL == this) {
            return StrategoConstants.COR_MAX;
        } else if (PieceEnum.CAPTAIN == this) {
            return StrategoConstants.CAP_MAX;
        } else if (PieceEnum.LIEUTENANT == this) {
            return StrategoConstants.TEN_MAX;
        } else if (PieceEnum.MAJOR == this) {
            return StrategoConstants.COM_MAX;
        } else if (PieceEnum.MARSHALL == this) {
            return StrategoConstants.MAR_MAX;
        } else if (PieceEnum.SCOUT == this) {
            return StrategoConstants.EXP_MAX;
        } else if (PieceEnum.SERGEANT == this) {
            return StrategoConstants.SAR_MAX;
        }
        return 0;
    }
}