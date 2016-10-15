package com.totris.zebra.Utils;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Created by thomaslecoeur on 15/10/2016.
 */
public class OnlineStorage {
    private static final String TAG = "OnlineStorage";

    private static StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();

    private static OnlineStorage ourInstance = new OnlineStorage();

    public static OnlineStorage getInstance() {
        return ourInstance;
    }

    private OnlineStorage() {

    }

    public static StorageReference getImageReference(String imageName) {
        return mStorageRef.child(imageName);
    }

    public static void uploadImage(byte[] data, String imageName) {
        UploadTask uploadTask = mStorageRef.child(imageName).putBytes(data);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                Log.d(TAG, downloadUrl.toString());
            }
        });
    }

    public static String RandomIdGenerator() {
        SecureRandom random = new SecureRandom();

        return new BigInteger(130, random).toString(32);
    }
}
