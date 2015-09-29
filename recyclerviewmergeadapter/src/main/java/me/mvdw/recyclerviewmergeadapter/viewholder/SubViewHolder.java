package me.mvdw.recyclerviewmergeadapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import me.mvdw.recyclerviewmergeadapter.adapter.RecyclerViewMergeAdapter;

/**
 * Created by Martijn van der Woude on 29-09-15.
 */
public class SubViewHolder extends RecyclerView.ViewHolder {
    private RecyclerView.Adapter adapter;

    public SubViewHolder(View itemView, RecyclerView.Adapter adapter) {
        super(itemView);

        this.adapter = adapter;
    }

    public int getLocalPosition(){
        RecyclerView recyclerView = (RecyclerView) itemView.getParent();
        RecyclerView.Adapter mainAdapter = recyclerView.getAdapter();

        int position = super.getAdapterPosition();

        if(mainAdapter instanceof RecyclerViewMergeAdapter){
            for(Object localAdapter : ((RecyclerViewMergeAdapter) mainAdapter).mAdapters){
                RecyclerView.Adapter adapter = ((RecyclerViewMergeAdapter.LocalAdapter) localAdapter).mAdapter;

                if(adapter.equals(this.adapter)){
                    break;
                } else {
                    position -= adapter.getItemCount();
                }
            }
        }

        return position;
    }
}