package de.arvato.stratego;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

public class StrategoImageView extends View {

        public static final String TAG = "ChessImageView";

        public static Bitmap[][] arrPieceBitmaps = new Bitmap[2][12];
        public static Bitmap bmpBorder, bmpSelect, bmpSelectLight;
        public static Bitmap bmpTile;
        public static Bitmap [] cover = new Bitmap[2];

        public static Bitmap[] arrFieldBitmap = new Bitmap[100];

        private static String sActivity;

        // 5 colorschemes with 2 colors each
        public static int[][] arrColorScheme = new int[6][3];
        public static int colorScheme = 2;
        public static Paint _paint = new Paint();
        public static Matrix _matrix = null;
        public static Matrix _matrixTile = null;

        static {
                _paint.setFlags(Paint.ANTI_ALIAS_FLAG);
                _paint.setFilterBitmap(true);
        }



        private ImageCacheObject _ico;

        public StrategoImageView(Context context) {
                super(context);
        }

        public StrategoImageView(Context context, @Nullable AttributeSet attrs) {
                super(context, attrs);
        }

        @Override
        protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                if(arrColorScheme[0][0] == 0){
                        return;
                }

                if (_matrix == null) {
                        _matrix = new Matrix();
                        float scale = 1.0F;
                        Bitmap bmp = arrPieceBitmaps[StrategoConstants.RED][PieceEnum.FLAG.getId()]; // any dynamic
                        scale = (float)getWidth() / bmp.getWidth();
                        Log.i("paintBoard", "init " + scale + " : " + bmp.getWidth() + ", " + getWidth());
                        _matrix.setScale(scale, scale);
                        if(bmpTile != null){
                                _matrixTile = new Matrix();
                                bmp = bmpTile;
                                scale = (float)getWidth() / bmp.getWidth();
                                _matrixTile.setScale(scale, scale);
                        }

                }

                Bitmap bmp;
                ImageCacheObject ico = _ico;

                SharedPreferences pref = getContext().getSharedPreferences("StrategoPlayer", Context.MODE_PRIVATE);
                // first draw field background
                if(ico == null)
                        Log.e("err", "err");

                /*if(hasFocus()){
                        _paint.setColor(0xffff9900);
                        canvas.drawRect(new Rect(0, 0, getWidth(), getHeight()), _paint);
                } else {
                        _paint.setColor(ico._fieldColor == 0 ? arrColorScheme[colorScheme][0] : arrColorScheme[colorScheme][1]);
                        canvas.drawRect(new Rect(0, 0, getWidth(), getHeight()), _paint);
                        if (colorScheme == 6){ // 6 is color picker
                                _paint.setColor(ico._fieldColor == 0 ? pref.getInt("color2", 0xffdddddd) : pref.getInt("color1", 0xffff0066));
                                canvas.drawRect(new Rect(0, 0, getWidth(), getHeight()), _paint);
                                if (ico._selected){
                                        _paint.setColor(pref.getInt("color3", 0xcc00dddd) & 0xccffffff);
                                        canvas.drawRect(new Rect(0, 0, getWidth(), getHeight()), _paint);
                                }
                        } else {
                                _paint.setColor(ico._fieldColor == 0 ? arrColorScheme[colorScheme][0] : arrColorScheme[colorScheme][1]);
                                canvas.drawRect(new Rect(0, 0, getWidth(), getHeight()), _paint);
                                if (ico._selected) {
                                        _paint.setColor(arrColorScheme[colorScheme][2]);
                                        canvas.drawRect(new Rect(0, 0, getWidth(), getHeight()), _paint);
                                }
                        }
                }*/

                if (ico.boardField != -1) {
                    bmp = arrFieldBitmap[ico.boardField];
                    canvas.drawBitmap(bmp, _matrix, _paint);
                }

                //draw border
                if (StrategoControl.isPlayablePosition(ico.boardField)) {
                        _paint.setStyle(Paint.Style.STROKE);
                        _paint.setStrokeWidth(3);
                        _paint.setColor(Color.BLACK);
                        canvas.drawRect(new Rect(0, 0, getWidth(), getHeight()), _paint);
                } else {
                        _paint.setStrokeWidth(0);
                }

                if(bmpTile != null){
                        canvas.drawBitmap(bmpTile, _matrixTile, _paint);
                }

                if(bmpBorder != null && (ico.selected || hasFocus())){
                        canvas.drawBitmap(bmpBorder, _matrix, _paint);
                }

                if(ico.selectedPos){
                        canvas.drawBitmap(bmpSelectLight, _matrix, _paint);
                }

                if(ico.bPiece){
                        bmp = arrPieceBitmaps[ico.color][ico.piece];
                        //sActivity = (StrategoImageView.get_ssActivity() == null) ?  "" : start.get_ssActivity();
                        // todo if it's fine then will put back && statements
                        /*
                        if (_sActivity.equals(getContext().getString(R.string.start_play))){
                                if(options.is_sbFlipTopPieces()){
                                        if((options.is_sbPlayAsBlack() ? ico._color == 1 : ico._color == 0)) {   // flips top pieces for human vs human without
                                                canvas.rotate(180, getWidth() / 2, getHeight() / 2);                 // autoflip on in Play mode
                                        }
                                }
                        }*/
                        canvas.drawBitmap(bmp, _matrix, _paint);
                }

                /*if(ico._coord != null){
                        _paint.setColor(0x99ffffff);
                        canvas.drawRect(0, getHeight() - 14,  _paint.measureText(ico._coord) + 4, getHeight(), _paint);
                        _paint.setColor(Color.BLACK);

                        _paint.setTextSize(getHeight() > 50 ? (int)(getHeight()/5) : 10);
                        canvas.drawText(ico._coord, 2, getHeight() - 2, _paint);

                        if(ico._coord.equals("A") && !ImageCacheObject._flippedBoard){  // bottom-left corner coordinates
                                canvas.drawText("1", 2 , getHeight() - 30, _paint);
                        }
                        else if(ico._coord.equals("H") && ImageCacheObject._flippedBoard){
                                canvas.drawText("8", 2 , getHeight() - 30, _paint);
                        }
                }*/
        }

        public ImageCacheObject getICO() {
                return _ico;
        }

        public void setICO(ImageCacheObject _ico) {
                this._ico = _ico;
        }

}
