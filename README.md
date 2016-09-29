[![Platform](http://img.shields.io/badge/platform-android-brightgreen.svg?style=flat)](http://developer.android.com/index.html)
[![License](https://img.shields.io/badge/license-Apache%202-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)

RecyclerViewMergeAdapter
===

A merge adapter for the RecyclerView, a continuation on this gist: https://gist.github.com/athornz/008edacd1d3b2f1e1836

The purpose of this merge adapter is to let you use multiple adapters with a RecyclerView instead of only one. 

## Download
Gradle (jCenter)
```
dependencies {
    compile 'me.mvdw.recyclerviewmergeadapter:recyclerviewmergeadapter:2.0.0'
}
```

## Usage

Add subadapters to the merge adapter and then set it on the RecyclerView. It's also possible to add individual views or a list of views to the merge adapter, using `addView(View... view)` or `addView(List<View> viewList)`

```
RecyclerView myRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);

// Create a new merge adapter.
RecyclerViewMergeAdapter mergeAdapter = new RecyclerViewMergeAdapter();

// Add any number of subadapters to the merge adapter.
RecyclerView.Adapter subAdapter1 = new RecyclerView.Adapter();
RecyclerView.Adapter subAdapter2 = new RecyclerView.Adapter();

mergeAdapter.addAdapter(subAdapter1);
mergeAdapter.addAdapter(subAdapter2);

// Set the merge adapter on the RecyclerView.
myRecyclerView.setAdapter(mergeAdapter);
}
```

##### RecyclerViewMergeAdapter methods

- `addAdapter(RecyclerView.Adapter adapter)`
- `addAdapter(int index, RecyclerView.Adapter adapter)`
- `removeAdapter(RecyclerView.Adapter adapter)`
- `removeAdapter(int index)`
- `addView(View... view)`
- `addViews(List<View> views)`
- `getSubAdapterCount()`
- `getSubAdapter(int index)`
- `clearAdapters()`


## License

Licensed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)

## Development

We have a small test project called [RecyclerViewMergeAdapterTest](https://github.com/ronaldw/RecyclerViewMergeAdapterTest) where you can find an example of how to import and use the adapter. If you find any bugs or issues, please file a report with the tracker.
