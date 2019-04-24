package com.muyu.mapnote.app.network.okayapi;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.muyu.mapnote.app.network.Network;
import com.muyu.mapnote.app.network.okayapi.callback.CommonCallback;
import com.muyu.mapnote.app.network.okayapi.callback.LoginCallback;
import com.muyu.mapnote.app.network.okayapi.callback.RegisterCallback;
import com.muyu.mapnote.app.network.okayapi.callback.UploadCallback;
import com.muyu.mapnote.app.network.okayapi.utils.SignUtils;
import com.muyu.minimalism.utils.MD5Utils;
import com.muyu.minimalism.utils.Logs;

import java.io.IOException;
import java.util.SortedMap;
import java.util.TreeMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OkUser extends OkObject {
    private String nickname = "未设置昵称";
    private String userName = "";
    private String password;
    private String uuid;
    private String token;
    private String headimg = "";
    private int sex = 0;

    public OkUser() {}

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setHeadimg(String headimg) {
        this.headimg = headimg;
    }

    public void setUsername(String username) {
        this.userName = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserName() {
        return userName;
    }

    public String getNickname() {
        return nickname;
    }

    public int getSex() {
        return sex;
    }

    public String getHeadimg() {
        return headimg;
    }

    public String getPassword() {
        return password;
    }

    public String getUuid() {
        return uuid;
    }

    public String getToken() {
        return token;
    }


    private String getRegisterUrl() {
        JsonObject object = new JsonObject();
        object.addProperty("nickname", nickname);
        object.addProperty("sex", sex);
        object.addProperty("head", headimg);
        String json = new Gson().toJson(object);

        SortedMap<String, String> map = new TreeMap<>();
        map.put("s", "App.User.Register");
        map.put("app_key", OkayApi.get().getAppKey());
        map.put("username", userName);
        map.put("password", MD5Utils.md5(password));
        map.put("ext_info", json);
        String sign = SignUtils.getSign(map);
        return OkayApi.get().getHost() + "?s=App.User.Register"
                + "&username=" + userName
                + "&password=" + MD5Utils.md5(password)
                + "&app_key=" + OkayApi.get().getAppKey()
                + "&ext_info=" + json
                + "&sign=" + sign;
    }

    private String getLoginUrl() {
        SortedMap<String, String> map = new TreeMap<>();
        map.put("s", "App.User.Login");
        map.put("app_key", OkayApi.get().getAppKey());
//        map.put("uuid", mUuid);
//        map.put("token", mToken);
        map.put("username", userName);
        map.put("password", MD5Utils.md5(password));
        String sign = SignUtils.getSign(map);
        return OkayApi.get().getHost() + "?s=App.User.Login"
                + "&username=" + userName
                + "&password=" + MD5Utils.md5(password)
                + "&app_key=" + OkayApi.get().getAppKey()
                + "&sign=" + sign;
    }

    private String getLoginInfoUrl() {
        SortedMap<String, String> map = new TreeMap<>();
        String api = "App.User.Profile";
        map.put("s", api);
        map.put("app_key", OkayApi.get().getAppKey());
        map.put("uuid", uuid);
        map.put("token", token);
        String sign = SignUtils.getSign(map);
        return OkayApi.get().getHost() + "?s=" + api
                + "&app_key=" + OkayApi.get().getAppKey()
                + "&uuid=" + uuid
                + "&token=" + token
                + "&sign=" + sign;
    }

    public void registerInBackground(String headImgPath, @NonNull RegisterCallback callback) {
        if (headImgPath != null) {
            new OkImage(headImgPath).upload(new UploadCallback() {
                @Override
                public void onSuccess(String url) {
                    headimg = url;
                    postRegister(callback);
                }

                @Override
                public void onFail(OkException e) {
                    OkException oe = new OkException(e.getMessage());
                    callback.done(oe);
                }
            });
        } else {
            postRegister(callback);
        }
    }

    private void postRegister(@NonNull RegisterCallback callback) {
        OkHttpClient client = Network.getClient();
        final Request req = new Request.Builder()
                .url(getRegisterUrl())
                .get()
                .build();
        Call call = client.newCall(req);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                OkException oe = new OkException(e.getMessage());
                callback.done(oe);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JsonObject jsonData = getResponseJson(response);
                    if (jsonData != null) {
                        String errCode = jsonData.get("err_code").getAsString();
                        if (errCode.equals("0")) {
                            uuid = jsonData.get("uuid").getAsString();
                            callback.done(null);
                        } else {
                            String msg = jsonData.get("err_msg").getAsString();
                            OkException oe = new OkException(msg);
                            callback.done(oe);
                        }
                    } else {
                        OkException oe = new OkException("连接失败");
                        callback.done(oe);
                    }
                } catch (OkException e) {
                    callback.done(e);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void loginInBackground(LoginCallback callback) {
        OkHttpClient client = Network.getClient();
        final Request req = new Request.Builder()
                .url(getLoginUrl())
                .get()
                .build();
        Call call = client.newCall(req);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                OkException oe = new OkException(e.getMessage());
                callback.done(null, oe);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JsonObject jsonData = getResponseJson(response);
                    if (jsonData != null) {
                        String errCode = jsonData.get("err_code").getAsString();
                        if (errCode.equals("0")) {
                            String uuid = jsonData.get("uuid").getAsString();
                            String token = jsonData.get("token").getAsString();
                            setUuid(uuid);
                            setToken(token);

                            postCommonRequest(new CommonCallback() {
                                @Override
                                public void onSuccess(String json) {
                                    JsonParser parser = new JsonParser();
                                    JsonObject data = parser.parse(json).getAsJsonObject();
                                    data = data.get("info").getAsJsonObject();
                                    if (data.get("ext_info") != null) {
                                        data = data.get("ext_info").getAsJsonObject();
                                        if (data.get("nickname") != null) {
                                            nickname = data.get("nickname").getAsString();
                                        }
                                        if (data.get("sex") != null) {
                                            sex = data.get("sex").getAsInt();
                                        }
                                        if (data.get("head") != null) {
                                            headimg = data.get("head").getAsString();
                                        }
                                    }
                                    OkayApi.get().setUser(OkUser.this);
                                    callback.done(OkUser.this, null);
                                }

                                @Override
                                public void onFail(OkException e) {
                                    String msg = jsonData.get("err_msg").getAsString();
                                    Logs.e(msg);
                                    callback.done(OkUser.this, e);
                                }
                            }, new UrlCallback() {
                                @Override
                                public String getUrl() {
                                    return getLoginInfoUrl();
                                }
                            });
                        } else {
                            String msg = jsonData.get("err_msg").getAsString();
                            Logs.e(msg);
                            OkException oe = new OkException("用户名或密码错误");
                            callback.done(null, oe);
                        }
                    } else {
                        OkException oe = new OkException("连接失败");
                        callback.done(null, oe);
                    }
                } catch (OkException e) {
                    callback.done(null, e);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
