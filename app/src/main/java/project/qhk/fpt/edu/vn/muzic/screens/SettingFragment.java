package project.qhk.fpt.edu.vn.muzic.screens;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import project.qhk.fpt.edu.vn.muzic.Logistic;
import project.qhk.fpt.edu.vn.muzic.R;
import project.qhk.fpt.edu.vn.muzic.notifiers.FragmentChanger;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends Fragment {

    @BindView(R.id.toolbar)
    Toolbar myToolbar;

    @BindView(R.id.tab_layout)
    TabLayout myTabLayout;

    @BindView(R.id.pager)
    ViewPager myViewPager;


    public SettingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        settingThingsUp(view);

        return view;
    }

    private void settingThingsUp(View view) {
        ButterKnife.bind(this, view);

        goTabLayout();
    }

    private void goTabLayout() {
        myToolbar.setTitle(Logistic.TITLE);
        myToolbar.inflateMenu(R.menu.menu_main);

        myToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                System.out.println("onMenuItemClick");
                EventBus.getDefault().post(new FragmentChanger(
                        SettingFragment.class.getSimpleName(), new LoginFragment(), true));

                return true;
            }
        });

        myTabLayout.addTab(myTabLayout.newTab().setText(Logistic.GENRES));
        myTabLayout.addTab(myTabLayout.newTab().setText(Logistic.PLAYLIST));
        myTabLayout.addTab(myTabLayout.newTab().setText(Logistic.SEARCH));

        myTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        myViewPager.setAdapter(new project.qhk.fpt.edu.vn.muzic.adapters.PagerAdapter(
                getChildFragmentManager(), myTabLayout.getTabCount()) {
        });

        myViewPager.addOnPageChangeListener(
                new TabLayout.TabLayoutOnPageChangeListener(myTabLayout));
        myTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                myViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

}