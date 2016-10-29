package hr.duby.rcmower;

/**
 * Created by Duby on 18.9.2016..
 */
public class Const {

    public final static int LOW = 0;
    public final static int HIGHT = 1;
    public final static int SPEED = 120;

    //public final static String UNIT_IP   = "192.168.1.26";
    //public final static String UNIT_PORT = "80";

    //WEB API - REST
    //**********************************************************************************************
    //public static final String BASE_URL_RADIO = "http://test.mradio.tria.hr/";
    //public static final String BASE_URL    = "http://" + UNIT_IP + ":" + UNIT_PORT;
    public static final String CMD_FORWARD = "/cmd/forward";
    public static final String CMD_BACK    = "/cmd/back";
    public static final String CMD_RLEFT   = "/cmd/rotateleft";
    public static final String CMD_RRIGHT  = "/cmd/rotateright";
    public static final String CMD_STOP    = "/cmd/stop";
    public static final String CMD_SPEED    = "/cmd/speed/";

    //shared prefs key
    public final static String PREF_IP = "PREF_IP";
    public final static String PREF_PORT = "PREF_PORT";

    //GENERAL
    //**********************************************************************************************
    public static final int MSG_TYPE_ERROR = 8001;
    public static final int MSG_TYPE_WARNING = 8002;
    public static final int MSG_TYPE_INFO = 8003;
}
