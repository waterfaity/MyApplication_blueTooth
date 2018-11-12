package com.waterfairy.bluetoothchart.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2018/11/12
 * @info:
 */
public class ChatActivity extends AppCompatActivity {
    private static final String TAG = "ChatActivity";
    private final int RESULT_CODE_BTDEVICE = 0;
    private ConnectionManager mConnectionManager;
    private EditText mMessageEditor;
    private ImageButton mSendBtn;
    private ListView mMessageListView;
    private MenuItem mConnectionMenuItem;
    private final static int MSG_SENT_DATA = 0;
    private final static int MSG_RECEIVE_DATA = 1;
    private final static int MSG_UPDATE_UI = 2;

    private List<String> stringList = new ArrayList<>();

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mConnectionManager = new ConnectionManager(mConnectionListener);
        mConnectionManager.startListen();
        listView = findViewById(R.id.message_list);

        PermissionUtils.requestPermission(this, PermissionUtils.REQUEST_LOCATION);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mConnectionManager != null) {
            mConnectionManager.disconnect();
            mConnectionManager.stopListen();
        }
    }

    private ConnectionManager.ConnectionListener mConnectionListener = new
            ConnectionManager.ConnectionListener() {
                @Override
                public void onConnectStateChange(int oldState, int State) {
                    Log.i(TAG, "onConnectStateChange: ");
                }

                @Override
                public void onListenStateChange(int oldState, int State) {
                    Log.i(TAG, "onListenStateChange: ");
                }

                @Override
                public void onSendData(boolean suc, byte[] data) {
                    Log.i(TAG, "onSendData: ");
                }

                @Override
                public void onReadData(byte[] data) {
                    Log.i(TAG, "onReadData: ");
                    stringList.add(new String(data));

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listView.setAdapter(new ArrayAdapter(ChatActivity.this, android.R.layout.activity_list_item, android.R.id.text1, stringList));

                        }
                    });
                }
            };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);
        mConnectionMenuItem = menu.findItem(R.id.connect_menu);
        updateUI();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.connect_menu: {
                if (mConnectionManager.getCurrentConnectState() ==
                        ConnectionManager.CONNECT_STATE_CONNECTED) {
                    mConnectionManager.disconnect();
                } else if (mConnectionManager.getCurrentConnectState() ==
                        ConnectionManager.CONNECT_STATE_CONNECTING) {
                    mConnectionManager.disconnect();
                } else if (mConnectionManager.getCurrentConnectState() ==
                        ConnectionManager.CONNECT_STATE_IDLE) {
                    Intent i = new Intent(ChatActivity.this, DeviceListActivity.class);
//下个步骤就要新建DeviceListActivity，否则缺失报错
                    startActivityForResult(i, RESULT_CODE_BTDEVICE);
                }
            }
            return true;
            case R.id.about_menu: {
                Intent i = new Intent(this, AboutActivity.class);
//下个步骤就要新建AboutActivity，否则缺失报错
                startActivity(i);
            }
            return true;
            default:
                return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult,requestCode=" + requestCode + "resultCode=" + resultCode);
        if (requestCode == RESULT_CODE_BTDEVICE && resultCode == RESULT_OK) {
            String deviceAddr = data.getStringExtra("DEVICE_ADDR");
            Log.i(TAG, "onActivityResult: " + deviceAddr);
            mConnectionManager.connect(deviceAddr);
        }
    }

    private void updateUI() {
//完整代码留待后续补上
    }

    public void send_bt(View view) {
        EditText editText = findViewById(R.id.msg_editor);
        String s = editText.getText().toString();
        if (!TextUtils.isEmpty(s)) {
            boolean b = mConnectionManager.sendData(s.getBytes());
            if (b){
                editText.setText("");
            }
        }
    }
}
