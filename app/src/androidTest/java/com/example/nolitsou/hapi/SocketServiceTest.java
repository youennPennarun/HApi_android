package com.example.nolitsou.hapi;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.test.ServiceTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;

import com.example.nolitsou.hapi.server.SocketService;

public class SocketServiceTest extends ServiceTestCase<SocketService> {
    private final static int CONNECTION_TIMEOUT = 10000;
    private Messenger messenger = new Messenger(new IncomingHandler());
    private SocketService service;

    public SocketServiceTest() {
        super(SocketService.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @SmallTest
    public void testPreconditions() {
    }

    /**
     * Test basic startup/shutdown of Service
     */
    @SmallTest
    public void testStartable() {
        Intent startIntent = new Intent();
        startIntent.setClass(getContext(), SocketService.class);
        startService(startIntent);
    }

    /**
     * Test binding to service
     */
    @MediumTest
    public void testBindable() {
        Intent startIntent = new Intent();
        startIntent.setClass(getContext(), SocketService.class);
        service = ((SocketService.SocketBinder) bindService(startIntent)).getService();
    }

    @MediumTest
    public void testRegistrable() {
        if (service == null) {
            testBindable();
        }
        assertNotNull(service);
        Messenger socketMessenger = new Messenger(service.getMessenger().getBinder());
        Message msg = Message.obtain(null, SocketService.REGISTER_CLIENT);
        msg.replyTo = messenger;
        try {
            socketMessenger.send(msg);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void testReconnection() {
        long start = System.currentTimeMillis();
        Intent startIntent = new Intent();
        startIntent.setClass(getContext(), SocketService.class);
        startService(startIntent);
        service = ((SocketService.SocketBinder) bindService(startIntent)).getService();

        assertNotNull(service);
        while (!service.isConnected() && ((System.currentTimeMillis() - start) < CONNECTION_TIMEOUT)) {
            System.out.println((System.currentTimeMillis() - start) + " > " + CONNECTION_TIMEOUT);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        assertTrue(service.isConnected());
        service.reconnect();
        start = System.currentTimeMillis();
        while (!service.isConnected() && ((System.currentTimeMillis() - start) < CONNECTION_TIMEOUT)) {
            System.out.println((System.currentTimeMillis() - start) + " > " + CONNECTION_TIMEOUT);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        assertTrue(service.isConnected());
    }


    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SocketService.SOCKET_CONNECTED:
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }


}