package app.biblipad.actArea;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import app.biblipad.AppController;
import app.biblipad.R;
import app.biblipad.functions.Static_Catelog;

public class Sign extends AppCompatActivity implements View.OnClickListener {

    private Button facebookb,jointheclub,browse;
    private LoginButton loginButton;
    Context context;
    TextView logins;
    private CallbackManager callbackManager;
    String name, first_name, last_name, email, fb_handle, photo;
    private PendingAction pendingAction = PendingAction.NONE;
    private enum PendingAction {
        NONE,
        POST_PHOTO,
        POST_STATUS_UPDATE
    }
    ViewGroup constraint_signup,constraint_signup_page;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=this;
        setContentView(R.layout.activity_sign);
        initView();
    }

    private void initView() {
        constraint_signup = (ViewGroup) findViewById(R.id._login);
        constraint_signup_page = (ViewGroup) findViewById(R.id._signup);
        constraint_signup_page.setVisibility(View.VISIBLE);
        constraint_signup.setVisibility(View.GONE);
        callbackManager = CallbackManager.Factory.create();
        facebookb = (Button) findViewById(R.id.facebooklog);
        jointheclub = (Button) findViewById(R.id.jointheclub);
        browse = (Button) findViewById(R.id.browse);
        logins = (TextView) findViewById(R.id.logins);
        loginButton = (LoginButton) findViewById(R.id.loginButton);
        loginButton.setReadPermissions(Arrays.asList("email", "user_photos", "public_profile", "user_friends"));

        facebookb.setOnClickListener(this);
        loginButton.setOnClickListener(this);
        jointheclub.setOnClickListener(this);
        browse.setOnClickListener(this);
        logins.setOnClickListener(this);

        loginButton.registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject object, GraphResponse response) {
                                        email = object.optString("email");
                                        last_name = object.optString("last_name");
                                        first_name = object.optString("first_name");
                                        fb_handle = object.optString("id");
                                        Log.e("test2",object.optString("email")+" ::: "+fb_handle);
                                        name=object.optString("first_name")+" "+object.optString("last_name");
                                        JSONObject picture = object.optJSONObject("picture");
                                        JSONObject data=picture.optJSONObject("data");
                                        try {
                                            photo=data.optString("url");
                                        } catch (Exception e) {
                                            photo="http://jlabs.co/no_image.png";
                                            e.printStackTrace();
                                        }
                                        Log.e("test1",last_name+" ::: "+photo);
                                        SendFbData();

                                    }
                                });

                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,last_name,first_name,picture.type(large),email");
                        request.setParameters(parameters);
                        request.executeAsync();


                    }


                    @Override
                    public void onCancel() {
                        Log.d("user2", "hello2");

                    }

                    @Override
                    public void onError(FacebookException exception) {

                        if (pendingAction != PendingAction.NONE
                                && exception instanceof FacebookAuthorizationException) {
                            showAlert();
                            pendingAction = PendingAction.NONE;
                        }

                    }

                    private void showAlert() {
                        new AlertDialog.Builder(Sign.this)
                                .setTitle(R.string.cancelled)
                                .setMessage(R.string.permission_not_granted)
                                .setPositiveButton(R.string.ok, null)
                                .show();
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.facebooklog:
                loginButton.performClick();
                break;
            case R.id.jointheclub:
                constraint_signup_page.setVisibility(View.GONE);
                constraint_signup.setVisibility(View.VISIBLE);
                break;
            case R.id.logins:
                constraint_signup_page.setVisibility(View.GONE);
                constraint_signup.setVisibility(View.VISIBLE);
                break;
            case R.id.browse:
                Intent intent=new Intent(this,Home.class);
                startActivity(intent);
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    private void SendFbData() {

        StringRequest jsonObjRequest = new StringRequest(Request.Method.POST,
                "http://biblipad.com/auth/signup",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("QSQSQS",""+response.toString());
                        try {
                            JSONObject respo=new JSONObject(response);
                            if(respo.getBoolean("success")){
                                Log.e("Response",""+response.toString());
                                Static_Catelog.setStringProperty(context,"email",email);
                                String m="m";
                                try {
                                    m=respo.getString("case");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                if (m.equalsIgnoreCase("2")) {
                                    Static_Catelog.setStringProperty(context,"case","2");
                                    Intent intent=new Intent(context,Home.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Intent intent=new Intent(context,Sign2.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("volley", "Error: " + error.getMessage());

            }
        }) {

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", name);
                params.put("first_name", first_name);
                params.put("last_name", last_name);
                params.put("email",email);
                params.put("created_date", "");
                params.put("fb_handle", fb_handle);
                params.put("photo", photo);
                JSONArray jsonArray=new JSONArray();
                params.put("followed_by",jsonArray.toString());
                params.put("following",jsonArray.toString());
                params.put("messages",jsonArray.toString());

                return params;
            }

        };

        AppController.getInstance().addToRequestQueue(jsonObjRequest);
    }
}
