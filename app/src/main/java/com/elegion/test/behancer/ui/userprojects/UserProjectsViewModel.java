package com.elegion.test.behancer.ui.userprojects;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.PagedList;
import android.support.v4.widget.SwipeRefreshLayout;

import com.elegion.test.behancer.data.Storage;
import com.elegion.test.behancer.data.model.project.ProjectResponse;
import com.elegion.test.behancer.data.model.project.RichProject;
import com.elegion.test.behancer.utils.ApiUtils;

import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class UserProjectsViewModel extends ViewModel {
    private Disposable mDisposable;
    private Storage mStorage;

    private MutableLiveData<Boolean> mIsLoading = new MutableLiveData<>();
    private MutableLiveData<Boolean> mIsErrorVisible = new MutableLiveData<>();
    private LiveData<PagedList<RichProject>> mProjects;
    private MutableLiveData<String> mUser = new MutableLiveData<>();
    private SwipeRefreshLayout.OnRefreshListener mOnRefreshListener = this::updateProjects;

    public UserProjectsViewModel(Storage storage, String user) {
        mStorage = storage;
        mProjects = mStorage.getUserProjectsPaged(user);
        mUser.postValue(user);
        updateProjects();
    }

    private void updateProjects() {
        if (mUser.getValue()!= null && mUser.getValue().length()!=0) {
            mDisposable = ApiUtils.getApiService().getUserProjects(mUser.getValue())
                    .map(ProjectResponse::getProjects)
                    .doOnSubscribe(disposable -> mIsLoading.postValue(true))
                    .doFinally(() -> mIsLoading.postValue(false))
                    .doOnSuccess(response -> mIsErrorVisible.postValue(false))
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                            response -> mStorage.insertProjects(response),
                            throwable -> {
                                boolean value = mProjects.getValue() == null || mProjects.getValue().size() == 0;
                                mIsErrorVisible.postValue(value);
                            });
        }

    }


    public MutableLiveData<Boolean> getIsLoading() {
        return mIsLoading;
    }

    public MutableLiveData<Boolean> getIsErrorVisible() {
        return mIsErrorVisible;
    }

    public LiveData<PagedList<RichProject>> getProjects() {
        return mProjects;
    }

    public MutableLiveData<String> getUser() {
        return mUser;
    }

    public SwipeRefreshLayout.OnRefreshListener getOnRefreshListener() {
        return mOnRefreshListener;
    }
}
