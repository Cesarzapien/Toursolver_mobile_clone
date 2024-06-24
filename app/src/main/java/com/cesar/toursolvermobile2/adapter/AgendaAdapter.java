package com.cesar.toursolvermobile2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cesar.toursolvermobile2.R;
import com.cesar.toursolvermobile2.model.OperationalOrderAchievement;
import com.cesar.toursolvermobile2.model.Order;
import com.cesar.toursolvermobile2.model.PlannedOrder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AgendaAdapter extends RecyclerView.Adapter<AgendaAdapter.AgendaViewHolder> {

    private List<PlannedOrder> plannedOrders;
    private List<Order> orders;
    private List<OperationalOrderAchievement> achievements;
    private Context context;

    public AgendaAdapter(Context context, List<PlannedOrder> plannedOrders, List<Order> orders, List<OperationalOrderAchievement> achievements) {
        this.context = context;
        this.plannedOrders = plannedOrders;
        this.orders = orders;
        this.achievements = achievements;
    }

    public void updateData(List<PlannedOrder> newPlannedOrders, List<Order> newOrders, List<OperationalOrderAchievement> newAchievements) {
        this.plannedOrders = newPlannedOrders;
        this.orders = newOrders;
        this.achievements = newAchievements;
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
        PlannedOrder plannedOrder = plannedOrders.get(position);
        Order order = orders.get(position); // Get corresponding order
        OperationalOrderAchievement achievement = achievements.get(position); // Get corresponding achievement

        if (!"Llegada".equals(plannedOrder.getStopId())) {
            holder.bind(plannedOrder, order, achievement); // Pass achievement to bind method
        }
    }

    @Override
    public int getItemCount() {
        return plannedOrders.size();
    }

    public class AgendaViewHolder extends RecyclerView.ViewHolder {
        TextView citaHoraTwo, citaNombre;
        ImageButton arrowButton;

        public AgendaViewHolder(@NonNull View itemView) {
            super(itemView);
            citaHoraTwo = itemView.findViewById(R.id.cita_hora_two);
            citaNombre = itemView.findViewById(R.id.cita_nombre);
            arrowButton = itemView.findViewById(R.id.arrow_button);
        }

        public void bind(PlannedOrder plannedOrder, Order order, OperationalOrderAchievement achievement) {
            SimpleDateFormat inputFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

            try {
                // Parsear stopStartTime
                Date startTime = inputFormat.parse(plannedOrder.getStopStartTime());
                // Formatear stopStartTime a "HH:mm"
                String formattedStartTime = outputFormat.format(startTime);

                // Parsear stopDuration
                String[] durationParts = plannedOrder.getStopDuration().split(":");
                int durationMinutes = Integer.parseInt(durationParts[1]);

                // Crear un calendario para sumar stopDuration a startTime
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(startTime);
                calendar.add(Calendar.MINUTE, durationMinutes);

                // Formatear el tiempo final a "HH:mm"
                String formattedEndTime = outputFormat.format(calendar.getTime());

                // Set the formatted time and duration
                citaHoraTwo.setText(String.format("%s - %s (%d min)", formattedStartTime, formattedEndTime, durationMinutes));

            } catch (ParseException e) {
                e.printStackTrace();
                citaHoraTwo.setText(plannedOrder.getStopStartTime());
            }

            // Set the name from order
            if (order != null) {
                citaNombre.setText(order.getLabel());
            } else {
                citaNombre.setText("Nombre");
            }

            // Add OnClickListener to show a Toast message
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ("Salida".equals(plannedOrder.getStopId())) {
                        Toast.makeText(context, "Itinerario", Toast.LENGTH_SHORT).show();
                    } else if (achievement != null && "FINISHED".equals(achievement.getStatus())) {
                        Toast.makeText(context, "Cita terminada", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Cita aceptada", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            // Add OnClickListener to arrowButton
            arrowButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Implement your action for the arrow button click here
                }
            });
        }
    }
}