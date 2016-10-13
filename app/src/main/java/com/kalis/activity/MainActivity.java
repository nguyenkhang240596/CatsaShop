package com.kalis.activity;


import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;

import com.facebook.CallbackManager;
import com.google.android.gcm.GCMRegistrar;
import com.kalis.R;
import com.kalis.ServerTask;
import com.kalis.adapter.ListViewAdapter;
import com.kalis.adapter.ViewPagerAdapter;
import com.kalis.connection.ConnectionDetector;
import com.kalis.dialog.AlertDialog;
import com.kalis.dialog.MyToast;
import com.kalis.fragment.HomePageFragment;
import com.kalis.fragment.ProductFragment;
import com.kalis.keys.KeySource;
import com.kalis.listener.SearchViewListener;
import com.kalis.log.LogSystem;
import com.kalis.model.Category;
import com.kalis.model.Product;
import com.kalis.request.ProductRequest;
import com.kalis.request.RequestCategories;
import com.kalis.request.RequestProducts;
import com.kalis.slidingtabs.SlidingTabLayout;
import com.kalis.thread.CloseActivityThread;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;


public class MainActivity extends BaseActivity {

    //private TabLayout tab Layout;
    private SlidingTabLayout slidingTabLayout;
    private ViewPager viewPager;
    public AlertDialog alertDialog;
    private ViewPagerAdapter viewPagerAdapter;
    private CoordinatorLayout coordinatorLayout;
    private PopupWindow popupWindow;
    private LayoutInflater layoutInflater;
    AsyncTask<Void, Void, Void> gcmRegisterTask;

    final BroadcastReceiver handleMessageReceiver =
            new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String newMessage = intent.getExtras().getString(ServerTask.EXTRA_MESSAGE);
                    Log.i(ServerTask.TAG, newMessage);
                }
            };



    //fb
    private CallbackManager callBackManager;
    private ArrayList<Category> categories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);
        clearCacheImage();
        addControls();
    }

    private void clearCacheImage() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Ion.getDefault(MainActivity.this).getCache().clear();
            }
        });
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub

        try{
            if(handleMessageReceiver!=null)
                unregisterReceiver(handleMessageReceiver);
        }catch(Exception e)
        {

        }
        super.onDestroy();

    }

    protected void xuLyDangKyGCMServer() {
        // Make sure the device has the proper dependencies.
        GCMRegistrar.checkDevice(this);
        // Make sure the manifest was properly set - comment out this line
        // while developing the app, then uncomment it when it's ready.
        GCMRegistrar.checkManifest(this);

        //msg = (TextView) findViewById(R.id.display);

        registerReceiver(handleMessageReceiver,
                new IntentFilter(ServerTask.DISPLAY_MESSAGE_ACTION));
        final String regId = GCMRegistrar.getRegistrationId(this);
        if (regId.equals("")) {
            // Automatically registers application on startup.
            GCMRegistrar.register(this, ServerTask.SENDER_ID);
            LogSystem.E(regId);
        }
        else {
            // Device is already registered on GCM, check server.
            if (GCMRegistrar.isRegisteredOnServer(this)) {
                // Skips registration.
                //msg.append(getString(R.string.already_registered) + "\n");
                Log.i(ServerTask.TAG, "đã đăng ký :\n" + regId + "\n Thành công");
                final Context context = this;
                gcmRegisterTask = new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... params) {
                        boolean registered =
                                ServerTask.register(context, regId);
                        // At this point all attempts to register with the app
                        // server failed, so we need to unregister the device
                        // from GCM - the app will try to register again when
                        // it is restarted. Note that GCM will send an
                        // unregistered callback upon completion, but
                        // GCMIntentService.onUnregistered() will ignore it.
                        if (!registered) {
                            GCMRegistrar.unregister(context);
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        gcmRegisterTask = null;
                    }

                };
                gcmRegisterTask.execute(null, null, null);
            } else {
                // Try to register again, but not in the UI thread.
                // It's also necessary to cancel the thread onDestroy(),
                // hence the use of AsyncTask instead of a raw thread.
                final Context context = this;
                gcmRegisterTask = new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... params) {
                        boolean registered =
                                ServerTask.register(context, regId);
                        // At this point all attempts to register with the app
                        // server failed, so we need to unregister the device
                        // from GCM - the app will try to register again when
                        // it is restarted. Note that GCM will send an
                        // unregistered callback upon completion, but
                        // GCMIntentService.onUnregistered() will ignore it.
                        if (!registered) {
                            GCMRegistrar.unregister(context);
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        gcmRegisterTask = null;
                    }

                };
                gcmRegisterTask.execute(null, null, null);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (handleMessageReceiver == null)
        {
            xuLyDangKyGCMServer();
        }
        if (!ConnectionDetector.isConnected(this)) {
            alertDialog.dialogOnlyRead("Warning", getString(R.string.no_internet_access));
            new CloseActivityThread().waitSecondToCloseActivity(2, this);
        }
    }

//    private void updateUI() {
//
//    }

    private void addControls() {
        addConnectionDetector();
        boolean status = ConnectionDetector.isConnected(this);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.mainactivity);

        super.addToolbar(this, R.drawable.ic_store_white_24dp);
        if (status) {
            addViewPager();
        } else {
            alertDialog.dialogOnlyRead("Warning", getString(R.string.no_internet_access));

            new CloseActivityThread().waitSecondToCloseActivity(2, this);
        }

    }

    private void addSlidingTabLayout() {
        slidingTabLayout = (SlidingTabLayout) findViewById(R.id.slidingtablayout);
        slidingTabLayout.setDistributeEvenly(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            slidingTabLayout.setSelectedIndicatorColors(getColor(R.color.toolbar));
        }
        else
        {
            slidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.toolbar));
        }
        slidingTabLayout.setViewPager(viewPager);
    }

    private void addConnectionDetector() {
        alertDialog = new AlertDialog(this);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
//        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private void addViewPager() {
        if (viewPager == null) viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

    }


    private void setupViewPager(ViewPager viewPager) {
        if (viewPagerAdapter == null)
            viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        addFragmentToViewPager();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callBackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == KeySource.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

//                Bitmap photo = (Bitmap) data.getExtras().get("data");
//                imageView.setImageBitmap(photo);

            }
        }
    }

    private void addFragmentToViewPager() {
        categories = loadCategories();
        if (categories != null) {
            viewPagerAdapter.addFragment(new HomePageFragment(0), getString(R.string.tab_homepage_title));

            for (int i = 0; i < categories.size(); i++) {
                viewPagerAdapter.addFragment(new ProductFragment(categories.get(i)), categories.get(i).getCateName());
//                new ProductRequest().loadData(i, viewPagerAdapter);
                new ProductRequest().loadData(i + 1, viewPagerAdapter);
            }
            viewPager.setOffscreenPageLimit(viewPagerAdapter.getCount());
        } else {
            alertDialog.dialogOnlyRead("Warning", getString(R.string.no_internet_access));
            new CloseActivityThread().waitSecondToCloseActivity(2, this);
        }
        viewPager.setAdapter(viewPagerAdapter);
        addSlidingTabLayout();
    }

    private ArrayList<Category> loadCategories() {
        try {
            return new RequestCategories().execute(KeySource.LINK_CATEGORIES).get();
        } catch (Exception e) {
            LogSystem.E(e.toString());
        }
        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.main_toolbar, menu);
        searchEvent(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                showPopupWindow(findViewById(R.id.action_settings));
                break;
            case R.id.action_shopping_cart:
                openShoppingCartActivity();
                break;
        }
//        if (mDrawerToggle.onOptionsItemSelected(item)) {
//            return true;
//        }
        return super.onOptionsItemSelected(item);
    }


    String[] objects = {KeySource.f1, KeySource.f2};

    private void showPopupWindow(View v) {
        try {

//            coordinatorLayout.setAlpha(0.2f);

            layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
            ViewGroup containter = (ViewGroup) layoutInflater.inflate(R.layout.popup_menu_window_layout, null);
            final ListView lv = (ListView) containter.findViewById(R.id.listViewPopupMenu);


            ListViewAdapter adapter = new ListViewAdapter(this, R.layout.listview_custom_popup_item_layout, objects);

            LinearLayout ll = adapter.getLinearLayout();
            ImageView img = (ImageView) ll.findViewById(R.id.imgListView);

            lv.setAdapter(adapter);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (position == 0) {
                        backToHomePage();
                    } else if (position == 1) {
                        openFavoriteProductActivity();
                    }
                    popupWindow.dismiss();
                    coordinatorLayout.setAlpha(1f);
                }
            });
            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            int width = displayMetrics.widthPixels;
            int height;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                height = getResources().getDrawable(R.drawable.ic_home_white_24dp, getTheme()).getMinimumHeight()*objects.length*2 + 10;
            } else {
                height = getResources().getDrawable(R.drawable.ic_home_white_24dp).getMinimumHeight()*objects.length*2 + 10;
            }
            popupWindow = new PopupWindow(containter, (int) (width/1.5), height, true);
            popupWindow.showAsDropDown(v, 0, 2);
            containter.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    coordinatorLayout.setAlpha(1f);
                    popupWindow.dismiss();
                    return false;
                }
            });
        } catch (Exception e) {
            LogSystem.E(e.toString());
        }
    }

    private void openShoppingCartActivity() {
        Intent i = new Intent(this, FavoriteProductsAndShoppingCart.class);
        Bundle b = new Bundle();
        b.putInt(KeySource.BUNDLE_PUT_DATA, KeySource.ADD_TO_SHOPPING_CART_TABLE);
        i.putExtras(b);
        startActivity(i);
    }

    private void openFavoriteProductActivity() {
        Intent i = new Intent(this, FavoriteProductsAndShoppingCart.class);
        Bundle b = new Bundle();
        b.putInt(KeySource.BUNDLE_PUT_DATA, KeySource.ADD_TO_FAVORITE_TABLE);
        i.putExtras(b);
        startActivity(i);
    }
    ProgressDialog progressDialog;

    private void backToHomePage() {
        viewPager.setCurrentItem(0);
    }

    private void setupTabIcons() {
//        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
//        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
    }

    MenuItem searchItem;
    ArrayList<Product> listProduct;

    public void searchEvent(Menu menu) {
        searchItem = menu.findItem(R.id.action_search);

        // Associate searchable configuration with the SearchView
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
//        searchView.setIconifiedByDefault(false); để logo chiếm chỗ
        searchView.setQueryHint(getString(R.string.search_hint));
        SearchViewListener searchViewListener = new SearchViewListener(this, searchItem, getString(R.string.error_search));
        //searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(searchViewListener);

    }


}
