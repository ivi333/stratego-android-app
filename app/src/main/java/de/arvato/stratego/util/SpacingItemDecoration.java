package de.arvato.stratego.util;

import android.graphics.Rect;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SpacingItemDecoration extends RecyclerView.ItemDecoration {
    private final int spacing;

    public SpacingItemDecoration(int spacing) {
        this.spacing = spacing;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        int spanCount = 2; // Number of columns
        int column = position % spanCount;

        outRect.left = column == 0 ? 0 : spacing / 2;
        outRect.right = column == 1 ? 0 : spacing / 2;

        if (position >= spanCount) {
            outRect.top = spacing;
        }

        // Add bottom margin to ensure proper spacing for last row items
        if (position < state.getItemCount() - spanCount) {
            outRect.bottom = spacing;
        }
    }
}