package com.totris.zebra.utils;

/**
 * Created by thomaslecoeur on 19/10/2016.
 */

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

public class RsaCrypto {
    private static final String TAG = "RsaCryptoTest";

    public static PublicKey InitRsaKeys(Context context) {

        //////////////////////
        // On both sides
        /////////////////////

        // Generate a new public/private key pair
        KeyPair keyPair = getKeyPair();

        // Store keys in shared preferences
        storeKeys(context, keyPair);

        return keyPair.getPublic();
    }

    public static String encrypt(String data, PublicKey key) {
        PublicKey publicKey = key;

        // Encrypt!
        String encrypted = null;
        try {
            encrypted = RsaEcb.encrypt(data, publicKey);
        } catch (GeneralSecurityException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // Uh Oh, something went wrong!
        if (encrypted == null) throw new AssertionError();

        Log.d(TAG, "encrypted: " + encrypted);

        return encrypted;
    }

    public static String decrypt(Context context, String encrypted) {
        //////////////////////
        // On recipient side
        /////////////////////

        // Retrieve our private key
        PrivateKey privateKey = getPrivateKey(context);

        // Decrypt!
        String decrypted = null;
        try {
            decrypted = RsaEcb.decrypt(encrypted, privateKey);
        } catch (GeneralSecurityException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return decrypted;

        /*if (decrypted != null) {
            if (decrypted.equals("super secret text")) {
                // Yay, it works
                Log.d(TAG, "RSACrypto seems to work!");
            } else {
                // Oh no! Decryption failed!
                throw new AssertionError();
            }
        }*/
    }

    @NonNull
    private static PrivateKey getPrivateKey(Context context) {
        String privateKeyString = context.getSharedPreferences("Crypto", Context.MODE_PRIVATE)
                .getString("private key", "");

        Log.d(TAG, privateKeyString);
        PrivateKey privateKey = null;
        try {
            privateKey = RsaEcb.getRSAPrivateKeyFromString(privateKeyString);
        } catch (GeneralSecurityException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if (privateKey == null) throw new AssertionError();

        Log.d(TAG, privateKey.toString());
        return privateKey;
    }

    private static void storeKeys(Context context, KeyPair keyPair) {
        // Do the same for public key
        try {
            Log.d(TAG, RsaEcb.getPrivateKeyString(keyPair.getPrivate()));
            context.getSharedPreferences("Crypto", Context.MODE_PRIVATE)
                    .edit()
                    .putString("private key", RsaEcb.getPrivateKeyString(keyPair.getPrivate()))
                    .apply();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @NonNull
    private static KeyPair getKeyPair() {
        KeyPair keyPair = null;
        try {
            keyPair = RsaEcb.generateKeys();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }

        if (keyPair == null) throw new AssertionError();

        try {
            Log.d(TAG, "public key: " + RsaEcb.getPublicKeyString(keyPair.getPublic()));
            Log.d(TAG, "private key: " + RsaEcb.getPrivateKeyString(keyPair.getPrivate()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return keyPair;
    }

    public static String getBase64PublicKey(PublicKey key) {
        return Base64.encodeToString(key.toString().getBytes(), Base64.DEFAULT);
    }
}