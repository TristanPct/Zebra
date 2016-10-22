package com.totris.zebra.messages;


import android.util.Log;

import com.totris.zebra.utils.AesCrypto;

import java.io.Serializable;

public class EncryptedMessage extends AesCrypto.Data implements Serializable {

    private static final String TAG = "EncryptedMessage";

    public EncryptedMessage() {

    }

    public EncryptedMessage(AesCrypto.Data data) {
        super(data);
    }

    public Message decrypt(String passphrase) {
        Log.d(TAG, "decrypt: " + passphrase);
        return Message.decrypt(getValue(), passphrase, getSalt());
    }

    @Override
    public int hashCode() {
        return (getValue() + getSalt()).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof EncryptedMessage) {
            EncryptedMessage message = (EncryptedMessage) obj;
            return message.getSalt().equals(getSalt()) && message.getValue().equals(getValue());
        }

        return super.equals(obj);
    }
}
