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
        RecyclerView recyclerView = (RecyclerView) itemView.getParent();
        RecyclerView.Adapter mainAdapter = recyclerView.getAdapter();

        int position = super.getAdapterPosition();

        if(mainAdapter instanceof RecyclerViewMergeAdapter){
            for(Object localAdapter : ((RecyclerViewMergeAdapter) mainAdapter).mAdapters){
                RecyclerView.Adapter adapter = ((RecyclerViewMergeAdapter.LocalAdapter) localAdapter).mAdapter;

                if(adapter.equals(this.mAdapter)){
                    break;
                } else {
                    position -= adapter.getItemCount();
                }
            }
        }

        return position;
    }
}