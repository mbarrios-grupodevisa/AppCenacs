package gt.com.metrocasas.appcenacs;

import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class FragmentRevisionesList extends Fragment {

    public String proyecto, userid, init;
    String name, last, estado;
    private GoogleApiClient googleApiClient;
    public ImageView registro;
    String longitudeNetwork, latitudeNetwork, service;
    boolean bandera  = true;
    TextView estad;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View partenView = inflater.inflate(R.layout.lista_revisiones, container, false);
        SharedPreferences settings = getActivity().getSharedPreferences("User",0);
        name = settings.getString("firstname", null);
        last = settings.getString("lastname", null);
        estado = settings.getString("estado", null);
        service = settings.getString("service",null);
        userid = getArguments().getString("id");
        init = getArguments().getString("init");
        proyecto = getArguments().getString("proyecto");
        estad = (TextView)partenView.findViewById(R.id.txtIngreso);
        if (estado.equals("Salida")) {
            estad.setText("Pulse para registrar su ingreso");
        } else {
            estad.setText("Pulse para registrar su salida");
        }

        registro = (ImageView)partenView.findViewById(R.id.registro);
        registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    solicitarGPS();
            }
        });

        return partenView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (estado.equals("Salida")) {
            estad.setText("Pulse para registrar su ingreso");
        } else {
            estad.setText("Pulse para registrar su salida");
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public void solicitarGPS() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Activar GPS");
            builder.setMessage("Se necesita tener activo el GPS para usar esta función.");
            builder.setIcon(android.R.drawable.ic_menu_info_details);
            builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                    Intent gpsOptionsIntent = new Intent(
                            android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(gpsOptionsIntent);
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }
        else {
            googleApiClient = LocationProviderReceiver.googleApiClient;
            googleApiClient.connect();
            if(service.equals("Stop")) {
                startLocationRequest();
                SharedPreferences settings = getActivity().getSharedPreferences("User",0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("service","Start");
                service = "Start";
                editor.apply();
            }
            else {
                startLocationRequest();
            }
        }
    }

    public void startLocationRequest() {
        try {
            LocationRequest locationRequest = LocationRequest.create()
                    .setInterval(10000)
                    .setFastestInterval(5000)
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,
                    locationRequest, new com.google.android.gms.location.LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            latitudeNetwork = String.valueOf(location.getLatitude());
                            longitudeNetwork = String.valueOf(location.getLongitude());

                            if(init.equals("normal") && bandera) {
                                SharedPreferences settings = getActivity().getSharedPreferences("User",0);
                                if(!settings.getString("estado", null).equals("Ingreso")) {
                                    estado = "Ingreso";
                                } else {
                                    estado = "Salida";
                                }
                                bandera = false;
                                Intent detalleRevision = new Intent(getActivity(), DetalleRevisionActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("id", userid);
                                bundle.putString("proyecto", proyecto);
                                bundle.putString("estado", estado);
                                bundle.putString("latitud", latitudeNetwork);
                                bundle.putString("longitud", longitudeNetwork);
                                detalleRevision.putExtras(bundle);
                                startActivity(detalleRevision);
                            }
                        }
                    });
        } catch (SecurityException se) {
            //Log.i("ERROR", se.toString());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        bandera = true;
        if (estado.equals("Salida")) {
            estad.setText("Pulse para registrar su ingreso");
        } else {
            estad.setText("Pulse para registrar su salida");
        }
    }
}
