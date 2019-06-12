package com.elegion.test.behancer.utils;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.elegion.test.behancer.data.Storage;
import com.elegion.test.behancer.ui.projects.ProjectsAdapter;
import com.elegion.test.behancer.ui.projects.ProjectsViewModel;
import com.elegion.test.behancer.ui.userprojects.UserProjectsViewModel;

/**
 * @author Azret Magometov
 */
public class CustomFactory extends ViewModelProvider.NewInstanceFactory {

    private Storage mStorage;
    private ProjectsAdapter.OnItemClickListener mOnItemClickListener;
    private String mUsername;

    public CustomFactory(Storage storage, ProjectsAdapter.OnItemClickListener onItemClickListener, String username) {
        mStorage = storage;
        mOnItemClickListener = onItemClickListener;
        mUsername = username;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ProjectsViewModel.class)) {
            return (T) new ProjectsViewModel(mStorage, mOnItemClickListener);
        }

        if (modelClass.isAssignableFrom(UserProjectsViewModel.class)) {
            return (T) new UserProjectsViewModel(mStorage,  mUsername);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");

    }
}
