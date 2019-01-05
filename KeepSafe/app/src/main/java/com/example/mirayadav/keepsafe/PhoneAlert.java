package com.example.mirayadav.keepsafe;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

public class PhoneAlert extends AppCompatActivity{

    //Initializing variables for xml elements and bluetooth connection
    Button btn1,btnDis, btnTrusted;
    String address = null;
    TextView lumn, welcome;
    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    public static String EXTRA_ADDRESS = "device_address";
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //address of bluetooth module

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent newint = getIntent();
        address = newint.getStringExtra(DeviceList.EXTRA_ADDRESS);

        setContentView(R.layout.activity_phone_alert);

        //Connecting variables to xml elements
        btn1 = (Button) findViewById(R.id.button2);
        btnDis = (Button) findViewById(R.id.button4);
        btnTrusted = (Button) findViewById(R.id.button3);
        welcome = (TextView) findViewById(R.id.welcomeText);
        lumn = (TextView) findViewById(R.id.textView2);
        new ConnectBT().execute();

        //Button for sending continuous signal to Bluetooth module
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                while(myBluetooth.isEnabled()){
                    sendSignal("1");
                }
            }
        });

        //Button to disconnect phone from Bluetooth module
        btnDis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                Disconnect();
            }
        });

        //Button to set up trusted places
        btnTrusted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                Trusted();
            }
        });
    }

    private void Trusted() {
        Intent i = new Intent(PhoneAlert.this, TrustedPlaces.class);
        i.putExtra(EXTRA_ADDRESS, address);
        startActivity(i);
    }

    //Method to send signal to Bluetooth module
    private void sendSignal ( String number ) {
        if ( btSocket != null ) {
            try {
                btSocket.getOutputStream().write(number.toString().getBytes());
            } catch (IOException e) {
                msg("Error");
            }
        }
    }

    private void Disconnect () {
        if ( btSocket!=null ) {
            try {
                btSocket.close();
            } catch(IOException e) {
                msg("Error");
            }
        }

        finish();
    }

    private void msg (String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

    //Method to connect to the bluetooth module
    private class ConnectBT extends AsyncTask<Void, Void, Void> {
        private boolean ConnectSuccess = true;

        @Override
        protected  void onPreExecute () {
            progress = ProgressDialog.show(PhoneAlert.this, "Connecting...", "Please Wait");
        }

        @Override
        protected Void doInBackground (Void... devices) {
            try {
                if ( btSocket==null || !isBtConnected ) {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();
                }
            } catch (IOException e) {
                ConnectSuccess = false;
            }

            return null;
        }

        @Override
        protected void onPostExecute (Void result) {
            super.onPostExecute(result);

            if (!ConnectSuccess) {
                msg("Connection Failed. Try again.");
                finish();
            } else {
                msg("Connected!");
                isBtConnected = true;
            }

            progress.dismiss();
        }

    }
}
