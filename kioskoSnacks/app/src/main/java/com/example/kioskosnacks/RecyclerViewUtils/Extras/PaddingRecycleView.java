package com.example.kioskosnacks.RecyclerViewUtils.Extras;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class PaddingRecycleView extends RecyclerView.ItemDecoration {
    private int leftSpacing;
    private int rightSpacing;
    private int topSpacing;
    private int bottomSpacing;

    public PaddingRecycleView(int leftSpacing, int rightSpacing, int topSpacing, int bottomSpacing) {
        this.leftSpacing = leftSpacing;
        this.rightSpacing = rightSpacing;
        this.topSpacing = topSpacing;
        this.bottomSpacing = bottomSpacing;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.left = leftSpacing;
        outRect.right = rightSpacing;
        outRect.top = topSpacing;
        outRect.bottom = bottomSpacing;
    }
}
