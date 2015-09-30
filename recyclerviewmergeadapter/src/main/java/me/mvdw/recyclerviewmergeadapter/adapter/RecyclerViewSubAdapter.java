package me.mvdw.recyclerviewmergeadapter.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import me.mvdw.recyclerviewmergeadapter.viewholder.ViewHolder;

/**
 * Created by Martijn van der Woude on 29-09-15.
 */
public class RecyclerViewSubAdapter<VH extends ViewHolder> extends RecyclerView.Adapter<VH> {

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}