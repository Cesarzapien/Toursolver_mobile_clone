package com.cesar.toursolvermobile2.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cesar.toursolvermobile2.R;
import com.cesar.toursolvermobile2.model.AgendaModel;

import java.util.List;

public class AgendaAdapter extends RecyclerView.Adapter<AgendaAdapter.AgendaViewHolder> {

    private Context context;
    private List<AgendaModel> agendaList;

    public AgendaAdapter(Context context, List<AgendaModel> agendaList) {
        this.context = context;
        this.agendaList = agendaList;
    }

    public void updateData(List<AgendaModel> newAgendaList) {
        this.agendaList = newAgendaList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AgendaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cita_recycler_two, parent, false);
        return new AgendaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AgendaViewHolder holder, int position) {
        AgendaModel agendaModel = agendaList.get(position);

        // Set data to views
        holder.citaHoraTextView.setText(String.format("%s - %s",
                agendaModel.getStopStartTime(), agendaModel.getStopEndTime()));
        holder.citaNombreTextView.setText(agendaModel.getLabel());

        // Set drawable based on status
        Drawable drawable;
        switch (agendaModel.getStatus()) {
            case "ACCEPTED":
                drawable = context.getResources().getDrawable(R.drawable.vertical_rectangle);
                break;
            case "STARTED":
                drawable = context.getResources().getDrawable(R.drawable.rectangulo_amarillo);
                break;
            case "CANCELLED":
                drawable = context.getResources().getDrawable(R.drawable.rectangulo_morado);
                break;
            case "FINISHED":
                drawable = context.getResources().getDrawable(R.drawable.rectangulo_verde);
                break;
            default:
                drawable = context.getResources().getDrawable(R.drawable.line_pattern);
                break;
        }
        holder.rectangulo.setBackground(drawable);

        // Example for arrow button click listener
        holder.arrowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle arrow button click
                // Example: You can implement navigation or other actions here
            }
        });
    }

    @Override
    public int getItemCount() {
        return agendaList.size();
    }

    public static class AgendaViewHolder extends RecyclerView.ViewHolder {
        TextView citaHoraTextView, citaNombreTextView;
        View rectangulo;
        ImageButton arrowButton;

        public AgendaViewHolder(@NonNull View itemView) {
            super(itemView);
            citaHoraTextView = itemView.findViewById(R.id.cita_hora_two);
            citaNombreTextView = itemView.findViewById(R.id.cita_nombre_two);
            rectangulo = itemView.findViewById(R.id.rectangulo_cita_two);
            arrowButton = itemView.findViewById(R.id.arrow_button);
        }
    }
}