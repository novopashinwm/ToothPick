package com.elegion.test.behancer.ui.profile;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import com.elegion.test.behancer.data.Storage;
import com.elegion.test.behancer.data.model.user.User;
import com.elegion.test.behancer.utils.ApiUtils;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ProfileViewModel {

    private String mUsername;
    private Disposable mDisposable;
    private Storage mStorage;

    private ObservableBoolean mIsLoading = new ObservableBoolean(false);
    private ObservableBoolean mIsErrorVisible = new ObservableBoolean(false);
    private ObservableField<User> mProfile = new ObservableField<>();
    private SwipeRefreshLayout.OnRefreshListener mOnRefreshListener = this::loadProfile;
    private View.OnClickListener mViewClickListener;


    public ProfileViewModel(Storage mStorage) {
        this.mStorage = mStorage;
    }

    public void setmUsername(String username) {
        this.mUsername = username;
    }

    public void loadProfile() {
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
