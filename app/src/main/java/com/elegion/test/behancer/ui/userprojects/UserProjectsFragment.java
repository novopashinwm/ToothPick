package com.elegion.test.behancer.ui.userprojects;


import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.elegion.test.behancer.data.Storage;
import com.elegion.test.behancer.databinding.UserProjectsBinding;
import com.elegion.test.behancer.utils.CustomFactory;

public class UserProjectsFragment extends Fragment {
    public static final String USERPROJECT_KEY = "USERPROJECT_KEY";
    private UserProjectsViewModel mUserProjectsViewModel;

    public static UserProjectsFragment newInstance(String user) {
        UserProjectsFragment fragment = new UserProjectsFragment();

        Bundle args = new Bundle();
        args.putString(USERPROJECT_KEY, user);
        fragment.setArguments(args);

        return fragment;
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Storage.StorageOwner) {
            Storage storage = ((Storage.StorageOwner) context).obtainStorage();
            CustomFactory factory = new CustomFactory(storage, null, getArguments().getString(USERPROJECT_KEY));
            mUserProjectsViewModel = ViewModelProviders.of(this, factory).get(UserProjectsViewModel.class);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        UserProjectsBinding binding = UserProjectsBinding.inflate(inflater, container, false);
        binding.setVm(mUserProjectsViewModel);
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }

}
