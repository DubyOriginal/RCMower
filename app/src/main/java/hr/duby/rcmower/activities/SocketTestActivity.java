package hr.duby.rcmower.activities;

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

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;

import hr.duby.rcmower.R;

public class SocketTestActivity extends AppCompatActivity implements Runnable, View.OnClickListener, AdapterView.OnItemClickListener {
    /**
     * Called when the activity is first created.
     */

    final String SERVER_IP = "192.168.4.1";
    final int SERVER_PORT = 81;

    final static int TIME_OUT = 5000;
    final static byte NM_SEND_OS_TYPE = 1;
    final static byte NM_SEND_MESSAGE = 2;


    private Socket m_client_socket = null;
    private BufferedOutputStream m_out_stream = null;
    private InputStream m_in_stream = null;


    private Thread m_client_thread;

    private ArrayAdapter<String> m_adapter;

    //WIDGETS
    private ListView m_list_view;
    private TextView m_text_view;
    private EditText etInputMsg;
    private Button btnSendMsg, btnPumpOn, btnPumpOff;

    private String m_recv_string;
    private String m_debug_string;

    //연결 상태 표시용 쓰레드
    Runnable m_debug_run = new Runnable() {
        public void run() {
            m_text_view.setText(m_debug_string);
        }
    };

    Runnable m_insert_list_run = new Runnable() {
        public void run() {

            DLog("m_insert_list_run...");

            int count = m_adapter.getCount();
            m_adapter.insert(m_recv_string, count);
            m_list_view.setSelection(count);

            etInputMsg.setText("");
        }

    };


    @Override
    //**********************************************************************************************
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_socket_test);

        ArrayList<String> list_string = new ArrayList<String>();

        m_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list_string);
        m_list_view = (ListView) findViewById(R.id.id_list);
        m_list_view.setAdapter(m_adapter);
        m_list_view.setOnItemClickListener(this);

        m_text_view = (TextView) findViewById(R.id.id_tv);
        m_text_view.setText("tring connect!");

        etInputMsg = (EditText) findViewById(R.id.etInputMsg_sta);

        btnSendMsg = (Button) findViewById(R.id.btnSendMsg_sta);
        btnPumpOn = (Button) findViewById(R.id.btnPumpOn_sta);
        btnPumpOff = (Button) findViewById(R.id.btnPumpOff_sta);

        btnSendMsg.setOnClickListener(this);
        btnPumpOn.setOnClickListener(this);
        btnPumpOff.setOnClickListener(this);

        m_client_thread = new Thread(this);
        m_client_thread.start();

    }

    @Override
    //**********************************************************************************************
    protected void onStop() {

        if (m_client_socket != null) {
            try {

                if (m_in_stream != null) m_client_socket.shutdownInput();
                if (m_out_stream != null) m_client_socket.shutdownOutput();

                m_client_thread.interrupt();
                m_client_thread.join();

            } catch (Exception e) {
                //nothing
            }
        }

        super.onStop();
    }

    //***********************************************************************************************************************************
    //***********************************************************************************************************************************

    //**********************************************************************************************
    public void run() {

        try {
            SocketAddress sock_addr = new InetSocketAddress(SERVER_IP, SERVER_PORT);
            m_client_socket = new Socket();
            m_client_socket.setReceiveBufferSize(1024);
            m_client_socket.setSendBufferSize(1024);
            m_client_socket.setSoLinger(true, TIME_OUT);
            m_client_socket.setSoTimeout(1000 * 60 * 15);
            m_client_socket.connect(sock_addr, TIME_OUT);
            m_debug_string = "Connect Server!!";
            m_text_view.post(m_debug_run);

            m_out_stream = new BufferedOutputStream(m_client_socket.getOutputStream());
            m_in_stream = m_client_socket.getInputStream();

            int data_size;

            String cmd_buffer = "wO";
            data_size = cmd_buffer.length();
            DLog("----data_size--------> " + data_size);


            byte[] data = cmd_buffer.getBytes();

            DLog("------data0------> " + data[0]);
            DLog("------data1------> " + data[1]);

            byte[] size = new byte[2];
            size[0] = (byte) (data_size & 0x00ff);
            size[1] = (byte) ((data_size & 0xff00) >> 8);


            int s = ((size[1] << 8) & 0xff00) + (size[0] & 0x00ff);
            DLog("------result------> " + s);


            // #O   open
            // #C   close
            // #S   Get current status
            // #T   Obtaining Temperature
            //  m_out_stream.write(NM_SEND_OS_TYPE);
            m_out_stream.write(size);
            m_out_stream.write(data);
            m_out_stream.flush();

            onReadStream();

        } catch (Exception e) {

            m_debug_string = e.getMessage();
            m_text_view.post(m_debug_run);

        } finally {

            try {
                if (m_client_socket != null) {
                    if (m_out_stream != null) {
                        m_out_stream.close();
                    }
                    if (m_in_stream != null) {
                        m_in_stream.close();
                    }

                    m_client_socket.close();
                    m_client_socket = null;
                }
            } catch (IOException e) {
                //nothing
            }

        }
    }

    //***********************************************************************************************************************************
    public void onReadStream() throws IOException {

        byte msg_id;
        byte[] size = new byte[2];


        while (!m_client_thread.isInterrupted()) {

            msg_id = (byte) m_in_stream.read();

            DLog("-------------------" + msg_id);

            if (msg_id == NM_SEND_MESSAGE) {
                // Read 2 bytes of size information.
                if (m_in_stream.read(size) == 2) {
                    // Because Linux and Windows, which are Android's bases, have different Byte Ordering
                    // When transmitting / receiving two bytes of data, it is necessary to change the value one byte at a time.
                    int data_size = 0;
                    data_size = size[1];
                    data_size = data_size << 8;
                    data_size = data_size | size[0];
                    // Allocate an array of data size.
                    byte[] data = new byte[data_size];
                    // Data is read as much as the data size.
                    if (m_in_stream.read(data) == data_size) {
                        // Converts byte data to a string.
                        m_recv_string = new String(data);
                        //Pass the m_insert_list_run interface to the main thread so that the string is added to the list view.
                        m_text_view.post(m_insert_list_run);
                    }
                }
            }
        }
    }

    //***********************************************************************************************************************************
    //***********************************************************************************************************************************
    public void onClick(View view) {

        if (m_client_socket != null && m_client_socket.isConnected() && !m_client_socket.isClosed()) {

            int data_size;
            String cmd_buffer = null;
            try {
                //It should be done in a thread.
                //******************************************
                if(view.getId() == R.id.btnPumpOn_sta) {
                    cmd_buffer = "wo";
                }else if (view.getId() == R.id.btnPumpOff_sta){
                    cmd_buffer = "wf";
                }else if (view.getId() == R.id.btnSendMsg_sta){
                    cmd_buffer = etInputMsg.getText().toString();
                    //byte[] data = cmd_buffer.getBytes();
                    //data_size = data.length;

                    // Stores the size of the byte. Because Linux and Windows, which are Android's bases, have different Byte Ordering
                    // When sending / receiving 2 bytes of data, you have to change the value by 1 byte.
                    //byte[] size = new byte[2];
                    //size[0] = (byte)data_size;
                    //size[1] = (byte)(data_size >> 8);
                }

                DLog("---button-------> " +  cmd_buffer);

                data_size = cmd_buffer.length();
                DLog("----data_size--------> " + data_size);

                byte[] data = cmd_buffer.getBytes();

                byte[] size = new byte[2];
                size[0] = (byte) (data_size & 0x00ff);   //1을 넣고
                size[1] = (byte) ((data_size & 0xff00) >> 8);       //0으로 채운다.

                int s = ((size[1] << 8) & 0xff00) + (size[0] & 0x00ff);
                DLog("------result------> " + s);

                m_out_stream.write(size);   // Write size information.
                m_out_stream.write(data);   //Write a byte array.
                m_out_stream.flush(); // It sends the information written to the stream to the server.


            } catch (IOException e) {

                m_text_view.setText(e.getMessage());
            }
        } else {
            DLog("---------non connect");
        }
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