package app.biblipad.actArea;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import app.biblipad.R;
import app.biblipad.functions.JSONfunctions;
import app.biblipad.functions.Static_Catelog;

public class Splash extends AppCompatActivity {
    Intent myIntent;
    public static int splash_time = 1000;
    Context context;
    CoordinatorLayout coordinatorLayout;
    private static final int PERMISSION_CALLBACK_CONSTANT = 100;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    String[] permissionsRequired = new String[]{Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE};
    private SharedPreferences permissionStatus;
    private boolean sentToSettings = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_main);
        permissionStatus = getSharedPreferences("permissionStatus",MODE_PRIVATE);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        printHashKey();
        permissions();
    }
    public void printHashKey(){
        // Add code to print out the key hash
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "app.biblipad",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }

        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }
    public void permissions(){
        if(ActivityCompat.checkSelfPermission(Splash.this, permissionsRequired[0]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(Splash.this, permissionsRequired[1]) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(Splash.this,permissionsRequired[0])
                    || ActivityCompat.shouldShowRequestPermissionRationale(Splash.this,permissionsRequired[1])){
                //Show Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(Splash.this);
                builder.setTitle("Need Multiple Permissions");
                builder.setMessage("This app needs Camera and Location permissions.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(Splash.this,permissionsRequired,PERMISSION_CALLBACK_CONSTANT);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else if (permissionStatus.getBoolean(permissionsRequired[0],false)) {
                //Previously Permission Request was cancelled with 'Dont Ask Again',
                // Redirect to Settings after showing Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(Splash.this);
                builder.setTitle("Need Multiple Permissions");
                builder.setMessage("This app needs Camera and Location permissions.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        sentToSettings = true;
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                        Toast.makeText(getBaseContext(), "Go to Permissions to Grant  Camera and Location", Toast.LENGTH_LONG).show();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }  else {
                //just request the permission
                ActivityCompat.requestPermissions(Splash.this,permissionsRequired,PERMISSION_CALLBACK_CONSTANT);
            }

            //txtPermissions.setText("Permissions Required");

            SharedPreferences.Editor editor = permissionStatus.edit();
            editor.putBoolean(permissionsRequired[0],true);
            editor.commit();
        } else {
            //You already have the permission, just go ahead.
            proceedAfterPermission();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PERMISSION_CALLBACK_CONSTANT){
            //check if all permissions are granted
            boolean allgranted = false;
            for(int i=0;i<grantResults.length;i++){
                if(grantResults[i]==PackageManager.PERMISSION_GRANTED){
                    allgranted = true;
                } else {
                    allgranted = false;
                    break;
                }
            }

            if(allgranted){
                proceedAfterPermission();
            } else if(ActivityCompat.shouldShowRequestPermissionRationale(Splash.this,permissionsRequired[0])
                    || ActivityCompat.shouldShowRequestPermissionRationale(Splash.this,permissionsRequired[1])){
                //txtPermissions.setText("Permissions Required");
                AlertDialog.Builder builder = new AlertDialog.Builder(Splash.this);
                builder.setTitle("Need Multiple Permissions");
                builder.setMessage("This app needs Camera and Location permissions.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(Splash.this,permissionsRequired,PERMISSION_CALLBACK_CONSTANT);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else {
                Toast.makeText(getBaseContext(),"Unable to get Permission",Toast.LENGTH_LONG).show();
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PERMISSION_SETTING) {
            if (ActivityCompat.checkSelfPermission(Splash.this, permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED) {
                //Got Permission
                proceedAfterPermission();
            }
        }
    }

    private void proceedAfterPermission() {
        //txtPermissions.setText("We've got all permissions");
//        isInternetAvailable();
        Toast.makeText(getBaseContext(), "We got All Permissions", Toast.LENGTH_LONG).show();
        final Handler ha = new Handler();
        ha.postDelayed(new Runnable() {

            @Override
            public void run() {
                if (isInternetAvailable()) {

                    if (Static_Catelog.getStringProperty(context,"email")==null) {
                        myIntent = new Intent(Splash.this, Home.class);
                        // startActivity(myIntent);
                        new Handler().postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                startActivity(myIntent);
                                finish();
                            }
                        }, splash_time);
//                    } else if (Static_Catelog.getStringProperty(context,"string_category")==null){
//                        myIntent = new Intent(Splash.this, CategoryActivity.class);
//                        // startActivity(myIntent);
//                        new Handler().postDelayed(new Runnable() {
//
//                            @Override
//                            public void run() {
//                                startActivity(myIntent);
//                                finish();
//                            }
//                        }, splash_time);
//                    } else if(Static_Catelog.getStringProperty(context,"five_art")==null){
//                        myIntent = new Intent(Splash.this, ChooseFive.class);
//                        new Handler().postDelayed(new Runnable() {
//
//                            @Override
//                            public void run() {
//                                startActivity(myIntent);
//                                finish();
//                            }
//                        }, splash_time);
                    }else{
                        if (Static_Catelog.getStringProperty(context,"case")==null) {
                            myIntent = new Intent(Splash.this, Home.class);
                            new Handler().postDelayed(new Runnable() {

                                @Override
                                public void run() {
                                    startActivity(myIntent);
                                    finish();
                                }
                            }, splash_time);
                        } else {
                            myIntent = new Intent(Splash.this, Home.class);
                            new Handler().postDelayed(new Runnable() {

                                @Override
                                public void run() {
                                    startActivity(myIntent);
                                    finish();
                                }
                            }, splash_time);
                        }
                    }

                } else {
                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, "No internet connection!", Snackbar.LENGTH_LONG)
                            .setAction("RETRY", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                }
                            });
                    View sbView = snackbar.getView();
                    TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(Color.YELLOW);

                    snackbar.show();






                    //  ll.setVisibility(View.VISIBLE);


                    ha.postDelayed(this, 10000);
                }
            }
        }, 500);
        Log.e("sasa","sasa3");
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (sentToSettings) {
            if (ActivityCompat.checkSelfPermission(Splash.this, permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED) {
                //Got Permission
                proceedAfterPermission();
            }
        }
    }

    public boolean isInternetAvailable() {

        return JSONfunctions.isNetworkAvailable(context);

    }
}
