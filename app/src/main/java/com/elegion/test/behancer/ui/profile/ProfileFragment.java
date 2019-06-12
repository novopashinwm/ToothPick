package com.elegion.test.behancer.ui.profile;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.elegion.test.behancer.data.Storage;
import com.elegion.test.behancer.databinding.ProfileBinding;

/**
 * Created by Vladislav Falzan.
 */

public class ProfileFragment extends Fragment {

    private String username = "";
    private ProfileViewModel mProfileViewModel;

    public static final String PROFILE_KEY = "PROFILE_KEY";

    public static ProfileFragment newInstance(Bundle args) {
        ProfileFragment fragment = new ProfileFragment();
        fragment.setArguments(args);

        return fragment;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Storage.StorageOwner) {
            Storage storage = ((Storage.StorageOwner) context).obtainStorage();
            mProfileViewModel = new ProfileViewModel(storage);

        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ProfileBinding binding = ProfileBinding.inflate(inflater, container, false);
        binding.setVm(mProfileViewModel);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        if (getArguments() != null) {
            username = getArguments().getString(PROFILE_KEY);
        }

        if (getActivity() != null) {
            getActivity().setTitle(username);
        }

        mProfileViewModel.setmUsername(username);
        mProfileViewModel.loadProfile();
    }

    @Override
    public void onDetach() {
        mProfileViewModel.dispatchDetach();
        super.onDetach();
    }

}