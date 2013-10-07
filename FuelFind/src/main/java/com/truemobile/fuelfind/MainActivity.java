package com.truemobile.fuelfind;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TabWidget;
import android.widget.Toast;

import com.google.android.gms.internal.ca;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.truemobile.fuelfind.data.FuelFindDataContext;
import com.truemobile.fuelfind.data.ServiceStation;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends Activity {
    static final LatLng HATFIELD = new LatLng(-25.748752, 28.237165);
    private static Context mAppContext;
    private static Context mContext;
    private GoogleMap mMap;

    private String[] mDrawerListTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private EditText mSearch;
    private ListView mSearchList;
    private Marker ClickedSearchMarker;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;

    private FuelFindDataContext mFuelFindDataContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAppContext = this.getApplicationContext();
        mContext = this;
        mFuelFindDataContext = new FuelFindDataContext(mAppContext);

        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                .getMap();
        mMap.setOnMarkerClickListener(new MapMarkerClickListener());

        mMap.setOnMyLocationChangeListener(new MyLocationChangeListener());
        mTitle = mDrawerTitle = getTitle();
        mDrawerListTitles = getResources().getStringArray(R.array.left_drawer_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mSearch = (EditText) findViewById(R.id.search_edit);
        mSearchList = (ListView) findViewById(R.id.search_list);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mDrawerListTitles));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        mSearchList.setOnItemClickListener(new SearchItemClickListener());

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mSearch.addTextChangedListener(new SearchTextChanged() {
        });

        if (savedInstanceState == null) {
            selectItem(0);
        }

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(false);

        PopulateMarkers();
    }

    private class MyLocationChangeListener implements GoogleMap.OnMyLocationChangeListener {

        @Override
        public void onMyLocationChange(Location location) {
            MoveTo(null, location.getLatitude(), location.getLongitude(), false, mMap.getCameraPosition().zoom);
        }
    }

    private class MapMarkerClickListener implements GoogleMap.OnMarkerClickListener {
        @Override
        public boolean onMarkerClick(Marker marker) {
            LatLng markerLocation = marker.getPosition();
            Location currentLocation = mMap.getMyLocation();
            DecimalFormat df = new DecimalFormat("0.00##");
            String navigationUrl = "http://maps.google.com/maps?saddr=%s,%s&daddr=%s,%s";
            try {
                Intent navIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(navigationUrl.format(df.format(currentLocation.getLatitude()), df.format(currentLocation.getLongitude()), df.format(markerLocation.latitude), df.format(markerLocation.longitude))));
                mContext.startActivity(navIntent);
            } catch (Exception ex) {
                return false;
            }
            return true;
        }
    }

    private class SearchItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, final View view,
                                int position, long id) {
            final Address item = (Address) parent.getItemAtPosition(position);
            mSearchList.setAdapter(null);
            MoveTo(item, item.getLatitude(), item.getLongitude(), true, 15);
        }

    }

    private class SearchTextChanged implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            try {
                if (mSearch.getText().toString().length() > 0) {
                    Location currentLocation = mMap.getMyLocation();
                    // PolygonOptions rectange = new PolygonOptions();
                    //LatLng leftTop = new LatLng(currentLocation.getLatitude()-1,currentLocation.getLongitude()+1);
                    LatLng leftBot = new LatLng(currentLocation.getLatitude() - 1, currentLocation.getLongitude() - 1);
                    LatLng rightTop = new LatLng(currentLocation.getLatitude() + 1, currentLocation.getLongitude() + 1);
                    //LatLng rightBot =  new LatLng(currentLocation.getLatitude()+1,currentLocation.getLongitude()-1);
                    //rectange.add(leftTop);
                    // rectange.add(rightTop);
                    //rectange.add(rightBot);
                    // rectange.add(leftBot);
                    // rectange.fillColor(android.R.color.holo_red_dark);
                    // mMap.addPolygon(rectange);
                    List<Address> addresses = new Geocoder(mAppContext).getFromLocationName(mSearch.getText().toString(), 5, leftBot.latitude, leftBot.longitude, rightTop.latitude, rightTop.longitude);
                    if (addresses.size() == 1) {
                        MoveTo(addresses.get(0), addresses.get(0).getLatitude(), addresses.get(0).getLongitude(), true, 15);
                        mSearchList.setAdapter(null);
                    } else if (addresses.size() > 1) {
                        final AddressArrayAdapter adapter = new AddressArrayAdapter(mContext,
                                R.layout.search_list_item, addresses);
                        mSearchList.setAdapter(adapter);

                    }
                } else {
                    mSearchList.setAdapter(null);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void PopulateMarkers() {
        List<ServiceStation> allStations = mFuelFindDataContext.GetAllServiceStations();

        for (ServiceStation station : allStations) {
            LatLng currentMarkerLocation = new LatLng(station.Lat, station.Lng);

            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(currentMarkerLocation)
                    .title(station.name)
                    .snippet(station.GetDescription())
                    .icon(BitmapDescriptorFactory
                            .fromResource(R.drawable.ic_launcher)));
        }
    }


    private Location GetCurrentLocation() {
        return mMap.getMyLocation();
    }
/*
    private void SampleMapInfo() {

        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(HATFIELD)
                .title("Title")
                .snippet("Seems to work fine")
                .flat(true)
                .icon(BitmapDescriptorFactory
                        .fromResource(R.drawable.ic_launcher)));


        List<String> PetrolLocations = Arrays.asList(getResources().getStringArray(R.array.petrol_location));

        for (String item : PetrolLocations) {
            String[] LatLong = item.split(",");
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(Double.parseDouble(LatLong[1]), Double.parseDouble(LatLong[0])))
                    .title("Title")
                    .snippet("Seems to work fine")
                    .icon(BitmapDescriptorFactory
                            .fromResource(R.drawable.ic_launcher)));
        }
        MoveTo(null, HATFIELD.latitude, HATFIELD.longitude);
    }*/

    private void MoveTo(Address address, double latitude, double longitude, boolean animateCamera, float zoom) {
        if (address != null) {
            if (ClickedSearchMarker != null) {
                ClickedSearchMarker.remove();
            }

            ClickedSearchMarker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(latitude, longitude))
                    .title(address.getFeatureName())
                    .snippet(address.getAddressLine(2))
                    .flat(true)
                    .icon(BitmapDescriptorFactory
                            .fromResource(R.drawable.ic_launcher)));
        }
        // Move the camera instantly to hamburg with a zoom of 15.

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), zoom));

        // Zoom in, animating the camera.
        if (animateCamera)
            mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action buttons
      /*  switch(item.getItemId()) {
            case R.id.action_websearch:
                // create intent to perform web search for this planet
                Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                intent.putExtra(SearchManager.QUERY, getActionBar().getTitle());
                // catch event that there's no activity to handle intent
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    Toast.makeText(this, R.string.app_not_available, Toast.LENGTH_LONG).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }*/
        return super.onOptionsItemSelected(item);
    }

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {

        switch (position) {   //Normal
            case 0:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            //Satellite
            case 1:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            //Hybrid
            case 2:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
            //Terrain
            case 3:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
            //Help
            case 4:
                break;
            //Settings
            case 5:
                break;
            //Send feedback
            case 6:
                break;

        }

        mDrawerList.setItemChecked(position, true);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);

            if (drawerOpen) {
                mDrawerLayout.closeDrawers();
            } else {
                mDrawerLayout.openDrawer(mDrawerList);
            }
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }
}