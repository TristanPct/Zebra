package com.totris.zebra.Models;


import com.google.firebase.database.DatabaseReference;
import com.totris.zebra.Utils.Database;

import java.util.ArrayList;
import java.util.List;

public class Group {
    private List<String> userIds;
    private static DatabaseReference dbRef = Database.getInstance().getReference("groups");

    public Group(List<User> users) {

        userIds = new ArrayList<String>();

        for (User u : users) {
            userIds.add(u.getUid());
        }
    }

    public List<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }

    public void persist() {
        dbRef.push().setValue(this);
    }
}
