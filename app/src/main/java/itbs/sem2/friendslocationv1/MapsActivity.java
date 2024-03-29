package itbs.sem2.friendslocationv1;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;

import android.net.Uri;
import android.os.Bundle;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        getLastKnownLocation();
    }

    /**
     * Obtient la dernière position connue de l'appareil.
     */
    private void getLastKnownLocation() {
        // Vérifier si l'application a la permission d'accéder à la localisation
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Si les permissions ne sont pas accordées, demander à l'utilisateur de les autoriser
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        // Si les permissions sont accordées, obtenir la dernière position connue de l'appareil
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            // Logique pour manipuler l'objet de localisation
                            handleLocation(location);
                        }
                    }
                });
    }

    /**
     * Manipule la dernière position connue de l'appareil.
     */
    private void handleLocation(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.addMarker(new MarkerOptions().position(latLng).title("My Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                Intent resultIntent = new Intent();
                // Envoyer les coordonnées en tant que chaîne unique dans l'intent
                resultIntent.setData(Uri.parse(latLng.latitude + "," + latLng.longitude));
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }

        });
        //ma position
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                // Récupérer les coordonnées du marqueur
                LatLng position = marker.getPosition();

                // Créer un nouvel Intent pour envoyer les coordonnées en tant que résultat
                Intent resultIntent = new Intent();
                // Convertir les coordonnées en chaîne unique et les ajouter à l'Intent
                resultIntent.setData(Uri.parse(position.latitude + "," + position.longitude));

                // Définir le résultat de l'activité comme OK avec l'Intent contenant les coordonnées
                setResult(Activity.RESULT_OK, resultIntent);

                // Terminer l'activité actuelle
                finish();

                // Retourner true pour indiquer que l'événement de clic sur le marqueur est consommé
                return true;
            }
        });


    }

    // Gérer la réponse de l'utilisateur concernant la demande de permissions
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permissions accordées, obtenir la dernière position connue
                getLastKnownLocation();
            } else {
                // Permissions refusées, afficher un message à l'utilisateur ou effectuer une autre action appropriée
            }
        }
    }
}
