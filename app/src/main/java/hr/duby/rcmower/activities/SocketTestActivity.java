package hr.duby.rcmower.activities;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import hr.duby.rcmower.R;

public class SocketTestActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    final String SERVER_IP = "192.168.4.1";
    final int SERVER_PORT = 81;

    //VARS
    private ArrayAdapter<String> m_adapter;
    private ArrayList<String> msgCodeList;

    //WIDGETS
    private ListView m_list_view;
    private TextView m_text_view;
    private TextView m_tv_log;
    private EditText etInputMsg;
    private Button btnSendMsg, btnPumpOn, btnPumpOff;

    private WebSocketClient mWebSocketClient;


    @Override
    //**********************************************************************************************
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_socket_test);

        msgCodeList = new ArrayList<String>();

        m_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, msgCodeList);
        m_list_view = (ListView) findViewById(R.id.id_list);
        m_list_view.setAdapter(m_adapter);
        m_list_view.setOnItemClickListener(this);

        m_text_view = (TextView) findViewById(R.id.id_tv);
        m_text_view.setText("NOT Connected!");


        m_tv_log = (TextView) findViewById(R.id.tv_log);
        m_tv_log.setText("LOG");

        etInputMsg = (EditText) findViewById(R.id.etInputMsg_sta);

        btnSendMsg = (Button) findViewById(R.id.btnSendMsg_sta);
        btnPumpOn = (Button) findViewById(R.id.btnPumpOn_sta);
        btnPumpOff = (Button) findViewById(R.id.btnPumpOff_sta);

        btnSendMsg.setOnClickListener(this);
        btnPumpOn.setOnClickListener(this);
        btnPumpOff.setOnClickListener(this);

        //m_client_thread = new Thread(this);
        //m_client_thread.start();

        connectWebSocket();

    }

    @Override
    //**********************************************************************************************
    protected void onStop() {
        DLog("onStop");

        super.onStop();
    }

    private void updateMsgList(final String msg_code){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                msgCodeList.add(msg_code);
                m_adapter.notifyDataSetChanged();
            }
        });
    }

    //***********************************************************************************************************************************
    private void connectWebSocket() {
        URI uri;
        try {
            uri = new URI("ws://192.168.4.1:81");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        //***********************************************************************************************************************************
        mWebSocketClient = new WebSocketClient(uri, new Draft_17()){
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                DLog("Websocket Opened");
                updateMsgList("Websocket Opened");
                mWebSocketClient.send("Hello from " + Build.MANUFACTURER + " " + Build.MODEL);
            }

            @Override
            public void onMessage(String s) {
                final String message = s;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        m_tv_log.setText(m_tv_log.getText() + "\n" + message);
                    }
                });
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                DLog("Websocket Closed " + s);
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
        mWebSocketClient.send(msg);
        etInputMsg.setText("");
    }


    //***********************************************************************************************************************************
    //***********************************************************************************************************************************
    public void onClick(View view) {
        DLog("onClick");
        String msg_code = "";

        int vId = view.getId();
        //******************************************
        if(vId == R.id.btnPumpOn_sta) {
            msg_code = "p_on";
        }else if (vId == R.id.btnPumpOff_sta){
            msg_code = "p_off";
        }else if (vId == R.id.btnSendMsg_sta){
            msg_code = etInputMsg.getText().toString();
        }

        sendMessage(msg_code);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        etInputMsg.setText(m_adapter.getItem(position));

    }


    //**********************************************************************************************
    private void DLog(String msg) {
        String className = this.getClass().getSimpleName();
        Log.d("DTag", className + ": " + msg);
    }

}