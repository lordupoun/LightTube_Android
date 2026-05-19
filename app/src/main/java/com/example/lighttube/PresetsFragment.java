package com.example.lighttube;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridLayout;

import androidx.constraintlayout.helper.widget.Flow;
import androidx.fragment.app.Fragment;

import java.util.HashMap;
import java.util.Map;


//ToDo: FIX state when HC-05 disconnects, handleDisconnect() is called which waits 5s Thread.sleep(5000)) and then calls connectBT -> connectBT creates a new thread each time (new Thread(() -> {...}).start();).

public class PresetsFragment extends Fragment {

    private Map<Integer, Byte> btnColours = new HashMap<>();
    private Map<Integer, byte[]> btnEffects = new HashMap<>();
    private byte[] data = new byte[6];




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_preset, container, false);

        setupColourButtons();
        setupSecondaryColourButtons();
        setupEffectButtons();

        //default values:
        data[0] = (byte)164; //default tempo
        data[1] = (byte)255; //default brightness
        data[2] = (byte)10; //default colour 1
        data[3] = (byte)50; //default colour 2
        data[4] = (byte)1; //default effect 1
        data[5] = (byte)160; //default effect 2

        CheckBox[] check = new CheckBox[6];
        check[0] = v.findViewById(R.id.check1);
        check[1] = v.findViewById(R.id.check2);
        check[2] = v.findViewById(R.id.check3);
        check[3] = v.findViewById(R.id.check4);
        check[4] = v.findViewById(R.id.check5);
        check[5] = v.findViewById(R.id.check6);
        EditText brightness = v.findViewById(R.id.brightness);
        EditText tempo = v.findViewById(R.id.tempo);
        GridLayout gridColours = v.findViewById(R.id.grid_barvy);
        GridLayout gridSecondaryColours = v.findViewById(R.id.grid_barvy2);
        GridLayout gridEffects = v.findViewById(R.id.grid_efekty);

        brightness.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Not needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Not needed
            }

            @Override
            public void afterTextChanged(Editable s) {
                String enteredValue = s.toString();
                int brightness = 0;
                if (!enteredValue.isEmpty()) {
                    try {
                        brightness = Integer.parseInt(enteredValue);
                        if(brightness>255||brightness<0)
                        {
                            brightness=0;
                        }
                    }
                    catch (NumberFormatException e) {
                        //Log.e("Control, "Error when converting", e);
                    }
                    data[1]=(byte)brightness;
                }
            }
        });

        tempo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Not needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Not needed
            }

            @Override
            public void afterTextChanged(Editable s) {
                String enteredValue = s.toString();
                int tempo = 0;
                if (!enteredValue.isEmpty()) {
                    try {
                        tempo = Integer.parseInt(enteredValue);
                        if(tempo>255||tempo<0)
                        {
                            tempo=0;
                        }
                    }
                    catch (NumberFormatException e) {
                        //Log.e("Control, "Error when converting", e);
                    }
                    data[0]=(byte)tempo;
                    //Doesnt send packet immediately - colour or effect needs to be changed first!
                }
            }
        });

        //Colours setOnClick
        for (int l = 0; l < gridColours.getChildCount(); l++) {

            View child = gridColours.getChildAt(l);
            child.setOnClickListener(view -> {
                data[2] = btnColours.get(view.getId());
                if (getActivity() != null) {
                    ((MainActivity) getActivity()).setTempo(data[0]);
                }
                for(int i=0; i<check.length; i++){
                    if(check[i].isChecked()){
                        if (getActivity() != null) {
                            ((MainActivity) getActivity()).setData(data,i*5+1);
                        }
                    }
                }
                if (getActivity() != null) {
                    ((MainActivity) getActivity()).sendToBT();
                }
            });
        }

        //Secondary colours setOnClick
        for (int l = 0; l < gridSecondaryColours.getChildCount(); l++) {

            View child = gridSecondaryColours.getChildAt(l);
            child.setOnClickListener(view -> {
                data[3] = btnColours.get(view.getId());
                if (getActivity() != null) {
                    ((MainActivity) getActivity()).setTempo(data[0]);
                }
                for(int i=0; i<check.length; i++){
                    if(check[i].isChecked()){
                        if (getActivity() != null) {
                            ((MainActivity) getActivity()).setData(data,i*5+1);
                        }
                    }
                }
                if (getActivity() != null) {
                    ((MainActivity) getActivity()).sendToBT();
                }
            });
        }

        //Effects setOnClick
        for (int l = 0; l < gridEffects.getChildCount(); l++) {

            View child = gridEffects.getChildAt(l);
            child.setOnClickListener(view -> {
                byte[] values = btnEffects.get(view.getId());
                data[4] = values[0];
                data[5] = values[1];
                if (getActivity() != null) {
                    ((MainActivity) getActivity()).setTempo(data[0]);
                }
                for(int i=0; i<check.length; i++){
                    if(check[i].isChecked()){
                        if (getActivity() != null) {
                            ((MainActivity) getActivity()).setData(data,i*5+1);
                        }
                    }
                }
                if (getActivity() != null) {
                    ((MainActivity) getActivity()).sendToBT();
                }
            });
        }

        //DEFAULT - init fragment:
        if (getActivity() != null) {
            ((MainActivity) getActivity()).setTempo(data[0]);
        }
        for(int i=0; i<check.length; i++){
            if (getActivity() != null) {
                ((MainActivity) getActivity()).setData(data,i*5+1);
            }
        }
        if (getActivity() != null) {
            ((MainActivity) getActivity()).sendToBT();
        }

        return v;
    }

    private void setupColourButtons() {
        btnColours.put(R.id.btn1, (byte)0);
        btnColours.put(R.id.btn2, (byte)240);
        btnColours.put(R.id.btn3, (byte)249);
        btnColours.put(R.id.btn4, (byte)145);
        btnColours.put(R.id.btn5, (byte)217);
        btnColours.put(R.id.btn6, (byte)173);
        btnColours.put(R.id.btn7, (byte)213);
        btnColours.put(R.id.btn8, (byte)209);
        btnColours.put(R.id.btn9, (byte)241);
        btnColours.put(R.id.btn10, (byte)14);
        btnColours.put(R.id.btn11, (byte)10);
        btnColours.put(R.id.btn12, (byte)205);
        btnColours.put(R.id.btn13, (byte)197);
        btnColours.put(R.id.btn14, (byte)181);
        btnColours.put(R.id.btn15, (byte)245);
        btnColours.put(R.id.btn16, (byte)185);
        btnColours.put(R.id.btn17, (byte)125);
        btnColours.put(R.id.btn18, (byte)161);
        btnColours.put(R.id.btn19, (byte)153);
        btnColours.put(R.id.btn20, (byte)255);
    }

    private void setupSecondaryColourButtons() {
        btnColours.put(R.id.btn01, (byte)0);
        btnColours.put(R.id.btn02, (byte)240);
        btnColours.put(R.id.btn03, (byte)249);
        btnColours.put(R.id.btn04, (byte)145);
        btnColours.put(R.id.btn05, (byte)217);
        btnColours.put(R.id.btn06, (byte)173);
        btnColours.put(R.id.btn07, (byte)213);
        btnColours.put(R.id.btn08, (byte)209);
        btnColours.put(R.id.btn09, (byte)241);
        btnColours.put(R.id.btn010, (byte)14);
        btnColours.put(R.id.btn011, (byte)10);
        btnColours.put(R.id.btn012, (byte)205);
        btnColours.put(R.id.btn013, (byte)197);
        btnColours.put(R.id.btn014, (byte)181);
        btnColours.put(R.id.btn015, (byte)245);
        btnColours.put(R.id.btn016, (byte)185);
        btnColours.put(R.id.btn017, (byte)125);
        btnColours.put(R.id.btn018, (byte)161);
        btnColours.put(R.id.btn019, (byte)153);
        btnColours.put(R.id.btn020, (byte)255);
    }

    private void setupEffectButtons() {
        btnEffects.put(R.id.btne1,  new byte[]{ (byte)0,  (byte)100 });
        btnEffects.put(R.id.btne2,  new byte[]{ (byte)2,  (byte)100 });
        btnEffects.put(R.id.btne3,  new byte[]{ (byte)8,  (byte)100 });
        btnEffects.put(R.id.btne4,  new byte[]{ (byte)10,  (byte)100 });

        btnEffects.put(R.id.btne5,  new byte[]{ (byte)14,  (byte)100 });
        btnEffects.put(R.id.btne6,  new byte[]{ (byte)14,  (byte)120 });
        btnEffects.put(R.id.btne7,  new byte[]{ (byte)14,  (byte)160 });
        btnEffects.put(R.id.btne8,  new byte[]{ (byte)14,  (byte)200 });

        btnEffects.put(R.id.btne9,  new byte[]{ (byte)16,  (byte)120 });
        btnEffects.put(R.id.btne10, new byte[]{ (byte)16,  (byte)150 });
        btnEffects.put(R.id.btne11, new byte[]{ (byte)16,  (byte)200 });
        btnEffects.put(R.id.btne12, new byte[]{ (byte)16,  (byte)250 });

        btnEffects.put(R.id.btne13, new byte[]{ (byte)18,  (byte)100 });
        btnEffects.put(R.id.btne14, new byte[]{ (byte)18,  (byte)120 });
        btnEffects.put(R.id.btne15, new byte[]{ (byte)18,  (byte)160 });
        btnEffects.put(R.id.btne16, new byte[]{ (byte)18,  (byte)200 });

        btnEffects.put(R.id.btne17, new byte[]{ (byte)20,  (byte)100 });
        btnEffects.put(R.id.btne18, new byte[]{ (byte)20,  (byte)120 });
        btnEffects.put(R.id.btne19, new byte[]{ (byte)20,  (byte)160 });
        btnEffects.put(R.id.btne20, new byte[]{ (byte)20,  (byte)200 });

        btnEffects.put(R.id.btne21, new byte[]{ (byte)24,  (byte)160 });
        btnEffects.put(R.id.btne22, new byte[]{ (byte)24,  (byte)200 });
        btnEffects.put(R.id.btne23, new byte[]{ (byte)28,  (byte)100 });
        btnEffects.put(R.id.btne24, new byte[]{ (byte)28,  (byte)200 });

        btnEffects.put(R.id.btne25, new byte[]{ (byte)30,  (byte)120 });
        btnEffects.put(R.id.btne26, new byte[]{ (byte)30,  (byte)180 });
        btnEffects.put(R.id.btne27, new byte[]{ (byte)38,  (byte)200 });
        btnEffects.put(R.id.btne28, new byte[]{ (byte)38,  (byte)250 });

        btnEffects.put(R.id.btne29, new byte[]{ (byte)44,  (byte)100 });
        btnEffects.put(R.id.btne30, new byte[]{ (byte)44,  (byte)200 });
        btnEffects.put(R.id.btne31, new byte[]{ (byte)46,  (byte)100 });
        btnEffects.put(R.id.btne32, new byte[]{ (byte)46,  (byte)200 });

        btnEffects.put(R.id.btne33, new byte[]{ (byte)48,  (byte)100 });
        btnEffects.put(R.id.btne34, new byte[]{ (byte)48,  (byte)200 });
        btnEffects.put(R.id.btne35, new byte[]{ (byte)52,  (byte)70 });
        btnEffects.put(R.id.btne36, new byte[]{ (byte)52,  (byte)200 });

        btnEffects.put(R.id.btne37, new byte[]{ (byte)54,  (byte)50 });
        btnEffects.put(R.id.btne38, new byte[]{ (byte)54,  (byte)60 });
        btnEffects.put(R.id.btne39, new byte[]{ (byte)58,  (byte)180 });
        btnEffects.put(R.id.btne40, new byte[]{ (byte)58,  (byte)200 });

        btnEffects.put(R.id.btne41, new byte[]{ (byte)62,  (byte)150 });
        btnEffects.put(R.id.btne42, new byte[]{ (byte)62,  (byte)160 });
        btnEffects.put(R.id.btne43, new byte[]{ (byte)64,  (byte)180 });
        btnEffects.put(R.id.btne44, new byte[]{ (byte)66,  (byte)160 });

        btnEffects.put(R.id.btne45, new byte[]{ (byte)68,  (byte)160 });
        btnEffects.put(R.id.btne46, new byte[]{ (byte)68,  (byte)180 });
        btnEffects.put(R.id.btne47, new byte[]{ (byte)70,  (byte)160 });
        btnEffects.put(R.id.btne48, new byte[]{ (byte)70,  (byte)180 });

        btnEffects.put(R.id.btne49, new byte[]{ (byte)72,  (byte)180 });
        btnEffects.put(R.id.btne50, new byte[]{ (byte)72,  (byte)200 });
        btnEffects.put(R.id.btne51, new byte[]{ (byte)74,  (byte)200 });
        btnEffects.put(R.id.btne52, new byte[]{ (byte)74,  (byte)250 });

        btnEffects.put(R.id.btne53, new byte[]{ (byte)78,  (byte)80 });
        btnEffects.put(R.id.btne54, new byte[]{ (byte)78,  (byte)200 });
        btnEffects.put(R.id.btne55, new byte[]{ (byte)80,  (byte)200 });
        btnEffects.put(R.id.btne56, new byte[]{ (byte)82,  (byte)70 });

        btnEffects.put(R.id.btne57, new byte[]{ (byte)84,  (byte)200 });
        btnEffects.put(R.id.btne58, new byte[]{ (byte)86,  (byte)50 });
        btnEffects.put(R.id.btne59, new byte[]{ (byte)88,  (byte)54 });
        btnEffects.put(R.id.btne60, new byte[]{ (byte)250,  (byte)0 });

    }



}


