package com.totris.zebra.Utils;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.totris.zebra.Models.EncryptedMessage;
import com.totris.zebra.Models.Message;
import com.totris.zebra.Models.MessageType;

/**
 * Created by thomaslecoeur on 11/10/2016.
 */
public class Database {
    private static String TAG = "Database";

    private static Database ourInstance = new Database();

    private static FirebaseDatabase database;
    DatabaseReference messageRef;

    public static Database getInstance() {
        return ourInstance;
    }

    private Database() {
        database = FirebaseDatabase.getInstance();
    }

    public void sendMessage(Message message) {
        getMessagesReference().push().setValue(message);
    }

    public void sendMessage(EncryptedMessage message) {
        getMessagesReference().push().setValue(message);
    }

    public DatabaseReference getReference(String ref) { return database.getReference(ref); }

    public DatabaseReference getMessagesReference() {
        return database.getReference("messages");
    }

}
