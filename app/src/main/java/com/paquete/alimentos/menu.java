package com.paquete.alimentos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class menu extends AppCompatActivity {
    private FusedLocationProviderClient fusedLocationClient;
    private FirebaseDatabase database;
    private DatabaseReference gpsRef;

    private static final int LOCATION_REQUEST_CODE = 1;

    private ProgressBar loadingSpinner;
    private TextView titleText;
    private TextView descriptionText;
    private ImageView locationIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);


        loadingSpinner = findViewById(R.id.loadingSpinner);
        titleText = findViewById(R.id.titleText);
        descriptionText = findViewById(R.id.descriptionText);
        locationIcon = findViewById(R.id.locationIcon);


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        database = FirebaseDatabase.getInstance();
        gpsRef = database.getReference("gpsLocation");


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        } else {
            obtenerUbicacion();
        }
    }

    private void obtenerUbicacion() {

        titleText.setText("Obteniendo tu ubicación...");
        descriptionText.setText("Por favor, espera mientras obtenemos tu ubicación.");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {

                        gpsRef.setValue(location);

                        titleText.setText("¡Ubicación guardada!");
                        descriptionText.setText("Tu ubicación se ha guardado correctamente.");
                        loadingSpinner.setVisibility(View.GONE);
                        Toast.makeText(menu.this, "Ubicación guardada en Firebase", Toast.LENGTH_SHORT).show();
                    } else {
                        mostrarError("No se pudo obtener la ubicación.");
                    }
                })
                .addOnFailureListener(e -> mostrarError("Error al obtener la ubicación."));
    }

    private void mostrarError(String mensaje) {
        titleText.setText("Error");
        descriptionText.setText(mensaje);
        loadingSpinner.setVisibility(View.GONE);
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                obtenerUbicacion();
            } else {
                mostrarError("Permiso de ubicación denegado.");
            }
        }
    }
}
