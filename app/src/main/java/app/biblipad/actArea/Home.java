package app.biblipad.actArea;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import app.biblipad.R;

public class Home extends DrawerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int setLayout() {
        return R.layout.activity_drawer;
    }
}
