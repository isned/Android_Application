package itbs.sem2.friendslocationv1.ui.home;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import itbs.sem2.friendslocationv1.Config;
import itbs.sem2.friendslocationv1.JSONParser;
import itbs.sem2.friendslocationv1.MapsActivity;
import itbs.sem2.friendslocationv1.MapsActivity2;
import itbs.sem2.friendslocationv1.Position;
import itbs.sem2.friendslocationv1.R;
import itbs.sem2.friendslocationv1.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {
    ArrayList<Position> data = new ArrayList<>();
    private FragmentHomeBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.btntelechargement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Téléchargement des données
                Telechargement t = new Telechargement(requireContext());
                t.execute();
            }
        });
        // Ajouter un écouteur de clic à la ListView
        binding.lvPosition.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Récupérer la position sélectionnée
                Position selectedPosition = data.get(position);

                // Créer un Intent pour passer les informations de la position à MapsActivity2
                Intent intent = new Intent(requireContext(), MapsActivity2.class);
                intent.putExtra("latitude", selectedPosition.getLatitude());
                intent.putExtra("longitude", selectedPosition.getLongitude());
                intent.putExtra("pseudo", selectedPosition.getPseudo());
                startActivity(intent);
            }
        });


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    class Telechargement extends AsyncTask<Void, Void, Void> {
        Context context;
        AlertDialog dialog;

        public Telechargement(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Afficher la boîte de dialogue
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Téléchargement");
            builder.setMessage("Veuillez patienter...");
            dialog = builder.create();
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            data.clear();
            JSONParser parser = new JSONParser();
            JSONObject response = parser.makeHttpRequest(Config.URL_GETALL, "GET", null);
            try {
                int success = response.getInt("success");
                if (success == 1) {
                    JSONArray positionsArray = response.getJSONArray("positions");
                    for (int i = 0; i < positionsArray.length(); i++) {
                        JSONObject positionObject = positionsArray.getJSONObject(i);
                        int id = positionObject.getInt("idposition");
                        String longitude = positionObject.getString("longitude");
                        String latitude = positionObject.getString("latitude");
                        String pseudo = positionObject.getString("pseudo");
                        Position position = new Position(id, longitude, latitude, pseudo);
                        data.add(position);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            // Afficher les données dans la liste
            ArrayAdapter<Position> adapter = new ArrayAdapter<Position>(context, R.layout.list_item_position, data) {
                @NonNull
                @Override
                public View getView(int position, @androidx.annotation.Nullable View convertView, @NonNull ViewGroup parent) {
                    if (convertView == null) {
                        convertView = LayoutInflater.from(context).inflate(R.layout.list_item_position, parent, false);
                    }

                    // Obtenez la position actuelle dans la liste
                    Position currentPosition = data.get(position);

                    // Obtenez les références des vues de mise en page
                    TextView textViewPseudo = convertView.findViewById(R.id.textViewPseudo);
                    TextView textViewLatitude = convertView.findViewById(R.id.textViewLatitude);
                    TextView textViewLongitude = convertView.findViewById(R.id.textViewLongitude);
                    // Mettez à jour les vues avec les données de la position actuelle
                    textViewPseudo.setText(currentPosition.getPseudo());
                   // String locationText = "Latitude: " + currentPosition.getLatitude() + "\nLongitude: " + currentPosition.getLongitude();

                    textViewLatitude.setText(currentPosition.getLatitude() );
                    textViewLongitude.setText(currentPosition.getLongitude());

                    return convertView;
                }
            };

            // Attachez l'adaptateur à la ListView pour afficher les données
            binding.lvPosition.setAdapter(adapter);
        }
    }
}