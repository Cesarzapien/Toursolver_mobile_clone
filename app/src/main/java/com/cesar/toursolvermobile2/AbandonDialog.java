package com.cesar.toursolvermobile2;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AbandonDialog extends DialogFragment {

    public interface AbandonDialogListener {
        void onAbandonConfirmed(String reason);
    }

    private AbandonDialogListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity(), R.style.DialogTheme);
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_abandon, null);

        builder.setView(view);

        // Evita que se cierre al tocar fuera del diálogo
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);

        Button btnCancel = view.findViewById(R.id.btn_cancel);
        Button btnAbandonar = view.findViewById(R.id.btn_abandonar);
        EditText editText = view.findViewById(R.id.edit_text);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        btnAbandonar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String reason = editText.getText().toString();
                if (listener != null) {
                    listener.onAbandonConfirmed(reason);
                }
                dismiss();
            }
        });

        return dialog;
    }

    public void setAbandonDialogListener(AbandonDialogListener listener) {
        this.listener = listener;
    }
}