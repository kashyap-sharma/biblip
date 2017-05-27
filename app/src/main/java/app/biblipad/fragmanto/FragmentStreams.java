package app.biblipad.fragmanto;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import app.biblipad.R;
import app.biblipad.actArea.Home;
import app.biblipad.cusCompo.CustomViewPager;

/**
 * Created by JLabs on 05/04/17.
 */

public class FragmentStreams extends RootFragment {
    private TabLayout tabLayout;
    private CustomViewPager viewPager;
    ViewPagerAdapter adapter;
    SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();
    public FragmentStreams() {
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



        View rootView = inflater.inflate(R.layout.fragment_stream, container, false);
        tabLayout = (TabLayout) rootView.findViewById(R.id.tabs);
        viewPager = (CustomViewPager) rootView.findViewById(R.id.viewpager);
        viewPager.setPagingEnabled(true);

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();


        return rootView;
    }




    private void setupTabIcons() {

        RelativeLayout ll1 = (RelativeLayout) LayoutInflater.from(getContext()).inflate(R.layout.custom_tab, null);
        TextView tabOne1=(TextView)ll1.findViewById(R.id.taba) ;
        tabOne1.setText("Latest Feeds");
        tabLayout.getTabAt(0).setCustomView(ll1);

        RelativeLayout ll2 = (RelativeLayout) LayoutInflater.from(getContext()).inflate(R.layout.custom_tab, null);
        TextView tabOne2=(TextView)ll2.findViewById(R.id.taba) ;
        tabOne2.setText("Favourites");
        tabLayout.getTabAt(1).setCustomView(ll2);

    }

    private void setupViewPager(CustomViewPager viewPager) {
        adapter = new ViewPagerAdapter(getFragmentManager());
        adapter.addFragment(new FragmenttLatestFeeds(), "Latest Feeds");
        adapter.addFragment(new FragmenttFavourites(), "Favourites");
        viewPager.setAdapter(adapter);
    }


    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }
        public Fragment getRegisteredFragment(int position) {
            return registeredFragments.get(position);
        }
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            registeredFragments.put(position, fragment);
            return fragment;
        }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            registeredFragments.remove(position);
            super.destroyItem(container, position, object);
        }



        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
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
