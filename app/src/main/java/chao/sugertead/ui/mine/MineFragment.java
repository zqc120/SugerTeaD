package chao.sugertead.ui.mine;

import android.app.ActionBar;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import chao.sugertead.R;
import chao.sugertead.base.BaseFragment;
import chao.sugertead.constant.Constant;
import chao.sugertead.model.bean.LoginInfo;
import chao.sugertead.model.bean.UserInfoBean;
import chao.sugertead.ui.login.LoginActivity;
import chao.sugertead.ui.main.MainActivity;
import chao.sugertead.utils.SPUtils;
import chao.sugertead.widget.MineListItemView;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;

/**
 * Created by guaju on 2017/8/22.
 */
public class MineFragment extends BaseFragment implements View.OnClickListener, MineContract.MineView {
    private FrameLayout fl_icon;
    private MainActivity activity;
    private MineContract.MinePresenter presenter;
    private View v;
    private ImageView iv_avatar;
    private TextView tv_name;
    private MineListItemView item_dingdan;
    private MineListItemView item_huiyuanka;
    private MineListItemView item_kefu;
    private MineListItemView item_ruzhu;
    private MineListItemView item_shoucang;
    private MineListItemView item_youhuiquan;

    @Override
    protected void initData() {
        //本生命周期在view创建完成之后调用
        presenter = new MinePresenterImpl(this);
        presenter.hide();
        //做回显操作，从缓存的数据中拿东西展示数据
        presenter.readSaveLoginInfo(SPUtils.getInstance(getActivity(), Constant.SPNAME));
    }

    @Override
    protected View initView(LayoutInflater inflater) {
        EventBus.getDefault().register(this);
        v = inflater.inflate(R.layout.fragment_mine, null, false);
        activity = (MainActivity) getActivity();
        fl_icon = (FrameLayout) v.findViewById(R.id.fl_icon);
        iv_avatar = (ImageView) v.findViewById(R.id.iv_avatar);
        tv_name = (TextView) v.findViewById(R.id.tv_name);

        item_dingdan = (MineListItemView) v.findViewById(R.id.item_dingdan);
        item_huiyuanka = (MineListItemView) v.findViewById(R.id.item_huiyuanka);
        item_kefu = (MineListItemView) v.findViewById(R.id.item_kefu);
        item_ruzhu = (MineListItemView) v.findViewById(R.id.item_ruzhu);
        item_shoucang = (MineListItemView) v.findViewById(R.id.item_shoucang);
        item_youhuiquan = (MineListItemView) v.findViewById(R.id.item_youhuiquan);

        fl_icon.setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fl_icon:
                if ("未登录".equals("未登录")) {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }
                break;
            default:
                break;
        }

    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void loginEvent(LoginInfo bean) {
        UserInfoBean user = bean.getObj().getUser();
        showLoginView(user);
//        presenter.saveLoginStatus(bean);

    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void loginEvent(String info) {
    }


    //隐藏actionbar的逻辑
    @Override
    public void hideActionBar() {
        ActionBar actionBar = activity.getActionBar();
        actionBar.hide();
        int statusBarHeight = activity.statusBarHeight;
        v.setPadding(0, statusBarHeight, 0, 0);
    }

    @Override
    public void showLoginView(UserInfoBean user) {
        String avatar = user.getAvatar();//头像
        String nickname = user.getNickname();//昵称
        String phone = user.getPhone();//电话号码
        String sex = user.getSex();//拿到性别
        String xiadanshu = user.getXiadanshu();//订单数量
        String shoucangshanghu = user.getShoucangshanghu();//收藏店铺
        String youhuiquan = user.getYouhuiquan();//优惠券
        String huiyuanka = user.getHuiyuanka();//会员卡
        //头像
        if (!TextUtils.isEmpty(avatar)) {
            String url = Constant.IMAGE_AVATAR + avatar;
            //TODO 切圆
            //加载图片
            Glide.with(this).load(url).into(iv_avatar);
        }
        if (!TextUtils.isEmpty(nickname)) {
            tv_name.setText(nickname);

        }
        if (!TextUtils.isEmpty(phone)) {
            if (TextUtils.isEmpty(nickname)) {
                tv_name.setText(phone);
            }

        }
        if (!TextUtils.isEmpty(sex)) {
            //TODO  跳转

        }
        if (!TextUtils.isEmpty(xiadanshu)) {
            item_dingdan.setNum(xiadanshu);

        }
        if (!TextUtils.isEmpty(shoucangshanghu)) {
            item_shoucang.setNum(shoucangshanghu);

        }
        if (!TextUtils.isEmpty(youhuiquan)) {
            item_youhuiquan.setNum(youhuiquan);

        }
        if (!TextUtils.isEmpty(huiyuanka)) {
            item_huiyuanka.setNum(huiyuanka);
        }

    }

    @Override
    public void showLoginError() {
        Toast.makeText(activity, "登录失败~~~", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
