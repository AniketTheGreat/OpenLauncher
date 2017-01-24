package com.benny.openlauncher;

import android.content.Intent;
import android.os.Bundle;

import com.benny.openlauncher.activity.Home;
import com.chyrta.onboarder.OnboarderActivity;
import com.chyrta.onboarder.OnboarderPage;

import java.util.ArrayList;
import java.util.List;


public class Activity_init extends OnboarderActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        List<OnboarderPage> onboarderPages = new ArrayList<>();

        // Create your first page
        OnboarderPage onboarderPage1 = new OnboarderPage(getString(R.string.intro1_title), getString(R.string.intro1_text), R.drawable.ic_launcher_intro);
        OnboarderPage onboarderPage2 = new OnboarderPage(getString(R.string.intro2_title), getString(R.string.intro2_text), R.drawable.screenshot_1);
        OnboarderPage onboarderPage3 = new OnboarderPage(getString(R.string.intro3_title), getString(R.string.intro3_text), R.drawable.screenshot_2);

        // You can define title and description colors (by default white)
        onboarderPage1.setTitleColor(R.color.md_yellow_500);
        onboarderPage1.setBackgroundColor(R.color.colorPrimary);
        onboarderPage2.setTitleColor(R.color.md_yellow_500);
        onboarderPage2.setBackgroundColor(R.color.colorPrimary);
        onboarderPage3.setTitleColor(R.color.md_yellow_500);
        onboarderPage3.setBackgroundColor(R.color.colorPrimary);

        // Add your pages to the list
        onboarderPages.add(onboarderPage1);
        onboarderPages.add(onboarderPage2);
        onboarderPages.add(onboarderPage3);

        // And pass your pages to 'setOnboardPagesReady' method
        setActiveIndicatorColor(android.R.color.white);
        setInactiveIndicatorColor(android.R.color.darker_gray);
        shouldDarkenButtonsLayout(true);
        setSkipButtonTitle(getString(R.string.intro_skip));
        setFinishButtonTitle(getString(R.string.intro_finish));
        setOnboardPagesReady(onboarderPages);
    }

    @Override
    public void onSkipButtonPressed() {
        // Optional: by default it skips onboarder to the end
        super.onSkipButtonPressed();
        // Define your actions when the user press 'Skip' button
        Intent intent = new Intent(Activity_init.this, Home.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);

        finish();
    }

    @Override
    public void onFinishButtonPressed() {
        // Define your actions when the user press 'Finish' button
        Intent intent = new Intent(Activity_init.this, Home.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);

        finish();
    }
}
