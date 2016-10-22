package com.totris.zebra.groups;


import android.content.Context;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.totris.zebra.messages.EncryptedMessage;
import com.totris.zebra.messages.Message;
import com.totris.zebra.models.GroupRecord;
import com.totris.zebra.users.User;
import com.totris.zebra.utils.Database;
import com.totris.zebra.utils.OnlineStorage;
import com.totris.zebra.utils.RsaCrypto;

import org.jdeferred.Promise;

import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class Group implements Serializable {
    private final static String TAG = "Group";

    private static final long serialVersionUID = 8392718937281219L;

    private static DatabaseReference dbRef = Database.getInstance().getReference("groups");

    private Context context;
    private String passphrase;

    private String uid;
    private Date createdAt;
    private List<User> users = new ArrayList<>();
    private List<String> usersIds = new ArrayList<>();
    private List<Message> messages = new ArrayList<>();
    @Exclude
    private List<EncryptedMessage> encryptedMessages = new ArrayList<>();
    private boolean messagesLoaded = false;

    private static List<Group> groups = new ArrayList<>();

    public Group() {
        createdAt = new Date();

        KeyGenerator keyGen = null;
        try {
            keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(256);
            passphrase = keyGen.generateKey().toString();
            Log.d(TAG, "Group passphrase: " + passphrase);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public Group(List<User> users) {
        this();
        this.users = users;

        for (User u : users) {
            usersIds.add(u.getUid());
        }
    }

    public Group(String uid) {
        this();
        this.uid = uid;
    }

    @Exclude
    public String getEncryptedPassphrase(User user) { // TODO: only works for two users conversation
        return RsaCrypto.encrypt(passphrase, user.getPublicKey());
    }

    public void setPassphrase(String passphrase) {
        this.passphrase = RsaCrypto.decrypt(context, passphrase);
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public List<String> getUsersIds() {
        return usersIds;
    }

    public void setUsersIds(List<String> usersIds) {
        this.usersIds = usersIds;
    }

    @Exclude
    public GroupUser getGroupUser() { //TODO: update all concerned users of the group
        return new GroupUser(usersIds, getUid());
    }

    public List<Message> getMessages() {
        return messages;
    }

    public List<EncryptedMessage> getEncryptedMessages() {
        return encryptedMessages;
    }

    public void setMessages(Map<String, EncryptedMessage> messages) {
        setMessages(new ArrayList<>(messages.values()));
    }

    @Exclude
    public <T> void setMessages(List<T> messages) {
        messagesLoaded = false;
        this.messages.clear();
        encryptedMessages.clear();

        if (messages == null) {
            Log.d(TAG, "setMessages: null");
            return;
        }

        if (messages.size() != 0) {
            if (messages.get(0) instanceof Message) {
                Log.d(TAG, "setMessages: normal messages");
                this.messages = (List<Message>) messages;
            } else if (messages.get(0) instanceof EncryptedMessage) {
                Log.d(TAG, "setMessages: encrypted messages");
                encryptedMessages = (List<EncryptedMessage>) messages;
                for (EncryptedMessage em : encryptedMessages) {
                    this.messages.add(decryptMessage(em));
                }
            }
        }

        messagesLoaded = true;
    }

    @Exclude
    public boolean isMessagesLoaded() {
        return messagesLoaded;
    }

    @Exclude
    public Message getLastMessage() {
        if (messages.size() == 0) return null;

        return messages.get(messages.size() - 1);
    }

    public void addMessage(Message message) {
        messages.add(message);
    }

    public EncryptedMessage encryptMessage(Message message) {
        return message.encrypt(passphrase);
    }

    public Message decryptMessage(EncryptedMessage encryptedMessage) {
        return encryptedMessage.decrypt(passphrase);
    }

    @Exclude
    public String getTitle() {
        String title = "";

        for (User u : User.getAll()) {
            if (getUsersIds().contains(u.getUid()) && !User.getCurrent().getUid().equals(u.getUid())) {
                title += u.getUsername() + ", ";
            }
        }
        if (title.length() != 0) {
            title = title.substring(0, title.length() - 2);
        }

        return title;
    }

    /**
     * Cache list
     */

    public static List<Group> getAll() {
        return getAll(false);
    }

    public static List<Group> getAll(boolean refresh) {
        Log.d(TAG, "getAll: refresh: " + (refresh || groups.size() == 0));

        if (refresh || groups.size() == 0) {
            groups.clear();
            List<GroupRecord> groupRecords = GroupRecord.findAllByUserId(User.getCurrent().getUid());

            Group group;
            for (GroupRecord record : groupRecords) {
                group = new Group();
                group.setUid(record.getUid());
                group.setCreatedAt(record.getCreatedAt());
                group.setUsersIds(record.getUsersIds());
                group.setMessages(record.getMessages());
                groups.add(group);
            }
        }

        return groups;
    }

    public static void add(Group group) {
        boolean added = false;

        for (Group g : groups) {
            if (g.getUid().equals(group.getUid())) {
                groups.remove(g);
                groups.add(group);
                added = true;
                break;
            }
        }

        if (!added) {
            groups.add(group);
        }
    }

    public static void update(Group group) {
        for (Group g : groups) {
            if (g.getUid().equals(group.getUid())) {
                groups.remove(g);
                groups.add(group);
                break;
            }
        }
    }

    public static void remove(Group group) {
        groups.remove(group);
    }

    public static Group getCommonGroup(List<User> users) {
        String commonGroupId = null;

        //TODO: make it work for selection > 1
        for (GroupUser group : User.getCurrent().getGroups()) {
            for (String userId : group.getUsersIds()) {
                if (userId.equals(users.get(0).getUid())) {
                    commonGroupId = group.getGroupId();
                }
            }
        }

        Group group;

        if (commonGroupId != null) {
            group = new Group(commonGroupId);
        } else {
            users.add(User.getCurrent());
            group = new Group(users);
            group.persist();

            for (User u : users) {
                u.registerGroup(group);
                u.persist();
            }
        }

        return group;
    }

    private boolean isDefined() {
        return !(uid == null || uid.trim().equals(""));
    }

    public void persist() {
        Log.d(TAG, "persist: ");

        if (isDefined()) {
            DatabaseReference tmpRef = dbRef.child(uid);

            tmpRef.setValue(this);

            for (User u: users) {
                tmpRef.child("encryptedPassphrase_" + u.getUid());
            }

            Log.d(TAG, "persisted  exisitng: " + uid);
        } else {
            DatabaseReference groupRef = dbRef.push();
            groupRef.setValue(this);
            uid = groupRef.getKey();
            Log.d(TAG, "persisted: " + uid);
        }
    }

    public DatabaseReference sendMessage(Message message) {
        DatabaseReference tmpRef = dbRef.child(uid).child("messages").push();

        tmpRef.setValue(encryptMessage(message));

        return tmpRef;
    }

    public DatabaseReference sendImageMessage(Message message, byte[] imageBitmap) {
        DatabaseReference tmpRef = dbRef.child(uid).child("messages").push();

        OnlineStorage.uploadImage(imageBitmap, message.getContent());

        tmpRef.setValue(encryptMessage(message));

        return tmpRef;
    }
}
