package de.arvato.stratego;


import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

public class StrategoCapturedImageView extends View {

    public static final String TAG = "CapturedImageView";

    private Bitmap _bitMap;
    private boolean _bHighlighted;

    private boolean _allDead;

    public StrategoCapturedImageView(Context context) {
        super(context);
        _bitMap = null;
        _bHighlighted = false;
        _allDead = false;
        setFocusable(false);
    }

    public StrategoCapturedImageView(Context context, AttributeSet atts) {
        super(context, atts);
        _bitMap = null;
        _bHighlighted = false;
        _allDead = false;
        setFocusable(false);
    }

    public void initBitmap(String sPiece) {
        AssetManager am = this.getContext().getAssets();
        String sFolder = "highres/";
        try {
            _bitMap = BitmapFactory.decodeStream(am.open(sFolder + sPiece));
        } catch (Exception ex) {

        }
    }

    public void setHighlighted(boolean highlighted) {
        _bHighlighted = highlighted;
    }

    public void set_allDead(boolean _allDead) {
        this._allDead = _allDead;
    }

    public void setBitmap (Bitmap bitmap) {
        this._bitMap = bitmap;
    }

    public void onDraw(Canvas canvas) {
        if(_bitMap == null){
            return;
        }
        //float scale = (float) getWidth() / _bitMap.getWidth();
        float scale=1.00F;
        //Log.d (TAG, "scale:" + String.valueOf(scale) + " getWidth():" + getWidth() + " bitMap.getWidh():" + _bitMap.getWidth() );
        Matrix m = new Matrix();
        m.setScale(scale, scale);
        Paint p = new Paint();
        p.setFlags(Paint.ANTI_ALIAS_FLAG);
        p.setFilterBitmap(true);
        p.setColor(Color.WHITE);

        if(hasFocus()){
            p.setColor(0xffff9900);
            canvas.drawRect(new Rect(0, 0, getWidth(), getHeight()), p);
        } else if (_bHighlighted) {
            p.setColor(0xff999999);
            canvas.drawRect(new Rect(0, 0, getWidth(), getHeight()), p);
        }

        if (_allDead) {
            Paint xPaint = new Paint();
            xPaint.setColor(Color.RED);
            xPaint.setStrokeWidth(10);
            xPaint.setAntiAlias(true);

            // Draw a red X over the bitmap
            canvas.drawLine(0, 0, getWidth(), getHeight(), xPaint);
            canvas.drawLine(0, getHeight(), getWidth(), 0, xPaint);
        }

        canvas.drawBitmap(_bitMap, m, p);
    }
}
