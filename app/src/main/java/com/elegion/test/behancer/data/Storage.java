package com.elegion.test.behancer.data;

import android.arch.lifecycle.LiveData;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;

import com.elegion.test.behancer.data.database.BehanceDao;
import com.elegion.test.behancer.data.model.project.Owner;
import com.elegion.test.behancer.data.model.project.Project;
import com.elegion.test.behancer.data.model.project.ProjectResponse;
import com.elegion.test.behancer.data.model.project.RichProject;
import com.elegion.test.behancer.data.model.user.Image;
import com.elegion.test.behancer.data.model.user.User;
import com.elegion.test.behancer.data.model.user.UserResponse;
import com.elegion.test.behancer.data.model.user.UserWithImage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vladislav Falzan.
 */

public class Storage {

    public static final int PAGE_SIZE = 10;
    private BehanceDao mBehanceDao;

    public Storage(BehanceDao behanceDao) {
        mBehanceDao = behanceDao;
    }

    public void insertProjects(ProjectResponse response) {
        insertProjects(response.getProjects());

    }

    public void insertProjects(List<Project> projects) {
        mBehanceDao.insertProjects(projects);

        List<Owner> owners = getOwners(projects);

        mBehanceDao.clearOwnerTable();
        mBehanceDao.insertOwners(owners);
    }

    private List<Owner> getOwners(List<Project> projects) {

        List<Owner> owners = new ArrayList<>();
        for (int i = 0; i < projects.size(); i++) {

            Owner owner = projects.get(i).getOwners().get(0);
            owner.setId(i);
            owner.setProjectId(projects.get(i).getId());
            owners.add(owner);
        }
        return owners;
    }

    public LiveData<List<RichProject>> getProjectsLive() {
        return mBehanceDao.getProjectsLive();
    }

    public LiveData<PagedList<RichProject>> getProjectsPaged() {
        return new LivePagedListBuilder<>(mBehanceDao.getProjectsPaged(), PAGE_SIZE).build();
    }

    public LiveData<PagedList<RichProject>> getUserProjectsPaged(String user) {
        return new LivePagedListBuilder<>(mBehanceDao.getUserProjectsPaged(user), PAGE_SIZE).build();
    }

    public ProjectResponse getProjects() {
        List<Project> projects = mBehanceDao.getProjects();
        for (Project project : projects) {
            project.setOwners(mBehanceDao.getOwnersFromProject(project.getId()));
        }

        ProjectResponse response = new ProjectResponse();
        response.setProjects(projects);

        return response;
    }

    public void insertUser(UserResponse response) {
        User user = response.getUser();
        Image image = user.getImage();
        image.setId(user.getId());
        image.setUserId(user.getId());

        mBehanceDao.insertUser(user);
        mBehanceDao.insertImage(image);
    }

    public UserResponse getUser(String username) {
        User user = mBehanceDao.getUserByName(username);
        Image image = mBehanceDao.getImageFromUser(user.getId());
        user.setImage(image);

        UserResponse response = new UserResponse();
        response.setUser(user);

        return response;
    }

    public interface StorageOwner {
        Storage obtainStorage();
    }

    public LiveData<UserWithImage> getUserWithImageLiveByName(String name) {
        return mBehanceDao.getUserWithImageLiveByName(name);
    }


}
