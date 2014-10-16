package com.hendon.moodsy;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.hendon.moodsy.data.MoodDataSource;
import com.hendon.moodsy.notification.Alarm;

public class LogMoodActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    final static String[] MOODS = {"Worst day ever.", "Outright Shitty", "Ugh...", "Pretty Bad",
            "Meh", "Alright", "Good-ish", "Fairly Good", "Great!", "Fantastic!",
            "Frickin' Amazing!!!"};

    // TODO: Add UTF codes for emoji.
    final static String[] EMOJI = {};
    static MoodDataSource datasource;
    private Alarm alarm;

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    // UI Elements
//    private SeekBar seekBarSelectMood;
//    private Button buttonSubmitMood;
//    private View backgroundView;
//    private TextView moodTextView;

    // TODO: Immersive Hide Screen
    //final View decorView = getWindow().getDecorView();
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //hideSystemUI();
        setContentView(R.layout.activity_log_mood);
/*
        seekBarSelectMood = (SeekBar) findViewById(R.id.seekBarMood);
        buttonSubmitMood = (Button) findViewById(R.id.button_submit_mood);
        backgroundView = (View) findViewById(R.id.fullscreen_content);
        moodTextView = (TextView) findViewById(R.id.moodTextView);
*/
        datasource = new MoodDataSource(this);
        datasource.open();

        //final View controlsView = findViewById(R.id.fullscreen_content_controls);
        //final View contentView = findViewById(R.id.fullscreen_content);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = "Moodsy";

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        if (alarm == null) {
            alarm = new Alarm();
            alarm.setAlarm(this, null);
        }

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        //hideSystemUI();
    }
/*
    public void writeMoodToDatabase() {
        int moodIndex = seekBarSelectMood.getProgress() / 10;
        String description = MOODS[moodIndex];
        datasource.createMood(seekBarSelectMood.getProgress(), description);
        // TODO: Remove this
        Toast.makeText(this, "Wrote to database!", Toast.LENGTH_SHORT).show();
    }
    */

    @Override
    protected void onResume() {
        datasource.open();
        //hideSystemUI();
        super.onResume();
    }

    @Override
    protected void onPause() {
        datasource.close();
        super.onPause();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, LogMoodFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.drawer_record);
                break;
            case 2:
                mTitle = getString(R.string.drawer_graph);
                //Toast.makeText(this, "Coming soon!", Toast.LENGTH_SHORT).show();
                Intent graphIntent = new Intent(this, BarPlotExampleActivity.class);
                startActivity(graphIntent);
                break;
            case 3:
                mTitle = getString(R.string.drawer_list);
                Intent listIntent = new Intent(this, GraphActivity.class);
                startActivity(listIntent);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.logmood, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class LogMoodFragment extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";
        private SeekBar mSeekBarSelectMood;
        private Button mButtonSubmitMood;
        private View mBackgroundView;
        private TextView mMoodTextView;

        public LogMoodFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static LogMoodFragment newInstance(int sectionNumber) {
            LogMoodFragment fragment = new LogMoodFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_log_mood, container, false);
            setUpFragmentUI(rootView);
            return rootView;
        }

        private void setUpFragmentUI(View view) {
            mSeekBarSelectMood = (SeekBar) view.findViewById(R.id.seekBarMood);
            mButtonSubmitMood = (Button) view.findViewById(R.id.button_submit_mood);
            mBackgroundView = (View) view.findViewById(R.id.fullscreen_content);
            mMoodTextView = (TextView) view.findViewById(R.id.moodTextView);

            final int buttonYellow = Color.parseColor("#FFED90");
            final int buttonGreen = Color.parseColor("#359668");
            // final int buttonYellow  = Color.parseColor(#d3ffa926);


            //mButtonSubmitMood.setBackgroundColor(buttonYellow);

            mSeekBarSelectMood.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    int greenValue = (int) (progress * 1.5) + 25;
                    int redValue = (int) (progress * 0.125) + 25;
                    int blueValue = (int) (progress * 1.1) + 100;
                    mBackgroundView.setBackgroundColor(Color.rgb(redValue, greenValue, blueValue));
                    int moodIndex = (int) (progress / ((float) seekBar.getMax() + 1) * MOODS.length);
                    // Updates the TextView to the selected Mood
                    if (!mMoodTextView.getText().toString().equals(MOODS[moodIndex])) {
                        mMoodTextView.setText(MOODS[moodIndex]);
                    }
                }

                public void onStartTrackingTouch(SeekBar seekBar) {
                    // TODO Auto-generated method stub
                }


                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });

            mButtonSubmitMood.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    writeMoodToDatabase();
                    mButtonSubmitMood.setBackgroundColor(buttonGreen);
                    // TODO: Add Check Mark when submitted.
                }
            });
        }

        public void writeMoodToDatabase() {
            int moodIndex = mSeekBarSelectMood.getProgress() / 10;
            String description = MOODS[moodIndex];
            datasource.createMood(mSeekBarSelectMood.getProgress(), description);
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((LogMoodActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

/*    private void hideSystemUI() {
        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.
        if (decorView!=null) {
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                            | View.SYSTEM_UI_FLAG_IMMERSIVE);
        }
    }
*/

}
