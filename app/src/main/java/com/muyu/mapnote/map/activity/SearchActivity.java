package com.muyu.mapnote.map.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.SearchView;

import com.muyu.mapnote.R;
import com.muyu.mapnote.app.MapBaseActivity;
import com.muyu.mapnote.app.network.Umeng;
import com.muyu.mapnote.map.MapOptEvent;
import com.muyu.mapnote.map.map.poi.Poi;
import com.muyu.mapnote.map.map.poi.SearchHelper;
import com.muyu.minimalism.view.recyclerview.CommonRecyclerAdapter;
import com.muyu.minimalism.view.recyclerview.CommonViewHolder;
import com.muyu.minimalism.view.recyclerview.VerticalRecyclerView;
import com.muyu.minimalism.view.tag.Tag;
import com.muyu.minimalism.view.tag.TagListView;
import com.muyu.minimalism.view.tag.TagView;
import com.tencent.lbssearch.object.result.SearchResultObject;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends MapBaseActivity {
    private static final String PARAM_KEY_KEYWORKD = "keyword";
    private List<SearchResultObject.SearchResultData> dataList = new ArrayList<>();
    private SearchView searchView;
    private VerticalRecyclerView listView;
    private CommonRecyclerAdapter adapter;
    private TagListView tagListView;

    CommonViewHolder.onItemCommonClickListener resultClicked = new CommonViewHolder.onItemCommonClickListener() {
        @Override
        public void onItemClickListener(int position) {
            MapOptEvent.showSearchResult(searchView.getQuery().toString(), Poi.toPoi(dataList.get(position)));
            finish();
        }

        @Override
        public void onItemLongClickListener(int position) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setStatusBarColor(Color.WHITE);

        tagListView = findViewById(R.id.search_tag);
        ArrayList<Tag> tagList = new ArrayList<>();
        tagList.add(new Tag("景点"));
        tagList.add(new Tag("酒店"));
        tagList.add(new Tag("美食"));
        tagList.add(new Tag("酒吧"));
        tagList.add(new Tag("超市"));
        tagList.add(new Tag("银行"));
        tagListView.setTags(tagList);
        tagListView.setOnTagClickListener(new TagListView.OnTagClickListener() {
            @Override
            public void onTagClick(TagView tagView, Tag tag) {
                searchView.setQuery(tag.getTitle(), true);
            }
        });

        searchView = findViewById(R.id.search_sv);
        listView = findViewById(R.id.search_list);
//        listView.addItemDecoration(new RecyclerViewDivider(this, LinearLayoutManager.VERTICAL));

        adapter = new SearchResultAdapter(this, dataList, resultClicked);
        listView.setAdapter(adapter);

        // 设置搜索文本监听
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // 当点击搜索按钮时触发该方法
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            // 当搜索内容改变时触发该方法
            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() > 1) {
                    Umeng.record("Search", newText);
                    SearchHelper.searchPoiByKeyWord(SearchActivity.this, newText, new SearchHelper.OnSearchKeyWordCallback() {
                        @Override
                        public void onSearchKeyWordCallback(List<SearchResultObject.SearchResultData> result) {
                            if (result.size() > 0)
                                adapter.setDataList(result);
                            else
                                adapter.clear();
                        }
                    });
                } else {
                    adapter.clear();
                }
                tagListView.setVisibility(newText.length() == 0 ? View.VISIBLE : View.GONE);
                return false;
            }
        });

        Intent intent = getIntent();
        String keyword = intent.getStringExtra(PARAM_KEY_KEYWORKD);
        if (!TextUtils.isEmpty(keyword) && !keyword.equals(searchView.getQueryHint().toString())) {
            searchView.setQuery(keyword, true);
        }
    }


    public class SearchResultAdapter extends CommonRecyclerAdapter<SearchResultObject.SearchResultData> {

        private CommonViewHolder.onItemCommonClickListener commonClickListener;

        public SearchResultAdapter(Context context, List<SearchResultObject.SearchResultData> dataList) {
            super(context, dataList, R.layout.item_search_result);
        }

        public SearchResultAdapter(Context context, List<SearchResultObject.SearchResultData> dataList, CommonViewHolder.onItemCommonClickListener commonClickListener) {
            super(context, dataList, R.layout.item_search_result);
            this.commonClickListener = commonClickListener;
        }

        @Override
        public void bindData(CommonViewHolder holder, SearchResultObject.SearchResultData data, int position) {
            holder.setText(R.id.item_search_tv, data.title)
                    .setCommonClickListener(commonClickListener);
        }

    }

    @Override
    public void onPause() {
        overridePendingTransition(0,0);
        super.onPause();
    }

    public static void startSearch(Activity activity, String keyword) {
        Intent intent = new Intent(activity, SearchActivity.class);
        intent.putExtra(PARAM_KEY_KEYWORKD, keyword);
        activity.startActivity(intent);
        activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        if (dataList.size() == 0) {
            MapOptEvent.showSearchResult(null, null);
        }
    }
}
