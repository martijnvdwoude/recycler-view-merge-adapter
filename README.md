[![Platform](http://img.shields.io/badge/platform-android-brightgreen.svg?style=flat)](http://developer.android.com/index.html)
[![License](https://img.shields.io/badge/license-Apache%202-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)

RecyclerViewMergeAdapter
===

A merge adapter for the RecyclerView, a continuation on this gist: https://gist.github.com/athornz/008edacd1d3b2f1e1836

## Download
Gradle (jCenter)
```
dependencies {
    compile 'me.mvdw.recyclerviewmergeadapter:recyclerviewmergeadapter:1.0.0'
}
```

## Usage

Add subadapters to the merge adapter and then set it on the RecyclerView. It's also possible to add individual views or a list of views to the merge adapter.

```
RecyclerView myRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);

// Create new merge adapter
RecyclerViewMergeAdapter mergeAdapter = new RecyclerViewMergeAdapter();

// Add any number of subadapters to merge adapter
MyRecyclerViewSubAdapter subAdapter1 = new MyRecyclerViewSubAdapter();
MyRecyclerViewSubAdapter subAdapter2 = new MyRecyclerViewSubAdapter();

mergeAdapter.addAdapter(subAdapter1);
mergeAdapter.addAdapter(subAdapter2);

// Set merge adapter to RecyclerView
myRecyclerView.setAdapter(mergeAdapter);
```

##### RecyclerViewMergeAdapter methods

- `addAdapter(T adapter)`
- `addAdapter(int index, T adapter)`
- `removeAdapter(T adapter)`
- `removeAdapter(int index)`
- `addView(View view)`
- `addViews(List<View> views)`
- `getSubAdapterCount()`
- `getSubAdapter(int index)`

##### RecyclerViewSubAdapter

Included in this library is the `RecyclerViewSubAdapter` class which extends the `RecyclerView.Adapter` you would normally use with a `RecyclerView`. Make sure the adapters you implement and add extend this `RecyclerViewSubAdapter` for the reason described below. Implement your adapters the same way you would do normally.

##### RecyclerViewSubAdapter.ViewHolder and getSubAdapterPosition()
You should extend your ViewHolders from `RecyclerViewSubAdapter.ViewHolder`. This ViewHolder implements a `getSubAdapterPosition()` method which returns the position of ViewHolder in its subadapter. On the standard `RecyclerViewAdapter.ViewHolder` there are only the `getAdapterPosition()` and `getLayoutPosition()` methods which return the position of the ViewHolder as seen in the context of the whole merge adapter, not the subadapters. This makes properly handling click events, updating datasets and notifying the subadapter of inserted, changed or removed items problematic. So use `getSubAdapterPosition()` where you would normally use `getAdapterPosition()`.

`public static class MyViewHolder extends RecyclerViewSubAdapter.ViewHolder`

##### Note
You need to call `super.onBindViewHolder()` in order to keep the reference to the adapter up to date in the ViewHolder.

```
@Override
public void onBindViewHolder(final MyViewHolder myViewHolder, int i) {
    super.onBindViewHolder(myViewHolder, i);

    // Bind your viewHolder here
}
```

## License

Licensed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)
