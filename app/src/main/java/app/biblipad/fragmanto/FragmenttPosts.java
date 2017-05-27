package app.biblipad.fragmanto;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import app.biblipad.R;

/**
 * Created by JLabs on 05/04/17.
 */

public class FragmenttPosts extends RootFragment {
    public FragmenttPosts() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        addFragB();
        // Inflate the layout for this fragment



        View rootView = inflater.inflate(R.layout.fragment_posts, container, false);
        Log.e("now m at","brouse");
//        rootView.findViewById(R.id.by_artist).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                addFragB();
//            }
//        });


        return rootView;
    }

    public void addFragB() {
//        FragmentManager childFragMan = getChildFragmentManager();
//
//        FragmentTransaction childFragTrans = childFragMan.beginTransaction();
//        FragmentDash fragB = new FragmentDash();
//        childFragTrans.add(R.id.fragA_LinearLayout, fragB);
//        childFragTrans.addToBackStack("B");
//        childFragTrans.commit();

//        FragmentBrowse1 a2Fragment = new FragmentBrowse1();
//        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
//
//        // Store the Fragment in stack
//        transaction.addToBackStack("B");
//        transaction.replace(R.id.fragA_LinearLayout, a2Fragment).commit();

    }

}
