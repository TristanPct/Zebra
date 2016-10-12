package com.totris.zebra.Models;


import com.totris.zebra.Utils.AesCrypto;

public class EncryptedMessage extends AesCrypto.Data {

    public EncryptedMessage() {

    }

    public EncryptedMessage(AesCrypto.Data data) {
        super(data);
    }

    public Message decrypt(String passphrase) {
        return Message.decrypt(getValue(), passphrase, getSalt());
    }

}
