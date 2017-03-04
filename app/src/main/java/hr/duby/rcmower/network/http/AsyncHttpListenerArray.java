package hr.duby.rcmower.network.http;

import org.json.JSONArray;

/**
 * Created by dvrbancic on 22/08/16.
 */
public interface AsyncHttpListenerArray extends AsyncHttpListener {

    public void onGetArrayDone(JSONArray array);

}
