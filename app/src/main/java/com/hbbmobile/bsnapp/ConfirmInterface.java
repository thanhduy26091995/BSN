package com.hbbmobile.bsnapp;

/**
 * Created by buivu on 16/01/2017.
 */
public class ConfirmInterface {
    DialogReturn dialogReturn;

    //public void
    public interface DialogReturn {
        boolean onDialogCompleted(boolean answer);
    }

    public void setListener(DialogReturn dialogReturn) {
        this.dialogReturn = dialogReturn;
    }

    public DialogReturn getListener() {
        return dialogReturn;

    }
}
