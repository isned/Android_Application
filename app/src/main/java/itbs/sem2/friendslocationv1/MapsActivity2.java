package itbs.sem2.friendslocationv1;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import itbs.sem2.friendslocationv1.databinding.ActivityMaps2Binding;

public class MapsActivity2 extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMaps2Binding binding;
    private double latitude;
    private double longitude;
    private  String pseudo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMaps2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Récupérer les données de latitude et de longitude de l'intent en tant que chaînes de caractères
        String latitudeStr = getIntent().getStringExtra("latitude");
        String longitudeStr = getIntent().getStringExtra("longitude");
        pseudo = getIntent().getStringExtra("pseudo"); // Initialisez la variable de classe pseudo

        // Convertir les chaînes de caractères en double
        if (latitudeStr != null && longitudeStr != null) {
            try {
                latitude = Double.parseDouble(latitudeStr);
                longitude = Double.parseDouble(longitudeStr);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }







    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Créer un objet LatLng avec les données de latitude et de longitude
        LatLng position = new LatLng(latitude, longitude); // Créer un objet LatLng avec les données récupérées

        // Ajouter un marqueur à la position sur la carte
        if (mMap != null) {
            // Ajouter un marqueur à la position sélectionnée avec un titre
            mMap.addMarker(new MarkerOptions().position(position).title(pseudo));
            // Déplacer la caméra pour centrer la carte sur la position sélectionnée
            mMap.moveCamera(CameraUpdateFactory.newLatLng(position));

            // Afficher les valeurs de latitude et de longitude dans une alerte
            showAlert("Coordonnées de position", "Latitude : " + latitude + "\nLongitude : " + longitude + "\nPseudo : " + pseudo);
        }
    }

    private void showAlert(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("OK", null);
        builder.show();
    }
}
