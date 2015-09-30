package me.mvdw.recyclerviewmergeadapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import me.mvdw.recyclerviewmergeadapter.adapter.RecyclerViewMergeAdapter;

/**
 * Created by Martijn van der Woude on 29-09-15.
 */
public class ViewHolder extends RecyclerView.ViewHolder {
    private RecyclerView.Adapter mAdapter;

    public ViewHolder(View itemView, RecyclerView.Adapter adapter) {
        super(itemView);

        this.mAdapter = adapter;
    }

    public int getLocalPosition(){
        int position = super.getAdapterPosition();

        RecyclerView parentRecyclerView = (RecyclerView) itemView.getParent();

        if(parentRecyclerView.getAdapter() instanceof RecyclerViewMergeAdapter){
            for(Object localAdapter : ((RecyclerViewMergeAdapter) parentRecyclerView.getAdapter()).mAdapters){
                RecyclerView.Adapter adapter = ((RecyclerViewMergeAdapter.LocalAdapter) localAdapter).mAdapter;

                if(adapter ==   this.mAdapter){
                    break;
                } else {
                    position -= adapter.getItemCount();
                }
            }
        }

        return position;
    }
}