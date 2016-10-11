package com.totris.zebra.Utils;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by thomaslecoeur on 11/10/2016.
 */
public class Database {
    private static Database ourInstance = new Database();

    private static FirebaseDatabase database;
    DatabaseReference messageRef;

    public static Database getInstance() {
        return ourInstance;
    }

    private Database() {
        database = FirebaseDatabase.getInstance();
    }

    public void sendMessage() {

    }
}
