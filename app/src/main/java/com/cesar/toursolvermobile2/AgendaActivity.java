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
import androidx.recyclerview.widget.RecyclerView;

import com.cesar.toursolvermobile2.adapter.AgendaAdapter;
import com.cesar.toursolvermobile2.databinding.ActivityAgendaBinding;
import com.cesar.toursolvermobile2.model.AgendaModel;
import com.cesar.toursolvermobile2.model.ApiResponse;
import com.cesar.toursolvermobile2.model.Geocode;
import com.cesar.toursolvermobile2.model.LastKnownPosition;
import com.cesar.toursolvermobile2.model.OperationalOrderAchievement;
import com.cesar.toursolvermobile2.model.Order;
import com.cesar.toursolvermobile2.model.PlannedOrder;
import com.google.android.material.navigation.NavigationView;

import java.text.ParseException;
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
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

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
    private RecyclerView recyclerView;
    private AgendaAdapter adapter;

    ImageButton arrowBack, arrowForward;
    TextView dateTextView;
    Calendar calendar;
    Calendar today;
    Calendar maxDate;

    public interface ApiService2 {
        @GET("fulfillment")
        Call<ApiResponse> getFulfillment(
                @Header("tsCloudApiKey") String apiKey,
                @Header("Accept") String accept,
                @Query("endDate") String endDate,
                @Query("startDate") String startDate,
                @Query("userLogin") String userLogin
        );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflar el binding y configurar la vista
        activityAgendaBinding = ActivityAgendaBinding.inflate(getLayoutInflater());
        setContentView(activityAgendaBinding.getRoot());

        List<OperationalOrderAchievement> achievementsAgenda = getIntent().getParcelableArrayListExtra("achievementsAgenda");
        List<PlannedOrder> plannedOrdersAgenda = getIntent().getParcelableArrayListExtra("plannedOrdersAgenda");
        List<Order> ordersAgenda = getIntent().getParcelableArrayListExtra("ordersAgenda");
        List<Geocode> geocodes = getIntent().getParcelableArrayListExtra("geocodes");

        Log.d(TAG,"achievements Agenda "+achievementsAgenda.toString());
        Log.d(TAG,"plannedOrders Agenda "+plannedOrdersAgenda.toString());
        Log.d(TAG,"orders Agenda "+ordersAgenda.toString());

        // Dentro del método onCreate, donde se procesan plannedOrdersAgenda y ordersAgenda
        List<AgendaModel> agendaModels = new ArrayList<>();

        // Iterar sobre plannedOrdersAgenda para crear objetos AgendaModel
        for (int i = 0; i < plannedOrdersAgenda.size(); i++) {
            PlannedOrder plannedOrder = plannedOrdersAgenda.get(i);
            Order order = ordersAgenda.get(i);

            // Crear un nuevo objeto AgendaModel
            AgendaModel agendaModel = new AgendaModel();
            agendaModel.setStopId(plannedOrder.getStopId());

            // Establecer el label según la condición de "Salida" o el Order
            if (plannedOrder.getStopId().equals("Salida")) {
                agendaModel.setLabel("Itinerario");
            } else {
                agendaModel.setLabel(order != null ? order.getLabel() : "Itinerario");
            }

            agendaModel.setStopStartTime(formatStopTime(plannedOrder.getStopStartTime()));

            // Establecer StopEndTime según la condición de "Salida" o el siguiente elemento
            if (plannedOrder.getStopId().equals("Salida")) {
                // Establecer StopEndTime como StopStartTime del siguiente elemento, si existe
                if (i + 1 < plannedOrdersAgenda.size()) {
                    PlannedOrder nextOrder = plannedOrdersAgenda.get(i + 1);
                    agendaModel.setStopEndTime(formatStopTime(nextOrder.getStopStartTime()));
                } else {
                    // Si no hay siguiente elemento, mantener StopEndTime igual a StopStartTime
                    agendaModel.setStopEndTime(formatStopTime(plannedOrder.getStopStartTime()));
                }
            } else {
                agendaModel.setStopEndTime(calculateStopEndTime(plannedOrder.getStopStartTime(), plannedOrder.getStopDuration()));
            }

            // Añadir el objeto AgendaModel a la lista
            agendaModels.add(agendaModel);

            // Si el stopId contiene "ORDER" o es "5", crear un nuevo objeto AgendaModel de itinerario
            if (plannedOrder.getStopId().contains("ORDER") || plannedOrder.getStopId().equals("5")) {
                // Crear un nuevo objeto AgendaModel de itinerario
                AgendaModel itinerarioModel = new AgendaModel();
                itinerarioModel.setStopId("itinerario");
                itinerarioModel.setLabel("Itinerario");

                // Establecer stopStartTime como stopEndTime del último AgendaModel
                if (!agendaModels.isEmpty()) {
                    AgendaModel lastModel = agendaModels.get(agendaModels.size() - 1);
                    itinerarioModel.setStopStartTime(lastModel.getStopEndTime());
                }

                // Establecer stopEndTime como stopStartTime del siguiente AgendaModel, si existe
                if (i + 1 < plannedOrdersAgenda.size()) {
                    PlannedOrder nextOrder = plannedOrdersAgenda.get(i + 1);
                    itinerarioModel.setStopEndTime(formatStopTime(nextOrder.getStopStartTime()));
                }

                // Añadir el objeto AgendaModel de itinerario a la lista
                agendaModels.add(itinerarioModel);
            }
        }


        Log.d(TAG,"AgendaModel: "+agendaModels.toString());

// Resto del código del onCreate...




        // Inicializar vistas
        arrowBack = findViewById(R.id.arrow_back);
        arrowForward = findViewById(R.id.arrow_forward);
        dateTextView = findViewById(R.id.dateTextView);

        // Configurar fecha actual en dateTextView
        calendar = Calendar.getInstance();
        today = Calendar.getInstance();
        maxDate = Calendar.getInstance();
        maxDate.add(Calendar.DAY_OF_YEAR, 4);
        updateDateInView(calendar);

        // Ocultar arrowBack inicialmente
        arrowBack.setVisibility(View.GONE);

        // Configurar el RecyclerView con el adaptador
        //adapter = new PlannedOrderAdapter(this, plannedOrders, orders,achievements); // Pass orders to adapter
        //recyclerView.setAdapter(adapter);

        // Configurar OnClickListener para arrowForward
        arrowForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Avanzar un día en el calendario
                calendar.add(Calendar.DAY_OF_YEAR, 1);

                // Actualizar la fecha en la vista
                updateDateInView(calendar);

                // Mostrar arrowBack después de avanzar un día, si no estamos en el día de hoy
                if (!isToday(calendar)) {
                    arrowBack.setVisibility(View.VISIBLE);
                }

                // Verificar si hemos alcanzado el límite de 4 días desde hoy
                if (calendar.equals(maxDate) || calendar.after(maxDate)) {
                    arrowForward.setVisibility(View.GONE);
                }
            }
        });

        // Configurar OnClickListener para arrowBack
        arrowBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retroceder un día en el calendario
                calendar.add(Calendar.DAY_OF_YEAR, -1);
                updateDateInView(calendar);

                // Ocultar arrowBack si volvemos al día actual
                if (isToday(calendar)) {
                    arrowBack.setVisibility(View.GONE);
                }

                // Habilitar arrowForward en cualquier caso si se ha deshabilitado
                if (!calendar.equals(maxDate)) {
                    arrowForward.setVisibility(View.VISIBLE);
                }
            }
        });

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

            // Listas para almacenar datos
            List<Date> dates = new ArrayList<>();
            List<String> startDates = new ArrayList<>();
            List<String> endDates = new ArrayList<>();

            // Calcular las fechas para los próximos 5 días
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            for (int i = 0; i < 5; i++) {
                dates.add(calendar.getTime());
                startDates.add(sdf.format(calendar.getTime()));
                // Avanzar al siguiente día
                calendar.add(Calendar.DAY_OF_YEAR, 1);
                endDates.add(sdf.format(calendar.getTime()));
            }

            // Obtener el correo del usuario (este valor debe ser obtenido desde el intent o algún otro lugar)
            String userLogin = getIntent().getStringExtra("user_email"); // Asumiendo que es el correo

            // Crear una instancia de Retrofit
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            // Crear una instancia del servicio de la API
            ApiService2 apiService = retrofit.create(ApiService2.class);

            // Realizar las llamadas a la API de manera asíncrona para los próximos 5 días
            for (int i = 0; i < 5; i++) {
                final int finalI = i;
                Call<ApiResponse> call = apiService.getFulfillment(
                        API_KEY,
                        ACCEPT,
                        endDates.get(finalI),
                        startDates.get(finalI),
                        userLogin
                );

                call.enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                        if (response.isSuccessful()) {
                            ApiResponse apiResponse = response.body();
                            if (apiResponse != null) {
                                // Manejar la respuesta de la API aquí
                                Log.d(TAG, "Data for day " + (finalI + 1) + ": " + apiResponse.toString());
                            }
                        } else {
                            // Manejar errores en la respuesta aquí
                            Log.e(TAG, "Error in API response: " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {
                        // Manejar fallo en la llamada a la API aquí
                        Log.e(TAG, "API call failed: " + t.getMessage());
                    }
                });
            }

            // Ocultar el diálogo de carga después de que todas las llamadas se hayan realizado
            actualizarAlert.closeAlertDialog();
        }
    }

    // Función para calcular stopEndTime sumando stopDuration a stopStartTime y formatear en HH:mm
    // Método para formatear el tiempo (ejemplo: quitar los segundos)
    private String formatStopTime(String stopTime) {
        // Verificar si stopTime tiene el formato completo "HH:mm:ss"
        if (stopTime.length() >= 8 && stopTime.charAt(2) == ':' && stopTime.charAt(5) == ':') {
            // Extraer HH:mm del tiempo completo "HH:mm:ss"
            return stopTime.substring(0, 5);
        } else {
            // Devolver el tiempo sin cambios si no tiene el formato esperado
            return stopTime;
        }
    }

    // Método para calcular el tiempo de finalización basado en el tiempo de inicio y la duración
    private String calculateStopEndTime(String stopStartTime, String stopDuration) {
        try {
            // Parsear stopStartTime a Date
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            Date startTime = sdf.parse(stopStartTime);

            // Parsear stopDuration a minutos (asumiendo formato "HH:mm")
            String[] parts = stopDuration.split(":");
            int durationHours = Integer.parseInt(parts[0]);
            int durationMinutes = Integer.parseInt(parts[1]);

            // Calcular stopEndTime sumando la duración a startTime
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(startTime);
            calendar.add(Calendar.HOUR_OF_DAY, durationHours);
            calendar.add(Calendar.MINUTE, durationMinutes);

            // Formatear la hora resultante como HH:mm
            return sdf.format(calendar.getTime());
        } catch (Exception e) {
            // Manejar cualquier excepción durante el cálculo y devolver el tiempo de inicio como fallback
            Log.e("AgendaActivity", "Error al calcular stopEndTime: " + e.getMessage());
            return stopStartTime;
        }
    }


    private void updateDateInView(Calendar calendar) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE d 'de' MMMM", new Locale("es", "ES"));
        dateTextView.setText(sdf.format(calendar.getTime()));
    }

    private boolean isToday(Calendar calendar) {
        return calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR);
    }
}