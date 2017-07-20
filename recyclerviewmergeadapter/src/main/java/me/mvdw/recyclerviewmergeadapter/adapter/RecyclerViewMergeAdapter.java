package me.mvdw.recyclerviewmergeadapter.adapter;

import android.support.v4.util.LongSparseArray;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecyclerViewMergeAdapter extends RecyclerView.Adapter {

    private class AdapterDataObserver extends RecyclerView.AdapterDataObserver {

        private RecyclerView.Adapter mAdapter;

        AdapterDataObserver(RecyclerView.Adapter adapter) {
            mAdapter = adapter;
        }

        @Override
        public void onChanged() {
            RecyclerViewMergeAdapter.this.notifyDataSetChanged();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            int subAdapterOffset = getSubAdapterFirstGlobalPosition(mAdapter);
            RecyclerViewMergeAdapter.this.notifyItemRangeChanged(subAdapterOffset + positionStart, itemCount);
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            int subAdapterOffset = getSubAdapterFirstGlobalPosition(mAdapter);
            RecyclerViewMergeAdapter.this.notifyItemRangeInserted(subAdapterOffset + positionStart, itemCount);
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            int subAdapterOffset = getSubAdapterFirstGlobalPosition(mAdapter);
            RecyclerViewMergeAdapter.this.notifyItemMoved(subAdapterOffset + fromPosition, subAdapterOffset + toPosition);
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            int subAdapterOffset = getSubAdapterFirstGlobalPosition(mAdapter);
            RecyclerViewMergeAdapter.this.notifyItemRangeRemoved(subAdapterOffset + positionStart, itemCount);
        }
    }

    /**
     * LocalAdapter is a wrapper class that, for a given adapter, maintains a map of which indices in the global set of items refer to which view type(s).
     */
    public static class LocalAdapter {
        public final RecyclerView.Adapter mAdapter;
        public Map<Integer, Integer> mViewTypesMap = new HashMap<>();
        public AdapterDataObserver adapterDataObserver;
        public LongSparseArray<Long> mItemIdMap = new LongSparseArray<>();

        public LocalAdapter(RecyclerView.Adapter adapter, AdapterDataObserver adapterDataObserver) {
            mAdapter = adapter;
            this.adapterDataObserver = adapterDataObserver;
        }
    }

    /**
     * PosSubAdapterInfo is a wrapper class that holds a local position of an item and reference to the adapter it belongs to.
     */
    public static class PosSubAdapterInfo {
        public final LocalAdapter localAdapter;
        public final int posInSubAdapter;

        public PosSubAdapterInfo(LocalAdapter adapter, int position) {
            localAdapter = adapter;
            posInSubAdapter = position;
        }

        public RecyclerView.Adapter getAdapter() {
            return localAdapter != null ? localAdapter.mAdapter : null;
        }

        Map<Integer, Integer> getViewTypesMap() {
            return localAdapter != null ? localAdapter.mViewTypesMap : null;
        }
    }

    protected List<LocalAdapter> mAdapters;
    private int mViewTypeIndex;
    private long mNextItemId;


    public RecyclerViewMergeAdapter() {
        mAdapters = new ArrayList<>();
        mViewTypeIndex = 0;
        mNextItemId = 0;
    }

    /**
     * @return A List of LocalAdapter objects.
     */
    public List<LocalAdapter> getAdapters() {
        return mAdapters;
    }

    /**
     * @param adapter Append an adapter to the list of adapters.
     */
    public void addAdapter(RecyclerView.Adapter adapter) {
        addAdapter(mAdapters.size(), adapter);
    }

    /**
     * @param index   The index at which to add an adapter to the list of adapters.
     * @param adapter The adapter to add.
     */
    public void addAdapter(int index, RecyclerView.Adapter adapter) {
        AdapterDataObserver adapterDataObserver = new AdapterDataObserver(adapter);
        mAdapters.add(index, new LocalAdapter(adapter, adapterDataObserver));
        adapter.registerAdapterDataObserver(adapterDataObserver);
    }

    /**
     * Clear all adapters from the list of adapters.
     */
    public void clearAdapters() {
        for (LocalAdapter localAdapter : mAdapters) {
            localAdapter.mAdapter.unregisterAdapterDataObserver(localAdapter.adapterDataObserver);
        }
        mAdapters.clear();
    }

    /**
     * Check if a specified adapter has been added.
     *
     * @param adapter The adapter to check in the list of adapters.
     */
    public boolean containsAdapter(RecyclerView.Adapter adapter) {
        for (LocalAdapter localAdapter : mAdapters) {
            if (localAdapter.mAdapter.equals(adapter)) return true;
        }

        return false;
    }

    /**
     * Remove a specific adapter from the list of adapters.
     *
     * @param adapter The adapter to remove from the list of adapters.
     */
    public void removeAdapter(RecyclerView.Adapter adapter) {
        for (int i = mAdapters.size() - 1; i >= 0; i--) {
            LocalAdapter local = mAdapters.get(i);
            if (local.mAdapter.equals(adapter)) {
                removeAdapter(mAdapters.indexOf(local));
            }
        }
    }

    /**
     * Remove the adapter at a specific index from the list of adapters.
     *
     * @param index The index in the adapter list at which an adapter should be removed.
     */
    public void removeAdapter(int index) {
        LocalAdapter localAdapter = mAdapters.get(index);
        localAdapter.mAdapter.unregisterAdapterDataObserver(localAdapter.adapterDataObserver);
        mAdapters.remove(localAdapter);
    }

    /**
     * Return the number of adapters in the list of adapters.
     *
     * @return The number of adapters.
     */
    public int getSubAdapterCount() {
        return mAdapters.size();
    }

    /**
     * Get a specific adapter for a given index.
     *
     * @param index The index for which the return the adapter.
     * @return The adapter which was found at the given index.
     */
    public RecyclerView.Adapter getSubAdapter(int index) {
        return mAdapters.get(index).mAdapter;
    }

    /**
     * Return a PosSubAdapterInfo object for a given global position.
     *
     * @param globalPosition The global position in the entire set of items.
     * @return A PosSubAdapterInfo object containing a reference to the adapter and the local
     * position in that adapter that corresponds to the given global position.
     */
    public PosSubAdapterInfo getPosSubAdapterInfoForGlobalPosition(final int globalPosition) {

        final int adapterCount = mAdapters.size();

        int i = 0;
        int count = 0;

        while (i < adapterCount) {
            LocalAdapter a = mAdapters.get(i);
            int newCount = count + a.mAdapter.getItemCount();
            if (globalPosition < newCount) {
                return new PosSubAdapterInfo(a, globalPosition - count);
            }
            count = newCount;
            i++;
        }

        return null;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        for (LocalAdapter adapter : mAdapters) {
            if (adapter.mViewTypesMap.containsKey(viewType)) {
                return adapter.mAdapter.onCreateViewHolder(viewGroup, adapter.mViewTypesMap.get(viewType));
            }
        }
        return null;
    }

    /**
     * Return the first global position in the entire set of items for a given adapter.
     *
     * @param adapter The adapter for which to the return the first global position.
     * @return The first global position for the given adapter, or -1 if no such position could be found.
     */
    public int getSubAdapterFirstGlobalPosition(RecyclerView.Adapter adapter) {

        int count = 0;

        for (LocalAdapter localAdapter : mAdapters) {
            RecyclerView.Adapter adapter_ = localAdapter.mAdapter;
            if (adapter_.equals(adapter) && adapter_.getItemCount() > 0) {
                return count;
            }
            count += adapter_.getItemCount();
        }

        return -1;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        PosSubAdapterInfo posSubAdapterInfo = getPosSubAdapterInfoForGlobalPosition(position);
        RecyclerView.Adapter adapter = posSubAdapterInfo.getAdapter();
        adapter.onBindViewHolder(viewHolder, posSubAdapterInfo.posInSubAdapter);
    }

    @Override
    public int getItemViewType(int position) {
        PosSubAdapterInfo result = getPosSubAdapterInfoForGlobalPosition(position);
        int localViewType = result.getAdapter().getItemViewType(result.posInSubAdapter);
        if (result.getViewTypesMap().containsValue(localViewType)) {
            for (Map.Entry<Integer, Integer> entry : result.getViewTypesMap().entrySet()) {
                if (entry.getValue() == localViewType) {
                    return entry.getKey();
                }
            }
        }

        mViewTypeIndex += 1;
        result.getViewTypesMap().put(mViewTypeIndex, localViewType);

        return mViewTypeIndex;
    }

    @Override
    public long getItemId(int position) {
        PosSubAdapterInfo posSubAdapterInfo = getPosSubAdapterInfoForGlobalPosition(position);
        long localItemId = posSubAdapterInfo.getAdapter().getItemId(posSubAdapterInfo.posInSubAdapter);
        if (RecyclerView.NO_ID == localItemId) {
            return localItemId;
        } else {
            long globalItemId = posSubAdapterInfo.localAdapter.mItemIdMap.get(localItemId, RecyclerView.NO_ID);
            if (RecyclerView.NO_ID == globalItemId) {
                mNextItemId++;
                globalItemId = mNextItemId;
                posSubAdapterInfo.localAdapter.mItemIdMap.put(localItemId, globalItemId);
            }
            return globalItemId;
        }
    }

    @Override
    public int getItemCount() {
        int count = 0;
        for (LocalAdapter adapter : mAdapters) {
            count += adapter.mAdapter.getItemCount();
        }
        return count;
    }

    /**
     * Add one or more View objects to the adapter.
     *
     * @param view The View or View(s) to add to the adapter. Do not pass null into this method.
     */
    public void addView(View... view) {
        List<View> viewList = Arrays.asList(view);
        addViews(viewList);
    }

    /**
     * A List of View objects to the adapter at once. Make sure your list does not contain null.
     */
    public void addViews(List<View> views) {
        addAdapter(new ViewAdapter(views));
    }
}
