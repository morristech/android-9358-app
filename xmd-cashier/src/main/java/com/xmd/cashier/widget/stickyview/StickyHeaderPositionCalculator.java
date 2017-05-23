package com.xmd.cashier.widget.stickyview;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by sdcm on 16-6-21.
 */
public class StickyHeaderPositionCalculator {
    private StickyHeaderInterface mStickyHeaderInterface;

    private final Rect mTempRect1 = new Rect();
    private final Rect mTempRect2 = new Rect();

    private HeaderViewCache mHeaderProvider;

    public StickyHeaderPositionCalculator(StickyHeaderInterface stickyHeaderInterface, HeaderViewCache headerCache) {
        mStickyHeaderInterface = stickyHeaderInterface;
        mHeaderProvider = headerCache;
    }

    public boolean hasItemHeader(int position) {
        if (!mStickyHeaderInterface.showItemHeader()) {
            return false;
        }
        if (indexOutOfBounds(position)) {
            return false;
        }

        String headerId = getHeaderId(position);
        if (TextUtils.isEmpty(headerId)) {
            return false;
        }

        String nextHeaderId = null;
        int nextPosition = position - 1;
        if (!indexOutOfBounds(nextPosition)) {
            nextHeaderId = getHeaderId(nextPosition);
        }

        return position == 0 || !headerId.equals(nextHeaderId);
    }

    public boolean hasStickyHeader(View itemView, int position) {
        return itemView.getTop() <= 0 && mStickyHeaderInterface.getHeaderId(position) != null;
    }

    private boolean indexOutOfBounds(int position) {
        return position < 0 || position >= mStickyHeaderInterface.getItemCount();
    }

    public String getHeaderId(int position) {
        return mStickyHeaderInterface.getHeaderId(position);
    }

    public void initClipRectForHeader(Rect clipRect, RecyclerView recyclerView, View header) {
        initMargins(clipRect, header);
        clipRect.set(
                recyclerView.getPaddingLeft(),
                recyclerView.getPaddingTop(),
                recyclerView.getWidth() - recyclerView.getPaddingRight() - clipRect.right,
                recyclerView.getHeight() - recyclerView.getPaddingBottom());
    }

    public void initHeaderBounds(Rect bounds, RecyclerView recyclerView, View header, View firstView, boolean firstHeader) {

        initDefaultHeaderOffset(bounds, recyclerView, header, firstView);

        if (firstHeader && isStickyHeaderBeingPushedOffscreen(recyclerView, header)) {
            View viewAfterNextHeader = getFirstViewUnobscuredByHeader(recyclerView, header);
            int firstViewUnderHeaderPosition = recyclerView.getChildAdapterPosition(viewAfterNextHeader);
            View secondHeader = mHeaderProvider.getHeader(recyclerView, firstViewUnderHeaderPosition);
            translateHeaderWithNextHeader(recyclerView, bounds,
                    header, viewAfterNextHeader, secondHeader);
        }
    }

    private void translateHeaderWithNextHeader(RecyclerView recyclerView, Rect translation, View currentHeader, View viewAfterNextHeader, View nextHeader) {
        initMargins(mTempRect1, nextHeader);
        initMargins(mTempRect2, currentHeader);
        int topOfStickyHeader = getListTop(recyclerView) + mTempRect2.top + mTempRect2.bottom;
        int shiftFromNextHeader = viewAfterNextHeader.getTop() - nextHeader.getHeight() - mTempRect1.bottom - mTempRect1.top - currentHeader.getHeight() - topOfStickyHeader;
        if (shiftFromNextHeader < topOfStickyHeader) {
            translation.top += shiftFromNextHeader;
        }
    }

    private void initDefaultHeaderOffset(Rect headerMargins, RecyclerView recyclerView, View header, View firstView) {
        int translationX, translationY;
        initMargins(mTempRect1, header);

        ViewGroup.LayoutParams layoutParams = firstView.getLayoutParams();
        int leftMargin = 0;
        int topMargin = 0;
        if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) layoutParams;
            leftMargin = marginLayoutParams.leftMargin;
            topMargin = marginLayoutParams.topMargin;
        }

        translationX = firstView.getLeft() - leftMargin + mTempRect1.left;
        translationY = Math.max(
                firstView.getTop() - topMargin - header.getHeight() - mTempRect1.bottom,
                getListTop(recyclerView) + mTempRect1.top);

        headerMargins.set(translationX, translationY, translationX + header.getWidth(),
                translationY + header.getHeight());
    }

    private int getListTop(RecyclerView view) {
        if (view.getLayoutManager().getClipToPadding()) {
            return view.getPaddingTop();
        } else {
            return 0;
        }
    }

    private boolean isStickyHeaderBeingPushedOffscreen(RecyclerView recyclerView, View stickyHeader) {
        View viewAfterHeader = getFirstViewUnobscuredByHeader(recyclerView, stickyHeader);
        int firstViewUnderHeaderPosition = recyclerView.getChildAdapterPosition(viewAfterHeader);
        if (firstViewUnderHeaderPosition == RecyclerView.NO_POSITION) {
            return false;
        }

        if (firstViewUnderHeaderPosition > 0 && hasItemHeader(firstViewUnderHeaderPosition)) {
            View nextHeader = mHeaderProvider.getHeader(recyclerView, firstViewUnderHeaderPosition);
            initMargins(mTempRect1, nextHeader);
            initMargins(mTempRect2, stickyHeader);

            int topOfNextHeader = viewAfterHeader.getTop() - mTempRect1.bottom - nextHeader.getHeight() - mTempRect1.top;
            int bottomOfThisHeader = recyclerView.getPaddingTop() + stickyHeader.getBottom() + mTempRect2.top + mTempRect2.bottom;
            if (topOfNextHeader < bottomOfThisHeader) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns the first item currently in the RecyclerView that is not obscured by a header.
     *
     * @param parent Recyclerview containing all the list items
     * @return first item that is fully beneath a header
     */
    private View getFirstViewUnobscuredByHeader(RecyclerView parent, View firstHeader) {
        boolean isReverseLayout = false;
        int step = isReverseLayout ? -1 : 1;
        int from = isReverseLayout ? parent.getChildCount() - 1 : 0;
        for (int i = from; i >= 0 && i <= parent.getChildCount() - 1; i += step) {
            View child = parent.getChildAt(i);
            if (!itemIsObscuredByHeader(parent, child, firstHeader)) {
                return child;
            }
        }
        return null;
    }

    /**
     * Determines if an item is obscured by a header
     *
     * @param parent
     * @param item   to determine if obscured by header
     * @param header that might be obscuring the item
     * @return true if the item view is obscured by the header view
     */
    private boolean itemIsObscuredByHeader(RecyclerView parent, View item, View header) {
        RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) item.getLayoutParams();
        initMargins(mTempRect1, header);

        int adapterPosition = parent.getChildAdapterPosition(item);
        if (adapterPosition == RecyclerView.NO_POSITION || mHeaderProvider.getHeader(parent, adapterPosition) != header) {
            // Resolves https://github.com/timehop/sticky-headers-recyclerview/issues/36
            // Handles an edge case where a trailing header is smaller than the current sticky header.
            return false;
        }

        int itemTop = item.getTop() - layoutParams.topMargin;
        int headerBottom = getListTop(parent) + header.getBottom() + mTempRect1.bottom + mTempRect1.top;
        if (itemTop >= headerBottom) {
            return false;
        }

        return true;
    }

    /**
     * Populates {@link Rect} with margins for any view.
     *
     * @param margins rect to populate
     * @param view    for which to get margins
     */
    public void initMargins(Rect margins, View view) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();

        if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) layoutParams;
            initMarginRect(margins, marginLayoutParams);
        } else {
            margins.set(0, 0, 0, 0);
        }
    }

    /**
     * Converts {@link ViewGroup.MarginLayoutParams} into a representative {@link Rect}.
     *
     * @param marginRect         Rect to be initialized with margins coordinates, where
     *                           {@link ViewGroup.MarginLayoutParams#leftMargin} is equivalent to {@link Rect#left}, etc.
     * @param marginLayoutParams margins to populate the Rect with
     */
    private void initMarginRect(Rect marginRect, ViewGroup.MarginLayoutParams marginLayoutParams) {
        marginRect.set(
                marginLayoutParams.leftMargin,
                marginLayoutParams.topMargin,
                marginLayoutParams.rightMargin,
                marginLayoutParams.bottomMargin
        );
    }
}
