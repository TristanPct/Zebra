package com.totris.zebra.Models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thomaslecoeur on 13/10/2016.
 */

public class GroupUser {
    private List<String> usersIds = new ArrayList<>();
    private String groupId;

    public GroupUser() {

    }

    public GroupUser(List<User> usersIds, Group groupId) {
        for(User u : usersIds) {
            this.usersIds.add(u.getUid());
        }
        this.groupId = groupId.getUid();
    }

    public GroupUser(List<String> usersIds, String groupId) {
        this.usersIds = usersIds;
        this.groupId = groupId;
    }

    public List<String> getUsersIds() {
        return usersIds;
    }

    public String getGroupId() {
        return groupId;
    }
}
