package leo.unsw.comp9336assn.utils;

/**
 * Created by LeoPC on 2015/8/3.
 */
public class Ap {
    public String ssid;
    public int level;
    public String mac;
    public String capabilities;
    public String bssid;
    public String savedState = "";
    public String connectState = "";

    public Ap(){

    }
    public Ap(String ssid){
        this.ssid = ssid;
    }

    @Override
    public String toString() {
        return ssid + "   " + mac + "   " + level;
    }
}
