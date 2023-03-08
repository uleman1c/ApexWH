package com.example.apexwh.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.apexwh.R;

public class Dialogs {


    public static void showQuestionYesNoCancel(Context mCtx, Activity activity, final BundleMethodInterface bundleMethodInterface, final Bundle arguments, String question, String title) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mCtx);
        alertDialogBuilder.setTitle(title);

        //alertDialogBuilder.setIcon(R.drawable.sklad96);

        LayoutInflater inflater = activity.getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_yes_no_cancel, null);

        ((TextView) view.findViewById(R.id.tvQuestion)).setText(question);

        alertDialogBuilder.setView(view);
        //alertDialogBuilder.setIcon(R.drawable.sklad96);

        final AlertDialog alertDialog = alertDialogBuilder.create();

        view.findViewById(R.id.btnYes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                bundleMethodInterface.callMethod(arguments);

                alertDialog.cancel();


            }
        });

        view.findViewById(R.id.btnNo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.cancel();

            }
        });

        view.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.cancel();

            }
        });

        alertDialog.show();



    }
    public static void showProductMenu(Context mCtx, Activity activity, final BundleMethodInterface bundleMethodInterface, final Bundle arguments, String question, String title) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mCtx);
        alertDialogBuilder.setTitle(title);

        //alertDialogBuilder.setIcon(R.drawable.sklad96);

        LayoutInflater inflater = activity.getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_product_menu, null);

        ((TextView) view.findViewById(R.id.tvQuestion)).setText(question);

        alertDialogBuilder.setView(view);
        //alertDialogBuilder.setIcon(R.drawable.sklad96);

        final AlertDialog alertDialog = alertDialogBuilder.create();

        view.findViewById(R.id.btnInputNumber).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                arguments.putString("btn", "InputNumber");

                bundleMethodInterface.callMethod(arguments);

                alertDialog.cancel();


            }
        });

        view.findViewById(R.id.btnFoto).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                arguments.putString("btn", "Foto");

                bundleMethodInterface.callMethod(arguments);

                alertDialog.cancel();

            }
        });

        view.findViewById(R.id.btnChangeCharcteristic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                arguments.putString("btn", "ChangeCharcteristic");

                bundleMethodInterface.callMethod(arguments);

                alertDialog.cancel();

            }
        });

        alertDialog.show();



    }

    public static void showReturnMenu(Context mCtx, Activity activity, final BundleMethodInterface bundleMethodInterface, final Bundle arguments, String question, String title) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mCtx);
        alertDialogBuilder.setTitle(title);

        //alertDialogBuilder.setIcon(R.drawable.sklad96);

        LayoutInflater inflater = activity.getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_return_menu, null);

        ((TextView) view.findViewById(R.id.tvQuestion)).setText(question);

        alertDialogBuilder.setView(view);
        //alertDialogBuilder.setIcon(R.drawable.sklad96);

        final AlertDialog alertDialog = alertDialogBuilder.create();

        view.findViewById(R.id.btnClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                arguments.putString("btn", "Close");

                bundleMethodInterface.callMethod(arguments);

                alertDialog.cancel();


            }
        });

        view.findViewById(R.id.btnCloseToChangeCharacteristic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                arguments.putString("btn", "CloseToChangeCharacteristic");

                bundleMethodInterface.callMethod(arguments);

                alertDialog.cancel();

            }
        });

        alertDialog.show();



    }

    public static void showReturnOfProductsMenu(Context mCtx, Activity activity, final BundleMethodInterface bundleMethodInterface, final Bundle arguments, String question, String title) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mCtx);
        alertDialogBuilder.setTitle(title);

        //alertDialogBuilder.setIcon(R.drawable.sklad96);

        LayoutInflater inflater = activity.getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_return_of_products_menu, null);

        ((TextView) view.findViewById(R.id.tvQuestion)).setText(question);

        alertDialogBuilder.setView(view);
        //alertDialogBuilder.setIcon(R.drawable.sklad96);

        final AlertDialog alertDialog = alertDialogBuilder.create();

        view.findViewById(R.id.btnOrderToChangeCharacteristic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                arguments.putString("btn", "OrderToChangeCharacteristic");

                bundleMethodInterface.callMethod(arguments);

                alertDialog.cancel();

            }
        });

        alertDialog.show();



    }

    public static void showInputQuantity(Context mCtx, Integer quantity, Activity activity, final BundleMethodInterface bundleMethodInterface, final Bundle arguments, String question, String title) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mCtx);
        alertDialogBuilder.setTitle("Ввод количества");

        LayoutInflater inflater = activity.getLayoutInflater();

        final View view = inflater.inflate(R.layout.dialog_input_quantity, null);

        alertDialogBuilder.setTitle(title);

        ((TextView) view.findViewById(R.id.tvCode)).setText(question);

        Button btnQuantity = view.findViewById(R.id.btnQuantity);

        EditText etQuantity = view.findViewById(R.id.etQuantity);

        if(quantity == null){

            btnQuantity.setVisibility(View.GONE);
        }
        else {

            btnQuantity.setVisibility(View.VISIBLE);
            btnQuantity.setText("<<  " + quantity.toString());
        }

        alertDialogBuilder.setView(view);
        //alertDialogBuilder.setIcon(R.drawable.sklad96);

        final AlertDialog quantityDialog = alertDialogBuilder.create();

        btnQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                etQuantity.setText(quantity.toString());

                quantityDialog.cancel();

                Integer quantity2 = Integer.valueOf(((TextView) view.findViewById(R.id.etQuantity)).getText().toString());

                arguments.putInt("quantity", quantity2);

                bundleMethodInterface.callMethod(arguments);

            }
        });

        view.findViewById(R.id.btnOK).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String strQuantity = ((TextView) view.findViewById(R.id.etQuantity)).getText().toString();

                if (!strQuantity.isEmpty()) {

                    if (Integer.valueOf(strQuantity) > quantity){

                        etQuantity.setText("");

                    }
                    else {
                        quantityDialog.cancel();

                        Integer quantity2 = Integer.valueOf(strQuantity);

                        arguments.putInt("quantity", quantity2);

                        bundleMethodInterface.callMethod(arguments);
                    }
                }
            }
        });

        quantityDialog.show();

        etQuantity.requestFocus();

        final Handler h2 = new Handler();

        final Runnable setFocus2 = new Runnable() {
            public void run() {

                InputMethodManager imm = (InputMethodManager) quantityDialog.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

                etQuantity.requestFocus();

                imm.showSoftInput(etQuantity, 0, null);

//                h2.postDelayed(setFocus2, 500);

            }
        };

        h2.postDelayed(setFocus2, 500);

//        Thread t = new Thread(new Runnable() {
//            public void run() {
//                h2.post(setFocus2);
//            }
//        });
//        t.start();




    }


}
