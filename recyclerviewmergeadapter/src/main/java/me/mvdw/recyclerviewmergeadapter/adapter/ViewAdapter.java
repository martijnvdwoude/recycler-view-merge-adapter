package me.mvdw.recyclerviewmergeadapter.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * ViewsAdapter, ported from CommonsWare SackOfViews adapter -> https://github.com/commonsguy/cwac-sacklist.
 */
public class ViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public List<View> viewList;

    public ViewAdapter(List<View> viewList) {
        super();
        this.viewList = viewList;
    }

    @Override
    public int getItemCount() {
        return viewList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // View type is the index in the list of views.
        return new RecyclerView.ViewHolder(viewList.get(viewType)) {};
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {}

    @Override
    public long getItemId(int position) {
        return position;
    }
}
