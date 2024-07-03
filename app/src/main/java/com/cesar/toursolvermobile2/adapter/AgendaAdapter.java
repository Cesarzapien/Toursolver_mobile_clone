package com.cesar.toursolvermobile2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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

        // Adjust visibility or behavior based on your logic if needed

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
        ImageButton arrowButton;

        public AgendaViewHolder(@NonNull View itemView) {
            super(itemView);
            citaHoraTextView = itemView.findViewById(R.id.cita_hora_two);
            citaNombreTextView = itemView.findViewById(R.id.cita_nombre);
            arrowButton = itemView.findViewById(R.id.arrow_button);
        }
    }
}
