package com.cesar.toursolvermobile2;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsCompat.Type;

import com.cesar.toursolvermobile2.databinding.ActivityAgendaBinding;
import com.cesar.toursolvermobile2.model.ApiResponse;
import com.cesar.toursolvermobile2.model.Geocode;
import com.cesar.toursolvermobile2.model.LastKnownPosition;
import com.cesar.toursolvermobile2.model.OperationalOrderAchievement;
import com.cesar.toursolvermobile2.model.Order;
import com.cesar.toursolvermobile2.model.PlannedOrder;
import com.google.android.material.navigation.NavigationView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AgendaActivity extends DrawerBaseActivity {
    public String hora_global;
    public static final String BASE_URL = "https://api.geoconcept.com/tsapi/";
    public static final String API_KEY = "9e313fb763515473";
    public static final String ACCEPT = "application/json";
    ActualizarAlert actualizarAlert = new ActualizarAlert(AgendaActivity.this);
    TextView userNamee, userEmaill;
    ActivityAgendaBinding activityAgendaBinding;
    private List<LastKnownPosition> lastKnownPositions2;
    private List<PlannedOrder> plannedOrders2;
    private List<Order> orders2;
    private List<OperationalOrderAchievement> achievements2;
    private List<Geocode> geocodes2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflar el binding y configurar la vista
        activityAgendaBinding = ActivityAgendaBinding.inflate(getLayoutInflater());
        setContentView(activityAgendaBinding.getRoot());

        // Establecer el título de la actividad
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Agenda");
        }

        // Obtener los datos del Intent
        Intent intent = getIntent();
        String userName = intent.getStringExtra("user_name");
        String userEmail = intent.getStringExtra("user_email");

        // Obtener la referencia del NavigationView
        NavigationView navigationView = findViewById(R.id.nav_view);

        // Obtener la referencia del HeaderView del NavigationView
        View headerView = navigationView.getHeaderView(0);

        // Obtener los TextView del HeaderView
        userNamee = headerView.findViewById(R.id.username); // Asegúrate de tener este TextView en tu layout
        userEmaill = headerView.findViewById(R.id.useremail); // Asegúrate de tener este TextView en tu layout

        // Establecer los datos del usuario en los TextView
        if (userName != null) {
            userNamee.setText(userName);
        }

        if (userEmail != null) {
            userEmaill.setText(userEmail);
        }

        // Obtener la referencia al Toolbar
        Toolbar toolbar = findViewById(R.id.toolBar);

        // Crear y configurar el ImageButton
        ImageButton imageButton = new ImageButton(this);
        imageButton.setImageResource(R.drawable.baseline_autorenew_24); // Reemplaza con tu imagen
        imageButton.setContentDescription(getString(R.string.image_button_desc));
        imageButton.setVisibility(View.VISIBLE);

        // Establecer fondo selectable para el ImageButton
        TypedValue outValue = new TypedValue();
        getTheme().resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, outValue, true);
        imageButton.setBackgroundResource(outValue.resourceId);

        // Configurar el evento de clic del ImageButton
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Manejar el evento de clic del ImageButton
                Toast.makeText(AgendaActivity.this, "ImageButton clicked", Toast.LENGTH_SHORT).show();
            }
        });

        // Añadir margen al ImageButton
        Toolbar.LayoutParams layoutParams = new Toolbar.LayoutParams(
                Toolbar.LayoutParams.WRAP_CONTENT,
                Toolbar.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = android.view.Gravity.END;
        layoutParams.setMarginEnd((int) getResources().getDimension(R.dimen.image_button_margin_end));
        toolbar.addView(imageButton, layoutParams);
    }

    private void refreshData(boolean updateData) {
        if (updateData) {
            // Mostrar el diálogo de carga
            actualizarAlert.startAlertDialog();

            // Obtener la fecha y hora actual
            Calendar calendar = Calendar.getInstance();
            Date currentDate = calendar.getTime();

            // Calcular la fecha para el día siguiente
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            Date tomorrowDate = calendar.getTime();

            // Formatear las fechas en el formato necesario para la llamada a la API
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm", Locale.getDefault());
            hora_global = sdf2.format(currentDate);
            String startDate = sdf.format(currentDate);
            String endDate = sdf.format(tomorrowDate);

            // Obtener el correo del usuario (este valor debe ser obtenido desde el intent o algún otro lugar)
            String userLogin = getIntent().getStringExtra("user_email"); // Asumiendo que es el correo

            // Crear una instancia de Retrofit
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            // Crear una instancia del servicio de la API
            CitaActivity.ApiService2 apiService = retrofit.create(CitaActivity.ApiService2.class);

            // Realizar la llamada a la API
            Call<ApiResponse> call = apiService.getFulfillment(
                    API_KEY,
                    ACCEPT,
                    endDate, // Usar la fecha de mañana como endDate
                    startDate, // Usar la fecha actual como startDate
                    userLogin
            );

            // Ejecutar la llamada de manera asíncrona
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    actualizarAlert.closeAlertDialog();
                    if (response.isSuccessful() && response.body() != null) {
                        ApiResponse apiResponse = response.body();

                        lastKnownPositions2 = apiResponse.getLastKnownPosition();
                        achievements2 = apiResponse.getOperationalOrderAchievements();
                        plannedOrders2 = new ArrayList<>();
                        orders2 = new ArrayList<>();
                        geocodes2 = new ArrayList<>();

                        // Obtener la lista de PlannedOrder de OperationalOrderAchievement y filtrar los elementos con stopId "Llegada"
                        for (OperationalOrderAchievement achievement : achievements2) {
                            PlannedOrder plannedOrder = achievement.getPlannedOrder();
                            if (plannedOrder != null && !"Llegada".equals(plannedOrder.getStopId())) {
                                plannedOrders2.add(plannedOrder);
                            }
                        }

                        // Obtener la lista de Orders de OperationalOrderAchievement
                        for (OperationalOrderAchievement achievement : achievements2) {
                            Order order = achievement.getOrder();
                            orders2.add(order);
                        }

                        for (OperationalOrderAchievement achievement : achievements2) {
                            Geocode geocode = achievement.getGeocode();
                            if (geocode != null) {
                                geocodes2.add(geocode);
                            }
                        }

                        Log.d(TAG, "Achievement 2: " + achievements2.toString());
                        Log.d(TAG, "PlannedOrder 2: " + plannedOrders2.toString());
                        Log.d(TAG, "Order 2: " + orders2.toString());

                        // Actualizar los datos en el intent
                        /*getIntent().putParcelableArrayListExtra("positioning", new ArrayList<>(lastKnownPositions2));
                        getIntent().putParcelableArrayListExtra("plannedOrders", new ArrayList<>(plannedOrders));
                        getIntent().putParcelableArrayListExtra("orders", new ArrayList<>(orders));
                        getIntent().putParcelableArrayListExtra("achievements", new ArrayList<>(achievementsList));
                        getIntent().putParcelableArrayListExtra("geocodes", new ArrayList<>(geocodes));
                        getIntent().putExtra("hora_exacta", hora_global);*/

                    } else {
                        Log.e(TAG, "Error en la respuesta de la API: " + response.errorBody());
                        Toast.makeText(AgendaActivity.this, "Error en la respuesta de la API", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    actualizarAlert.closeAlertDialog();
                    Log.e(TAG, "Error en la llamada a la API", t);
                    Toast.makeText(AgendaActivity.this, "Error en la llamada a la API", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}