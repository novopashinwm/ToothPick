package com.elegion.test.behancer.data.model.user;

import android.arch.persistence.room.Embedded;

public class UserWithImage {

    @Embedded
    public User user;

    public String photoUrl;

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}

