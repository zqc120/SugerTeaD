package chao.sugertead.app;

import android.app.Application;
import android.content.Context;

import com.mob.MobSDK;

/**
 * Created by Chao on 2017/8/29.
 */

public class App extends Application{
    public static Context appContext;

    @Override
    public void onCreate() {
        super.onCreate();
        appContext=getApplicationContext();
        MobSDK.init(this);
    }
}
