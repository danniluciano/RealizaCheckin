package br.com.danielaluciano.realizacheckin1;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    //o mapa
    private GoogleMap mMap;
    //localização atual representada por Location
    private Location currentLocation = null;
    //tradução da localização atual para colocar no mapa (um location deve ser
    //traduzido para um LatLng)
    private LatLng loc = null;
    //permite acesso ao GPS
    private LocationManager locationManager;
    //notificado quando eventos de GPS acontecem
    private static final int REQUEST_GPS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED){
                //verifica se deve-se exibir uma explicação sobre a necessidade da permissão
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)){
                Toast.makeText(this, "Para exibir coordenadas o app precisa do GPS",
                        Toast.LENGTH_SHORT).show();
            }
                //pede permissão
            ActivityCompat.requestPermissions(this, new String
                    []{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_GPS);
        }
        else{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
                    0, locationListener);
        }
    }


    private LocationListener locationListener =
            new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    currentLocation = location;
                }
                @Override
                public void onStatusChanged(String provider, int status,
                                            Bundle extras) {
                }
                @Override
                public void onProviderEnabled(String provider) {
                }
                @Override
                public void onProviderDisabled(String provider) {
                }
            };
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new
                Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) !=
                null) {
            startActivityForResult(takePictureIntent,
                    REQUEST_IMAGE_CAPTURE);
        }
    }

    public void checkin (View view){
        if (mMap == null){ //somente se o mapa estiver pronto
            Toast.makeText(this, "Mapa não está pronto",
                    Toast.LENGTH_LONG).show();
        }
        else if (currentLocation == null){
            Toast.makeText(this, "Sem sinal GPS", Toast.LENGTH_LONG).show();
            loc = new LatLng(-23.5631338 , -46.6543286);//vai pra Paulista
        }
        else{
            loc = new LatLng(currentLocation.getLatitude() ,
                    currentLocation.getLongitude());
        }
        dispatchTakePictureIntent();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode ==
                RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            mMap.addMarker(new
                    MarkerOptions().position(loc).title("Estou Aqui!!").icon(BitmapDescriptorFactory.fromBitmap(imageBitmap)));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        /*LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/
    }

    @Override
    protected void onStop() {
        super.onStop();
        locationManager.removeUpdates(locationListener);
    }
}
