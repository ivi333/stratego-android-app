package de.arvato.stratego;

public class StrategoConstants
{
	//public static Valuation m_valuation = new Valuation();
	
	// states of the game ////////////////////////////////////////////////////////////////////////////
	public static final int PLAY = 1;
	public static final int CHECK = 2;
	public static final int INVALID = 3; // only occurs when king can be hit or when invalid number of pieces is on the board (more than one king). can be used when setting up a new position
	public static final int DRAW_MATERIAL = 4; // no one can win (ie KING against KING)
	public static final int DRAW_50 = 5; // after 25 full moves no hit or pawnmove occured
	public static final int MATE = 6;
	public static final int STALEMATE = 7;
	public static final int DRAW_REPEAT = 8; // draw by repetition (3 times same board position)
	// ///////////////////////////////////////////////////////////////////////////////////////////////
	
	// array index of data memebers that hold data for either black or white. these must be 0 and 1 cause arrays are of length 2
	public static final int RED = 0;
	public static final int BLUE = 1;
	
	// index and representation of the pieces array and values - must be from [0-5].
	public static final int PAWN = 0;
	public static final int KNIGHT = 1;
	public static final int BISHOP = 2;
	public static final int ROOK = 3;
	public static final int QUEEN = 4;
	public static final int KING = 5;

	// New pieces for stratego
	public static final int FLAG = 0;
	public static final int BOMB = 1;
	public static final int SPY = 2;
	public static final int SCOUT = 3;
	public static final int MINER = 4;
	public static final int SERGEANT = 5;
	public static final int LIEUTENANT = 6;
	public static final int CAPITAN = 7;
	public static final int MAJOR = 8;
	public static final int COLONEL = 9;
	public static final int GENERAL= 10;
	public static final int MARSHALL= 11;


	// not a piece: a field
	public static final int FIELD = -1;

	// "enumeration" integer position values
	public static final int a10 = 0, b10 = 1, c10 = 2, d10 = 3, e10 = 4, f10 = 5, g10 = 6, h10 = 7, i10=8, j10=9;
	public static final int a9 = 10, b9 = 11, c9 = 12, d9 = 13, e9 = 14, f9 = 15, g9 = 16, h9 = 17, i9= 18, j9=19;
	public static final int a8 = 20, b8 = 21, c8 = 22, d8 = 23, e8 = 24, f8 = 25, g8 = 26, h8 = 27, i8= 28, j8=29;
	public static final int a7 = 30, b7 = 31, c7 = 32, d7 = 33, e7 = 34, f7 = 35, g7 = 36, h7 = 37, i7=38, j7=39;
	public static final int a6 = 40, b6 = 41, c6 = 42, d6 = 43, e6 = 44, f6 = 45, g6 = 46, h6 = 47, i6=48, j6=49;
	public static final int a5 = 50, b5 = 51, c5 = 52, d5 = 53, e5 = 54, f5 = 55, g5 = 56, h5 = 57, i5=58, j5=59;
	public static final int a4 = 60, b4 = 61, c4 = 62, d4 = 63, e4 = 64, f4 = 65, g4 = 66, h4 = 67, i4=68, j4=69;
	public static final int a3 = 70, b3 = 71, c3 = 72, d3 = 73, e3 = 74, f3 = 75, g3 = 76, h3 = 77, i3=78, j3=79;
	public static final int a2 = 80, b2 = 81, c2 = 82, d2 = 83, e2 = 84, f2 = 85, g2 = 86, h2 = 87, i2=88, j2=89;
	public static final int a1 = 90, b1= 91, c1 = 92, d1 = 93, e1 = 94, f1 = 95, g1 = 96, h1 = 97, i1=98, j1=99;


	/*public static final int a7 = 8, b7 = 9, c7 = 10, d7 = 11, e7 = 12, f7 = 13, g7 = 14, h7 = 15;
	public static final int a6 = 16, b6 = 17, c6 = 18, d6 = 19, e6 = 20, f6 = 21, g6 = 22, h6 = 23;
	public static final int a5 = 24, b5 = 25, c5 = 26, d5 = 27, e5 = 28, f5 = 29, g5 = 30, h5 = 31;
	public static final int a4 = 32, b4 = 33, c4 = 34, d4 = 35, e4 = 36, f4 = 37, g4 = 38, h4 = 39;
	public static final int a3 = 40, b3 = 41, c3 = 42, d3 = 43, e3 = 44, f3 = 45, g3 = 46, h3 = 47;
	public static final int a2 = 48, b2 = 49, c2 = 50, d2 = 51, e2 = 52, f2 = 53, g2 = 54, h2 = 55;
	public static final int a1 = 56, b1 = 57, c1 = 58, d1 = 59, e1 = 60, f1 = 61, g1 = 62, h1 = 63;*/
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	// the row or rank seen from the oposite colors is mirrored, so also convenient use from an array
	// first index color, second index position
	/*public static final int[][] ROW_TURN = {
			{0, 0, 0, 0, 0, 0, 0, 0,
				 1, 1, 1, 1, 1, 1, 1, 1, 
				 2, 2, 2, 2, 2, 2, 2, 2,
				 3, 3, 3, 3, 3, 3, 3, 3, 
				 4, 4, 4, 4, 4, 4, 4, 4, 
				 5, 5, 5, 5, 5, 5, 5, 5, 
				 6, 6, 6, 6, 6, 6, 6, 6, 
				 7, 7, 7, 7, 7, 7, 7, 7}
			,
				 {7, 7, 7, 7, 7, 7, 7, 7,
				 	6, 6, 6, 6, 6, 6, 6, 6,
				 	 5, 5, 5, 5, 5, 5, 5, 5,
				 	4, 4, 4, 4, 4, 4, 4, 4,
				 	 3, 3, 3, 3, 3, 3, 3, 3,
				 	2, 2, 2, 2, 2, 2, 2, 2,
					1, 1, 1, 1, 1, 1, 1, 1, 
					0, 0, 0, 0, 0, 0, 0, 0}
	};*/
	
}
