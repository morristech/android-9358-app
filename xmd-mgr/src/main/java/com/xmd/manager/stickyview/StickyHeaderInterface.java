package com.xmd.manager.stickyview;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

/**
 * Created by sdcm on 16-6-22.
 */
public abstract class StickyHeaderInterface<VH extends RecyclerView.ViewHolder> {
    /**
     * Get the ID of the header associated with this item.  For example, if your headers group
     * items by their first letter, you could return the character representation of the first letter.
     * Return a value &lt; 0 if the view should not have a header (like, a header view or footer view)
     *
     * @param position the position of the view to get the header ID of
     * @return the header ID
     */
    protected abstract String getHeaderId(int position);

    /**
     * Creates a new ViewHolder for a header.  This works the same way onCreateViewHolder in
     * Recycler.Adapter, ViewHolders can be reused for different views.  This is usually a good place
     * to inflate the layout for the header.
     *
     * @param parent the view to create a header view holder for
     * @return the view holder
     */
    protected abstract VH onCreateHeaderViewHolder(ViewGroup parent);

    /**
     * Binds an existing ViewHolder to the specified adapter position.
     *
     * @param holder   the view holder
     * @param position the adapter position
     */
    protected abstract void onBindHeaderViewHolder(VH holder, int position);

    /**
     * @return the number of views in the adapter
     */
    protected abstract int getItemCount();

    protected abstract boolean showItemHeader();
}
