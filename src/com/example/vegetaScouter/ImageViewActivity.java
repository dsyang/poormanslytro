package com.example.vegetaScouter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created with IntelliJ IDEA.
 * User: dsyang
 * Date: 7/28/13
 * Time: 12:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class ImageViewActivity extends SingleFragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    protected Fragment createFragment() {
        return ImageViewFragment.newInstance();
    }
}
