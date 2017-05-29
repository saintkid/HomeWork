package com.example.aqr.homework.util;

import com.example.aqr.homework.domain.User;

import cn.bmob.v3.exception.BmobException;

/**
 * Created by 北 on 2017/5/29.
 */

public interface IUserListener {
    void onMsgSendSuccess();
    void onMsgSendFailure();
    void onLoginSuccess();
    void onLoginFailure();
    void onSignUpSuccess(User user);
    void onSignUpFailure(BmobException e);
}
