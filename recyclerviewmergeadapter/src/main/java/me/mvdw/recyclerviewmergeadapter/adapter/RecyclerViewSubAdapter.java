package me.mvdw.recyclerviewmergeadapter.adapter;

import android.support.annotation.CallSuper;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.lang.ref.WeakReference;

/**
 * Created by Martijn van der Woude on 29-09-15.
 */
public abstract class RecyclerViewSubAdapter<VH extends RecyclerViewSubAdapter.ViewHolder> extends RecyclerView.Adapter<VH> {

    @Override
    @CallSuper
    public void onBindViewHolder(VH vh, int i) {
        vh.setAdapter(this);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private WeakReference<? extends RecyclerView.Adapter> mAdapter;

        public ViewHolder(View itemView) {
            super(itemView);
        }

        public void setAdapter(RecyclerView.Adapter adapter){
            this.mAdapter = new WeakReference<>(adapter);
        }

        public RecyclerView.Adapter getAdapter(){
            if(mAdapter != null)
                return mAdapter.get();

            return null;
        }

        public int getSubAdapterPosition(){
            int position = super.getAdapterPosition();

            RecyclerView parentRecyclerView = (RecyclerView) itemView.getParent();

            if(parentRecyclerView.getAdapter() instanceof RecyclerViewMergeAdapter){
                if(((RecyclerViewMergeAdapter) parentRecyclerView.getAdapter()).mAdapters.size() > 1) {
                    for (Object localAdapter : ((RecyclerViewMergeAdapter) parentRecyclerView.getAdapter()).mAdapters) {
                        RecyclerView.Adapter adapter = ((RecyclerViewMergeAdapter.LocalAdapter) localAdapter).mAdapter;

                        if (this.mAdapter != null && adapter == this.mAdapter.get()) {
                            break;
                        } else {
                            position -= adapter.getItemCount();
                        }
                    }
                }
            }

            return position;
        }
    }
}
