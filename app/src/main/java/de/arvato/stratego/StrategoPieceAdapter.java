package de.arvato.stratego;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class StrategoPieceAdapter extends BaseAdapter {

    private final Context context;
    private final PieceEnum [] pieces;
    private final int player;

    public StrategoPieceAdapter (Context context, PieceEnum [] pieces, int player) {
        this.context = context;
        this.pieces = pieces;
        this.player = player;
    }

    @Override
    public int getCount() {
        return pieces.length;
    }

    @Override
    public Object getItem(int position) {
        return pieces[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Bitmap bmp = StrategoImageView.arrPieceBitmaps[player][pieces[position].getId()];
        ImageView imageView = new ImageView(context);
        imageView.setImageBitmap(bmp);
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        imageView.setAdjustViewBounds(true);
        return imageView;
    }
}
