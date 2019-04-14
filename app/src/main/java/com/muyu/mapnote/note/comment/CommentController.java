package com.muyu.mapnote.note.comment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.muyu.mapnote.R;
import com.muyu.mapnote.app.ImageLoader;
import com.muyu.mapnote.app.okayapi.OkComment;
import com.muyu.mapnote.app.okayapi.OkException;
import com.muyu.mapnote.app.okayapi.been.OkMomentItem;
import com.muyu.mapnote.app.okayapi.OkayApi;
import com.muyu.mapnote.app.okayapi.callback.CommentCallback;
import com.muyu.mapnote.app.okayapi.callback.CommonCallback;
import com.muyu.mapnote.app.okayapi.been.OkCommentItem;
import com.muyu.mapnote.user.activity.LoginActivity;
import com.muyu.minimalism.framework.app.BaseActivity;
import com.muyu.minimalism.framework.controller.ActivityController;
import com.muyu.minimalism.utils.StringUtils;
import com.muyu.minimalism.utils.SysUtils;
import com.muyu.minimalism.view.Loading;
import com.muyu.minimalism.view.Msg;
import com.muyu.minimalism.view.recyclerview.CommonRecyclerAdapter;
import com.muyu.minimalism.view.recyclerview.CommonViewHolder;
import com.muyu.minimalism.view.recyclerview.VerticalRecyclerView;

import java.util.ArrayList;

public class CommentController extends ActivityController {
    private Loading loading;
    private OkMomentItem poi;
    VerticalRecyclerView listView;
    CommentViewModel mViewModel;
    private View emptyView;
    TextView countView;

    public CommentController(OkMomentItem poi) {
        this.poi = poi;
    }

    @Override
    public void onCreate(BaseActivity activity) {
        super.onCreate(activity);
        View layout = activity.getContentView();
        loading = new Loading(activity);
        emptyView = layout.findViewById(R.id.detail_comment_empty);
        listView = layout.findViewById(R.id.detail_comment_list);
        countView = layout.findViewById(R.id.detail_comment_count);
        EditText contentEt = layout.findViewById(R.id.detail_comment_et);
        Button bt = layout.findViewById(R.id.detail_comment_bt);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = contentEt.getText().toString();
                if (StringUtils.isEmpty(content.trim())) {
                    Msg.show("请输入评论");
                    return;
                }
                if (!OkayApi.get().isLogined()) {
                    getActivity().startActivity(LoginActivity.class);
                    return;
                }
                new OkComment("moment_" + poi.id, content).postInBackground(new CommonCallback() {
                    @Override
                    public void onSuccess(String result) {
                        update();
                        SysUtils.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loading.dismiss();
                                Msg.show("发布成功");
                            }
                        });
                    }

                    @Override
                    public void onFail(OkException e) {
                        SysUtils.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loading.dismiss();
                                Msg.show(e.getMessage());
                            }
                        });
                        if (e.getCode() == OkException.CODE_NOT_LOGIN) {
                            getActivity().startActivity(LoginActivity.class);
                            return;
                        }
                    }
                });
                contentEt.setText("");
                loading.show("发布中…");
            }
        });
        initData();
        update();
    }

    public void initData() {

        ArrayList<OkCommentItem> list = new ArrayList<>();
        CommonRecyclerAdapter<OkCommentItem> adapter = new CommonRecyclerAdapter<OkCommentItem>(getActivity(), list, R.layout.item_detail_comment) {
            @Override
            public void bindData(CommonViewHolder holder, OkCommentItem data, int position) {
                View view = holder.itemView;
                ImageLoader.loadHead(CommentController.this.getActivity(), data.headimg, view.findViewById(R.id.detail_comment_head));
                TextView textView = view.findViewById(R.id.detail_comment_content);
                textView.setText(data.message_content);
                textView = view.findViewById(R.id.detail_comment_username);
                textView.setText(data.message_nickname);
            }
        };

        mViewModel = ViewModelProviders.of(getActivity()).get(CommentViewModel.class);
        mViewModel.getComment().observe(getActivity(), new Observer<ArrayList<OkCommentItem>>() {
            @Override
            public void onChanged(@Nullable ArrayList<OkCommentItem> items) {
                adapter.setDataList(items);
                emptyView.setVisibility(items.size() > 0 ? View.GONE : View.VISIBLE);
                countView.setText(" " + items.size());
            }
        });


        listView.setAdapterWithDivider(adapter);
    }

    public void update() {
        OkComment.requestInBackground("moment_" + poi.id, new CommentCallback() {
            @Override
            public void onSuccess(ArrayList<OkCommentItem> list) {
                mViewModel.getComment().postValue(list);
            }

            @Override
            public void onFail(OkException e) {
                Msg.show(e.getMessage());
            }
        });
    }
}
