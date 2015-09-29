package me.mvdw.recyclerviewmergeadapter.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecyclerViewMergeAdapter<T extends RecyclerView.Adapter> extends RecyclerView.Adapter {

    private Context mContext;
    public ArrayList<LocalAdapter> mAdapters = new ArrayList<>();
    private int mViewTypeIndex=0;
    private HashMap<RecyclerView.Adapter, ForwardingDataSetObserver> observers = new HashMap<>();

    public RecyclerViewMergeAdapter() {
    }

    public RecyclerViewMergeAdapter(Context context) {
        this.mContext = context;
    }

    /**
     * A Mergeable adapter must implement both ListAdapter and SpinnerAdapter to be useful in lists and spinners.
     */

    public class LocalAdapter {
        public final T mAdapter;
        public int mLocalPosition = 0;
        public Map<Integer, Integer> mViewTypesMap = new HashMap<>();

        public LocalAdapter(T adapter) {
            mAdapter = adapter;
        }
    }

    /** Append the given adapter to the list of merged adapters. */
    public void addAdapter(T adapter) {
        addAdapter(mAdapters.size(), adapter);
    }

    /** Add the given adapter to the list of merged adapters at the given index. */
    public void addAdapter(int index, T adapter) {
        mAdapters.add(index, new LocalAdapter(adapter));

        ForwardingDataSetObserver observer = new ForwardingDataSetObserver(adapter);
        observers.put(adapter, observer);

        adapter.registerAdapterDataObserver(observer);
        notifyDataSetChanged();
    }

	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	 * remove adapter
	 * - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */

    /** Remove the given adapter from the list of merged adapters. */
    public void removeAdapter(T adapter) {
        if (!mAdapters.contains(adapter)) return;
        removeAdapter(mAdapters.indexOf(adapter));
    }

    /** Remove the adapter at the given index from the list of merged adapters. */
    public void removeAdapter(int index) {
        if (index < 0 || index >= mAdapters.size()) return;
        LocalAdapter adapter = mAdapters.remove(index);

        ForwardingDataSetObserver observer = observers.get(adapter.mAdapter);
        adapter.mAdapter.unregisterAdapterDataObserver(observer);
        observers.remove(adapter.mAdapter);

        notifyDataSetChanged();
    }

    public int getSubAdapterCount() {
        return mAdapters.size();
    }

    public T getSubAdapter(int index) {
        return mAdapters.get(index).mAdapter;
    }

    /**
     * Adds a new View to the roster of things to appear in
     * the aggregate list.
     *
     * @param view
     *          Single view to add
     */
    public void addView(View view) {
        ArrayList<View> list=new ArrayList<View>(1);

        list.add(view);

        addViews(list);
    }

    /**
     * Adds a list of views to the roster of things to appear
     * in the aggregate list.
     *
     * @param views
     *          List of views to add
     */
    public void addViews(List<View> views) {
        addAdapter((T) new ViewsAdapter(mContext, views));
    }

    @Override public int getItemCount() {
        int count = 0;
        for (LocalAdapter adapter : mAdapters) {
            count += adapter.mAdapter.getItemCount();
        }
        return count;
        // TODO: cache counts until next onChanged
    }

    /**
     * For a given merged position, find the corresponding Adapter and local position within that Adapter by iterating through Adapters and
     * summing their counts until the merged position is found.
     *
     * @param position a merged (global) position
     * @return the matching Adapter and local position, or null if not found
     */
    public LocalAdapter getAdapterOffsetForItem(final int position) {
        final int adapterCount = mAdapters.size();
        int i = 0;
        int count = 0;

        while (i < adapterCount) {
            LocalAdapter a = mAdapters.get(i);
            int newCount = count + a.mAdapter.getItemCount();
            if (position < newCount) {
                a.mLocalPosition = position - count;
                return a;
            }
            count = newCount;
            i++;
        }
        return null;
    }

    @Override public long getItemId(int position) {
        return position;
    }


    @Override public int getItemViewType(int position) {
        LocalAdapter result = getAdapterOffsetForItem(position);
        int localViewType = result.mAdapter.getItemViewType(result.mLocalPosition);
        if (result.mViewTypesMap.containsValue(localViewType)) {
            for (Map.Entry<Integer, Integer> entry : result.mViewTypesMap.entrySet()) {
                if (entry.getValue() == localViewType) {
                    return entry.getKey();
                }
            }
        }
        mViewTypeIndex += 1;
        result.mViewTypesMap.put(mViewTypeIndex, localViewType);
        return mViewTypeIndex;
    }


    @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        for (LocalAdapter adapter: mAdapters){
            if (adapter.mViewTypesMap.containsKey(viewType))
                return adapter.mAdapter.onCreateViewHolder(viewGroup,adapter.mViewTypesMap.get(viewType));
        }
        return null;
    }

    @Override public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        LocalAdapter result = getAdapterOffsetForItem(position);
        result.mAdapter.onBindViewHolder(viewHolder, result.mLocalPosition);
    }

	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	 * forwarding data set observer
	 * - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */

    private int getMergePositionForLocalPosition(RecyclerView.Adapter adapter, int position){

        for(LocalAdapter localAdapter : mAdapters){
            if(!localAdapter.mAdapter.equals(adapter)){
                position += localAdapter.mAdapter.getItemCount();
            }
        }

        return position;
    }

    private class ForwardingDataSetObserver extends RecyclerView.AdapterDataObserver {

        private RecyclerView.Adapter mAdapter;

        public ForwardingDataSetObserver(RecyclerView.Adapter adapter) {
            this.mAdapter = adapter;
        }

        @Override public void onChanged() {
            notifyDataSetChanged();
        }

        @Override public void onItemRangeChanged(int positionStart, int itemCount) {
            int mergePositionStart = getMergePositionForLocalPosition(mAdapter, positionStart);

            super.onItemRangeChanged(mergePositionStart, itemCount);
            notifyItemRangeChanged(mergePositionStart, itemCount);
        }

        @Override public void onItemRangeInserted(int positionStart, int itemCount) {
            int mergePositionStart = getMergePositionForLocalPosition(mAdapter, positionStart);

            super.onItemRangeInserted(mergePositionStart, itemCount);
            notifyItemRangeInserted(mergePositionStart, itemCount);
        }

        @Override public void onItemRangeRemoved(int positionStart, int itemCount) {
            int mergePositionStart = getMergePositionForLocalPosition(mAdapter, positionStart);

            super.onItemRangeRemoved(mergePositionStart, itemCount);
            notifyItemRangeRemoved(mergePositionStart, itemCount);
        }
    }


    /**
     * ViewsAdapter, ported from CommonsWare SackOfViews adapter.
     */
    public static class ViewsAdapter extends RecyclerView.Adapter {
        private List<View> views=null;
        private Context context;

        /**
         * Constructor creating an empty list of views, but with
         * a specified count. Subclasses must override newView().
         */
        public ViewsAdapter(Context context, int count) {
            super();
            this.context = context;

            views=new ArrayList<>(count);

            for (int i=0;i<count;i++) {
                views.add(null);
            }
        }

        /**
         * Constructor wrapping a supplied list of views.
         * Subclasses must override newView() if any of the elements
         * in the list are null.
         */
        public ViewsAdapter(Context context, List<View> views) {
            super();
            this.context = context;

            this.views=views;
        }

        /**
         * How many items are in the data set represented by this
         * Adapter.
         */
        @Override
        public int getItemCount() {
            return(views.size());
        }

        /**
         * Get the type of View that will be created by getView()
         * for the specified item.
         * @param position Position of the item whose data we want
         */
        @Override
        public int getItemViewType(int position) {
            return(position);
        }

        @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            //view type is equal to the position in this adapter.
            ViewsViewHolder holder = new ViewsViewHolder(views.get(viewType));
            return holder;
        }

        @Override public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        }

        /**
         * Get the row id associated with the specified position
         * in the list.
         * @param position Position of the item whose data we want
         */
        @Override
        public long getItemId(int position) {
            return(position);
        }

        public boolean hasView(View v) {
            return(views.contains(v));
        }

        /**
         * Create a new View to go into the list at the specified
         * position.
         * @param position Position of the item whose data we want
         * @param parent ViewGroup containing the returned View
         */
        protected View newView(int position, ViewGroup parent) {
            throw new RuntimeException("You must override newView()!");
        }
    }

    public static class ViewsViewHolder extends RecyclerView.ViewHolder{
        public ViewsViewHolder(View itemView) {
            super(itemView);
        }
    }
}