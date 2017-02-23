package com.hbbmobile.bsnapp.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.hbbmobile.bsnapp.ConfirmInterface;
import com.hbbmobile.bsnapp.R;

/**
 * Created by buivu on 16/01/2017.
 */
public class DialogCallback implements ConfirmInterface.DialogReturn {
    ConfirmInterface confirmInterface;
    ConfirmInterface.DialogReturn dialogReturn;
    private Context context;
    private boolean result = false;

    public DialogCallback(Context context) {
        this.context = context;
        confirmInterface = new ConfirmInterface();
        confirmInterface.setListener(this);
    }

    public void showAlert(String mess, final Context context) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.customdialogpass);

        // set the custom dialog components - text, image and button
        TextView text = (TextView) dialog.findViewById(R.id.tv);
        text.setText(mess);
        Button dialogButton = (Button) dialog.findViewById(R.id.btnok);
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                result = true;
                dialog.dismiss();

              //  result = !result;
                //  confirmInterface.getListener().onDialogCompleted(true);
            }
        });

        dialog.show();
    }

    public boolean getResult() {
        return result;
    }

    @Override
    public boolean onDialogCompleted(boolean answer) {
        return answer;
    }
}
