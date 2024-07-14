package de.arvato.stratego;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.arvato.stratego.game.PieceEnum;

public class StrategoCapturedPieceAdapter extends RecyclerView.Adapter<StrategoCapturedPieceAdapter.ViewHolder> {

    private final List<CapturedPieceItem> listCapturedPieces;


    public StrategoCapturedPieceAdapter (List<CapturedPieceItem> listCapturedPieces) {
        this.listCapturedPieces = listCapturedPieces;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.captured_pieces, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        CapturedPieceItem capturedPieceItem = listCapturedPieces.get(i);
        //viewHolder.capturedText.setText(String.format("x %d", capturedPieceItem.getCaptured()));
        viewHolder.capturedText.setText(String.valueOf(capturedPieceItem.getCaptured()));
        viewHolder.capturedImage.setBitmap(StrategoImageView.arrPieceBitmaps[capturedPieceItem.getPlayer()][capturedPieceItem.getPieceEnum().getId()]);
    }

    @Override
    public int getItemCount() {
        return listCapturedPieces.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final StrategoCapturedImageView capturedImage;
        final TextView capturedText;

        ViewHolder(View itemView) {
            super(itemView);
            capturedText = itemView.findViewById(R.id.capturedText);
            capturedImage = itemView.findViewById(R.id.capturedImage);
        }
    }

}
