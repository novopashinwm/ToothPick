package com.elegion.test.behancer.ui.projects;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.PagedList;
import android.support.v4.widget.SwipeRefreshLayout;

import com.elegion.test.behancer.BuildConfig;
import com.elegion.test.behancer.data.Storage;
import com.elegion.test.behancer.data.model.project.ProjectResponse;
import com.elegion.test.behancer.data.model.project.RichProject;
import com.elegion.test.behancer.utils.ApiUtils;

import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @author Azret Magometov
 */
public class ProjectsViewModel extends ViewModel {

    private Disposable mDisposable;
    private Storage mStorage;

    private ProjectsAdapter.OnItemClickListener mOnItemClickListener;
    private MutableLiveData<Boolean> mIsLoading = new MutableLiveData<>();
    private MutableLiveData<Boolean> mIsErrorVisible = new MutableLiveData<>();
    private LiveData<PagedList<RichProject>> mProjects;
    private SwipeRefreshLayout.OnRefreshListener mOnRefreshListener = this::updateProjects;

    public ProjectsViewModel(Storage storage, ProjectsAdapter.OnItemClickListener onItemClickListener) {
        mStorage = storage;
        mOnItemClickListener = onItemClickListener;
        mProjects = mStorage.getProjectsPaged();
        updateProjects();
    }

    private void updateProjects() {
        mDisposable = ApiUtils.getApiService().getProjects(BuildConfig.API_QUERY)
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

    @Override
    public void onCleared() {
        mStorage = null;
        if (mDisposable != null) {
            mDisposable.dispose();
        }
    }

    public ProjectsAdapter.OnItemClickListener getOnItemClickListener() {
        return mOnItemClickListener;
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

    public SwipeRefreshLayout.OnRefreshListener getOnRefreshListener() {
        return mOnRefreshListener;
    }
}
