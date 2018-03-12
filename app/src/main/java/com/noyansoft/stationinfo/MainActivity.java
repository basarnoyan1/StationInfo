package com.noyansoft.stationinfo;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.CellIdentityGsm;
import android.telephony.CellInfo;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoWcdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.NeighboringCellInfo;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
            int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        001);
            }
            else {
                CInfo();
            }
        }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 001: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    CInfo();
                } else {
                }
                return;
            }
        }
    }
    public void CellInfo(){
        try {
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            List<CellInfo> cellInfos = (List<CellInfo>) telephonyManager.getAllCellInfo();
            for (CellInfo cellInfo : cellInfos) {
                CellInfoGsm cellInfoGsm = (CellInfoGsm) cellInfo;
                CellIdentityGsm cellIdentity = cellInfoGsm.getCellIdentity();
                CellSignalStrengthGsm cellSignalStrengthGsm = cellInfoGsm.getCellSignalStrength();

                // Log.d("cell", "registered: " + cellInfoGsm.isRegistered());
                Log.d("cell", cellIdentity.toString());
                Log.d("cell", cellSignalStrengthGsm.toString());
                try {
                    String t1 = cellIdentity.toString();
                    String t2 = cellSignalStrengthGsm.toString();
                    final Dialog dialog = new Dialog(getApplicationContext());
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setCancelable(false);
                    dialog.setTitle("Bilgi");
                    dialog.setContentView(R.layout.station_dialog);
                    // TextView text = (TextView) dialog.findViewById(R.id.txt1);
                    // text.setText(String.valueOf(cellInfoGsm.isRegistered()));
                    TextView text2 = (TextView) dialog.findViewById(R.id.txt2);
                    text2.setText(t1);
                    TextView text3 = (TextView) dialog.findViewById(R.id.txt3);
                    text3.setText(t2);
                    dialog.create();
                    dialog.show();
                } catch (Exception e) {
                    String m = e.getMessage();
                }
            }
        } catch (SecurityException e) {
            String m = e.getMessage();
        }
    }
    public void CInfo(){
        try{
            final Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        GsmCellLocation cellLocation = (GsmCellLocation) telephonyManager.getCellLocation();
        List<NeighboringCellInfo> NeighboringList = telephonyManager.getNeighboringCellInfo();
            String dBm;
            int rssi = NeighboringList.get(1).getRssi();
            if(rssi == NeighboringCellInfo.UNKNOWN_RSSI){
                dBm = "Unknown RSSI";
            }else{
                dBm = String.valueOf(rssi*2+113) + " dBm";
            }
        Dialog dialog = new Dialog(MainActivity.this,android.R.style.Theme_Material_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.station_dialog);
            final TextView text1 = (TextView) dialog.findViewById(R.id.txt1);
            text1.setText("");
        TextView text2 = (TextView) dialog.findViewById(R.id.txt2);
        text2.setText("Lac:"+cellLocation.getLac()+", Cid:"+cellLocation.getCid()+", Psc:"+cellLocation.getPsc());
            TextView text3 = (TextView) dialog.findViewById(R.id.txt3);
            List a = telephonyManager.getAllCellInfo();
            text3.setText(""+dBm+","+telephonyManager.getSimOperatorName());
            LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            try {
                Location l = locationManager.getLastKnownLocation(locationManager.getBestProvider(new Criteria(), true));

                if (l != null){
                    double latitude = l.getLatitude();
                double longitude = l.getLongitude();
                List<Address> addresses;
                try {
                    addresses = geocoder.getFromLocation(latitude, longitude, 1);
                    String mah = addresses.get(0).getSubLocality();
                    String ilce = addresses.get(0).getSubAdminArea();
                    String il = addresses.get(0).getAdminArea();
                    text1.setText(mah + ", " + ilce + ", " + il);
                } catch (IOException i) {
                    String m = i.getMessage();
                    text1.setText(latitude+","+longitude);
                }
            }
            }
            catch (SecurityException s){}
            LocationListener locationListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    List<Address> addresses;
                    try {
                        addresses = geocoder.getFromLocation(latitude, longitude, 1);
                        String mah = addresses.get(0).getSubLocality();
                        String ilce = addresses.get(0).getSubAdminArea();
                        String il = addresses.get(0).getAdminArea();
                        text1.setText(mah +", "+ ilce +", " + il);
                    }
                    catch (IOException | SecurityException i){
                        String m = i.getMessage();
                        text1.setText(latitude+","+longitude);
                    }
                }
                public void onStatusChanged(String provider, int status, Bundle extras) {}

                public void onProviderEnabled(String provider) {}

                public void onProviderDisabled(String provider) {}
            };
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        dialog.create();
        dialog.show();
            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    // TODO Auto-generated method stub
                    finish();
                }
            });
        } catch (SecurityException e) {
        String m = e.getMessage();
        finish();
        }
    }
    @Override
    public void onBackPressed() {
        finish();
    }
}
