package app.biblipad.actArea;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import app.biblipad.R;
import app.biblipad.cusCompo.CustomViewPager;
import app.biblipad.cusCompo.OnBackPressListener;
import app.biblipad.fragmanto.FragmentDiscover;
import app.biblipad.fragmanto.FragmentProfile;
import app.biblipad.fragmanto.FragmentStreams;

public class Home extends AppCompatActivity {

    private TabLayout tabLayout;
    private CustomViewPager viewPager;
    ViewPagerAdapter adapter;
    ImageView back;
    SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();
    String ad="ad";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        viewPager = (CustomViewPager) findViewById(R.id.viewpager);
        viewPager.setPagingEnabled(false);

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();
    }






    private void setupTabIcons() {

        RelativeLayout ll1 = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
       TextView tabOne1=(TextView)ll1.findViewById(R.id.taba) ;
        tabOne1.setText("Streams");
        tabLayout.getTabAt(0).setCustomView(ll1);

        RelativeLayout ll2 = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
       TextView tabOne2=(TextView)ll2.findViewById(R.id.taba) ;
        tabOne2.setText("Discover");
        tabLayout.getTabAt(1).setCustomView(ll2);

        RelativeLayout ll3 = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
       TextView tabOne3=(TextView)ll3.findViewById(R.id.taba) ;
        tabOne3.setText("Profile");
        tabLayout.getTabAt(2).setCustomView(ll3);
//        RelativeLayout ll4 = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
//       TextView tabOne4=(TextView)ll4.findViewById(R.id.taba) ;
//        TextView danda=(TextView) ll4.findViewById(R.id.danda) ;
//        danda.setVisibility(View.GONE);
//        tabOne4.setText("NOTIFICATION");
//        tabLayout.getTabAt(3).setCustomView(ll4);
    }

    private void setupViewPager(CustomViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new FragmentStreams(), "Streams");
        adapter.addFragment(new FragmentDiscover(), "Discover");
        adapter.addFragment(new FragmentProfile(), "Profile");


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

    public boolean onBackPresseds() {
        // currently visible tab Fragment
        OnBackPressListener currentFragment = (OnBackPressListener) adapter.getRegisteredFragment(viewPager.getCurrentItem());

        if (currentFragment != null) {
            // Log.e("not","sa");
            // lets see if the currentFragment or any of its childFragment can handle onBackPressed
            return currentFragment.onBackPressed();
        }
        Log.e("not","sad");
        // this Fragment couldn't handle the onBackPressed call
        return false;
    }
    @Override
    public void onBackPressed() {

        if (!onBackPresseds()) {
            Log.e("not","sad");
            // container Fragment or its associates couldn't handle the back pressed task
            // delegating the task to super class
            super.onBackPressed();

        } else {
            Log.e("not","sa");
            // carousel handled the back pressed task
            // do not call super
        }
    }
}
