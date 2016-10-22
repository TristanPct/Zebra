package com.totris.zebra.groups;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orm.SugarRecord;
import com.orm.dsl.Ignore;
import com.totris.zebra.models.GroupRecord;
import com.totris.zebra.models.UserRecord;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GroupUserRecord extends SugarRecord {

    private static ObjectMapper mapper = new ObjectMapper();

    private GroupRecord group;
    private String usersIdsJson;

    @Ignore
    private List<UserRecord> users = new ArrayList<>();

    public GroupUserRecord() {

    }

    public GroupRecord getGroup() {
        return group;
    }

    public void setUsersIdsJson(String usersIdsJson) {
        this.usersIdsJson = usersIdsJson;

        List<String> ids;
        try {
            ids = mapper.readValue(usersIdsJson, new TypeReference<List<String>>() {
            });
        } catch (IOException e) {
            ids = new ArrayList<>();
        }

        UserRecord user;
        for (String id : ids) {
            user = UserRecord.findByUid(id);
            if (user != null) {
                users.add(user);
            }
        }
    }

    public List<UserRecord> getUsers() {
        return users;
    }

    public void setUsers(List<UserRecord> users) {
        this.users = users;

        List<String> usersIds = new ArrayList<>();

        for (UserRecord user : users) {
            usersIds.add(user.getUid());
        }

        try {
            usersIdsJson = mapper.writeValueAsString(usersIds);
        } catch (JsonProcessingException e) {
            usersIdsJson = "";
        }
    }
}
