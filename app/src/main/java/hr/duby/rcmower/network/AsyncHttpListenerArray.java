package hr.duby.rcmower.network;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by dvrbancic on 22/08/16.
 */
public interface AsyncHttpListenerArray extends AsyncHttpListener {

    public void onGetArrayDone(JSONArray array);

}
