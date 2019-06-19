package com.elegion.test.behancer.ui.profile;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import com.elegion.test.behancer.AppDelegate;
import com.elegion.test.behancer.data.Storage;
import com.elegion.test.behancer.data.api.BehanceApi;
import com.elegion.test.behancer.data.model.user.User;
import com.elegion.test.behancer.data.model.user.UserWithImage;
import com.elegion.test.behancer.utils.ApiUtils;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import toothpick.Toothpick;

public class ProfileViewModel {

    private String mUsername;
    private Disposable mDisposable;

    @Inject
    Storage mStorage;

    @Inject
    BehanceApi mApi;

    private ObservableBoolean mIsLoading = new ObservableBoolean(false);
    private ObservableBoolean mIsErrorVisible = new ObservableBoolean(false);
    private ObservableField<User> mProfile = new ObservableField<>();
    private SwipeRefreshLayout.OnRefreshListener mOnRefreshListener = this::loadProfile;
    private View.OnClickListener mViewClickListener;
    private MutableLiveData<Boolean> mIsError = new MutableLiveData<>();

    private LiveData<UserWithImage> mUser;

    public ProfileViewModel( String username) {
        Toothpick.inject(this, Toothpick.openScope(AppDelegate.class));
        mIsError.postValue(false);
        mUsername = username;
        mUser = mStorage.getUserWithImageLiveByName(username);
        getProfile();
    }


    public void setmUsername(String username) {
        this.mUsername = username;
    }

    private void loadProfile() {
        mDisposable = ApiUtils.getApiService().getUserInfo(mUsername)
                .subscribeOn(Schedulers.io())
                .doOnSuccess(response -> mStorage.insertUser(response))
                .onErrorReturn(throwable ->
                        ApiUtils.NETWORK_EXCEPTIONS.contains(throwable.getClass()) ?
                                mStorage.getUser(mUsername) :
                                null)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> mIsLoading.set(true))
                .doFinally(() -> mIsLoading.set(false))
                .subscribe(
                        response -> {
                            mIsErrorVisible.set(false);
                            mProfile.set(response.getUser());
                        },
                        throwable -> mIsErrorVisible.set(true));
    }

    public SwipeRefreshLayout.OnRefreshListener getOnRefreshListener() {
        return mOnRefreshListener;
    }

    public void dispatchDetach() {
        mStorage = null;
        if (mDisposable != null) {
            mDisposable.dispose();
        }
    }

    public ObservableBoolean getIsLoading() {
        return mIsLoading;
    }

    public ObservableBoolean getIsErrorVisible() {
        return mIsErrorVisible;
    }

    public ObservableField<User> getProfile() {
        return mProfile;
    }

    public View.OnClickListener getViewClickListener() {
        return mViewClickListener;
    }

    public void setViewClickListener(View.OnClickListener mViewClickListener) {
        this.mViewClickListener = mViewClickListener;
    }
}
