package chao.sugertead.ui.main;

import android.app.Activity;

import chao.sugertead.base.BasePresenter;


/**
 * Created by guaju on 2017/8/16.
 */

public interface MainContract {
     interface MainView {
        //定义逻辑
        void setActionBar(Activity act);
        void setStatusBar(Activity act);
        void setLocationText(String address);
    }
     interface Presenter extends BasePresenter {
        void setLocation();
     }
}
