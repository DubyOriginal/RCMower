package hr.duby.rcmower.activities;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import hr.duby.rcmower.Const;
import hr.duby.rcmower.R;
import hr.duby.rcmower.util.BasicUtils;

public class TestLogActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    final String SERVER_IP = "192.168.4.1";
    final int SERVER_PORT = 81;

    //VARS
    private ArrayAdapter<String> m_adapter;
    private ArrayList<String> msgCodeList;

    //WIDGETS
    private ListView m_list_view;
    private TextView m_text_view;
    private EditText etInputMsg;
    private Button btnConnect, btnSendMsg, btnClrScr_sta, btnPumpOff;

    private WebSocketClient mWebSocketClient;


    @Override
    //**********************************************************************************************
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_log);

        msgCodeList = new ArrayList<String>();

        m_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, msgCodeList);
        m_list_view = (ListView) findViewById(R.id.id_list);
        m_list_view.setAdapter(m_adapter);
        m_list_view.setOnItemClickListener(this);

        m_text_view = (TextView) findViewById(R.id.id_tv);
        m_text_view.setText("NOT Connected!");

        etInputMsg = (EditText) findViewById(R.id.etInputMsg_sta);

        btnConnect = (Button) findViewById(R.id.btnConnect);
        btnSendMsg = (Button) findViewById(R.id.btnSendMsg_sta);
        btnClrScr_sta = (Button) findViewById(R.id.btnClrScr_sta);
        btnPumpOff = (Button) findViewById(R.id.btnPumpOff_sta);

        btnConnect.setOnClickListener(this);
        btnSendMsg.setOnClickListener(this);
        btnClrScr_sta.setOnClickListener(this);
        btnPumpOff.setOnClickListener(this);

        //m_client_thread = new Thread(this);
        //m_client_thread.start();

        //connectWebSocket();

    }

    @Override
    //**********************************************************************************************
    protected void onStop() {
        DLog("onStop");

        super.onStop();
    }

    private void updateMsgList(final String msg_code) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                msgCodeList.add(msg_code);
                m_adapter.notifyDataSetChanged();
            }
        });
    }

    private void clearList() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                msgCodeList.clear();
                m_adapter.notifyDataSetChanged();
            }
        });
    }

    //***********************************************************************************************************************************
    private void connectWebSocket() {
        String WSUrl = BasicUtils.getVALUEFromSharedPrefs(this, Const.PREF_WS, "ws://192.168.4.1:81");
        updateMsgList("connecting to: " + WSUrl);
        URI uri;
        try {
            uri = new URI(WSUrl);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        //***********************************************************************************************************************************
        mWebSocketClient = new WebSocketClient(uri, new Draft_17()) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                DLog("Websocket Opened");
                updateMsgList("Websocket Opened");
                mWebSocketClient.send("Hello from " + Build.MANUFACTURER + " " + Build.MODEL);

                //DLog("mWebSocketClient.getDraft: " + mWebSocketClient.getDraft());
                //DLog("mWebSocketClient.getURI: " + mWebSocketClient.getURI());
                //DLog("mWebSocketClient.getConnection: " + mWebSocketClient.getConnection());
            }

            @Override
            public void onMessage(String s) {
                DLog("onMessage -> " + s);
                final String message = s;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //m_tv_log.setText(m_tv_log.getText() + "\n" + message);
                        updateMsgList(">> " + message);
                    }
                });
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                DLog("Websocket Closed: " + s);
                updateMsgList("Websocket Closed " + s);
            }

            @Override
            public void onError(Exception e) {
                DLog("Websocket - Error " + e.getMessage());
                updateMsgList("Websocket - Error " + e.getMessage());
            }
        };
        mWebSocketClient.connect();
    }

    public void sendMessage(String msg) {
        DLog("webSocketClient -> sendMessage: " + msg);

        if (mWebSocketClient != null) {
            try {
                mWebSocketClient.send(msg);
                etInputMsg.setText("");

            }catch (Throwable tErr){
                DLog("Unable to send message! \n" + tErr);
            }
        } else {

        }
    }


    //***********************************************************************************************************************************
    //***********************************************************************************************************************************
    public void onClick(View view) {
        DLog("onClick");
        String msg_code = "";

        int vId = view.getId();
        //******************************************
        if (vId == R.id.btnConnect) {
            eventConnect();
            return;

        }else if (vId == R.id.btnClrScr_sta){
            clearList();
            return;

        } else if (vId == R.id.btnPumpOff_sta) {
            msg_code = "p_off";
        } else if (vId == R.id.btnSendMsg_sta) {
            msg_code = etInputMsg.getText().toString();
        }

        sendMessage(msg_code);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        etInputMsg.setText(m_adapter.getItem(position));

    }

    // EVENT HANDLING
    //***********************************************************************************************************************************
    //***********************************************************************************************************************************
    private void eventConnect(){
        updateMsgList("eventConnect");
        connectWebSocket();
    }


    //**********************************************************************************************
    private void DLog(String msg) {
        String className = this.getClass().getSimpleName();
        Log.d("DTag", className + ": " + msg);
    }

}