package com.xmd.cashier.widget.stickyview;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;


/**
 * Created by sdcm on 16-6-20.
 */
public class StickyRecyclerHeadersDecoration extends RecyclerView.ItemDecoration {

    private HeaderViewCache mHeaderViewCache;
    private StickyHeaderPositionCalculator mPositionCalculator;
    private SparseArray<Rect> mHeaderRects = new SparseArray<>();
    private Rect mTempRect = new Rect();

    public StickyRecyclerHeadersDecoration(StickyHeaderInterface stickyHeaderInterface) {
        mHeaderViewCache = new HeaderViewCache(stickyHeaderInterface);
        mPositionCalculator = new StickyHeaderPositionCalculator(stickyHeaderInterface, mHeaderViewCache);
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);

        int childCount = parent.getChildCount();
        if (childCount <= 0 || parent.getAdapter().getItemCount() <= 0) {
            return;
        }
        for (int i = 0; i < childCount; i++) {
            View itemView = parent.getChildAt(i);
            int position = parent.getChildAdapterPosition(itemView);
            if (position == RecyclerView.NO_POSITION) {
                continue;
            }

            boolean hasStickyHeader = mPositionCalculator.hasStickyHeader(itemView, position);
            if (hasStickyHeader || mPositionCalculator.hasItemHeader(position)) {
                View header = mHeaderViewCache.getHeader(parent, position);
                //re-use existing Rect, if any.
                Rect headerOffset = mHeaderRects.get(position);
                if (headerOffset == null) {
                    headerOffset = new Rect();
                    mHeaderRects.put(position, headerOffset);
                }
                mPositionCalculator.initHeaderBounds(headerOffset, parent, header, itemView, hasStickyHeader);
                drawHeader(parent, c, header, headerOffset);
            }
        }
    }

    void drawHeader(RecyclerView recyclerView, Canvas canvas, View header, Rect offset) {
        canvas.save();

        if (recyclerView.getLayoutManager().getClipToPadding()) {
            // Clip drawing of headers to the padding of the RecyclerView. Avoids drawing in the padding
            mPositionCalculator.initClipRectForHeader(mTempRect, recyclerView, header);
            canvas.clipRect(mTempRect);
        }

        canvas.translate(offset.left, offset.top);

        header.draw(canvas);
        canvas.restore();
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        int itemPosition = parent.getChildAdapterPosition(view);
        if (itemPosition == RecyclerView.NO_POSITION) {
            return;
        }

        if (mPositionCalculator.hasItemHeader(itemPosition)) {
            View header = mHeaderViewCache.getHeader(parent, itemPosition);
            setItemOffsetsForHeader(outRect, header);
        }
    }

    /**
     * Sets the offsets for the first item in a section to make room for the header view
     *
     * @param itemOffsets rectangle to define offsets for the item
     * @param header      view used to calculate offset for the item
     */
    private void setItemOffsetsForHeader(Rect itemOffsets, View header) {
        mPositionCalculator.initMargins(mTempRect, header);
        itemOffsets.top = header.getHeight() + mTempRect.top + mTempRect.bottom;
    }

    /**
     * Invalidates cached headers.  This does not invalidate the recyclerview, you should do that manually after
     * calling this method.
     */
    public void invalidateHeaders() {
        mHeaderViewCache.invalidate();
        mHeaderRects.clear();
    }
}
