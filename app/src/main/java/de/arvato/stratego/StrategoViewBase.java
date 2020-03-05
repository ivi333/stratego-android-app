package de.arvato.stratego;

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TableRow.LayoutParams;

import java.util.ArrayList;

public class StrategoViewBase {

	public static final String TAG = "StrategoViewBase";

	private RelativeLayout _mainLayout;
	private StrategoImageView[] _arrImages = new StrategoImageView[100];
	//public static final int SELECTED = 2;
	//Tablero invertido
	public static boolean _flippedBoard = false;
	protected Activity _activity;
	protected ImageCacheObject[] _arrImgCache;

	protected int _modeBlindfold;
	public static final int MODE_BLINDFOLD_HIDEPIECES = 1;
	public static final int MODE_BLINDFOLD_SHOWPIECELOCATION = 2;
	public static boolean _showCoords = false;
	//protected ImageView _imgOverlay;


	public StrategoViewBase(Activity activity) {
		_activity = activity;
		_modeBlindfold = 0;
		_arrImgCache = new ImageCacheObject[100];


	}

	public void init(OnClickListener ocl, OnLongClickListener olcl){
		Log.i("StrategoViewBase", "init() called");
		_flippedBoard = false;

		_mainLayout = (RelativeLayout)_activity.findViewById(R.id.LayoutMain);

		_arrImages[0] = (StrategoImageView)_activity.findViewById(R.id.a10);
		_arrImages[1] = (StrategoImageView)_activity.findViewById(R.id.b10);
		_arrImages[2] = (StrategoImageView)_activity.findViewById(R.id.c10);
		_arrImages[3] = (StrategoImageView)_activity.findViewById(R.id.d10);
		_arrImages[4] = (StrategoImageView)_activity.findViewById(R.id.e10);
		_arrImages[5] = (StrategoImageView)_activity.findViewById(R.id.f10);
		_arrImages[6] = (StrategoImageView)_activity.findViewById(R.id.g10);
		_arrImages[7] = (StrategoImageView)_activity.findViewById(R.id.h10);
		_arrImages[8] = (StrategoImageView)_activity.findViewById(R.id.i10);
		_arrImages[9] = (StrategoImageView)_activity.findViewById(R.id.j10);

		_arrImages[10] = (StrategoImageView)_activity.findViewById(R.id.a9);
		_arrImages[11] = (StrategoImageView)_activity.findViewById(R.id.b9);
		_arrImages[12] = (StrategoImageView)_activity.findViewById(R.id.c9);
		_arrImages[13] = (StrategoImageView)_activity.findViewById(R.id.d9);
		_arrImages[14] = (StrategoImageView)_activity.findViewById(R.id.e9);
		_arrImages[15] = (StrategoImageView)_activity.findViewById(R.id.f9);
		_arrImages[16] = (StrategoImageView)_activity.findViewById(R.id.g9);
		_arrImages[17] = (StrategoImageView)_activity.findViewById(R.id.h9);
		_arrImages[18] = (StrategoImageView)_activity.findViewById(R.id.i9);
		_arrImages[19] = (StrategoImageView)_activity.findViewById(R.id.j9);

		_arrImages[20] = (StrategoImageView)_activity.findViewById(R.id.a8);
		_arrImages[21] = (StrategoImageView)_activity.findViewById(R.id.b8);
		_arrImages[22] = (StrategoImageView)_activity.findViewById(R.id.c8);
		_arrImages[23] = (StrategoImageView)_activity.findViewById(R.id.d8);
		_arrImages[24] = (StrategoImageView)_activity.findViewById(R.id.e8);
		_arrImages[25] = (StrategoImageView)_activity.findViewById(R.id.f8);
		_arrImages[26] = (StrategoImageView)_activity.findViewById(R.id.g8);
		_arrImages[27] = (StrategoImageView)_activity.findViewById(R.id.h8);
		_arrImages[28] = (StrategoImageView)_activity.findViewById(R.id.i8);
		_arrImages[29] = (StrategoImageView)_activity.findViewById(R.id.j8);

		_arrImages[30] = (StrategoImageView)_activity.findViewById(R.id.a7);
		_arrImages[31] = (StrategoImageView)_activity.findViewById(R.id.b7);
		_arrImages[32] = (StrategoImageView)_activity.findViewById(R.id.c7);
		_arrImages[33] = (StrategoImageView)_activity.findViewById(R.id.d7);
		_arrImages[34] = (StrategoImageView)_activity.findViewById(R.id.e7);
		_arrImages[35] = (StrategoImageView)_activity.findViewById(R.id.f7);
		_arrImages[36] = (StrategoImageView)_activity.findViewById(R.id.g7);
		_arrImages[37] = (StrategoImageView)_activity.findViewById(R.id.h7);
		_arrImages[38] = (StrategoImageView)_activity.findViewById(R.id.i7);
		_arrImages[39] = (StrategoImageView)_activity.findViewById(R.id.j7);

		_arrImages[40] = (StrategoImageView)_activity.findViewById(R.id.a6);
		_arrImages[41] = (StrategoImageView)_activity.findViewById(R.id.b6);
		_arrImages[42] = (StrategoImageView)_activity.findViewById(R.id.c6);
		_arrImages[43] = (StrategoImageView)_activity.findViewById(R.id.d6);
		_arrImages[44] = (StrategoImageView)_activity.findViewById(R.id.e6);
		_arrImages[45] = (StrategoImageView)_activity.findViewById(R.id.f6);
		_arrImages[46] = (StrategoImageView)_activity.findViewById(R.id.g6);
		_arrImages[47] = (StrategoImageView)_activity.findViewById(R.id.h6);
		_arrImages[48] = (StrategoImageView)_activity.findViewById(R.id.i6);
		_arrImages[49] = (StrategoImageView)_activity.findViewById(R.id.j6);

		_arrImages[50] = (StrategoImageView)_activity.findViewById(R.id.a5);
		_arrImages[51] = (StrategoImageView)_activity.findViewById(R.id.b5);
		_arrImages[52] = (StrategoImageView)_activity.findViewById(R.id.c5);
		_arrImages[53] = (StrategoImageView)_activity.findViewById(R.id.d5);
		_arrImages[54] = (StrategoImageView)_activity.findViewById(R.id.e5);
		_arrImages[55] = (StrategoImageView)_activity.findViewById(R.id.f5);
		_arrImages[56] = (StrategoImageView)_activity.findViewById(R.id.g5);
		_arrImages[57] = (StrategoImageView)_activity.findViewById(R.id.h5);
		_arrImages[58] = (StrategoImageView)_activity.findViewById(R.id.i5);
		_arrImages[59] = (StrategoImageView)_activity.findViewById(R.id.j5);

		_arrImages[60] = (StrategoImageView)_activity.findViewById(R.id.a4);
		_arrImages[61] = (StrategoImageView)_activity.findViewById(R.id.b4);
		_arrImages[62] = (StrategoImageView)_activity.findViewById(R.id.c4);
		_arrImages[63] = (StrategoImageView)_activity.findViewById(R.id.d4);
		_arrImages[64] = (StrategoImageView)_activity.findViewById(R.id.e4);
		_arrImages[65] = (StrategoImageView)_activity.findViewById(R.id.f4);
		_arrImages[66] = (StrategoImageView)_activity.findViewById(R.id.g4);
		_arrImages[67] = (StrategoImageView)_activity.findViewById(R.id.h4);
		_arrImages[68] = (StrategoImageView)_activity.findViewById(R.id.i4);
		_arrImages[69] = (StrategoImageView)_activity.findViewById(R.id.j4);

		_arrImages[70] = (StrategoImageView)_activity.findViewById(R.id.a3);
		_arrImages[71] = (StrategoImageView)_activity.findViewById(R.id.b3);
		_arrImages[72] = (StrategoImageView)_activity.findViewById(R.id.c3);
		_arrImages[73] = (StrategoImageView)_activity.findViewById(R.id.d3);
		_arrImages[74] = (StrategoImageView)_activity.findViewById(R.id.e3);
		_arrImages[75] = (StrategoImageView)_activity.findViewById(R.id.f3);
		_arrImages[76] = (StrategoImageView)_activity.findViewById(R.id.g3);
		_arrImages[77] = (StrategoImageView)_activity.findViewById(R.id.h3);
		_arrImages[78] = (StrategoImageView)_activity.findViewById(R.id.i3);
		_arrImages[79] = (StrategoImageView)_activity.findViewById(R.id.j3);

		_arrImages[80] = (StrategoImageView)_activity.findViewById(R.id.a2);
		_arrImages[81] = (StrategoImageView)_activity.findViewById(R.id.b2);
		_arrImages[82] = (StrategoImageView)_activity.findViewById(R.id.c2);
		_arrImages[83] = (StrategoImageView)_activity.findViewById(R.id.d2);
		_arrImages[84] = (StrategoImageView)_activity.findViewById(R.id.e2);
		_arrImages[85] = (StrategoImageView)_activity.findViewById(R.id.f2);
		_arrImages[86] = (StrategoImageView)_activity.findViewById(R.id.g2);
		_arrImages[87] = (StrategoImageView)_activity.findViewById(R.id.h2);
		_arrImages[88] = (StrategoImageView)_activity.findViewById(R.id.i2);
		_arrImages[89] = (StrategoImageView)_activity.findViewById(R.id.j2);

		_arrImages[90] = (StrategoImageView)_activity.findViewById(R.id.a1);
		_arrImages[91] = (StrategoImageView)_activity.findViewById(R.id.b1);
		_arrImages[92] = (StrategoImageView)_activity.findViewById(R.id.c1);
		_arrImages[93] = (StrategoImageView)_activity.findViewById(R.id.d1);
		_arrImages[94] = (StrategoImageView)_activity.findViewById(R.id.e1);
		_arrImages[95] = (StrategoImageView)_activity.findViewById(R.id.f1);
		_arrImages[96] = (StrategoImageView)_activity.findViewById(R.id.g1);
		_arrImages[97] = (StrategoImageView)_activity.findViewById(R.id.h1);
		_arrImages[98] = (StrategoImageView)_activity.findViewById(R.id.i1);
		_arrImages[99] = (StrategoImageView)_activity.findViewById(R.id.j1);

		//_imgOverlay = (ImageView)_activity.findViewById(R.id.ImageBoardOverlay);

		AssetManager am = _activity.getAssets();
//		SharedPreferences prefs = _activity.getSharedPreferences("ChessPlayer", Activity.MODE_PRIVATE);

		//String sFolder = prefs.getString("pieceSet", "highres") + "/";
        String sFolder = "highres/";
//		String sPat  	= prefs.getString("tileSet", "");

		try{

			//StrategoImageView._svgTest =  SVGParser.getSVGFromAsset(activity.getAssets(), "svg/kb.svg");
			//StrategoImageView._svgTest =  SVGParser.getSVGFromInputStream(am.open("svg/kb.svg"));
            /*if(prefs.getBoolean("extrahighlight", false)) {
                StrategoImageView._bmpBorder = BitmapFactory.decodeStream(am.open(sFolder + "border.png"));
            } else {
                StrategoImageView._bmpBorder = null;
            }*/

			StrategoImageView.bmpBorder = BitmapFactory.decodeStream(am.open(sFolder + "border.png"));

			StrategoImageView.bmpSelect = BitmapFactory.decodeStream(am.open(sFolder + "select.png"));
			StrategoImageView.bmpSelectLight = BitmapFactory.decodeStream(am.open(sFolder + "select_light.png"));
			/*if(sPat.length() > 0){
				StrategoImageView._bmpTile = BitmapFactory.decodeStream(am.open("tiles/" + sPat + ".png"));
			} else {
				StrategoImageView._bmpTile = null;
			}*/

			// flag
			StrategoImageView.arrPieceBitmaps[StrategoConstants.RED][StrategoConstants.FLAG] = BitmapFactory.decodeStream(am.open(sFolder + "bandera.png"));
			StrategoImageView.arrPieceBitmaps[StrategoConstants.BLUE][StrategoConstants.FLAG] = BitmapFactory.decodeStream(am.open(sFolder + "bandera.png"));

			// bomb
			StrategoImageView.arrPieceBitmaps[StrategoConstants.RED][StrategoConstants.BOMB] = BitmapFactory.decodeStream(am.open(sFolder + "bomb.png"));
			StrategoImageView.arrPieceBitmaps[StrategoConstants.BLUE][StrategoConstants.BOMB] = BitmapFactory.decodeStream(am.open(sFolder + "bomb.png"));

			// spy
			StrategoImageView.arrPieceBitmaps[StrategoConstants.RED][StrategoConstants.SPY] = BitmapFactory.decodeStream(am.open(sFolder + "spy.png"));
			StrategoImageView.arrPieceBitmaps[StrategoConstants.BLUE][StrategoConstants.SPY] = BitmapFactory.decodeStream(am.open(sFolder + "spy.png"));

			// scout
			StrategoImageView.arrPieceBitmaps[StrategoConstants.RED][StrategoConstants.SCOUT] = BitmapFactory.decodeStream(am.open(sFolder + "scout.png"));
			StrategoImageView.arrPieceBitmaps[StrategoConstants.BLUE][StrategoConstants.SCOUT] = BitmapFactory.decodeStream(am.open(sFolder + "scout.png"));

			// miner
			StrategoImageView.arrPieceBitmaps[StrategoConstants.RED][StrategoConstants.MINER] = BitmapFactory.decodeStream(am.open(sFolder + "minero.png"));
			StrategoImageView.arrPieceBitmaps[StrategoConstants.BLUE][StrategoConstants.MINER] = BitmapFactory.decodeStream(am.open(sFolder + "minero.png"));

			// sergeant
			StrategoImageView.arrPieceBitmaps[StrategoConstants.RED][StrategoConstants.SERGEANT] = BitmapFactory.decodeStream(am.open(sFolder + "sargento.png"));
			StrategoImageView.arrPieceBitmaps[StrategoConstants.BLUE][StrategoConstants.SERGEANT] = BitmapFactory.decodeStream(am.open(sFolder + "sargento.png"));

			// lieutenant
			StrategoImageView.arrPieceBitmaps[StrategoConstants.RED][StrategoConstants.LIEUTENANT] = BitmapFactory.decodeStream(am.open(sFolder + "lituant.png"));
			StrategoImageView.arrPieceBitmaps[StrategoConstants.BLUE][StrategoConstants.LIEUTENANT] = BitmapFactory.decodeStream(am.open(sFolder + "lituant.png"));

			// capitan
			StrategoImageView.arrPieceBitmaps[StrategoConstants.RED][StrategoConstants.CAPITAN] = BitmapFactory.decodeStream(am.open(sFolder + "capitan.png"));
			StrategoImageView.arrPieceBitmaps[StrategoConstants.BLUE][StrategoConstants.CAPITAN] = BitmapFactory.decodeStream(am.open(sFolder + "capitan.png"));

			// major
			StrategoImageView.arrPieceBitmaps[StrategoConstants.RED][StrategoConstants.MAJOR] = BitmapFactory.decodeStream(am.open(sFolder + "major.png"));
			StrategoImageView.arrPieceBitmaps[StrategoConstants.BLUE][StrategoConstants.MAJOR] = BitmapFactory.decodeStream(am.open(sFolder + "major.png"));

			// colonel
			StrategoImageView.arrPieceBitmaps[StrategoConstants.RED][StrategoConstants.COLONEL] = BitmapFactory.decodeStream(am.open(sFolder + "coronel.png"));
			StrategoImageView.arrPieceBitmaps[StrategoConstants.BLUE][StrategoConstants.COLONEL] = BitmapFactory.decodeStream(am.open(sFolder + "coronel.png"));

			// general
			StrategoImageView.arrPieceBitmaps[StrategoConstants.RED][StrategoConstants.GENERAL] = BitmapFactory.decodeStream(am.open(sFolder + "general.png"));
			StrategoImageView.arrPieceBitmaps[StrategoConstants.BLUE][StrategoConstants.GENERAL] = BitmapFactory.decodeStream(am.open(sFolder + "general.png"));

			// marshall
			StrategoImageView.arrPieceBitmaps[StrategoConstants.RED][StrategoConstants.MARSHALL] = BitmapFactory.decodeStream(am.open(sFolder + "mariscal.png"));
			StrategoImageView.arrPieceBitmaps[StrategoConstants.BLUE][StrategoConstants.MARSHALL] = BitmapFactory.decodeStream(am.open(sFolder + "mariscal.png"));

		}catch(Exception ex){

		}

		//int _arrColorScheme[][] = StrategoImageView.getArrColorScheme();
		// yellow
		StrategoImageView.arrColorScheme[0][0] = 0xffdeac5d;
		StrategoImageView.arrColorScheme[0][1] = 0xfff9e3c0;
		StrategoImageView.arrColorScheme[0][2] = 0xccf3ed4b;

		// blue
		StrategoImageView.arrColorScheme[1][0] = 0xff28628b;
		StrategoImageView.arrColorScheme[1][1] = 0xff7dbdea;
		StrategoImageView.arrColorScheme[1][2] = 0xcc9fdef3;

		// green
		StrategoImageView.arrColorScheme[2][0] = 0xff8eb59b;
		StrategoImageView.arrColorScheme[2][1] = 0xffcae787;
		StrategoImageView.arrColorScheme[2][2] = 0xcc9ff3b4;

		// grey
		StrategoImageView.arrColorScheme[3][0] = 0xffc0c0c0;
		StrategoImageView.arrColorScheme[3][1] = 0xffffffff;
		StrategoImageView.arrColorScheme[3][2] = 0xccf3ed4b;

		// brown
		StrategoImageView.arrColorScheme[4][0] = 0xff65390d; //4c2b0a
		StrategoImageView.arrColorScheme[4][1] = 0xffb98b4f;
		StrategoImageView.arrColorScheme[4][2] = 0xccf3ed4b;
		// 347733
		// red
		StrategoImageView.arrColorScheme[5][0] = 0xffff2828;
		StrategoImageView.arrColorScheme[5][1] = 0xffffd1d1;
		StrategoImageView.arrColorScheme[5][2] = 0xccf3ed4b;

		for(int i = 0; i < 100; i++){
			_arrImages[i].setOnClickListener(ocl);
			//_arrImages[i].setFocusable(false);
			_arrImages[i].setOnLongClickListener(olcl);

			_arrImgCache[i] = new ImageCacheObject();
		}

		final View layout = (View) _activity.getWindow().getDecorView().findViewById(android.R.id.content);
		ViewTreeObserver vto = layout.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				layout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				adjustWidth();
			}
		});
	}

	private void adjustWidth(){
		StrategoImageView._matrix = null;
		final Window window = _activity.getWindow();
		final View v = window.getDecorView();

		v.post(new Runnable() {
			@Override
			public void run() {
				Rect rectangle = new Rect();
				v.getWindowVisibleDisplayFrame(rectangle);
				int contentViewTop = window.findViewById(Window.ID_ANDROID_CONTENT).getTop();
				//int titleBarHeight= contentViewTop - statusBarHeight;
				int availableHeight = (rectangle.bottom - rectangle.top) - contentViewTop;
				int availableWidth = rectangle.right - rectangle.left;
				int length, margin = 0;

				// portrait
				if (availableHeight > availableWidth) {
					length = availableWidth / 10;
					margin = (availableWidth - 10 * length) / 2;
				} else {
					length = availableHeight / 10;
				}

				if (margin > 0) {
					View viewBoard = (View) _activity.findViewById(R.id.includeboard);
					RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) viewBoard.getLayoutParams();
					params.setMargins(margin, 0, 0, 0); //substitute parameters for left, top, right, bottom
					viewBoard.setLayoutParams(params);
				}
				Log.i("StrategoViewBase", "availableHeight 2 " + availableHeight);

				LayoutParams params = new LayoutParams(length, length);
				for (int i = 0; i < 100; i++) {
					_arrImages[i].setLayoutParams(params);
				}
			}
		});
	}

	public void setBlindfoldMode(int mode){
		_modeBlindfold = mode;
	}
	public int getBlindfoldMode(){
		return _modeBlindfold;
	}

	public int getIndexOfButton(View but){
		for(int i = 0; i < 100; i++){
			if(_arrImages[i] == ((StrategoImageView)but)){
				_arrImages[i].setPressed(false);
				return i;
			}
		}
		return -1;
	}

	public void paintBoard(StrategoControl gameControl, int positionSelected, ArrayList<Integer> arrPos){
		//boolean bPiece, bSelected, bSelectedPosition;
		//int iResource, iPiece = StrategoConstants.PAWN, iColor = StrategoConstants.RED, iFieldColor;
		int iFieldColor1, iFieldColor2, iFieldColor;
		System.gc();

		for(int z = 0; z < 100; z++){
			_arrImages[z].setPressed(false);
			_arrImages[z].setSelected(false);
		}

		boolean change=true;
		for(int i = 0; i < 100; i++) {
			Piece piece = gameControl.getPieceAt (i);
			ImageCacheObject tmpCache = _arrImgCache[i];

			// determinate background color
			iFieldColor = i%2;
			if (i % 10 == 0) {
				if (change) change=false; else change = true;
			}
			if (change) {
				if (iFieldColor == 1) iFieldColor = 0; else iFieldColor=1;
			}


			tmpCache._bPiece=true;
			tmpCache._piece = piece.getId();
			tmpCache._color = piece.getColor();

			if (positionSelected != -1 && i == positionSelected) {
				tmpCache._selected = true;
			} else {
				tmpCache._selected = false;
			}
			tmpCache._selectedPos = false;
			tmpCache._fieldColor = iFieldColor;

			/*tmpCache = _arrImgCache[i];
			tmpCache._bPiece = bPiece;
			tmpCache._piece = iPiece;
			tmpCache._color = iColor;
			tmpCache._fieldColor = iFieldColor;
			tmpCache._selectedPos = bSelectedPosition;
			tmpCache._selected = bSelected;*/

			_arrImages[getFieldIndex(i)].setICO(tmpCache);
			_arrImages[getFieldIndex(i)].invalidate();

		}

		System.gc();
	}

	public void setFlippedBoard(boolean flipped){
		resetImageCache();
		_flippedBoard = flipped;
	}

	public boolean getFlippedBoard(){
		return _flippedBoard;
	}

	public void flipBoard(){
		resetImageCache();
		_flippedBoard = !_flippedBoard;
		setFlippedBoard(_flippedBoard);
	}

	public int getFieldIndex(int i){
		if(_flippedBoard){
			return 99 - i;
		}
		return i;
	}

	public void resetImageCache(){
		for(int i = 0; i < 100; i++){
			_arrImgCache[i]._bPiece = false;
			_arrImgCache[i]._fieldColor = (i&1)==0 ? (((i >> 3) & 1) == 0 ? StrategoConstants.RED : StrategoConstants.BLUE) : (((i >> 3) & 1) == 0 ? StrategoConstants.BLUE: StrategoConstants.RED);
			_arrImgCache[i]._selectedPos = false;
			_arrImgCache[i]._selected = false;
			_arrImgCache[i]._color = -1;
			_arrImgCache[i]._piece = -1;
		}
	}
}