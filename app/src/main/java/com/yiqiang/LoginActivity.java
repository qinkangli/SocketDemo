package com.yiqiang;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


/**
 * A login screen that offers login via username.
 */
public class LoginActivity extends Activity {

    private EditText mUsernameView;

    private String mUsername;

    private Socket mSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //ChatApplication app = (ChatApplication) getApplication();


        // Set up the login form.
        mUsernameView = (EditText) findViewById(R.id.username_input);
        mUsernameView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        findViewById(R.id.connect_button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mSocket = mSocket = IO.socket("http://api.staxi.app.estronger.cn"+"/system/get_socket_host?type=2");
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
                mSocket.on("new_msg", message);
                mSocket.on("update_online_count", onlineCount);
                //mSocket.on(Socket.EVENT_CONNECT,OnConnect);
                //mSocket.on(Socket.EVENT_CONNECT_ERROR,OnConnectError);
                mSocket.connect();
            }
        });


        Button signInButton = (Button) findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });


    }
    private Emitter.Listener OnConnect = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.i("MM","OnConnect="+args[0].toString());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(LoginActivity.this,args[0].toString(),Toast.LENGTH_SHORT).show();
                }
            });
        }
    };
    private Emitter.Listener OnConnectError = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.i("MM","OnConnectError="+args[0].toString());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(LoginActivity.this,args[0].toString(),Toast.LENGTH_SHORT).show();
                }
            });
        }
    };

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.i("MM",(boolean)msg.obj+"");
            mSocket.connect();
            Log.i("MM","是否连接上="+mSocket.connected());
        }
    };
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSocket.disconnect();
        mSocket.off("new_msg", message);
        mSocket.off("update_online_count", onlineCount);

    }

    /**
     * Attempts to sign in the account specified by the login form.
     * If there are form errors (invalid username, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        // Reset errors.
        mUsernameView.setError(null);

        // Store values at the time of the login attempt.
        String username = mUsernameView.getText().toString().trim();

        // Check for a valid username.
        if (TextUtils.isEmpty(username)) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            mUsernameView.setError(getString(R.string.error_field_required));
            mUsernameView.requestFocus();
            return;
        }

        mUsername = username;

        // perform the user login attempt.
        mSocket.emit("login", username);
    }




    private Emitter.Listener message = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.i("MM",args[0].toString());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(LoginActivity.this,args[0].toString(),Toast.LENGTH_SHORT).show();
                }
            });
        }
    };

    private Emitter.Listener onlineCount = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.i("MM",args[0].toString());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(LoginActivity.this,args[0].toString(),Toast.LENGTH_SHORT).show();
                }
            });
        }
    };
}



