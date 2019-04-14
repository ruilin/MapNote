package com.muyu.mapnote.app.okayapi;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.muyu.mapnote.app.okayapi.been.OkCommentItem;
import com.muyu.mapnote.app.okayapi.callback.CommentCallback;
import com.muyu.mapnote.app.okayapi.callback.CommonCallback;
import com.muyu.mapnote.app.okayapi.utils.SignUtils;
import com.muyu.minimalism.utils.Logs;
import com.muyu.minimalism.utils.StringUtils;

import java.util.ArrayList;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * 评论
 */
public class OkComment extends OkObject {
    String targetId;
    String content;

    public OkComment(String targetId, String content) {
        this.targetId = targetId;
        this.content = content;
    }

    public void postInBackground(CommonCallback callback) {
        if (!OkayApi.get().isLogined()) {
            callback.onFail(new OkException("登录后才可以操作", OkException.CODE_NOT_LOGIN));
            return;
        }
        postCommonRequest(callback, new UrlCallback() {
            @Override
            public String getUrl() {
                String apiKey = "App.Market_Message.Post";

                StringBuffer sb = new StringBuffer();
                sb.append(OkayApi.get().getHost());
                sb.append("/?s=" + apiKey);
                sb.append("&app_key=" + OkayApi.get().getAppKey());
                sb.append("&uuid=" + OkayApi.get().getCurrentUser().getUuid());
                sb.append("&token=" + OkayApi.get().getCurrentUser().getToken());
                content = content.replace("\n", "<br/>");
                sb.append("&message_content=" + content);
                sb.append("&message_nickname=" + OkayApi.get().getCurrentUser().getNickname());
                sb.append("&message_key=" + targetId);
                String head = OkayApi.get().getCurrentUser().getHeadimg();
                String json = "";
                if (!StringUtils.isEmpty(head)) {
                    JsonObject obj = new JsonObject();
                    obj.addProperty("headimg", head);
                    json = new Gson().toJson(obj);
                    sb.append("&more_data=" + json);
                }

                SortedMap<String, String> map = new TreeMap<>();
                map.put("s", apiKey);
                map.put("app_key", OkayApi.get().getAppKey());
                map.put("uuid", OkayApi.get().getCurrentUser().getUuid());
                map.put("token", OkayApi.get().getCurrentUser().getToken());
                map.put("message_content", content);
                map.put("message_nickname", OkayApi.get().getCurrentUser().getNickname());
                map.put("message_key", targetId);
                if (!StringUtils.isEmpty(head)) {
                    map.put("more_data", json);
                }

                String sign = SignUtils.getSign(map);
                sb.append("&sign=" + sign);
                return sb.toString();
            }
        });
    }

    public static void requestInBackground(String targetId, CommentCallback callback) {
        new OkObject().postCommonRequest(
                new CommonCallback() {
                     @Override
                     public void onSuccess(String result) {
                         JsonObject jsonData = new JsonParser().parse(result).getAsJsonObject();
                         String errCode = jsonData.get("err_code").getAsString();
                         if (errCode.equals("0")) {
                             ArrayList<OkCommentItem> list = new ArrayList<>();
                             JsonArray array = jsonData.get("items").getAsJsonArray();
                             Gson gson = new Gson();
                             for(JsonElement obj : array){
                                 OkCommentItem item = gson.fromJson( obj , OkCommentItem.class);
                                 if (!StringUtils.isEmpty(item.message_content)) {
                                     item.message_content = item.message_content.replace("<br/>", "\n");
                                 }
                                 list.add(item);
                             }
                             callback.onSuccess(list);
                         } else {
                             String msg = jsonData.get("err_msg").getAsString();
                             Logs.e(msg);
                             OkException oe = new OkException(msg);
                             callback.onFail(oe);
                         }
                     }

                     @Override
                     public void onFail(OkException e) {

                     }
                 },
                new UrlCallback() {
                    @Override
                    public String getUrl() {
                        String apiKey = "App.Market_Message.Show";

                        StringBuffer sb = new StringBuffer();
                        sb.append(OkayApi.get().getHost());
                        sb.append("/?s=" + apiKey);
                        sb.append("&app_key=" + OkayApi.get().getAppKey());
//                        sb.append("&uuid=" + OkayApi.get().getCurrentUser().getUuid());
//                        sb.append("&token=" + OkayApi.get().getCurrentUser().getToken());
                        sb.append("&message_key=" + targetId);
                        sb.append("&more_select=headimg,message_contact");

                        SortedMap<String, String> map = new TreeMap<>();
                        map.put("s", apiKey);
                        map.put("app_key", OkayApi.get().getAppKey());
//                        map.put("uuid", OkayApi.get().getCurrentUser().getUuid());
//                        map.put("token", OkayApi.get().getCurrentUser().getToken());
                        map.put("message_key", targetId);
                        map.put("more_select", "headimg,message_contact");

                        String sign = SignUtils.getSign(map);
                        sb.append("&sign=" + sign);
                        return sb.toString();
                    }
                });
    }
}
