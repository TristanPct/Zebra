package com.totris.zebra.models;


import android.support.annotation.Nullable;

import com.orm.SugarRecord;
import com.orm.dsl.Unique;
import com.totris.zebra.users.User;

import java.util.List;

public class UserRecord extends SugarRecord {

    @Unique
    private String uid;
    private String username;
    private String mail;
    // TODO: add missing fields

    public UserRecord() {

    }

    public UserRecord(String uid, String username, String mail) {
        this.uid = uid;
        this.username = username;
        this.mail = mail;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    @Nullable
    public static UserRecord findByUid(String uid) {
        List<UserRecord> results = UserRecord.find(UserRecord.class, "uid = ?", uid);
        return results.size() != 0 ? results.get(0) : null;
    }

    @Override
    public long save() {
        UserRecord user = findByUid(uid);

        if (user != null) {
            setId(user.getId());
        }

        return super.save();
    }

    @Override
    public boolean delete() {
        UserRecord user = findByUid(uid);

        if (user != null) {
            setId(user.getId());
        }

        return super.delete();
    }

    public static long save(User user) {
        UserRecord record = new UserRecord(user.getUid(), user.getUsername(), user.getMail());

        return record.save();
    }

    public static boolean delete(User user) {
        UserRecord record = new UserRecord(user.getUid(), user.getUsername(), user.getMail());

        return record.delete();
    }
}
