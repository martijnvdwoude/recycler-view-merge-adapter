package me.mvdw.recyclerviewmergeadapter.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.lang.ref.WeakReference;

/**
 * Created by Martijn van der Woude on 29-09-15.
 */
public abstract class RecyclerViewSubAdapter<VH extends RecyclerViewSubAdapter.ViewHolder> extends RecyclerView.Adapter<VH> {

    public static class ViewHolder extends RecyclerView.ViewHolder {
        WeakReference<RecyclerView.Adapter> mAdapter;

        public ViewHolder(View itemView, RecyclerView.Adapter adapter) {
            super(itemView);

            this.mAdapter = new WeakReference<>(adapter);
        }

        public int getLocalPosition(){
            int position = super.getAdapterPosition();

            RecyclerView parentRecyclerView = (RecyclerView) itemView.getParent();

            if(parentRecyclerView.getAdapter() instanceof RecyclerViewMergeAdapter){
                for(Object localAdapter : ((RecyclerViewMergeAdapter) parentRecyclerView.getAdapter()).mAdapters){
                    RecyclerView.Adapter adapter = ((RecyclerViewMergeAdapter.LocalAdapter) localAdapter).mAdapter;

                    if(adapter == this.mAdapter.get()){
                        break;
                    } else {
                        position -= adapter.getItemCount();
                    }
                }
            }

            return position;
        }
    }
}