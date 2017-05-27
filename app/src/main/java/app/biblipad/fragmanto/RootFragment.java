package app.biblipad.fragmanto;

import android.support.v4.app.Fragment;

import app.biblipad.cusCompo.OnBackPressListener;


/**
 * Created by kashyap on 6/6/14.
 */
public class RootFragment extends Fragment implements OnBackPressListener {

    @Override
    public boolean onBackPressed() {
        return new BackPressImpl(this).onBackPressed();
    }
}
