package chao.sugertead.ui.main;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.map.geolocation.TencentLocationRequest;

import chao.sugertead.R;
import chao.sugertead.constant.Constant;
import chao.sugertead.httputil.HttpHelper;
import chao.sugertead.model.MyQQLocationManager;
import chao.sugertead.model.StatusBarManager;
import chao.sugertead.model.bean.HomeShopBean;
import chao.sugertead.ui.login.ChooseLoginActivity;
import chao.sugertead.ui.mine.MineFragment;
import chao.sugertead.utils.MeasureUtils;
import chao.sugertead.utils.SPUtils;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;

public class MainActivity extends FragmentActivity implements MainContract.MainView{
    public static final int RB_MINE = 3;
    private static final String TAG = "MainActivity";
    private MainPresenter mainPresenter;
    private TencentLocationManager locationManager;
    private TencentLocationRequest request;
    private TextView tv_location;
    private EditText search;
    private FrameLayout fl_msg;
    public int statusBarHeight;
    private TextView tv;
    //存储点击的那个radiobutton
    private int savedPage = 0;
    private MineFragment mineFragment;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        setContentView(R.layout.activity_main);


        tv = (TextView) findViewById(R.id.tv);
        setPresenter(this);
        mainPresenter.mainView.setActionBar(this);
        mainPresenter.mainView.setStatusBar(this);
        HttpHelper.getInstance().getShopList("104cca5fad614b53e494e5198f4cdb47", "116.125584,40.232219");
        EventBus.getDefault().register(this);
        test();

    }

    private void test() {
        RadioGroup rg = (RadioGroup) findViewById(R.id.rg);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {


            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if ((checkedId == R.id.rb_mine)) {
                    switch2MineFragment();
                }


            }
        });
    }

    private void setPresenter(MainContract.MainView mainView) {
        mainPresenter = new MainPresenter(mainView);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //（处理登录逻辑）切换到之前点击的那个页面
        if (savedPage == RB_MINE ) {
            switch2MineFragment();
        }
    }

    public void switch2MineFragment() {
        //判断是不是登录状态
        if ((Boolean) SPUtils.getInstance
                (MainActivity.this, Constant.SPNAME)
                .getSp("islogin", Boolean.class)) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            if (mineFragment==null){
                mineFragment = new MineFragment();
                ft.add(R.id.fl_content, mineFragment, "mine");
                ft.commit();
            }
        } else {
            startActivity(new Intent(MainActivity.this, ChooseLoginActivity.class));
            savedPage = RB_MINE;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //可在此继续其他操作。
        if (grantResults[0] != 0 && grantResults[1] != 0 && grantResults[2] != 0) {
            Toast.makeText(this, "请打开定位权限", Toast.LENGTH_SHORT).show();
        } else if (grantResults[0] == 0 && grantResults[1] == 0 && grantResults[2] == 0) {
            int error = locationManager.requestLocationUpdates(request, new MyQQLocationManager.QQLocationListener());
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    @Override
    public void setActionBar(Activity act) {
        ActionBar actionBar = getActionBar();
        //让actionbar使用自定义的布局样式
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.head);
        View view = LayoutInflater.from(this).inflate(R.layout.head, null, false);
        tv_location = (TextView) view.findViewById(R.id.tv_location);
        search = (EditText) view.findViewById(R.id.search);
        fl_msg = (FrameLayout) view.findViewById(R.id.fl_msg);


//       //1.在代码中设置背景可用
//        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void showToast22(MyEvent event) {
        Toast.makeText(this, event.bean.getCode() + "hahaha", Toast.LENGTH_SHORT).show();

    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void setStatusBar(Activity act) {


        StatusBarManager.setTranStatusBar(act);
        statusBarHeight = MeasureUtils.getStatusBarHeight(this);

    }

    @Override
    public void setLocationText(String address) {
        if (address == null && "".equals(address)) {
            tv_location.setText("定位中...");
        } else {
            tv_location.setText(address);
        }
    }

    public static class MyEvent {
        HomeShopBean bean;

        public MyEvent(HomeShopBean bean) {
            this.bean = bean;
        }

        public void setBean(HomeShopBean bean) {
            this.bean = bean;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
