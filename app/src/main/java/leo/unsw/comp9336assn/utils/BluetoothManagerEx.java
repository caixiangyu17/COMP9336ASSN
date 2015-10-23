package leo.unsw.comp9336assn.utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;


/**
 * Created by LeoPC on 2015/10/6.
 */
public abstract class BluetoothManagerEx {
    Context context;
    private BluetoothAdapter blueadapter=null;
    LogManagerEx logManagerEx = LogManagerEx.getInstance();

    public static final int TYPE_SERVER = 0x0001;
    public static final int TYPE_CLIENT = 0x0002;
    public static final String PROTOCOL_SCHEME_RFCOMM = "btspp";


    private ArrayList<BluetoothDevice> newBtdList = new ArrayList<>();

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action =intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice btd=intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (btd.getBondState() != BluetoothDevice.BOND_BONDED) {
                    logManagerEx.show(btd.toString());
                    newBtdList.add(btd);
                    onDeviceFound(btd, newBtdList);
                }
            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){   //搜索结束
                onDiscoveryFinished(newBtdList);
            }
        }
    };
    private BluetoothSocket socket;
    private readThread mreadThread;
    private ServerThread serverThread;
    private ClientThread clientThread;
    private BluetoothServerSocket serverSocket;

    public BluetoothManagerEx(Context context){
        this.context = context;

    }

    public boolean start(){
        if(blueadapter == null){
            blueadapter=BluetoothAdapter.getDefaultAdapter();
            register();
        }
        return blueadapter!=null;
    }

    public void stop(){
        disconnectClient();
        disconnectServer();
        context.unregisterReceiver(receiver);

    }

    private void register(){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        
        context.registerReceiver(receiver, intentFilter);
    }

    public void startDiscovery(){

        blueadapter.startDiscovery();
    }
    public ArrayList<BluetoothDevice> getBondedList(){
        ArrayList<BluetoothDevice> bondedBtdList = new ArrayList<>();
        Set<BluetoothDevice> device=blueadapter.getBondedDevices();
        Iterator<BluetoothDevice> itr = device.iterator();
        while(itr.hasNext()){
            bondedBtdList.add(itr.next());
        }
        return bondedBtdList;
    }


    public void connect(String macAddress, int connectType){
        if(connectType == TYPE_CLIENT){
            clientThread = new ClientThread(macAddress, blueadapter);
            clientThread.start();
        }else if(connectType == TYPE_SERVER){
            serverThread = new ServerThread(macAddress, blueadapter);
            serverThread.start();
        }

    }

    private class ClientThread extends Thread {
        String macAddress;
        BluetoothDevice device;
        BluetoothAdapter adapter;
        public ClientThread(String macAddress, BluetoothAdapter adapter){
            this.macAddress = macAddress;
            this.adapter = adapter;
            device = adapter.getRemoteDevice(macAddress);
        }
        @Override
        public void run() {
            //创建一个Socket连接：只需要服务器在注册时的UUID号
            // socket = device.createRfcommSocketToServiceRecord(BluetoothProtocols.OBEX_OBJECT_PUSH_PROTOCOL_UUID);
            BluetoothConnector connector = new BluetoothConnector(device, true, adapter, null);
            try {
                socket = connector.connect().getUnderlyingSocket();
                onConnectedAsClient();
                mreadThread = new readThread();
                mreadThread.start();
            } catch (IOException e) {
                onConnectFailedAsClient(e);
            }
        }
    };

    private class ServerThread extends Thread {
        String macAddress;
        BluetoothDevice device;
        BluetoothAdapter adapter;

        public ServerThread(String macAddress, BluetoothAdapter adapter){
            this.macAddress = macAddress;
            this.adapter = adapter;
            device = adapter.getRemoteDevice(macAddress);
        }
        @Override
        public void run() {
            try {
                serverSocket = adapter.listenUsingRfcommWithServiceRecord(PROTOCOL_SCHEME_RFCOMM,
                        UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
                waitingForConnection();
                socket = serverSocket.accept();
                onConnectedAsServer();
                mreadThread = new readThread();
                mreadThread.start();
            } catch (IOException e) {
                onConnectFailedAsServer(e);
            }
        }
    };

    public void sendMessage(String msg)
    {
        if (socket == null)
        {
            Toast.makeText(context, "no connection built", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            OutputStream os = socket.getOutputStream();
            os.write(msg.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class readThread extends Thread {
        @Override
        public void run() {

            byte[] buffer = new byte[1024];
            int bytes;
            InputStream mmInStream = null;

            try {
                mmInStream = socket.getInputStream();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            while (true) {
                try {
                    // Read from the InputStream
                    if( (bytes = mmInStream.read(buffer)) > 0 )
                    {
                        byte[] buf_data = new byte[bytes];
                        for(int i=0; i<bytes; i++)
                        {
                            buf_data[i] = buffer[i];
                        }
                        onMsgReceived(new String(buf_data));
                    }
                } catch (IOException e) {
                    try {
                        mmInStream.close();
                    } catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                    break;
                }
            }
        }
    }

    private void disconnectServer() {
        new Thread() {
            @Override
            public void run() {
                if(serverThread != null)
                {
                    serverThread.interrupt();
                    serverThread = null;
                }
                if(mreadThread != null)
                {
                    mreadThread.interrupt();
                    mreadThread = null;
                }
                try {
                    if(socket != null)
                    {
                        socket.close();
                        socket = null;
                    }
                    if (serverSocket != null)
                    {
                        serverSocket.close();/* 关闭服务器 */
                        serverSocket = null;
                    }
                } catch (IOException e) {
                    Log.e("COMP9336", "mserverSocket.close()", e);
                }
            };
        }.start();
    }
    private void disconnectClient() {
        new Thread() {
            @Override
            public void run() {
                if(clientThread!=null)
                {
                    clientThread.interrupt();
                    clientThread= null;
                }
                if(mreadThread != null)
                {
                    mreadThread.interrupt();
                    mreadThread = null;
                }
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    socket = null;
                }
            };
        }.start();
    }




    public abstract void onDeviceFound(BluetoothDevice btd, ArrayList<BluetoothDevice> btdList);
    public abstract void onDiscoveryFinished(ArrayList<BluetoothDevice> btdList);
    public abstract void onConnectedAsClient();
    public abstract void onConnectFailedAsClient(Exception e);
    public abstract void waitingForConnection();
    public abstract void onConnectedAsServer();
    public abstract void onConnectFailedAsServer(Exception e);
    public abstract void onMsgReceived(String str);
}
