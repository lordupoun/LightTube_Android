package com.example.lighttube;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import androidx.fragment.app.Fragment;

public class OvladaniFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //VIEW from XML
        View v = inflater.inflate(R.layout.fragment_ovladani, container, false);

        Button btnSend = v.findViewById(R.id.btnSend);
        Button btnCancel = v.findViewById(R.id.btnCancel);
        CheckBox[] check = new CheckBox[6];
        check[0] = v.findViewById(R.id.check1);
        check[1] = v.findViewById(R.id.check2);
        check[2] = v.findViewById(R.id.check3);
        check[3] = v.findViewById(R.id.check4);
        check[4] = v.findViewById(R.id.check5);
        check[5] = v.findViewById(R.id.check6);
        EditText brightness = v.findViewById(R.id.brightness);
        EditText colour1 = v.findViewById(R.id.colour1);
        EditText colour2 = v.findViewById(R.id.colour2);
        EditText effect1 = v.findViewById(R.id.effect1);
        EditText effect2 = v.findViewById(R.id.effect2);
        EditText tempo = v.findViewById(R.id.tempo);


        btnSend.setOnClickListener(view -> {
            byte[] data = new byte[6];
            data[0] = parseInput(brightness);
            data[1] = parseInput(colour1);
            data[2] = parseInput(colour2);
            data[3] = parseInput(effect1);
            data[4] = parseInput(effect2);
            data[5] = parseInput(tempo);
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

        btnCancel.setOnClickListener(view -> {
            byte[] data = new byte[6];
            for(byte i : data){
                i=0;
            }
            if (getActivity() != null) {
                ((MainActivity) getActivity()).setTempo(data[0]);
            }
            for(int i=0; i<check.length; i++){
                //if(check[i].isChecked()){
                    if (getActivity() != null) {
                        ((MainActivity) getActivity()).setData(data,i*5+1);
                    }
                //}
            }
            if (getActivity() != null) {
                ((MainActivity) getActivity()).sendToBT();
            }
        });


        return v;
    }
    //Takes EditText and process it
    private byte parseInput(EditText et) {
        try {
            int val = Integer.parseInt(et.getText().toString());
            return (byte) Math.max(0, Math.min(255, val));
        } catch (Exception e) {
            return 0;
        }
    }
}

