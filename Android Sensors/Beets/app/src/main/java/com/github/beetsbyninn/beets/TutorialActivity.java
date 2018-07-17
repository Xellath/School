package com.github.beetsbyninn.beets;


import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.os.Bundle;

public class TutorialActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        ViewPager mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mViewPager.setAdapter(new MyAdapter());

        TabLayout tabLayout = (TabLayout)findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(mViewPager, true);

    }

    private class MyAdapter extends PagerAdapter {
        public int getCount() {
            return 4;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = null;
            LayoutInflater inflater = (LayoutInflater) container.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            switch (position) {
                case 0:
                    view = inflater.inflate(R.layout.first_welcome_slide,null);
                    break;
                case 1:
                    view = inflater.inflate(R.layout.second_welcome_slide,null);
                    break;
                case 2:
                    view = inflater.inflate(R.layout.third_welcome_slide,null);
                    break;
                case 3:
                    view = inflater.inflate(R.layout.last_welcome_slide,null);
                    Button readyBtn = (Button) view.findViewById(R.id.btnDoneTutorial);
                    readyBtn.setOnClickListener(new ButtonListener());
                    break;
            }
            ((ViewPager) container).addView(view, 0);

            return view;

        }

        /*
        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView((View) arg2);
        }

        */

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View)object);
        }

        /*
        @Override
        public Parcelable saveState() {
            return null;
        }
        */

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == ((View) arg1);
        }

        private class ButtonListener implements OnClickListener {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),MainActivity.class);
                TutorialActivity.this.startActivity(i);
                finish();
            }
        }
    }
}


