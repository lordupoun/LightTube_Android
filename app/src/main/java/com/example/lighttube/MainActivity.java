package com.example.lighttube;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import android.os.Build;
import android.content.pm.PackageManager;
import android.Manifest;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.provider.Settings;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    byte[] data = new byte[31];
    boolean disconnected=true;
    BluetoothSocket socket = null;
    OutputStream outputStream = null;
    TextView statusHeader;
    BottomNavigationView bottomNav;

    boolean textBox_viewed = false;

    // UUID and MAC address
    final UUID mUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    boolean isUserClosingApp = false; //Do not connect after closing App

    public void setTempo(byte paste){
        this.data[0]=paste;
    }
    //Copies data to local variable
    public void setData(byte[] paste,int destPos){
        System.arraycopy(paste, 1, this.data, destPos, paste.length-1); //STARTS FROM POS 1!!
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        for(byte i : data){
            i=0;
        }

        statusHeader = findViewById(R.id.statusHeader);
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);

        //--------------------------------------
        // After click:
                bottomNav.setOnItemSelectedListener(item -> {
                    int id = item.getItemId();

                    if (id == R.id.terminal) {
                        replaceFragment(new OvladaniFragment());
                        return true;
                    } else if (id == R.id.preset) {
                        replaceFragment(new PresetsFragment());
                        return true;
                    }
                    //else if (id == R.id.bluetooth) {
                    //    replaceFragment(new NastaveniFragment());
                    //    return true;
                    //}
                    return false;
                });
        // 3. Default page (prevents empty FrameLayout after start)
                if (savedInstanceState == null) {
                    replaceFragment(new OvladaniFragment());
                }
        //---------------------------------

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {

                // Zjistíme, jestli nás uživatel už dřív neodmítl trvale
                if (!shouldShowRequestPermissionRationale(Manifest.permission.BLUETOOTH_CONNECT)) {
                    //Alert dialog
                    new AlertDialog.Builder(this)
                            .setTitle("Oprávnění BT")
                            .setMessage("Bez příslušných oprávnění není možné používat aplikaci LightTube.")
                            .setPositiveButton("Přejít do nastavení", (d, w) -> openSettings())
                            .setNegativeButton("Zrušit", null)
                            .show();
                } else {
                    //Standard dialogue after first/second try
                    requestPermissions(new String[]{Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN}, 1);
                }
                return;
            } else {
                connectBT();
            }
        }
        else {
            connectBT();
        }
    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayout, fragment) //ovladani is a FrameLayout ID
                .commit();
    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    private void connectBT() {
        if (isUserClosingApp) return;

        new Thread(() -> {
            try {
                runOnUiThread(() -> {
                    statusHeader.setText("Zařízení: probíhá vyhledávání");
                    statusHeader.setBackgroundColor(Color.parseColor("#FF9800")); // Oranžová
                });

                BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                BluetoothDevice hc05 = null;

                //Find HC-05 as a paired device
                Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
                if (pairedDevices != null) {
                    for (BluetoothDevice device : pairedDevices) {
                        if (device.getName() != null && device.getName().contains("LightTube")) {
                            hc05 = device;
                            break;
                        }
                    }
                }

                if (hc05 != null) {
                    socket = hc05.createInsecureRfcommSocketToServiceRecord(mUUID);
                    bluetoothAdapter.cancelDiscovery();
                    socket.connect();

                    //SUCCESS
                    outputStream = socket.getOutputStream();
                    InputStream inputStream = socket.getInputStream();

                    runOnUiThread(() -> {
                        statusHeader.setText("Zařízení: připojeno");
                        statusHeader.setBackgroundColor(Color.parseColor("#4CAF50")); // Zelená
                        disconnected=false;
                    });

                    //CHECKS WHETHER HC-05 IS CONNECTED
                    byte[] buffer = new byte[1024];
                    while (true) {
                        try {
                            int bytes = inputStream.read(buffer);
                            //----------RECEIVE DATA--------
                        } catch (IOException e) {
                            handleDisconnect();
                            break;
                        }
                    }
                } else {
                    // HC-05 not found
                    if(textBox_viewed==false)
                    {
                        messageBox("Chyba!", "Zařízení LightTube nebylo nalezeno mezi spárovanými BT zařízeními. Proveďte prosím nejprve proces párování v nastavení vašeho telefonu.");
                        textBox_viewed=true;
                    }
                    handleDisconnect();
                }

            } catch (IOException e) {
                e.printStackTrace();
                handleDisconnect();
            }
        }).start();
    }

    private void handleDisconnect() {
        if (isUserClosingApp) return;

        disconnected=true;
        runOnUiThread(() -> {
            statusHeader.setText("Zařízení: odpojeno");
            statusHeader.setBackgroundColor(Color.parseColor("#FF0000")); //RED
        });

        try {
            if (socket != null) socket.close();
        } catch (IOException ignored) {}

        //Wait 5s and try again
        try {
            Thread.sleep(5000);
            connectBT();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void messageBox(String titul, String zprava) {
        runOnUiThread(() -> {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle(titul)
                    .setMessage(zprava)
                    .setPositiveButton("OK", null) //null -> closes after click
                    .show();
        });
    }

    //send data to BT
    public void sendToBT() {
        if (!disconnected) {
            if (outputStream == null) {
                //ToDo: add device not connected info
                return;
            }

            new Thread(() -> {
                try {
                    outputStream.write(data);
                    outputStream.flush(); //send all data from buffer
                } catch (IOException e) {
                    e.printStackTrace();
                    //if sending fails -> disconnects
                    handleDisconnect();
                }
            }).start();
        }
    }

    public boolean getDisconnected(){
        return disconnected;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isUserClosingApp = true;
        try {
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}