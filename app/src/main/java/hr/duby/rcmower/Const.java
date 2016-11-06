package hr.duby.rcmower;

/**
 * Created by Duby on 18.9.2016..
 */
public class Const {

    public static final int LOW = 0;
    public static final int HIGHT = 1;
    public static final int SPEED = 120;

    public static final int HCSR04_MAX = 4000;   //4000mm -> 4m

    //public final static String UNIT_IP   = "192.168.1.26";
    //public final static String UNIT_PORT = "80";

    //WEB API - REST
    //**********************************************************************************************
    //public static final String BASE_URL_RADIO = "http://test.mradio.tria.hr/";
    //public static final String BASE_URL    = "http://" + UNIT_IP + ":" + UNIT_PORT;
    public static final String CMD_FORWARD   = "/cmd/forward";
    public static final String CMD_BACK      = "/cmd/back";
    public static final String CMD_RLEFT     = "/cmd/rotateleft";
    public static final String CMD_RRIGHT    = "/cmd/rotateright";
    public static final String CMD_STOP      = "/cmd/stop";
    public static final String CMD_SPEED     = "/cmd/speed/";

    public static final String SENSOR_HCSR04 = "/sensor/hcsr04";

    //shared prefs key
    public static final String PREF_IP = "PREF_IP";
    public static final String PREF_PORT = "PREF_PORT";

    //GENERAL
    //**********************************************************************************************
    public static final int MSG_TYPE_ERROR = 8001;
    public static final int MSG_TYPE_WARNING = 8002;
    public static final int MSG_TYPE_INFO = 8003;
}
