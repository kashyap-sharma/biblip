package app.biblipad.actArea;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import app.biblipad.AppController;
import app.biblipad.R;
import app.biblipad.functions.JSONfunctions;
import app.biblipad.functions.Static_Catelog;

public class Sign extends FragmentActivity implements GoogleApiClient.OnConnectionFailedListener,View.OnClickListener{

    private Button facebookb,googlelog,jointheclub,browse;
    private LoginButton loginButton;
    Context context;
    TextView logins;
    private CallbackManager callbackManager;
    String name, first_name, last_name, email, fb_handle, photo;
    private PendingAction pendingAction = PendingAction.NONE;
    Boolean loginviafacebook=true;
    Boolean loginviagmail=false;
    private static final int STATE_DEFAULT = 0;
    private enum PendingAction {
        NONE,
        POST_PHOTO,
        POST_STATUS_UPDATE
    }
    private int mSignInProgress;
    ViewGroup constraint_signup,constraint_signup_page;
    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;
    private GoogleApiClient mGoogleApiClient;
    private static final String SAVED_PROGRESS = "sign_in_progress";
    ProgressDialog mProgressDialog;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=this;
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        setContentView(R.layout.activity_sign);
        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        initView();
        if (savedInstanceState != null) {
            mSignInProgress = savedInstanceState
                    .getInt(SAVED_PROGRESS, STATE_DEFAULT);
        }
    }

    private void initView() {
        mProgressDialog = new ProgressDialog(this);
        constraint_signup = (ViewGroup) findViewById(R.id._login);
        constraint_signup_page = (ViewGroup) findViewById(R.id._signup);
        constraint_signup_page.setVisibility(View.VISIBLE);
        constraint_signup.setVisibility(View.GONE);
        callbackManager = CallbackManager.Factory.create();
        facebookb = (Button) findViewById(R.id.facebooklog);
        googlelog = (Button) findViewById(R.id.googlelog);
        jointheclub = (Button) findViewById(R.id.jointheclub);

        logins = (TextView) findViewById(R.id.logins);
        loginButton = (LoginButton) findViewById(R.id.loginButton);
        loginButton.setReadPermissions(Arrays.asList("email", "user_photos", "public_profile", "user_friends"));

        facebookb.setOnClickListener(this);
        googlelog.setOnClickListener(this);
        loginButton.setOnClickListener(this);
        jointheclub.setOnClickListener(this);
        logins.setOnClickListener(this);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        loginButton.registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        loginviagmail=false;
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
            case R.id.googlelog:
                signinGoogle();
                break;
            case R.id.jointheclub:
                constraint_signup_page.setVisibility(View.GONE);
                constraint_signup.setVisibility(View.VISIBLE);
                break;
            case R.id.logins:
                constraint_signup_page.setVisibility(View.GONE);
                constraint_signup.setVisibility(View.VISIBLE);
                break;

        }
    }

    private void signinGoogle() {
        loginviafacebook=false;
        if(isNetworkAvailable()) {
            signOut();
            signIn();
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Please Check internet connectivity", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (loginviafacebook) {
            super.onActivityResult(requestCode, resultCode, data);
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            int statusCode = result.getStatus().getStatusCode();
            Log.e("chdsa",""+statusCode);
            handleSignInResult(result);
        }
    }

    public boolean isNetworkAvailable() {

        return JSONfunctions.isNetworkAvailable(context);

    }




    @Override
    public void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }



    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();


//            Log.e("email",""+acct.getEmail());
//            Log.e("id",""+acct.getId());
//            Log.e("url",""+acct.getPhotoUrl());
//            Log.e("last_name",""+acct.getFamilyName());
//            Log.e("first_name",""+acct.getGivenName());
            email = acct.getEmail();
            last_name = acct.getFamilyName();
            first_name = acct.getGivenName();
            fb_handle = acct.getId();
            name=first_name+" "+last_name;
            try {
                photo=acct.getPhotoUrl().toString();
            } catch (Exception e) {
                photo="http://jlabs.co/no_image.png";
                e.printStackTrace();
            }
            sendGoogle();



            // mStatusTextView.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));

        }
    }
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]

                        // [END_EXCLUDE]
                    }
                });
    }
    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]

                        // [END_EXCLUDE]
                    }
                });
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Loading");
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    private void SendFbData() {

        StringRequest jsonObjRequest = new StringRequest(Request.Method.POST,
                "http://biblipad.com/auth/fbLogin",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("QSQSQS",""+response.toString());
                        try {
                            JSONObject respo=new JSONObject(response);
                            if(respo.getBoolean("success")){
                                Log.e("Response",""+response.toString());
                                Static_Catelog.setStringProperty(context,"email",email);
                                Static_Catelog.setStringProperty(context,"token",respo.getString("token"));
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
                params.put("gplus_handle", "");
                params.put("photo", photo);


                return params;
            }

        };

        AppController.getInstance().addToRequestQueue(jsonObjRequest);
    }
    private void sendGoogle() {

        StringRequest jsonObjRequest = new StringRequest(Request.Method.POST,
                "http://biblipad.com/auth/gplusLogin",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("QSQSQS",""+response.toString());
                        try {
                            JSONObject respo=new JSONObject(response);
                            if(respo.getBoolean("success")){
                                Log.e("Response",""+response.toString());
                                Static_Catelog.setStringProperty(context,"email",email);
                                Static_Catelog.setStringProperty(context,"token",respo.getString("token"));
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
                params.put("fb_handle", "");
                params.put("gplus_handle", fb_handle);
                params.put("photo", photo);


                return params;
            }

        };

        AppController.getInstance().addToRequestQueue(jsonObjRequest);
    }
}
