package com.totris.zebra.utils;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jdeferred.Promise;
import org.jdeferred.impl.DeferredObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thomaslecoeur on 11/10/2016.
 */
public class Database {
    private static final String TAG = "Database";

    private static Database ourInstance = new Database();

    private static FirebaseDatabase database;

    public static Database getInstance() {
        return ourInstance;
    }

    private Database() {
        database = FirebaseDatabase.getInstance();
    }

    public DatabaseReference getReference(String ref) {
        return database.getReference(ref);
    }

    public <T> Promise get(DatabaseReference dbRef, final Class<T> type) {
        final DeferredObject deferred = new DeferredObject();

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            List<T> list = new ArrayList<>();

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    T item = postSnapshot.getValue(type);

                    list.add(item);
                }

                deferred.resolve(list);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                deferred.resolve(list);
            }
        });

        return deferred.promise();
    }

}
