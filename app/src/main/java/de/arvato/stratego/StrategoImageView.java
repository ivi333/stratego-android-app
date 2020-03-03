package de.arvato.stratego;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class StrategoImageView extends View {

        public static final String TAG = "ChessImageView";

        public static Bitmap[][] arrPieceBitmaps = new Bitmap[2][6];
        public static Bitmap bmpBorder, bmpSelect, bmpSelectLight;
        public static Bitmap bmpTile;

        private static String sActivity;

        // 5 colorschemes with 2 colors each
        public static int[][] arrColorScheme = new int[6][3];
        public static int colorScheme = 0;
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


        }
}
