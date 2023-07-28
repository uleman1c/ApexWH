package com.example.apexwh.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import android.os.Handler;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

public class ShtrihCodeInput {

    public EditText actvShtrihCode;
    public Handler hSetFocus;
    public Runnable setFocus;
    public Thread thread;
    public InputMethodManager imm;
    public Boolean shtrihCodeKeyboard;

    public BroadcastReceiver broadcastReceiver;


    public interface AfterScanShtrih{

        void Scan(String shtrihcode);

    }



    public ShtrihCodeInput(FragmentActivity fragmentActivity, View root, int RidactvShtrihCode, int RidibKeyboard, AfterScanShtrih afterScanShtrih) {

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String strCatName = intent.getStringExtra("EXTRA_BARCODE_DECODING_DATA");

                afterScanShtrih.Scan(strCatName.substring(0, strCatName.length()-1));

            }
        };

        actvShtrihCode = root.findViewById(RidactvShtrihCode);

        imm = (InputMethodManager) fragmentActivity.getSystemService(Context.INPUT_METHOD_SERVICE);

        shtrihCodeKeyboard = false;

        hSetFocus = new Handler();

        setFocus = new Runnable() {
            public void run() {

                actvShtrihCode.requestFocus();

                if (actvShtrihCode.isFocused() && !shtrihCodeKeyboard) {

                    imm.hideSoftInputFromWindow(actvShtrihCode.getWindowToken(), 0);

                }

                hSetFocus.postDelayed(setFocus, 500);

            }
        };


        thread = new Thread(new Runnable() {
            public void run() {
                hSetFocus.post(setFocus);
            }
        });
        thread.start();

        actvShtrihCode.requestFocus();

        imm.hideSoftInputFromWindow(actvShtrihCode.getWindowToken(), 0);

        actvShtrihCode.setOnKeyListener(new View.OnKeyListener() {
                                            public boolean onKey(View v, int keyCode, KeyEvent event) {
                                                if (event.getAction() == KeyEvent.ACTION_DOWN &&
                                                        (keyCode == KeyEvent.KEYCODE_ENTER)) {

                                                    String strCatName = actvShtrihCode.getText().toString();

                                                    actvShtrihCode.setText("");

                                                    shtrihCodeKeyboard = false;

                                                    imm.hideSoftInputFromWindow(actvShtrihCode.getWindowToken(), 0);

                                                    afterScanShtrih.Scan(strCatName);

                                                    return true;
                                                }
                                                return false;
                                            }
                                        }
        );


        root.findViewById(RidibKeyboard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                actvShtrihCode.requestFocus();

                if (shtrihCodeKeyboard) {

                    imm.hideSoftInputFromWindow(actvShtrihCode.getWindowToken(), 0);

                } else {

                    imm.showSoftInput(actvShtrihCode, 0, null);
                }

                shtrihCodeKeyboard = !shtrihCodeKeyboard;


            }
        });

    }

    public void RegisterReceiver(FragmentActivity fragmentActivity){

        fragmentActivity.registerReceiver(broadcastReceiver, new IntentFilter("com.xcheng.scanner.action.BARCODE_DECODING_BROADCAST"));

    }

}
