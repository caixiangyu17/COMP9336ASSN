package leo.unsw.comp9336assn.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiEnterpriseConfig;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by LeoPC on 2015/8/10.
 */
public abstract class WifiManagerEx {

    public static final int WIFI_CIPHER_WEP = 0x0001;
    public static final int WIFI_CIPHER_WPA = 0x0002;
    public static final int WIFI_CIPHER_NOPASS = 0x0003;
    public static final int WIFI_CIPHER_WPA_AUT = 0x0004;
    public static final int WIFI_CIPHER_INVALID = 0x0005;

    public static final int WIFI_STATE_DISABLED = WifiManager.WIFI_STATE_DISABLED;
    public static final int WIFI_STATE_DISABLING = WifiManager.WIFI_STATE_DISABLING;
    public static final int WIFI_STATE_ENABLED = WifiManager.WIFI_STATE_ENABLED;
    public static final int WIFI_STATE_ENABLING = WifiManager.WIFI_STATE_ENABLING;
    public static final int WIFI_STATE_UNKNOWN = WifiManager.WIFI_STATE_UNKNOWN;

    public static final HashMap<Integer, String> WIFI_STATE = new HashMap<Integer, String>() {
        {
            put(WIFI_STATE_DISABLED, "WIFI_STATE_DISABLED");
            put(WIFI_STATE_DISABLING, "WIFI_STATE_DISABLING");
            put(WIFI_STATE_ENABLED, "WIFI_STATE_ENABLED");
            put(WIFI_STATE_ENABLING, "WIFI_STATE_ENABLING");
            put(WIFI_STATE_UNKNOWN, "WIFI_STATE_UNKNOWN");
        }
    };

    public static final int WIFI_TYPE_WEP = 0x0001;
    public static final int WIFI_TYPE_WPA = 0x0002;
    public static final int WIFI_TYPE_NOPASS = 0x0003;
    public static final int WIFI_TYPE_WPA_PREAUTH = 0x0004;


    WifiManager wifiManager;
    Context context;
    LogManagerEx logManagerEx = LogManagerEx.getInstance();
    private BroadcastReceiver receiver;


    public WifiManagerEx(Context context) {
        this.context = context;
        wifiManager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
    }


    public void setWifiEnabled(boolean enabled) {
        wifiManager.setWifiEnabled(enabled);
    }

    public int getWifiState() {
        return wifiManager.getWifiState();
    }

    public List<ScanResult> getScanResults() {
        return wifiManager.getScanResults();
    }

    public void connect(String ssid, String id, String password, String type, boolean isCheckState) {
        if (!isCheckState) {
            Thread thread = new Thread(new ConnectRunnable(ssid, id, password, type));
            thread.start();
        } else {
            if (getWifiState() == WIFI_STATE_ENABLED) {
                ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                NetworkInfo.State state = wifiNetworkInfo.getState();
                if (state == NetworkInfo.State.CONNECTED) {

                }
            }
        }
    }


    public ArrayList<Ap> getScanAps() {
        List<ScanResult> result;
        while (true) {
            result = getScanResults();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (result.size() > 0) {
                break;
            }
        }
        final ArrayList<Ap> aps = new ArrayList<Ap>();
        for (int i = 0; i < result.size(); i++) {
            Ap ap = new Ap();
            ap.ssid = result.get(i).SSID;
            ap.level = result.get(i).level;
            ap.capabilities = result.get(i).capabilities;
            ap.bssid = result.get(i).BSSID;
            if (ap.ssid == null || ap.ssid.equals("")) {
                continue;
            }
            boolean isExit = false;
            for (int j = 0; j < aps.size(); j++) {
                if (aps.get(j).ssid.equals(ap.ssid)) {
                    if (aps.get(j).level < ap.level) {
                        aps.set(j, ap);
                    }
                    isExit = true;
                    break;
                }
            }
            if (!isExit) {
                aps.add(ap);
            }

        }
        processExist(aps);
        return aps;
    }


    private void processExist(ArrayList<Ap> aps) {
        WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiMgr.getConnectionInfo();
        String wifiId = info != null ? info.getSSID() : null;

        for (int i = 0; i < aps.size(); i++) {
            if (wifiId.equals("\"" + aps.get(i).ssid + "\"")) {
                aps.get(i).connectState = "connected";
            }

            if (isExist(aps.get(i).ssid)) {
                aps.get(i).savedState = "saved";
            }
        }
    }

    class ConnectRunnable implements Runnable {
        private String ssid;

        private String password;

        private String id;

        private String capasibilities;

        public ConnectRunnable(String ssid, String id, String password, String capasibilities) {
            this.ssid = ssid;
            this.id = id;
            this.password = password;
            this.capasibilities = capasibilities;
        }

        @Override
        public void run() {
// 打开wifi
//            openWifi();
// 开启wifi功能需要一段时间(我在手机上测试一般需要1-3秒左右)，所以要等到wifi
// 状态变成WIFI_STATE_ENABLED的时候才能执行下面的语句
            while (wifiManager.getWifiState() == android.net.wifi.WifiManager.WIFI_STATE_ENABLING) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ie) {
                }
            }

            WifiConfiguration wifiConfig = createWifiInfo();
            remove(ssid);
            wifiManager.disconnect();
            int netID = wifiManager.addNetwork(wifiConfig);
            logManagerEx.show("connect to the wifi No." + netID);
            boolean enabled = wifiManager.enableNetwork(netID, true);
            Log.d("COMP9336", "enableNetwork connected=" + enabled);
            wifiManager.saveConfiguration();
//            boolean connected = wifiManager.reconnect();
//            Log.d("COMP9336", "enableNetwork connected=" + connected);
        }

        private WifiConfiguration createWifiInfo() {
            clear();
            WifiConfiguration config = new WifiConfiguration();
//            WifiEnterpriseConfig config = new WifiEnterpriseConfig();
            config.allowedAuthAlgorithms.clear();
            config.allowedGroupCiphers.clear();
            config.allowedKeyManagement.clear();
            config.allowedPairwiseCiphers.clear();
            config.allowedProtocols.clear();
            config.SSID = "\"" + ssid + "\"";
            logManagerEx.show(ssid + "   " + capasibilities);

            String caps[] = capasibilities.split("[\\[\\-\\]]+");
            boolean isWep = false;
            boolean isWpa = false;
            boolean isWpaPreauth = false;
            boolean isNopass = false;
            boolean isCCMP = false;
            boolean isEAP = false;
            for (String cap : caps) {
                if (cap.contains("WPA")) {
                    isWpa = true;
                } else if (cap.contains("EAP")) {
                    isEAP = true;
                } else if (cap.contains("CCMP")) {
                    isCCMP = true;
                } else if (cap.contains("preauth")) {
                    isWpaPreauth = true;
                }
            }
            if (!isWpa && !isWep && !isWpaPreauth) {
                isNopass = true;
            }
            if (isNopass) {
//                nopass
                logManagerEx.show("nopass");
                config.wepKeys[0] = "";
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                config.wepTxKeyIndex = 0;
            } else if (isWep) {
//                wep
                logManagerEx.show("wep");
                if (!TextUtils.isEmpty(password)) {
                    if (isHexWepKey(password)) {
                        config.wepKeys[0] = password;
                    } else {
                        config.wepKeys[0] = "\"" + password + "\"";
                    }
                }
                config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                config.wepTxKeyIndex = 0;
            } else if (isWpa && !isWpaPreauth) {
//                wpa
                logManagerEx.show("wap");
//                config.preSharedKey = "\"" + password + "\"";
                config.preSharedKey = "\"" + password + "\"";
                config.status = WifiConfiguration.Status.ENABLED;
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            } else if (isWpaPreauth) {
//                isWpaPreauth
                logManagerEx.show("isWpaPreauth");
                WifiEnterpriseConfig enterpriseConfig = new WifiEnterpriseConfig();
                enterpriseConfig.setIdentity(id);
                enterpriseConfig.setPassword(password);
                enterpriseConfig.setEapMethod(WifiEnterpriseConfig.Eap.PEAP);
                config.enterpriseConfig = enterpriseConfig;
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP);
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.IEEE8021X);
            }
            return config;
        }

    }


    public void clear() {
        List<WifiConfiguration> existingConfigs = wifiManager
                .getConfiguredNetworks();
        if (existingConfigs != null && existingConfigs.size() > 0) {
            existingConfigs.clear();
        }
    }

    private void remove(String ssid) {
        List<WifiConfiguration> existingConfigs = wifiManager
                .getConfiguredNetworks();


        for (int i = 0; i < existingConfigs.size(); i++) {
            if (existingConfigs.get(i).SSID.equals("\"" + ssid + "\"")) {
                existingConfigs.remove(i);
            }
        }
    }

    public boolean isExist(String ssid) {
        List<WifiConfiguration> existingConfigs = wifiManager
                .getConfiguredNetworks();
        if(existingConfigs == null){
            return false;

        }

        for (int i = 0; i < existingConfigs.size(); i++) {
            if (existingConfigs.get(i).SSID.equals("\"" + ssid + "\"")) {
                return true;
            }
        }
        return false;
    }

    private static boolean isHexWepKey(String wepKey) {
        final int len = wepKey.length();

// WEP-40, WEP-104, and some vendors using 256-bit WEP (WEP-232?)
        if (len != 10 && len != 26 && len != 58) {
            return false;
        }

        return isHex(wepKey);
    }

    private static boolean isHex(String key) {
        for (int i = key.length() - 1; i >= 0; i--) {
            final char c = key.charAt(i);
            if (!(c >= '0' && c <= '9' || c >= 'A' && c <= 'F' || c >= 'a'
                    && c <= 'f')) {
                return false;
            }
        }

        return true;
    }

    public static int getType(String capasibilities) {
        String caps[] = capasibilities.split("[\\[\\-\\]]+");
        boolean isWep = false;
        boolean isWpa = false;
        boolean isWpaPreauth = false;
        boolean isNopass = false;
        boolean isCCMP = false;
        boolean isEAP = false;
        for (String cap : caps) {
            if (cap.contains("WPA")) {
                isWpa = true;
            } else if (cap.contains("EAP")) {
                isEAP = true;
            } else if (cap.contains("CCMP")) {
                isCCMP = true;
            } else if (cap.contains("preauth")) {
                isWpaPreauth = true;
            }
        }


        int result = -1;
        if (isWpaPreauth) {
            result = WIFI_TYPE_WPA_PREAUTH;
        } else if (isWpa) {
            result = WIFI_TYPE_WPA;
        } else if (isWep) {
            result = WIFI_TYPE_WEP;
        } else {
            result = WIFI_TYPE_NOPASS;
        }

        return result;
    }

    public void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        receiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context receiveContext, Intent intent) {
                if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {
                    int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
                    switch (wifiState) {
                        case WifiManager.WIFI_STATE_DISABLED:
                            onWifiDisabled();
                        case WifiManager.WIFI_STATE_DISABLING:
                            onWifiDisabling();
                        case WifiManager.WIFI_STATE_UNKNOWN:
                            break;
                        case WifiManager.WIFI_STATE_ENABLING:
                            onWifiEnabling();
                            break;
                        case WifiManager.WIFI_STATE_ENABLED:
                            onWifiEnabled();
                            break;
                        //
                    }
                } else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
                    Parcelable parcelableExtra = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                    if (null != parcelableExtra) {
                        NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
                        NetworkInfo.State state = networkInfo.getState();
                        if (state == NetworkInfo.State.CONNECTED) {
                            onNetworkConnected(networkInfo.getExtraInfo());
                        } else if (state == NetworkInfo.State.CONNECTING) {
                            onNetworkConnecting(networkInfo.getExtraInfo());
                        } else if (state == NetworkInfo.State.DISCONNECTED) {
                            onNetworkDisconnected();
                        } else if (state == NetworkInfo.State.DISCONNECTING) {
                            onNetworkDisconnecting();
                        } else if (state == NetworkInfo.State.SUSPENDED) {
                            onNetworkSuspened();
                        } else if (state == NetworkInfo.State.UNKNOWN) {
                            onNetworkUnknown();
                        }
                    }
                }

            }
        };
        context.registerReceiver(receiver, filter);
        setWifiEnabled(true);
    }

    public void unRegisterReceiver(){
        context.unregisterReceiver(receiver);
    }

    abstract public void onWifiEnabling();

    abstract public void onWifiEnabled();

    abstract public void onWifiDisabling();

    abstract public void onWifiDisabled();

    abstract public void onNetworkConnecting(String ssid);

    abstract public void onNetworkConnected(String ssid);

    abstract public void onNetworkDisconnecting();

    abstract public void onNetworkDisconnected();

    abstract public void onNetworkSuspened();

    abstract public void onNetworkUnknown();


}



