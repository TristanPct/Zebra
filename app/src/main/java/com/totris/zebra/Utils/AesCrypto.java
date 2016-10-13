package com.totris.zebra.Utils;


import android.support.annotation.NonNull;
import android.util.Log;

import com.tozny.crypto.android.AesCbcWithIntegrity;

import org.spongycastle.util.encoders.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

public class AesCrypto {
    private static final String TAG = "AesCrypto";

    public static Data encrypt(String value, String passphrase) {
        // Generate salt
        byte[] salt = getSalt();

        //  Generate secret keys
        AesCbcWithIntegrity.SecretKeys keys = getSecretKeys(passphrase, salt);

        // Encrypt!
        // Compute "encrypted" text, composed of
        // cipherText: encrypted content
        // Iv: initialization vector
        // Mac: hash to check integrity of cipherText
        AesCbcWithIntegrity.CipherTextIvMac encrypted = null;
        try {
            encrypted = AesCbcWithIntegrity.encrypt(value, keys);
        } catch (UnsupportedEncodingException | GeneralSecurityException e) {
            e.printStackTrace();
        }

        // Uh Oh, something went wrong!
        if (encrypted == null) throw new AssertionError();

        return new Data(encrypted.toString(), Base64.toBase64String(salt), true);
    }

    public static Data encrypt(Data data, String passphrase) {
        return encrypt(data.getValue(), passphrase);
    }

    public static Data encrypt(Serializable object, String passphrase) {
        String value = "";

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            oos.close();
            value = Base64.toBase64String(baos.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return encrypt(value, passphrase);
    }

    public static Data decrypt(String value, String passphrase, String salt) {
        // Regenerate secret keys from password and salt
        AesCbcWithIntegrity.SecretKeys keysDecrypt;
        keysDecrypt = getSecretKeys(passphrase, Base64.decode(salt));

        // Recreate CipherTextIvMac
        AesCbcWithIntegrity.CipherTextIvMac dataToDecrypt = new AesCbcWithIntegrity.CipherTextIvMac(value);

        // Decrypt!
        String decrypted = null;
        try {
            decrypted = AesCbcWithIntegrity.decryptString(dataToDecrypt, keysDecrypt);
        } catch (UnsupportedEncodingException | GeneralSecurityException e) {
            e.printStackTrace();
        }

        return new Data(decrypted, salt, false);
    }

    public static Data decrypt(Data data, String passphrase) {
        return decrypt(data.getValue(), passphrase, data.getSalt());
    }

    public static <T> T decrypt(String value, String passphrase, String salt, Class<T> type) {
        Data decrypted = decrypt(value, passphrase, salt);
        Object o = null;

        try {
            byte[] data = Base64.decode(decrypted.getValue());
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
            o = ois.readObject();
            ois.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return type.cast(o);
    }

    public static <T> T decrypt(Data data, String passphrase, Class<T> type) {
        return decrypt(data.getValue(), passphrase, data.getSalt(), type);
    }

    private static byte[] getSalt() {
        byte[] salt = new byte[0];

        // Generate random 128bits salt
        try {
            salt = AesCbcWithIntegrity.generateSalt();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }

        // salt generation failed
        if (salt.length <= 0) throw new AssertionError();

        return salt;
    }

    @NonNull
    private static AesCbcWithIntegrity.SecretKeys getSecretKeys(String password, byte[] salt) {
        // The secret keys
        AesCbcWithIntegrity.SecretKeys keys = null;

        // Generate secret keys from password and salt
        // password must be kept secret
        // salt can be stored with each message
        try {
            keys = AesCbcWithIntegrity.generateKeyFromPassword(password, salt);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }

        // keys generation failed
        if (keys == null) throw new AssertionError();

        return keys;
    }

    public static class Data {
        protected String value;
        protected String salt;
        private boolean isEncrypted;

        public Data() {

        }

        public Data(Data data) {
            this(data.value, data.salt, data.isEncrypted);
        }

        public Data(String value, String salt, boolean isEncrypted) {
            this.value = value;
            this.salt = salt;
            this.isEncrypted = isEncrypted;
        }

        public String getValue() {
            return value;
        }

        public String getSalt() {
            return salt;
        }
    }
}
