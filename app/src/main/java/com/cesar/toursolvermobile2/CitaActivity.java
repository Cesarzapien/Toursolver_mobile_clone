package com.cesar.toursolvermobile2;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.cesar.toursolvermobile2.model.ApiResponse;
import com.cesar.toursolvermobile2.model.Geocode;
import com.cesar.toursolvermobile2.model.LastKnownPosition;
import com.cesar.toursolvermobile2.model.OperationalOrderAchievement;
import com.cesar.toursolvermobile2.model.OperationalOrderUpdateRequest;
import com.cesar.toursolvermobile2.model.Order;
import com.cesar.toursolvermobile2.model.PlannedOrder;
import com.cesar.toursolvermobile2.model.ResponsePut;
import com.cesar.toursolvermobile2.model.updateOrdersOfOperationalPlanning;
import com.here.sdk.core.GeoCoordinates;
import com.here.sdk.mapview.MapView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public class CitaActivity extends AppCompatActivity implements mapHelper.PermissionResultCallback, PlatformPositioningProvider.PlatformLocationListener, AbandonDialog.AbandonDialogListener {

    private ImageButton Back_button,circularButton1,circularButton2;
    ActualizarAlert actualizarAlert = new ActualizarAlert(CitaActivity.this);
    public String hora_global;
    public String hora_global2,zipcode;
    private TextView nameLabel, zipCode, dateLabel, hourLabel, nameLabel2,actions,distance_label,time_label,description_label,salida_label,comentarios_label;
    private View line_one,line_two,line_three;
    private EditText salida_input,comentarios_input;
    private mapHelper mMapHelper;
    private MapView mapView;
    private boolean rutaGenerada,updateData;
    private GeoCoordinates coordenadasDestino;

    private PlatformPositioningProvider positioningProvider;
    private double achievementLat;
    private double achievementLon;
    private Button button1,button2,abandon_button,start_button,cancelButton,continueButton,informacion,acciones,informe;
    private LinearLayout button_layout,start_layout,cancel_layout,acciones_linear,acciones_linear_two,informe_lineal,informe_lineal_two;
    private RelativeLayout informacion_relative;

    public static final String BASE_URL = "https://api.geoconcept.com/tsapi/";
    public static final String API_KEY = "9e313fb763515473";
    public static final String ACCEPT = "application/json";
    private int validIndex;
    String userName,userEmail,hour;

    private List<LastKnownPosition> lastKnownPositions;
    private List<LastKnownPosition> lastKnownPositions2;
    private List<PlannedOrder> plannedOrders;
    private List<PlannedOrder> plannedOrdersAgenda;
    private List<PlannedOrder> plannedOrders2;
    private List<Order> orders;
    private List<Order> ordersAgenda;
    private List<Order> orders2;
    private List<OperationalOrderAchievement> achievements;
    private List<OperationalOrderAchievement> achievementsAgenda;
    private List<OperationalOrderAchievement> achievements2;
    private List<Geocode> geocodes;
    private List<Geocode> geocodes2;

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

    public interface ApiService {
        @Headers({
                "tsCloudApiKey: " + API_KEY,
                "Accept: " + ACCEPT
        })
        @PUT("updateOperationalOrder")
        Call<ResponsePut> updateOperationalOrder(@Body OperationalOrderUpdateRequest request);
    }

    public interface ApiService3 {
        @Headers({
                "tsCloudApiKey: " + API_KEY,
                "Accept: " + ACCEPT
        })
        @PUT("updateOrdersOfOperationalPlanning")
        Call<ResponsePut> updateOrdersOfOperationalPlanning(@Body updateOrdersOfOperationalPlanning request);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMapHelper = new mapHelper();
        mMapHelper.initializeHERESDK(this);

        setContentView(R.layout.activity_cita);

        // Get a MapView instance from the layout.
        mapView = findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);

        mMapHelper.permisoInternet(this, this);
        mMapHelper.permisoLocalizacion(this, this);

        mMapHelper.tiltMap(mapView);


        if (!mMapHelper.isGPSEnabled(CitaActivity.this)) {
            mMapHelper.showGPSDisabledDialog(CitaActivity.this);
        } else {
        }


        // Initialize positioning provider
        positioningProvider = new PlatformPositioningProvider(this);

        acciones_linear = findViewById(R.id.acciones_linear);
        acciones_linear_two = findViewById(R.id.acciones_linear_two);
        informacion_relative = findViewById(R.id.informacion_relative);
        distance_label = findViewById(R.id.distanceLabel);
        salida_label = findViewById(R.id.salidalabel);
        salida_input = findViewById(R.id.salidaInput);
        comentarios_label = findViewById(R.id.comentariosLabel);
        comentarios_input = findViewById(R.id.comentariosInput);
        time_label = findViewById(R.id.timeLabel);
        line_one = findViewById(R.id.line_one);
        line_two = findViewById(R.id.line_two);
        line_three = findViewById(R.id.line_three);
        informacion = findViewById(R.id.button1_2);
        acciones = findViewById(R.id.button2_2);
        informe = findViewById(R.id.button3_2);
        cancelButton = findViewById(R.id.cancelbuttonn);
        continueButton = findViewById(R.id.continuebutton);
        circularButton1 = findViewById(R.id.circularButton1);
        circularButton2 = findViewById(R.id.circularButton2);
        description_label = findViewById(R.id.descriptionLabel);
        actions = findViewById(R.id.actionss);
        cancel_layout = findViewById(R.id.newButtonLayout);
        informe_lineal = findViewById(R.id.informe_linear);
        informe_lineal_two = findViewById(R.id.informe_linear_two);
        button_layout = findViewById(R.id.buttonLayout);
        abandon_button = findViewById(R.id.abandonButton);
        start_button = findViewById(R.id.startButton);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        nameLabel = findViewById(R.id.nameLabel);
        zipCode = findViewById(R.id.idLabel);
        dateLabel = findViewById(R.id.dateLabel);
        hourLabel = findViewById(R.id.timeSlotLabel);
        nameLabel2 = findViewById(R.id.descriptionText);

        // Obtener los datos actuales del usuario
        Intent currentIntent = getIntent();
        validIndex = currentIntent.getIntExtra("position", -1);
        userName = currentIntent.getStringExtra("user_name");
        userEmail = currentIntent.getStringExtra("user_email");
        // Obtener los datos de PlannedOrder y Order del intent
        lastKnownPositions = getIntent().getParcelableArrayListExtra("positioning");
        plannedOrders = getIntent().getParcelableArrayListExtra("plannedOrders");
        plannedOrdersAgenda = getIntent().getParcelableArrayListExtra("plannedOrdersAgenda");
        orders = getIntent().getParcelableArrayListExtra("orders");
        ordersAgenda = getIntent().getParcelableArrayListExtra("ordersAgenda");
        achievements = getIntent().getParcelableArrayListExtra("achievements");
        achievementsAgenda = getIntent().getParcelableArrayListExtra("achievementsAgenda");
        geocodes = getIntent().getParcelableArrayListExtra("geocodes");
        hour = currentIntent.getStringExtra("hora_exacta");
        Back_button = findViewById(R.id.backButton);

        Log.d(TAG,"Positioning Cita "+lastKnownPositions.toString());
        updateData = false; // Variable para indicar si se debe actualizar los datos al presionar el botón back_button

        if (achievements != null && orders != null && validIndex >= 0) {
            Order order = orders.get(validIndex); // Posición válida
            OperationalOrderAchievement operationalOrderAchievement = achievements.get(validIndex);
            Geocode geocode = geocodes.get(validIndex);
            Log.d(TAG, "operational " + operationalOrderAchievement.toString());
            Log.d(TAG, "orders " + order.toString());
            Log.d(TAG,"Index "+validIndex);

            if ("STARTED".equals(operationalOrderAchievement.getStatus())){
                cancel_layout.setWeightSum(3);
                button1.setVisibility(View.GONE);
                button2.setVisibility(View.GONE);
                informacion.setVisibility(View.VISIBLE);
                acciones.setVisibility(View.VISIBLE);
                informe.setVisibility(View.VISIBLE);
                abandon_button.setVisibility(View.GONE);
                start_button.setVisibility(View.GONE);
                continueButton.setVisibility(View.VISIBLE);
                cancelButton.setVisibility(View.VISIBLE);
                mapView.setVisibility(View.GONE);
                informacion_relative.setVisibility(View.GONE);
                zipCode.setVisibility(View.GONE);
                distance_label.setVisibility(View.GONE);
                time_label.setVisibility(View.GONE);
                line_one.setVisibility(View.GONE);
                dateLabel.setVisibility(View.GONE);
                hourLabel.setVisibility(View.GONE);
                line_two.setVisibility(View.GONE);
                description_label.setVisibility(View.GONE);
                nameLabel2.setVisibility(View.GONE);
                line_three.setVisibility(View.GONE);
                actions.setText("Acciones a realizar");
                salida_label.setVisibility(View.VISIBLE);
                salida_input.setVisibility(View.VISIBLE);
                comentarios_label.setVisibility(View.VISIBLE);
                comentarios_input.setVisibility(View.VISIBLE);
                acciones_linear_two.setVisibility(View.VISIBLE);
                acciones_linear.setVisibility(View.VISIBLE);


            }else{
                cancel_layout.setWeightSum(2);
                button1.setVisibility(View.VISIBLE);
                button2.setVisibility(View.VISIBLE);
                informacion.setVisibility(View.GONE);
                acciones.setVisibility(View.GONE);
                informe.setVisibility(View.GONE);
                abandon_button.setVisibility(View.VISIBLE);
                start_button.setVisibility(View.VISIBLE);
                continueButton.setVisibility(View.GONE);
                cancelButton.setVisibility(View.GONE);
            }

            // Obtener el nombre de la cita
            String nombrecita = order.getLabel();
            zipcode = geocode.getPostcode();
            Log.d(TAG,"ZIP CODE "+zipcode);

            nameLabel.setText(nombrecita);
            zipCode.setText(zipcode);
            nameLabel2.setText(nombrecita);

            long fecha_inicio = operationalOrderAchievement.getStart();
            long fecha_termino = operationalOrderAchievement.getEnd();

            Log.d(TAG, "start " + fecha_inicio);
            Log.d(TAG, "end " + fecha_termino);

            // Agregar una hora a fecha_inicio y fecha_termino (3600000 milisegundos = 1 hora)
            fecha_inicio += 3600000;
            fecha_termino += 3600000;

            // Convertir fecha y hora de Epoch a formato legible
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE d MMMM yyyy", new Locale("es", "MX"));
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", new Locale("es", "MX"));
            TimeZone timeZone = TimeZone.getTimeZone("America/Mexico_City"); // Cambia la zona horaria si es necesario
            dateFormat.setTimeZone(timeZone);
            timeFormat.setTimeZone(timeZone);

            Date startDate = new Date(fecha_inicio);
            Date endDate = new Date(fecha_termino);

            String formattedDate = dateFormat.format(startDate);
            String formattedStartTime = timeFormat.format(startDate);
            String formattedEndTime = timeFormat.format(endDate);

            dateLabel.setText(formattedDate);
            hourLabel.setText(formattedStartTime + " - " + formattedEndTime);

            // Obtener latitud y longitud de achievements
            achievementLat = operationalOrderAchievement.getLat();
            achievementLon = operationalOrderAchievement.getLon();

            coordenadasDestino = new GeoCoordinates(achievementLat,achievementLon);

        }

        mMapHelper.loadMapScene(mapView);

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapView.setVisibility(View.GONE);
                informacion_relative.setVisibility(View.GONE);
                zipCode.setVisibility(View.GONE);
                distance_label.setVisibility(View.GONE);
                time_label.setVisibility(View.GONE);
                line_one.setVisibility(View.GONE);
                dateLabel.setVisibility(View.GONE);
                hourLabel.setVisibility(View.GONE);
                line_two.setVisibility(View.GONE);
                description_label.setVisibility(View.GONE);
                nameLabel2.setVisibility(View.GONE);
                line_three.setVisibility(View.GONE);
                actions.setText("Acciones a realizar");
                salida_label.setVisibility(View.GONE);
                salida_input.setVisibility(View.GONE);
                comentarios_label.setVisibility(View.GONE);
                comentarios_input.setVisibility(View.GONE);
                acciones_linear_two.setVisibility(View.VISIBLE);
                acciones_linear.setVisibility(View.VISIBLE);
            }
        });
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actions.setText("Información");
                acciones_linear_two.setVisibility(View.GONE);
                acciones_linear.setVisibility(View.GONE);
                mapView.setVisibility(View.VISIBLE);
                informacion_relative.setVisibility(View.VISIBLE);
                zipCode.setVisibility(View.VISIBLE);
                distance_label.setVisibility(View.VISIBLE);
                time_label.setVisibility(View.VISIBLE);
                line_one.setVisibility(View.VISIBLE);
                dateLabel.setVisibility(View.VISIBLE);
                hourLabel.setVisibility(View.VISIBLE);
                line_two.setVisibility(View.VISIBLE);
                description_label.setVisibility(View.VISIBLE);
                nameLabel2.setVisibility(View.VISIBLE);
                line_three.setVisibility(View.VISIBLE);
            }
        });

        informacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actions.setText("Información");
                informe_lineal.setVisibility(View.GONE);
                informe_lineal_two.setVisibility(View.GONE);
                acciones_linear_two.setVisibility(View.GONE);
                acciones_linear.setVisibility(View.GONE);
                mapView.setVisibility(View.VISIBLE);
                informacion_relative.setVisibility(View.VISIBLE);
                zipCode.setVisibility(View.VISIBLE);
                distance_label.setVisibility(View.VISIBLE);
                time_label.setVisibility(View.VISIBLE);
                line_one.setVisibility(View.VISIBLE);
                dateLabel.setVisibility(View.VISIBLE);
                hourLabel.setVisibility(View.VISIBLE);
                line_two.setVisibility(View.VISIBLE);
                description_label.setVisibility(View.VISIBLE);
                nameLabel2.setVisibility(View.VISIBLE);
                line_three.setVisibility(View.VISIBLE);
            }
        });

        acciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                informe_lineal.setVisibility(View.GONE);
                informe_lineal_two.setVisibility(View.GONE);
                mapView.setVisibility(View.GONE);
                informacion_relative.setVisibility(View.GONE);
                zipCode.setVisibility(View.GONE);
                distance_label.setVisibility(View.GONE);
                time_label.setVisibility(View.GONE);
                line_one.setVisibility(View.GONE);
                dateLabel.setVisibility(View.GONE);
                hourLabel.setVisibility(View.GONE);
                line_two.setVisibility(View.GONE);
                description_label.setVisibility(View.GONE);
                nameLabel2.setVisibility(View.GONE);
                line_three.setVisibility(View.GONE);
                actions.setText("Acciones a realizar");
                salida_label.setVisibility(View.VISIBLE);
                salida_input.setVisibility(View.VISIBLE);
                comentarios_label.setVisibility(View.VISIBLE);
                comentarios_input.setVisibility(View.VISIBLE);
                acciones_linear_two.setVisibility(View.VISIBLE);
                acciones_linear.setVisibility(View.VISIBLE);
            }
        });

        informe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actions.setText("Informe del cliente");
                salida_label.setVisibility(View.GONE);
                salida_input.setVisibility(View.GONE);
                comentarios_label.setVisibility(View.GONE);
                comentarios_input.setVisibility(View.GONE);
                acciones_linear_two.setVisibility(View.GONE);
                acciones_linear.setVisibility(View.GONE);
                informe_lineal.setVisibility(View.VISIBLE);
                informe_lineal_two.setVisibility(View.VISIBLE);

            }
        });


        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Prepare the data to be sent in the PUT request
                long achievedEndDate = 0;
                double achievedEndPositionLat = 0.0;
                double achievedEndPositionLon = 0.0;
                long achievedStartDate = 0;
                double achievedStartPositionLat = 0.0;
                double achievedStartPositionLon = 0.0;
                String operationalOrderId = "";
                String status = "ACCEPTED";
                String userLogin = "";

                if (achievements != null && orders != null && validIndex >= 0) {
                    OperationalOrderAchievement operationalOrderAchievement = achievements.get(validIndex);

                    achievedEndDate = operationalOrderAchievement.getEnd();
                    achievedEndPositionLat = lastKnownPositions.get(0).getLat();
                    achievedEndPositionLon = lastKnownPositions.get(0).getLon();
                    achievedStartPositionLat = lastKnownPositions.get(0).getLat();
                    achievedStartPositionLon = lastKnownPositions.get(0).getLon();
                    operationalOrderId = operationalOrderAchievement.getId();
                    achievedStartDate = operationalOrderAchievement.getStart();
                    userLogin = userEmail;

                    Log.d(TAG,"achievedEndDate "+achievedEndDate);
                    Log.d(TAG,"achievedEndPositionLat "+achievedEndPositionLat);
                    Log.d(TAG,"achievedEndPositionLon "+achievedEndPositionLon);
                    Log.d(TAG,"achievedStartPositionLat "+achievedStartPositionLat);
                    Log.d(TAG,"achievedStartPositionLon "+achievedStartPositionLon);
                    Log.d(TAG,"operationalOrderId "+operationalOrderId);
                    Log.d(TAG,"achievedStartDate "+achievedStartDate);
                    Log.d(TAG,"userLogin "+userLogin);


                }

                OperationalOrderUpdateRequest request = new OperationalOrderUpdateRequest(
                        achievedEndDate,
                        achievedEndPositionLat,
                        achievedEndPositionLon,
                        achievedStartDate,
                        achievedStartPositionLat,
                        achievedStartPositionLon,
                        operationalOrderId,
                        status,
                        userLogin
                );

                // Create Retrofit instance
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                // Create API service
                ApiService apiService = retrofit.create(ApiService.class);

                // Make the PUT request
                Call<ResponsePut> call = apiService.updateOperationalOrder(request);

                call.enqueue(new Callback<ResponsePut>() {
                    @Override
                    public void onResponse(Call<ResponsePut> call, Response<ResponsePut> response) {
                        if (response.isSuccessful()) {
                            Log.d(TAG, "PUT request successful"+response.body().toString());
                            cancel_layout.setWeightSum(2);
                            informe_lineal.setVisibility(View.GONE);
                            informe_lineal_two.setVisibility(View.GONE);
                            button1.setVisibility(View.VISIBLE);
                            button2.setVisibility(View.VISIBLE);
                            informacion.setVisibility(View.GONE);
                            acciones.setVisibility(View.GONE);
                            informe.setVisibility(View.GONE);
                            abandon_button.setVisibility(View.VISIBLE);
                            start_button.setVisibility(View.VISIBLE);
                            continueButton.setVisibility(View.GONE);
                            cancelButton.setVisibility(View.GONE);
                            actions.setText("Información");
                            acciones_linear_two.setVisibility(View.GONE);
                            acciones_linear.setVisibility(View.GONE);
                            mapView.setVisibility(View.VISIBLE);
                            informacion_relative.setVisibility(View.VISIBLE);
                            zipCode.setVisibility(View.VISIBLE);
                            distance_label.setVisibility(View.VISIBLE);
                            time_label.setVisibility(View.VISIBLE);
                            line_one.setVisibility(View.VISIBLE);
                            dateLabel.setVisibility(View.VISIBLE);
                            hourLabel.setVisibility(View.VISIBLE);
                            line_two.setVisibility(View.VISIBLE);
                            description_label.setVisibility(View.VISIBLE);
                            nameLabel2.setVisibility(View.VISIBLE);
                            line_three.setVisibility(View.VISIBLE);


                            // Call refreshData after a successful response
                            updateData = true; // Indicar que se ha realizado un cambio
                            refreshData(updateData); // Indicar que se ha realizado un cambio
                        } else {
                            Log.e(TAG, "PUT request failed: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponsePut> call, Throwable throwable) {
                        Log.e(TAG, "PUT request error: ", throwable);
                    }
                });
            }
        });

        start_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Prepare the data to be sent in the PUT request
                long achievedEndDate = 0;
                double achievedEndPositionLat = 0.0;
                double achievedEndPositionLon = 0.0;
                long achievedStartDate = 0;
                double achievedStartPositionLat = 0.0;
                double achievedStartPositionLon = 0.0;
                String operationalOrderId = "";
                String status = "STARTED";
                String userLogin = "";

                if (achievements != null && orders != null && validIndex >= 0) {
                    OperationalOrderAchievement operationalOrderAchievement = achievements.get(validIndex);

                    achievedEndDate = operationalOrderAchievement.getEnd();
                    achievedEndPositionLat = lastKnownPositions.get(0).getLat();
                    achievedEndPositionLon = lastKnownPositions.get(0).getLon();
                    achievedStartPositionLat = lastKnownPositions.get(0).getLat();
                    achievedStartPositionLon = lastKnownPositions.get(0).getLon();
                    operationalOrderId = operationalOrderAchievement.getId();
                    achievedStartDate = operationalOrderAchievement.getStart();
                    userLogin = userEmail;
                }

                OperationalOrderUpdateRequest request = new OperationalOrderUpdateRequest(
                        achievedEndDate,
                        achievedEndPositionLat,
                        achievedEndPositionLon,
                        achievedStartDate,
                        achievedStartPositionLat,
                        achievedStartPositionLon,
                        operationalOrderId,
                        status,
                        userLogin
                );

                // Create Retrofit instance
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                // Create API service
                ApiService apiService = retrofit.create(ApiService.class);

                // Make the PUT request
                Call<ResponsePut> call = apiService.updateOperationalOrder(request);

                call.enqueue(new Callback<ResponsePut>() {
                    @Override
                    public void onResponse(Call<ResponsePut> call, Response<ResponsePut> response) {
                        if (response.isSuccessful()) {
                            Log.d(TAG, "PUT request successful"+response.body().toString());
                            cancel_layout.setWeightSum(3);
                            button1.setVisibility(View.GONE);
                            button2.setVisibility(View.GONE);
                            informacion.setVisibility(View.VISIBLE);
                            acciones.setVisibility(View.VISIBLE);
                            informe.setVisibility(View.VISIBLE);
                            abandon_button.setVisibility(View.GONE);
                            start_button.setVisibility(View.GONE);
                            continueButton.setVisibility(View.VISIBLE);
                            cancelButton.setVisibility(View.VISIBLE);
                            mapView.setVisibility(View.GONE);
                            informacion_relative.setVisibility(View.GONE);
                            zipCode.setVisibility(View.GONE);
                            distance_label.setVisibility(View.GONE);
                            time_label.setVisibility(View.GONE);
                            line_one.setVisibility(View.GONE);
                            dateLabel.setVisibility(View.GONE);
                            hourLabel.setVisibility(View.GONE);
                            line_two.setVisibility(View.GONE);
                            description_label.setVisibility(View.GONE);
                            nameLabel2.setVisibility(View.GONE);
                            line_three.setVisibility(View.GONE);
                            actions.setText("Acciones a realizar");
                            salida_label.setVisibility(View.VISIBLE);
                            salida_input.setVisibility(View.VISIBLE);
                            comentarios_label.setVisibility(View.VISIBLE);
                            comentarios_input.setVisibility(View.VISIBLE);
                            acciones_linear_two.setVisibility(View.VISIBLE);
                            acciones_linear.setVisibility(View.VISIBLE);


                            // Call refreshData after a successful response
                            updateData = true; // Indicar que se ha realizado un cambio
                            refreshData(updateData); // Indicar que se ha realizado un cambio
                        } else {
                            Log.e(TAG, "PUT request failed: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponsePut> call, Throwable throwable) {
                        Log.e(TAG, "PUT request error: ", throwable);
                    }
                });




            }
        });

        circularButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri gmmIntentUri = Uri.parse("geo:37.7749,-122.4192?q=" + Uri.encode("0"+zipcode));
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });

        circularButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ubicación de origen (ejemplo: Taronga Zoo, Sydney, Australia)
                Uri originUri = Uri.parse("google.navigation:q=Taronga+Zoo,+Sydney+Australia");

                // Ubicación de destino (debes definir la ubicación de destino aquí)
                Uri destinationUri = Uri.parse("google.navigation:q=Destination+Address");

                // Crear un intent ACTION_VIEW con ambas ubicaciones
                Intent mapIntent = new Intent(Intent.ACTION_VIEW);
                mapIntent.setData(Uri.parse("https://www.google.com/maps/dir/?api=1&origin=" +
                        originUri.toString() +
                        "&destination=" +
                        destinationUri.toString()));

                // Asegurarse de que se abra en aplicaciones de mapas
                mapIntent.setPackage("com.google.android.apps.maps");

                // Verificar si hay aplicaciones disponibles para manejar el intent
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                } else {
                    Toast.makeText(getApplicationContext(), "No se encontró ninguna aplicación de mapas", Toast.LENGTH_SHORT).show();
                }
            }
        });

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mapView.getVisibility() == mapView.VISIBLE){
                    informe_lineal.setVisibility(View.GONE);
                    informe_lineal_two.setVisibility(View.GONE);
                    mapView.setVisibility(View.GONE);
                    informacion_relative.setVisibility(View.GONE);
                    zipCode.setVisibility(View.GONE);
                    distance_label.setVisibility(View.GONE);
                    time_label.setVisibility(View.GONE);
                    line_one.setVisibility(View.GONE);
                    dateLabel.setVisibility(View.GONE);
                    hourLabel.setVisibility(View.GONE);
                    line_two.setVisibility(View.GONE);
                    description_label.setVisibility(View.GONE);
                    nameLabel2.setVisibility(View.GONE);
                    line_three.setVisibility(View.GONE);
                    actions.setText("Acciones a realizar");
                    salida_label.setVisibility(View.VISIBLE);
                    salida_input.setVisibility(View.VISIBLE);
                    comentarios_label.setVisibility(View.VISIBLE);
                    comentarios_input.setVisibility(View.VISIBLE);
                    acciones_linear_two.setVisibility(View.VISIBLE);
                    acciones_linear.setVisibility(View.VISIBLE);
                } else if (acciones_linear.getVisibility() == acciones_linear.VISIBLE) {
                    actions.setText("Informe del cliente");
                    salida_label.setVisibility(View.GONE);
                    salida_input.setVisibility(View.GONE);
                    comentarios_label.setVisibility(View.GONE);
                    comentarios_input.setVisibility(View.GONE);
                    acciones_linear_two.setVisibility(View.GONE);
                    acciones_linear.setVisibility(View.GONE);
                    informe_lineal.setVisibility(View.VISIBLE);
                    informe_lineal_two.setVisibility(View.VISIBLE);
                }
            }
        });



        abandon_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AbandonDialog abandonDialog = new AbandonDialog();
                abandonDialog.setAbandonDialogListener(CitaActivity.this);
                abandonDialog.show(getSupportFragmentManager(), "AbandonDialog");
            }
        });

        Back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (updateData) {
                    Intent intent = new Intent(CitaActivity.this, InicioActivity.class);
                    //intent.putExtra("position", validIndex);
                    intent.putExtra("user_name", userName);
                    intent.putExtra("user_email", userEmail);
                    intent.putParcelableArrayListExtra("positioning",new ArrayList<>(lastKnownPositions2));
                    intent.putParcelableArrayListExtra("plannedOrders", new ArrayList<>(plannedOrders2));
                    intent.putParcelableArrayListExtra("orders", new ArrayList<>(orders2));
                    intent.putParcelableArrayListExtra("geocodes", new ArrayList<>(geocodes2));
                    intent.putParcelableArrayListExtra("achievements", new ArrayList<>(achievements2));
                    intent.putParcelableArrayListExtra("achievementsAgenda",new ArrayList<>(achievementsAgenda));
                    intent.putParcelableArrayListExtra("plannedOrdersAgenda",new ArrayList<>(plannedOrdersAgenda));
                    intent.putParcelableArrayListExtra("ordersAgenda",new ArrayList<>(ordersAgenda));
                    intent.putExtra("hora_exacta", hora_global2);
                    startActivity(intent);
                    finish();
                }else{
                    Intent intent = new Intent(CitaActivity.this, InicioActivity.class);
                    //intent.putExtra("position", validIndex);
                    intent.putExtra("user_name", userName);
                    intent.putExtra("user_email", userEmail);
                    intent.putParcelableArrayListExtra("positioning",new ArrayList<>(lastKnownPositions));
                    intent.putParcelableArrayListExtra("plannedOrders", new ArrayList<>(plannedOrders));
                    intent.putParcelableArrayListExtra("orders", new ArrayList<>(orders));
                    intent.putParcelableArrayListExtra("geocodes", new ArrayList<>(geocodes));
                    intent.putParcelableArrayListExtra("achievements", new ArrayList<>(achievements));
                    intent.putParcelableArrayListExtra("achievementsAgenda",new ArrayList<>(achievementsAgenda));
                    intent.putParcelableArrayListExtra("plannedOrdersAgenda",new ArrayList<>(plannedOrdersAgenda));
                    intent.putParcelableArrayListExtra("ordersAgenda",new ArrayList<>(ordersAgenda));
                    intent.putExtra("hora_exacta", hour);
                    startActivity(intent);
                    finish();
                }
            }
        });



        /*button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!rutaGenerada) {
                    mMapHelper.generarRuta(CitaActivity.this, mapView, coordenadasDestino);
                    rutaGenerada =true;
                }
            }
        });*/
    }


    @Override
    public void onAbandonConfirmed(String reason) {
        // Prepare the data to be sent in the PUT request
        long achievedEndDate = 0;
        double achievedEndPositionLat = 0.0;
        double achievedEndPositionLon = 0.0;
        long achievedStartDate = 0;
        double achievedStartPositionLat = 0.0;
        double achievedStartPositionLon = 0.0;
        String operationalOrderId = "";
        String status = "CANCELLED";
        String userLogin = "";
        String reasons = reason;

        if (achievements != null && orders != null && validIndex >= 0) {
            OperationalOrderAchievement operationalOrderAchievement = achievements.get(validIndex);

            achievedEndDate = operationalOrderAchievement.getEnd();
            achievedEndPositionLat = lastKnownPositions.get(0).getLat();
            achievedEndPositionLon = lastKnownPositions.get(0).getLon();
            achievedStartPositionLat = lastKnownPositions.get(0).getLat();
            achievedStartPositionLon = lastKnownPositions.get(0).getLon();
            operationalOrderId = operationalOrderAchievement.getId();
            achievedStartDate = operationalOrderAchievement.getStart();
            userLogin = userEmail;
        }

        OperationalOrderUpdateRequest request = new OperationalOrderUpdateRequest(
                achievedEndDate,
                achievedEndPositionLat,
                achievedEndPositionLon,
                achievedStartDate,
                achievedStartPositionLat,
                achievedStartPositionLon,
                operationalOrderId,
                status,
                userLogin
        );

        // Create Retrofit instance
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Create API service
        ApiService apiService = retrofit.create(ApiService.class);

        // Make the PUT request
        Call<ResponsePut> call = apiService.updateOperationalOrder(request);

        call.enqueue(new Callback<ResponsePut>() {
            @Override
            public void onResponse(Call<ResponsePut> call, Response<ResponsePut> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "PUT request successful"+response.body().toString());
                    // Call refreshData after a successful response
                    updateData = true; // Indicar que se ha realizado un cambio
                    refreshData(updateData); // Indicar que se ha realizado un cambio
                    Toast.makeText(CitaActivity.this,"Nuevo estado de la cita: Abandonada",Toast.LENGTH_SHORT).show();
                    button_layout.setVisibility(View.GONE);
                } else {
                    Log.e(TAG, "PUT request failed: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponsePut> call, Throwable throwable) {
                Log.e(TAG, "PUT request error: ", throwable);
            }
        });

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
            hora_global2 = sdf2.format(currentDate);
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
            ApiService2 apiService = retrofit.create(ApiService2.class);

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
                        Toast.makeText(CitaActivity.this, "Error en la respuesta de la API", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    actualizarAlert.closeAlertDialog();
                    Log.e(TAG, "Error en la llamada a la API", t);
                    Toast.makeText(CitaActivity.this, "Error en la llamada a la API", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void permisoConcedido() {
    }

    @Override
    public void permisoDenegado() {
    }

    private void startLocationUpdates() {
        positioningProvider.startLocating(this);
    }

    private void stopLocationUpdates() {
        positioningProvider.stopLocating();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mMapHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    protected void onResume() {
        mapView.onResume();
        super.onResume();
        startLocationUpdates();
    }

    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
        mMapHelper.disposeHERESDK();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        mapView.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onLocationUpdated(Location location) {
        GeoCoordinates userCoordinates = new GeoCoordinates(location.getLatitude(), location.getLongitude());

    }
}