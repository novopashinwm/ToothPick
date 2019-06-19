package com.elegion.test.behancer.ui.profile;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.elegion.test.behancer.databinding.ProfileBinding;

import javax.inject.Inject;

/**
 * Created by Vladislav Falzan.
 */

public class ProfileFragment extends Fragment {

    @Inject
    ProfileViewModel mProfileViewModel;

    public static final String PROFILE_KEY = "PROFILE_KEY";

    public static ProfileFragment newInstance(Bundle args) {
        ProfileFragment fragment = new ProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        String username = getArguments().getString(PROFILE_KEY);
        ViewModelProvider.Factory factory = new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new ProfileViewModel( username);
            }
        };
        mProfileViewModel = ViewModelProviders.of(this, factory).get(ProfileViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ProfileBinding binding = ProfileBinding.inflate(inflater, container, false);
        binding.setVm(mProfileViewModel);
        return binding.getRoot();
    }

}