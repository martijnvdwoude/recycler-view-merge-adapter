# RecyclerViewMergeAdapter
A merge adapter for the RecyclerView, a continuation on this gist: https://gist.github.com/athornz/008edacd1d3b2f1e1836

### Usage
Add subadapters or views to the merge adapter and then set it on the RecyclerView.

##### Methods

- `addAdapter(T adapter)`
- `addAdapter(int index, T adapter)`
- `removeAdapter(T adapter)`
- `removeAdapter(int index)`
- `addView(View view)`
- `addViews(List<View> views)`
- `getSubAdapterCount()`
- `getSubAdapter(int index)`

This `RecyclerViewMergeAdapter` should be used with `RecyclerViewSubAdapter`, this is a subclass of the regular `RecyclerViewAdapter`. Make sure the adapters you implement and add are subclasses of `RecyclerViewSubAdapter`.

You should extend your ViewHolders from `RecyclerViewSubAdapter.ViewHolder`. Without using the `RecyclerViewSubAdapter` and this ViewHolder there would only be access to `getAdapterPosition()` and `getLayoutPosition()` on the standard ViewHolder which return the position of the ViewHolder as seen in the context of the whole merge adapter, not the subadapters. This makes properly handling click events, updating data sets and notifying the subadapter of inserted, changed or removed items problematic.

`public static class MyViewHolder extends RecyclerViewSubAdapter.ViewHolder`

##### getLocalPosition()
This ViewHolder implements a `getLocalPosition()` method which returns the position of ViewHolder in its subadapter.

##### Note
You need to call `super.onBindViewHolder()` in order to keep the reference to the adapter up to date in the ViewHolder. This prevents issues that would otherwise be caused when recycling ViewHolders when swapping adapters for example.

```
@Override
public void onBindViewHolder(final MyViewHolder myViewHolder, int i) {
    super.onBindViewHolder(myViewHolder, i);
    
    // Bind your viewHolder
}
```
