package itbs.sem2.friendslocationv1.ui.gallery;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import org.json.JSONObject;

import java.util.HashMap;

import itbs.sem2.friendslocationv1.Config;
import itbs.sem2.friendslocationv1.JSONParser;
import itbs.sem2.friendslocationv1.MapsActivity;
import itbs.sem2.friendslocationv1.R;
import itbs.sem2.friendslocationv1.databinding.FragmentGalleryBinding;


public class GalleryFragment extends Fragment implements LocationListener {

    private FragmentGalleryBinding binding;
    private LocationManager locationManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.btnAjouter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String latitude = binding.edLatitude.getText().toString().trim();
                String longitude = binding.edLongitude.getText().toString().trim();
                String pseudo = binding.edPseudo.getText().toString().trim();

                // Vérifier si les champs sont vides
                if (latitude.isEmpty() || longitude.isEmpty() || pseudo.isEmpty()) {
                    Toast.makeText(requireContext(), "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                } else {
                    // Stocker les valeurs des champs de texte dans des variables locales
                    // avant de créer une instance de Insert AsyncTask
                    new Insert(requireActivity(), longitude, latitude,  pseudo).execute();
                }
            }
        });

        binding.btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(requireContext(), MapsActivity.class), 1);
            }
        });

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_galleryFragment_to_homeFragment);
            }
        });

        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            if (data.getData() != null) {
                String latLng = data.getData().toString().trim();
                // Divisez la chaîne des coordonnées en latitude et longitude
                String[] parts = latLng.split(",");
                if (parts.length == 2) {
                    double latitude = Double.parseDouble(parts[0]);
                    double longitude = Double.parseDouble(parts[1]);
                    // Mettez à jour les champs de texte avec les coordonnées séparées
                    binding.edLatitude.setText(String.valueOf(latitude));
                    binding.edLongitude.setText(String.valueOf(longitude));
                } else {
                    // Gérer le cas où les coordonnées ne sont pas dans le bon format
                    Toast.makeText(requireContext(), "Coordonnées invalides", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        requestLocationUpdates();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
    }

    private void requestLocationUpdates() {
        locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
        try {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, // Fournisseur d'emplacement
                    60000, // Intervalle de mise à jour en ms (10 secondes)
                    10, // Distance minimale de déplacement en mètres
                    this // LocationListener
            );
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        binding.edLatitude.setText(String.valueOf(latitude));
        binding.edLongitude.setText(String.valueOf(longitude));
        Toast.makeText(getActivity(), "Position changée", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // Gérer les changements d'état du fournisseur d'emplacement
    }

    @Override
    public void onProviderEnabled(String provider) {
        // Gérer le cas où le fournisseur d'emplacement est activé
    }

    @Override
    public void onProviderDisabled(String provider) {
        // Gérer le cas où le fournisseur d'emplacement est désactivé
    }

    class Insert extends AsyncTask<Void, Void, JSONObject> {
        private Context context;
        private String latitude;
        private String longitude;
        private String pseudo;

        public Insert(Context context, String latitude, String longitude, String pseudo) {
            this.context = context;
            this.latitude = latitude;
            this.longitude = longitude;
            this.pseudo = pseudo;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Vous pouvez afficher une boîte de dialogue de chargement ici si nécessaire
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {
            JSONParser parser = new JSONParser();

            // Créer un objet JSON avec les données
            HashMap<String, String> params = new HashMap<>();
            params.put("latitude", latitude);
            params.put("longitude", longitude);
            params.put("pseudo", pseudo);

            // Faire la requête HTTP POST pour ajouter les données dans la base de données
            JSONObject response = parser.makeHttpRequest(Config.URL_AddPosition, "POST", params);
            return response;
        }

        @Override
        protected void onPostExecute(JSONObject response) {
            super.onPostExecute(response);
            // Fermez la boîte de dialogue de chargement ici si nécessaire

            if (response != null) {
                // Traitez la réponse du serveur ici
                try {
                    // Vérifiez si l'insertion a réussi
                    int success = response.getInt("success");
                    String message = response.getString("message");
                    if (success == 1) {
                        // L'insertion a réussi
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    } else {
                        // L'insertion a échoué, affichez un message d'erreur
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage(message)
                                .setNegativeButton("Retry", null)
                                .create()
                                .show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                // La réponse est nulle, affichez un message d'erreur
                Toast.makeText(context, "Failed to connect to server", Toast.LENGTH_SHORT).show();
            }
        }
    }
}