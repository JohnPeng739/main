package org.mx.service.test.server.comm;

import org.mx.service.server.comm.ReceivedMessage;
import org.mx.service.server.comm.ReceiverListener;

public class DefaultCommReceiver implements ReceiverListener {
    private byte[] payload = null;

    public byte[] getPayload() {
        return this.payload;
    }

    @Override
    public void receiveMessage(ReceivedMessage receivedMessage) {
        if (receivedMessage != null) {
            this.payload = receivedMessage.getPayload();
        }
    }
}
