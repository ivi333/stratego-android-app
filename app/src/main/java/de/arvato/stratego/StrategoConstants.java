package de.arvato.stratego;

public class StrategoConstants
{
	//public static Valuation m_valuation = new Valuation();

	// states of the game ////////////////////////////////////////////////////////////////////////////
	public enum GameStatus {
		INIT_BOARD, PLAY, FINISH, DRAW_REPEAT
	}
	// ///////////////////////////////////////////////////////////////////////////////////////////////
	public static final int MAX_ROWS=10;
	public static final int MAX_COLS=10;
	// array index of data memebers that hold data for either black or white. these must be 0 and 1 cause arrays are of length 2
	public static final int RED = 0;
	public static final int BLUE = 1;

	//Max pieces for each player
	public static final int MAX_PIECES = 40;

	//Max pieces allowed for each character
	public static final int MAR_MAX = 1;
	public static final int GEN_MAX = 1;
	public static final int COR_MAX = 2;
	public static final int COM_MAX = 3;
	public static final int CAP_MAX = 4;
	public static final int TEN_MAX = 4;
	public static final int SAR_MAX = 4;
	public static final int MIN_MAX = 5;
	public static final int EXP_MAX = 8;
	public static final int ESP_MAX = 1;
	public static final int BOM_MAX = 6;
	public static final int BAN_MAX = 1;

	// not a piece: a field
	public static final int FIELD = -1;

	// Initial positions allowed for each player
	//public static final int [] BLUE_PLAYER = { 0, 40 };
	//public static final int [] RED_PLAYER = { 60, 100 };

	public static final int [] UP_PLAYER = { 0, 40 };
	public static final int [] DOWN_PLAYER = { 60, 100 };


	public static final int BOARD_SIZE = 100;

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

	//raspberry
	//public static final String ENDPOINT_COLYSEUS = "ws://192.168.1.156:2567";

	// home ip
	public static final String ENDPOINT_COLYSEUS = "ws://192.168.1.136:2567";

	// olesa ip
	//public static final String ENDPOINT_COLYSEUS = "ws://192.168.1.64:2567";

	public static final String PREFERENCES_NAME = "StrategoPrefs";
}
