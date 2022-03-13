package com.szip.jswitch.Activity.userInfo;

import android.app.Dialog;
import android.net.Uri;

import com.szip.jswitch.Model.UserInfo;
import com.szip.jswitch.View.CharacterPickerWindow;

import java.io.File;

public interface IUserInfoPresenter {
    void getSex(CharacterPickerWindow window, UserInfo userInfo);
    void getHeight(CharacterPickerWindow window, UserInfo userInfo);
    void getWeight(CharacterPickerWindow window, UserInfo userInfo);
    void getBirthday(CharacterPickerWindow window, UserInfo userInfo);
    void saveUserInfo(UserInfo userInfo);
    void selectPhoto(Dialog dialog, int y);
    void cropPhoto(Uri uri);
    void updownPhoto(File file);

    void setViewDestory();
}
