package chao.sugertead.ui.mine;


import chao.sugertead.model.bean.LoginInfo;
import chao.sugertead.model.bean.UserInfoBean;
import chao.sugertead.utils.SPUtils;

/**
 * Created by guaju on 2017/8/25.
 */

public interface MineContract {

    public interface MineView {
        void  hideActionBar();
        void  showLoginView(UserInfoBean user);
        void  showLoginError();
    }
    interface MinePresenter{
        //只需要操作逻辑事
        void hide();
    void readSaveLoginInfo(SPUtils spUtils);
        void saveLoginStatus(LoginInfo bean);

    }

}
