package com.elegion.test.behancer.ui.profile;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.databinding.ObservableField;
import android.support.v4.widget.SwipeRefreshLayout;

import com.elegion.test.behancer.AppDelegate;
import com.elegion.test.behancer.data.Storage;
import com.elegion.test.behancer.data.api.BehanceApi;
import com.elegion.test.behancer.data.model.user.User;
import com.elegion.test.behancer.data.model.user.UserWithImage;

import javax.inject.Inject;

import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import toothpick.Toothpick;

public class ProfileViewModel extends ViewModel {

    private String mUsername;
    private Disposable mDisposable;

    @Inject
    Storage mStorage;

    @Inject
    BehanceApi mApi;

    private MutableLiveData<Boolean> mIsLoading = new MutableLiveData<>();
    private MutableLiveData<Boolean> mIsError = new MutableLiveData<>();
    private ObservableField<User> mProfile = new ObservableField<>();

    private SwipeRefreshLayout.OnRefreshListener mOnRefreshListener = this::loadProfile;

    private LiveData<UserWithImage> mUser;

    @Inject
    public ProfileViewModel( String username) {
        Toothpick.inject(this, Toothpick.openScope(AppDelegate.class));
        mIsError.postValue(false);
        mUsername = username;
        mUser = mStorage.getUserWithImageLiveByName(username);
        loadProfile();
    }

    private void loadProfile() {
        mDisposable = mApi.getUserInfo(mUsername)
                .doOnSuccess(response -> mIsError.postValue(false))
                .doOnSubscribe(disposable -> mIsLoading.postValue(true))
                .doFinally(() -> mIsLoading.postValue(false))
                .subscribeOn(Schedulers.io())
                .subscribe(
                        response -> { mStorage.insertUser(response);
                            mProfile.set(response.getUser());
                        },
                        throwable -> mIsError.postValue(mUser.getValue() == null));

    }

    public SwipeRefreshLayout.OnRefreshListener getOnRefreshListener() {
        return mOnRefreshListener;
    }

    @Override
    public void onCleared() {
        mStorage = null;
        if (mDisposable != null) {
            mDisposable.dispose();
        }
    }

    public MutableLiveData<Boolean> getIsLoading() {
        return mIsLoading;
    }

    public MutableLiveData<Boolean> getIsErrorVisible() {
        return mIsError;
    }

    public LiveData<UserWithImage> getUserWithImage() {
        return mUser;
    }

    public ObservableField<User> getProfile() {
        return mProfile;
    }

}
