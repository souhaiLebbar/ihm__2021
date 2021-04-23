package com.polytech.ihm.activities.demand;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.VoiceInteractor;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.polytech.ihm.R;
import com.polytech.ihm.activities.give.Donate;
import com.polytech.ihm.activities.give.MyList;
import com.polytech.ihm.activities.give.Request;
import com.polytech.ihm.activities.stats.statistics;
import com.polytech.ihm.models.BasketHelper;
import com.polytech.ihm.models.Extra;
import com.polytech.ihm.models.Util;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.infowindow.InfoWindow;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

public class Demand extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final int PERMISSION_REQUEST_CODE = 1;
    private MapView map;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private ArrayList<BasketHelper> markers;
    private ArrayList<BasketHelper> favoriteBaskets;
    private ArrayList<BasketHelper> reqBaskets;
    //coordonée
    private double myLongitude;
    private double myLattitude;
    private MyLocationNewOverlay mLocationOverlay;
    //menu
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Button menuIcon;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration.getInstance().
                load(
                        getApplicationContext(),
                        PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                );
        setContentView(R.layout.activity_demand);
        //initialise baskets
        favoriteBaskets = new ArrayList<>();
        reqBaskets = new ArrayList<>();

        map = findViewById(R.id.map);
        // Hooks Drawer Menu
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        menuIcon = findViewById(R.id.menu);
        //TileSourceFactory.PUBLIC_TRANSPORT
        //http://leaflet-extras.github.io/leaflet-providers/preview/
        map.setTileSource(TileSourceFactory.MAPNIK); //render
        map.setBuiltInZoomControls(true);            //zoomable
        //should be Localisation
        GeoPoint startPoint = new GeoPoint(43.675050, 7.058324);
        IMapController mapController = map.getController();
        mapController.setZoom(14.0);
        mapController.setCenter(startPoint);
        //setLocalisation();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            generateMarkers();
        }
        //map.getOverlays().add(addMarker(new GeoPoint(43.61572296415799, 7.071842570348114)));
        map.invalidate();
        //navigation Drawer
        navifationDrawer();
    }
    //markers
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void generateMarkers() {
        markers = new ArrayList<>();
        for(int i=0;i<20;i++){
            addMarker(
                    new BasketHelper(generateTitle(),generateDescription(),generateWeight(),generatePNumber(),generateType(),generateGeoPoint(),generatePicture())
            );

        }
    }
    private int generatePicture() {
        Random rand = new Random();
        switch(rand.nextInt(5)){
            case 0: return R.drawable.image1;
            case 1: return R.drawable.image2;
            case 2: return R.drawable.image3;
            case 3: return R.drawable.image4;
            case 4: return R.drawable.image5;
        }
        return R.drawable.image1;
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    private String generateDescription() {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();

        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        //Util.print(getApplicationContext(),generatedString);
        return generatedString;
    }
    private float generateWeight() {
        return new Random().nextInt(100);
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    private String generatePNumber() {
        return "06"+(10000000 + new Random().nextInt(90000000));
    }
    private BasketHelper.Type generateType() {
        switch(new Random().nextInt(4)){
            case 0:
                return BasketHelper.Type.HONEYCOOB;
            case 1:
                return BasketHelper.Type.CORRUGATED;
            case 2:
                return BasketHelper.Type.PLAT;
            case 3:
                return BasketHelper.Type.WOODEN;
        }
        return BasketHelper.Type.HONEYCOOB;
    }
    private GeoPoint generateGeoPoint() {
        Random rand = new Random();
        return new GeoPoint(rand.nextDouble()*0.1 + 43.6,rand.nextDouble()*0.1 +7);
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    private String generateTitle() {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();

        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        //Util.print(getApplicationContext(),"title:"+generatedString);
        return generatedString;
    }
    @SuppressLint("UseCompatLoadingForDrawables")
    private Marker addMarker(BasketHelper basketHelper) {
        Marker m = new Marker((map));
        m.setPosition(basketHelper.getMyGeoPoint());
        m.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        m.setIcon(getResources().getDrawable(R.drawable.icons8_trolley_100));
        m.setDraggable(false);
        m.setInfoWindow(new MapCustomInfoBubble(map,basketHelper));
        m.setInfoWindowAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_TOP);

        map.getOverlayManager().add(m);
        //OnClick
        m.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker, MapView mapView) {
                if (m.isInfoWindowShown())
                    m.closeInfoWindow();
                else {
                    m.showInfoWindow();
                }
                return true;
            }
        });
        return m;

    }



    //Menu
    private void navifationDrawer() {
        //Navigation Drawer
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        //by default the nav_profile is selected
        navigationView.setCheckedItem(R.id.demandG);
        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.demand:
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.requests:
                Intent intentRequest = new Intent(this, Requests.class);
                intentRequest.putParcelableArrayListExtra(Extra.basketRList, reqBaskets);
                startActivity(intentRequest);
                break;
            case R.id.favorite:
                Intent intentFavorite = new Intent(this, Favorite.class);
                intentFavorite.putParcelableArrayListExtra(Extra.basketFList, favoriteBaskets);
                startActivity(intentFavorite);
                break;
            case R.id.new_requests:
                Intent intentReq = new Intent(this, Request.class);
                startActivity(intentReq);
                break;
            case R.id.give:
                Intent intentD = new Intent(this, Donate.class);
                startActivity(intentD);
                break;
            case R.id.myList:
                Intent intentL = new Intent(this, MyList.class);
                startActivity(intentL);
                break;
            case R.id.stats:
                Intent intentS = new Intent(this, statistics.class);
                startActivity(intentS);
                break;


        }
        return true;
    }


    //Map lifeCycle
    @Override
    protected void onPause() {
        super.onPause();
        map.onPause();
    }
    @Override
    protected void onResume() {
        super.onResume();
        map.onResume();
    }


    //
    public void toProfile(View view) {
        Intent intentProfile = new Intent(this, com.polytech.ihm.activities.Profile.class);
        startActivity(intentProfile);
    }
    public void makeCall(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE);
        if (result == PackageManager.PERMISSION_GRANTED) {

            startActivity(intent);

        } else {

            requestPermission();
        }
    }

    // Localisation
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setLocalisation() {
        //copier_coller
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                myLongitude = location.getLongitude();
                myLattitude = location.getLatitude();
                //textView.append("\n " + longitude + " " + lattitude);
                mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(getApplicationContext()), map);
                mLocationOverlay.enableMyLocation();
                map.setMultiTouchControls(true);
                map.getOverlays().add(mLocationOverlay);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.INTERNET}, 10);
            return;
        }
        /*
        locationManager.requestLocationUpdates("gps", 5000, 5, locationListener);
        Polyline line = new Polyline();
        line.setTitle("Un trajet");
        line.setSubDescription(Polyline.class.getCanonicalName());
        line.setWidth(10f);
        line.setId("-1");
        line.setColor(Color.RED);
        trajet = new ArrayList<GeoPoint>();
        OverlayItem point = new OverlayItem("dechet_organique", "moyen", new GeoPoint(45.31765771762817, 5.922782763890293));
        trajet.add(new GeoPoint(point.getPoint().getLatitude(), point.getPoint().getLongitude()));
        line.setPoints(trajet);
        line.setGeodesic(true);
        line.setInfoWindow(new BasicInfoWindow(R.layout.bonuspack_bubble, map));
        map.getOverlayManager().add(line);
        map.invalidate();
        */
        //fin
    }
    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(Demand.this, Manifest.permission.CALL_PHONE)) {
        } else {

            ActivityCompat.requestPermissions(Demand.this, new String[]{Manifest.permission.CALL_PHONE}, PERMISSION_REQUEST_CODE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    makeCall(null);
                }
                break;
        }
    }

    //costume the marker
    class MapCustomInfoBubble extends InfoWindow {
        private BasketHelper basketHelper;
        public MapCustomInfoBubble(MapView mapView,BasketHelper basketHelper) {
            super(R.layout.map_infobubble_black, mapView);//my custom layout and my mapView
            this.basketHelper=basketHelper;
        }

        @Override
        public void onClose() {
            //by default, do nothing
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onOpen(Object item) {
            Marker marker = (Marker) item; //the marker on which you click to open the bubble
            //marker infos
            TextView title = mView.findViewById(R.id.name);
            TextView desc = mView.findViewById(R.id.bubble_desc);
            TextView weight = mView.findViewById(R.id.weight);
            TextView type = mView.findViewById(R.id.type);
            ImageView favorite = (ImageView) mView.findViewById(R.id.bubble_favorie);
            ImageView agenda = (ImageView) mView.findViewById(R.id.bubble_agenda);
            ImageView call = (ImageView) mView.findViewById(R.id.bubble_call);
            Button request = (Button) mView.findViewById(R.id.request);

            title.setText("title: "+basketHelper.getTitle());
            desc.setText("description: "+basketHelper.getDescription());
            weight.setText("weight: "+basketHelper.getWeight());
            type.setText("type :"+basketHelper.getType());

            call.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    makeCall(basketHelper.getPhoneNumber());
                }
            });
            agenda.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Event is on January 23, 2021 -- from 7:30 AM to 10:30 AM.
                    Intent calendarIntent = new Intent(Intent.ACTION_INSERT, CalendarContract.Events.CONTENT_URI);
                    Calendar beginTime = Calendar.getInstance();
                    beginTime.set(2021, 0, 23, 7, 30);
                    Calendar endTime = Calendar.getInstance();
                    endTime.set(2021, 0, 23, 10, 30);
                    calendarIntent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis());
                    calendarIntent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis());
                    calendarIntent.putExtra(CalendarContract.Events.TITLE, "Basket");
                    calendarIntent.putExtra(CalendarContract.Events.EVENT_LOCATION, "Secret dojo");
                    startActivity(calendarIntent);
                }
            });
            favorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Util.print(getApplicationContext(),"test");
                    boolean isfilled = Util.checkImageResource(v.getContext(), favorite, R.drawable.icons8_heart_24);
                    if (isfilled) {
                        favorite.setImageResource(R.drawable.icons8_heart_48);
                        favoriteBaskets.add(basketHelper);
                        //System.out.println(basketHelper);
                    } else {
                        favorite.setImageResource(R.drawable.icons8_heart_24);
                        favoriteBaskets.remove(basketHelper);
                    }


                }
            });
            request.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Util.print(getApplicationContext(),"request clicked");
                    reqBaskets.add(basketHelper);
                    v.setEnabled(false);
                }
            });
        }

    }

}
