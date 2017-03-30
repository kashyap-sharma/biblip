package app.biblipad.actArea;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import app.biblipad.R;
import app.biblipad.functions.JSONfunctions;
import app.biblipad.functions.Static_Catelog;

public class Sign2 extends AppCompatActivity implements View.OnClickListener {

    private EditText username;
    private EditText blogname;
    private EditText blogurl;
    private EditText writesabt;
    private Button join;
    boolean isTwise = false ;
    boolean isEdit = true ;
    Context context;
    private ProgressDialog pdia;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=this;
        setContentView(R.layout.activity_sign2);
        initView();
    }

    private void initView() {
        username = (EditText) findViewById(R.id.username);
        blogname = (EditText) findViewById(R.id.blogname);
        blogurl = (EditText) findViewById(R.id.blogurl);
        writesabt = (EditText) findViewById(R.id.writesabt);
        join = (Button) findViewById(R.id.join);

        join.setOnClickListener(this);
        double scaletype =getResources().getDisplayMetrics().density;
        if(scaletype >=3.0){
            isTwise = true ;
        }
        writesabt.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (count >= 1 && !isEdit) {
                    if (!Character.isSpaceChar(s.charAt(0))) {
                        if (s.charAt(start) == ' ')
                            setTag(); // generate chips
                    } else {
                        writesabt.getText().clear();
                        writesabt.setSelection(0);
                    }

                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isEdit) {
                    setTag();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.join:
                submit();
                break;
        }
    }

    private void submit() {
        // validate
        String usernameString = username.getText().toString().trim();
        if (TextUtils.isEmpty(usernameString)) {
            Toast.makeText(this, "Username", Toast.LENGTH_SHORT).show();
            return;
        }

        String blognameString = blogname.getText().toString().trim();
        if (TextUtils.isEmpty(blognameString)) {
            Toast.makeText(this, "Blog Name", Toast.LENGTH_SHORT).show();
            return;
        }

        String blogurlString = blogurl.getText().toString().trim();
        if (TextUtils.isEmpty(blogurlString)) {
            Toast.makeText(this, "Blog URL (eg. http://xyz.com)", Toast.LENGTH_SHORT).show();
            return;
        }

        String writesabtString = writesabt.getText().toString().trim();
        if (TextUtils.isEmpty(writesabtString)) {
            Toast.makeText(this, "Writes about(eg. tech)", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.e("hanji",writesabtString);
        // TODO validate success, do something

        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("username",usernameString);
            jsonObject.put("blogUrl",blogurlString);
            jsonObject.put("blogName",blognameString);
            jsonObject.put("email", Static_Catelog.getStringProperty(context,"email"));
            JSONArray jsonArray=new JSONArray();
            String chip[] =writesabt.getText().toString().trim().split(" ");
            for(int i=0;i<chip.length;i++){
                JSONObject jsonObject1=new JSONObject();
                jsonObject1.put("text",chip[i].toString());
                jsonArray.put(jsonObject1);
            }
            jsonObject.put("blogCategories",jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("niggas",jsonObject.toString());
        new SignTwo(jsonObject).execute();

    }


    private class SignTwo extends AsyncTask<String,Void,Void>
    {
        JSONObject object;
        SignTwo(JSONObject obj)
        {
            object=obj;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdia = new ProgressDialog(context);
            pdia.setMessage("Saving...");
            pdia.show();
        }

        @Override
        protected Void doInBackground(String... args) {
            JSONObject obj= JSONfunctions.makenewHttpRequest(context, "http://biblipad.com/auth/updateProfile", object);
            try {
                if (obj.getBoolean("success")) {
                    Log.e("datata",""+obj.toString());
                    Intent intent =new Intent(context, Home.class);
                    startActivity(intent);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void val) {
            super.onPostExecute(val);
            pdia.dismiss();
            Toast.makeText(context, "Sign Up Completed.", Toast.LENGTH_LONG).show();

        }
    }



    public void setTag() {
        if (writesabt.getText().toString().contains(" ")||writesabt.getText().toString().contains(",")) // check comman in string
        {

            SpannableStringBuilder ssb = new SpannableStringBuilder(writesabt.getText());
            // split string wich comma
            String chips[] =writesabt.getText().toString().trim().split(" ");

            int x = 0;
            for (String c : chips) {
                LayoutInflater lf = (LayoutInflater)getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
                TextView textView = (TextView) lf.inflate(
                        R.layout.tag_edittext, null);
                textView.setText(c); // set text
                int spec = View.MeasureSpec.makeMeasureSpec(0,
                        View.MeasureSpec.UNSPECIFIED);
                textView.measure(spec, spec);
                textView.layout(0, 0, textView.getMeasuredWidth(),
                        textView.getMeasuredHeight());
                Bitmap b = Bitmap.createBitmap(textView.getWidth(),
                        textView.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(b);
                canvas.translate(-textView.getScrollX(), -textView.getScrollY());
                textView.draw(canvas);
                textView.setDrawingCacheEnabled(true);
                Bitmap cacheBmp = textView.getDrawingCache();
                Bitmap viewBmp = cacheBmp.copy(Bitmap.Config.ARGB_8888, true);
                textView.destroyDrawingCache(); // destory drawable
                BitmapDrawable bmpDrawable = new BitmapDrawable(viewBmp);
                int width = bmpDrawable.getIntrinsicWidth() ;
                int height = bmpDrawable.getIntrinsicHeight() ;
                if(isTwise){
                    width = width *2 ;
                    height = height *2;
                }
                bmpDrawable.setBounds(0, 0,width ,height);
                ssb.setSpan(new ImageSpan(bmpDrawable), x, x + c.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                x = x + c.length() + 1;
            }
            // set chips span
            isEdit = false ;
            writesabt.setText(ssb);
            // move cursor to last
            writesabt.setSelection(writesabt.getText().length());
        }

    }
    public  int convertDpToPixel(float dp){
        Resources resources = getApplicationContext().getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return (int)px;
    }
}
