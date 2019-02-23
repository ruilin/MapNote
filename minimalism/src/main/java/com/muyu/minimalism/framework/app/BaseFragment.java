package com.muyu.minimalism.framework.app;

import android.support.v4.app.Fragment;

public class BaseFragment extends Fragment {

    public BaseActivity baseActivity() {
        return (BaseActivity) getActivity();
    }
}
