package chao.sugertead.httputil;

import android.util.Log;

import chao.sugertead.api.API;
import chao.sugertead.app.App;
import chao.sugertead.constant.BSConstant;
import chao.sugertead.constant.Constant;
import chao.sugertead.model.bean.BaseBean;
import chao.sugertead.model.bean.HomeShopBean;
import chao.sugertead.model.bean.LoginBean;
import chao.sugertead.model.bean.LoginInfo;
import chao.sugertead.ui.login.LoginActivity;
import chao.sugertead.ui.main.MainActivity;
import chao.sugertead.utils.AppUtil;
import chao.sugertead.utils.SPUtils;
import de.greenrobot.event.EventBus;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static de.greenrobot.event.EventBus.TAG;

/**
 * Created by guaju on 2017/8/21.
 */

public class HttpHelper {
    private static HttpHelper helper=new HttpHelper();
    private Retrofit retrofit;
    private  final API api;

    private HttpHelper(){
        retrofit=new Retrofit.Builder()
                .baseUrl(Constant.ROOT+Constant.URL_VERSION)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        api = retrofit.create(API.class);
    }
    public static  HttpHelper getInstance(){
        return    helper;
    }

    /*
     访问商户列表
      */
    public void getShopList(String openId,String location){
        Observable<HomeShopBean> jingxuanshanghu = api.homeShop("jingxuanshanghu", openId, 0, location, 1);
        jingxuanshanghu.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<HomeShopBean>() {
                    @Override
                    public void call(HomeShopBean homeShopBean) {
                        Log.e(TAG, "call: "+homeShopBean.getCode() );
                        EventBus.getDefault().post(new MainActivity.MyEvent(homeShopBean));
                    }
                });

    }

    /*
    发送验证码
     */
    public void getVerifyCode(String phone,long time,String ip,String mac){
        Observable<BaseBean> verifycode = api.getVerifycode(BSConstant.VERIFY_CODE, phone, ip, mac, time, AppUtil.encryptSign((time-444)+"","",phone,ip), "1");
        verifycode.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<BaseBean>() {
                    @Override
                    public void call(BaseBean baseBean) {
                        Log.e(TAG, "call: "+baseBean.getCode());
                    }
                });
    }
    /*
   登录
    */
    public void login(final String phone, final String code){
        Observable<BaseBean<LoginBean>> baseBean = api.login(BSConstant.LOGIN, phone, code,null, null, null,null,null);
        baseBean.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<BaseBean<LoginBean>>() {
                    @Override
                    public void call(BaseBean<LoginBean> baseBean) {
                        if (baseBean.getCode()==200){
                            LoginBean obj = baseBean.getObj();
                            LoginInfo loginInfo = new LoginInfo(phone, code, obj);

//                           UserInfoBean user = obj.getUser();
//                           user.getAvatar();//头像
//                           user.getNickname();//昵称
//                           user.getPhone();//电话号码
//                           user.getSex();//拿到性别
//                           user.getXiadanshu();//订单数量
//                           user.getShoucangshanghu();//收藏店铺
//                           user.getYouhuiquan();//优惠券
//                           user.getHuiyuanka();//会员卡
                            EventBus.getDefault().post(loginInfo);
                            SPUtils instance = SPUtils.getInstance(App.appContext, Constant.SPNAME);
                            instance.putSp("phonenum",phone);
                            instance.putSp("logincode",code);
                            instance.putSp("islogin",true);

                            EventBus.getDefault().post(new LoginActivity.FinishEvent());


                        }
                        else{
                            EventBus.getDefault().post("ERROR");

                        }

                    }
                });
    }
}
