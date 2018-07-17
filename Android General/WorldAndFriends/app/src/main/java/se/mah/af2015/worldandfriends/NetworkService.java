package se.mah.af2015.worldandfriends;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;

public class NetworkService extends Service {
    private static final String IP = "195.178.227.53";
    private static final int PORT = 7117;

    private RunOnThread mThread;
    private Receive mReceive = null;

    private Buffer<String> mReceiveBuffer;

    private DataInputStream mInput;
    private DataOutputStream mOutput;
    private Socket mSocket;

    private static boolean mConnected = false;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mThread = new RunOnThread();
        mReceiveBuffer = new Buffer<>();
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new LocalService();
    }

    public void connect() {
        if(!mConnected) {
            mThread.start();
            mThread.execute(new Connect());
        }
    }

    public void disconnect() {
        if(mConnected) {
            mThread.execute(new Disconnect());
        }
    }

    public void send(String data) {
        mThread.execute(new Send(data));
    }

    public String receive() throws InterruptedException {
        return mReceiveBuffer.get();
    }

    public class LocalService extends Binder {
        public NetworkService getService() {
            return NetworkService.this;
        }
    }

    private class Receive extends Thread {
        public void run() {
            String result;
            try {
                while (mReceive != null) {
                    result = mInput.readUTF();
                    mReceiveBuffer.put(result);
                }
            } catch (Exception e) {
                mReceive = null;
            }
        }
    }

    private class Connect implements Runnable {
        public void run() {
            try {
                mSocket = new Socket(IP, PORT);
                mInput = new DataInputStream(mSocket.getInputStream());
                mOutput = new DataOutputStream(mSocket.getOutputStream());
                mOutput.flush();
                mReceive = new Receive();
                mReceive.start();
                mConnected = true;
            } catch(Exception e) {
                mConnected = false;
                e.printStackTrace();
            }
        }
    }

    private class Disconnect implements Runnable {
        public void run() {
            try {
                if(mInput != null) {
                    mInput.close();
                }
                if(mOutput != null) {
                    mOutput.close();
                }
                if(mSocket != null) {
                    mSocket.close();
                }
                mThread.stop();
                mConnected = false;
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class Send implements Runnable {
        private String data;

        public Send(String data) {
            this.data = data;
        }

        public void run() {
            try {
                if(mOutput != null) {
                    mOutput.writeUTF(data);
                    Log.d("Send()", "Sending" + data);
                    mOutput.flush();
                }
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class RunOnThread {
        private Buffer<Runnable> buffer = new Buffer<>();
        private Worker worker;

        public void start() {
            if(worker == null) {
                worker = new Worker();
                worker.start();
            }
        }

        public void stop() {
            if(worker != null) {
                worker.interrupt();
                worker = null;
            }
        }

        public void execute(Runnable runnable) {
            buffer.put(runnable);
        }

        private class Worker extends Thread {
            public void run() {
                Runnable runnable;
                while(worker != null) {
                    try {
                        runnable = buffer.get();
                        Log.d("Worker()", "get runnable");
                        runnable.run();
                    } catch(InterruptedException e) {
                        worker = null;
                    }
                }
            }
        }
    }

    private class Buffer<T> {
        private LinkedList<T> buffer = new LinkedList<>();

        public synchronized void put(T element) {
            buffer.addLast(element);
            notifyAll();
        }

        public synchronized T get() throws InterruptedException {
            while(buffer.isEmpty()) {
                wait();
            }
            return buffer.removeFirst();
        }
    }
}
