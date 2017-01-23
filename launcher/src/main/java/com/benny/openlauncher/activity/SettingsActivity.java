package com.benny.openlauncher.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.benny.openlauncher.util.AppManager;
import com.benny.openlauncher.util.Tool;
import com.benny.openlauncher.widget.AppDrawer;
import com.benny.openlauncher.widget.Desktop;
import com.bennyv5.materialpreffragment.BaseSettingsActivity;
import com.bennyv5.materialpreffragment.MaterialPrefFragment;
import com.benny.openlauncher.R;
import com.benny.openlauncher.util.LauncherSettings;

public class SettingsActivity extends BaseSettingsActivity implements MaterialPrefFragment.OnPrefClickedListener, MaterialPrefFragment.OnPrefChangedListener {

    private boolean requireLauncherRestart = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Tool.setTheme(this);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);
        setSupportActionBar((Toolbar) findViewById(R.id.tb));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle(R.string.settings);

        if (savedInstanceState == null) {
            LauncherSettings.GeneralSettings generalSettings = LauncherSettings.getInstance(this).generalSettings;
            MaterialPrefFragment fragment = MaterialPrefFragment.newInstance(new MaterialPrefFragment.Builder(this,Color.DKGRAY, getResources().getColor(R.color.Light_TextColor), getResources().getColor(R.color.Light_Background), getResources().getColor(R.color.colorAccent), false)


                    .add(new MaterialPrefFragment.GroupTitle(getString(R.string.settings_group_desktop)))
                    .add(new MaterialPrefFragment.ButtonPref("desktopMode", (getString(R.string.settings_desktopStyle)), (getString(R.string.settings_desktopStyle_summary))))
                    .add(new MaterialPrefFragment.TBPref("desktopSearchBar", (getString(R.string.settings_desktopSearch)), (getString(R.string.settings_desktopSearch_summary)), generalSettings.desktopSearchBar))
                    // FIXME: 11/25/2016 This will have problem (in allappsmode) as the apps will be cut off when scale down
                    .add(new MaterialPrefFragment.NUMPref("gridsizedesktop",(getString(R.string.settings_desktopSize)), (getString(R.string.settings_desktopSize_summary)),
                            new MaterialPrefFragment.NUMPref.NUMPrefItem("horigridsizedesktop",(getString(R.string.settings_column)), generalSettings.desktopGridX, 4, 10),
                            new MaterialPrefFragment.NUMPref.NUMPrefItem("vertgridsizedesktop",(getString(R.string.settings_row)), generalSettings.desktopGridY, 4, 10)
                    ))
                    .add(new MaterialPrefFragment.TBPref("fullscreen", (getString(R.string.settings_desktopFull)), (getString(R.string.settings_desktopFull_summary)), generalSettings.fullscreen))
                    .add(new MaterialPrefFragment.TBPref("swipe", (getString(R.string.settings_desktopClick)), (getString(R.string.settings_desktopClick_summary)), generalSettings.swipe))
                    .add(new MaterialPrefFragment.TBPref("hideIndicator", (getString(R.string.settings_desktopIndicator)), (getString(R.string.settings_desktopIndicator_summary)), generalSettings.hideIndicator))


                    .add(new MaterialPrefFragment.GroupTitle(getString(R.string.settings_group_dock)))
                    .add(new MaterialPrefFragment.TBPref("dockShowLabel",(getString(R.string.settings_dockLabel)),(getString(R.string.settings_dockLabel_summary)), generalSettings.dockShowLabel))
                    .add(new MaterialPrefFragment.NUMPref("gridsizedock",(getString(R.string.settings_dockSize)), (getString(R.string.settings_dockSize_summary)),
                            new MaterialPrefFragment.NUMPref.NUMPrefItem("horigridsizedock",(getString(R.string.settings_column)), generalSettings.dockGridX, 5, 10)
                    ))


                    .add(new MaterialPrefFragment.GroupTitle(getString(R.string.settings_group_drawer)))
                    .add(new MaterialPrefFragment.ButtonPref("drawerstyle", (getString(R.string.settings_drawerStyle)), (getString(R.string.settings_drawerStyle_summary))))
                    .add(new MaterialPrefFragment.TBPref("drawerCard", (getString(R.string.settings_drawerCard)), (getString(R.string.settings_drawerCard_summary)), generalSettings.drawerUseCard))
                    .add(new MaterialPrefFragment.TBPref("appdrawersearchbar", (getString(R.string.settings_drawerSearch)), (getString(R.string.settings_drawerSearch_summary)), generalSettings.drawerSearchBar))
                    .add(new MaterialPrefFragment.NUMPref("gridsize",(getString(R.string.settings_drawerSize)), (getString(R.string.settings_drawerSize_summary)),
                            new MaterialPrefFragment.NUMPref.NUMPrefItem("horigridsize",(getString(R.string.settings_column)), generalSettings.drawerGridX, 1, 10),
                            new MaterialPrefFragment.NUMPref.NUMPrefItem("vertgridsize",(getString(R.string.settings_row)), generalSettings.drawerGridY, 1, 10)
                    ))
                    .add(new MaterialPrefFragment.TBPref("drawerRememberPage", (getString(R.string.settings_drawerPage)), (getString(R.string.settings_drawerPage_summary)), !generalSettings.drawerRememberPage))


                    .add(new MaterialPrefFragment.GroupTitle(getString(R.string.settings_group_color)))
                    .add(new MaterialPrefFragment.ColorPref("dockBackground",(getString(R.string.settings_colorDock)),(getString(R.string.settings_colorDock_summary)),generalSettings.dockColor))
                    .add(new MaterialPrefFragment.ColorPref("drawerBackground",(getString(R.string.settings_colorDrawer)),(getString(R.string.settings_colorDrawer_summary)),generalSettings.drawerColor))
                    .add(new MaterialPrefFragment.ColorPref("drawerCardBackground",(getString(R.string.settings_colorFolder)),(getString(R.string.settings_colorFolder_summary)),generalSettings.drawerCardColor))
                    .add(new MaterialPrefFragment.ColorPref("drawerLabelColor",(getString(R.string.settings_colorLabel)),(getString(R.string.settings_colorLabel_summary)),generalSettings.drawerLabelColor))


                    .add(new MaterialPrefFragment.GroupTitle(getString(R.string.settings_group_icons)))
                    .add(new MaterialPrefFragment.NUMPref("iconsize", (getString(R.string.settings_iconSize)), (getString(R.string.settings_iconSize_summary)), generalSettings.iconSize, 30, 80))
                    .add(new MaterialPrefFragment.ButtonPref("iconpack", (getString(R.string.settings_iconPack_summary)), (getString(R.string.settings_iconPack_summary))))


                    .add(new MaterialPrefFragment.GroupTitle(getString(R.string.settings_group_others)))
                    .add(new MaterialPrefFragment.ButtonPref("restart", "Restart", "Restart the launcher"))
                    .setOnPrefChangedListener(this).setOnPrefClickedListener(this));
            setSettingsFragment(fragment);
            getSupportFragmentManager().beginTransaction().add(R.id.ll, fragment).commit();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_options,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.launcherInfo:
                startActivity(new Intent(this,AboutActivity.class));
//                new MaterialDialog.Builder(this).title("About")
//                        .content(R.string.launcherInfo)
//                        .positiveText("Ok")
//                        .negativeText("Rate")
//                        .onNegative(new MaterialDialog.SingleButtonCallback() {
//                            @Override
//                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                                String url = "https://play.google.com/store/apps/details?id=com.benny.openlauncher";
//                                Intent i = new Intent(Intent.ACTION_VIEW);
//                                i.setData(Uri.parse(url));
//                                startActivity(i);
//                            }
//                        })
//                        .neutralText("GitHub")
//                        .onNeutral(new MaterialDialog.SingleButtonCallback() {
//                            @Override
//                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                                String url = "https://github.com/BennyKok/OpenLauncher";
//                                Intent i = new Intent(Intent.ACTION_VIEW);
//                                i.setData(Uri.parse(url));
//                                startActivity(i);
//                            }
//                        })
//                        .show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPrefChanged(String id, Object p2) {
        LauncherSettings.GeneralSettings generalSettings = LauncherSettings.getInstance(this).generalSettings;
        switch (id) {
            case "drawerRememberPage":
                generalSettings.drawerRememberPage = !(boolean) p2;
                break;
            case "desktopSearchBar":
                generalSettings.desktopSearchBar = (boolean) p2;
                if (!(boolean) p2)
                    Home.launcher.searchBar.setVisibility(View.GONE);
                else
                    Home.launcher.searchBar.setVisibility(View.VISIBLE);
                break;
            case "fullscreen":
                generalSettings.fullscreen = (boolean)p2;
                prepareRestart();
                break;
            case "swipe":
                generalSettings.swipe = (boolean)p2;
                prepareRestart();
                break;
            case "hideIndicator":
                generalSettings.hideIndicator = (boolean)p2;
                prepareRestart();
                break;
            case "iconsize":
                generalSettings.iconSize = (int) p2;
                prepareRestart();
                break;
            case "horigridsize":
                generalSettings.drawerGridX = (int) p2;
                prepareRestart();
                break;
            case "vertgridsize":
                generalSettings.drawerGridY = (int) p2;
                prepareRestart();
                break;
            case "dockShowLabel":
                generalSettings.dockShowLabel = (boolean)p2;
                prepareRestart();
                break;
            case "appdrawersearchbar":
                generalSettings.drawerSearchBar = (boolean)p2;
                prepareRestart();
                break;
            case "horigridsizedesktop":
                generalSettings.desktopGridX = (int)p2;
                prepareRestart();
                break;
            case "vertgridsizedesktop":
                generalSettings.desktopGridY = (int)p2;
                prepareRestart();
                break;
            case "horigridsizedock":
                generalSettings.dockGridX = (int)p2;
                prepareRestart();
                break;
            case "dockBackground":
                generalSettings.dockColor = (int)p2;
                if (Home.launcher != null)
                    Home.launcher.dock.setBackgroundColor((int)p2);
                else
                    prepareRestart();
                break;
            case "drawerBackground":
                generalSettings.drawerColor = (int)p2;
                if (Home.launcher != null) {
                    Home.launcher.appDrawerOtter.setBackgroundColor((int) p2);
                    Home.launcher.appDrawerOtter.getBackground().setAlpha(0);
                }else
                    prepareRestart();
                break;
            case "drawerCard":
                generalSettings.drawerUseCard = (boolean)p2;
                if (Home.launcher != null) {
                    Home.launcher.appDrawerOtter.reloadDrawerCardTheme();
                }else
                    prepareRestart();
                break;
            case "drawerCardBackground":
                generalSettings.drawerCardColor = (int)p2;
                if (Home.launcher != null) {
                    Home.launcher.appDrawerOtter.reloadDrawerCardTheme();
                    prepareRestart();
                }else
                    prepareRestart();
                break;
            case "drawerLabelColor":
                generalSettings.drawerLabelColor = (int)p2;
                if (Home.launcher != null) {
                    Home.launcher.appDrawerOtter.reloadDrawerCardTheme();
                    prepareRestart();
                }else
                    prepareRestart();
                break;
        }
    }

    private void prepareRestart() {
        requireLauncherRestart = true;
    }

    @Override
    protected void onDestroy() {
        if (requireLauncherRestart && Home.launcher != null) Home.launcher.recreate();
        super.onDestroy();
    }

    @Override
    public void onPrefClicked(String id) {
        switch (id) {
            case "restart":
                if (Home.launcher != null)
                    Home.launcher.recreate();
                requireLauncherRestart = false;
                finish();
                break;
            case "iconpack":
                AppManager.getInstance(this).startPickIconPackIntent(this);
                break;
            case "drawerstyle":
                AppDrawer.startStylePicker(this);
                prepareRestart();
                break;
            case "desktopMode":
                Desktop.startStylePicker(this);
                prepareRestart();
                break;
        }
    }
}
