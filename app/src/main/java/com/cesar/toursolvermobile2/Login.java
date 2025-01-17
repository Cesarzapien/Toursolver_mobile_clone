package com.cesar.toursolvermobile2;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.cesar.toursolvermobile2.DB.DBHelper;
import com.cesar.toursolvermobile2.DB.User;
import com.cesar.toursolvermobile2.model.ApiResponse;
import com.cesar.toursolvermobile2.model.Geocode;
import com.cesar.toursolvermobile2.model.LastKnownPosition;
import com.cesar.toursolvermobile2.model.OperationalOrderAchievement;
import com.cesar.toursolvermobile2.model.Order;
import com.cesar.toursolvermobile2.model.PlannedOrder;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputLayout;

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

public class Login extends AppCompatActivity {

    public static final String BASE_URL = "https://api.geoconcept.com/tsapi/";
    public static final String API_KEY = "9e313fb763515473";
    public static final String ACCEPT = "application/json";
    private String fullName;

    TextInputLayout correo, contrasenia;
    CheckBox rememberMeCheckBox;

    public interface ApiService {
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
        setContentView(R.layout.activity_login);

        correo = findViewById(R.id.etCorreo);
        contrasenia = findViewById(R.id.etPassword);
        rememberMeCheckBox = findViewById(R.id.checkBox2);

        // Obtener la fecha y hora actual
        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();

        // Calcular la fecha para el día siguiente
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date tomorrowDate = calendar.getTime();

        // Formatear las fechas en el formato necesario para la llamada a la API
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'00:00:00", Locale.getDefault());
        SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm",Locale.getDefault());
        String hour = sdf2.format(currentDate);
        String startDate = sdf.format(currentDate);
        String endDate = sdf.format(tomorrowDate);

        // Cargar credenciales guardadas si existen
        loadCredentials();

        // Obtener referencia al botón de inicio de sesión
        Button button = findViewById(R.id.button);

        // Configurar el OnClickListener para el botón
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLogin(startDate, endDate,hour); // Pasar las fechas como argumentos
            }
        });
    }

    private void handleLogin(String startDate, String endDate, String hour) {
        // Obtener el correo y la contraseña del TextInputLayout
        String userLogin = correo.getEditText().getText().toString();
        String password = contrasenia.getEditText().getText().toString();

        // Validar si los campos están vacíos
        if (userLogin.isEmpty()) {
            correo.setError("Por favor, ingrese el correo");
            return;
        } else {
            correo.setError(null); // Limpiar el error
        }

        if (password.isEmpty()) {
            contrasenia.setError("Por favor, ingrese la contraseña");
            return;
        } else {
            contrasenia.setError(null); // Limpiar el error
        }

        // Mostrar el diálogo de carga
        LoadingAlert loadingAlert = new LoadingAlert(Login.this);
        loadingAlert.startAlertDialog();

        // Guardar las credenciales si el checkbox está activado
        if (rememberMeCheckBox.isChecked()) {
            saveCredentials(userLogin, password);
        } else {
            clearCredentials();
        }

        callApi(userLogin,loadingAlert,startDate,endDate,hour);
    }

    private void callApi(String userLogin, LoadingAlert loadingAlert, String startDate, String endDate,String hour) {
        // Crear una instancia de Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Crear una instancia del servicio de la API
        ApiService apiService = retrofit.create(ApiService.class);

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
                loadingAlert.closeAlertDialog();
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    if("ERROR".equals(apiResponse.getStatus())){
                        showErrorAndClearFields();
                    } else{
                        // Crear una instancia de DBHelper
                        DBHelper dbHelper = new DBHelper(Login.this);
                        // Obtener los detalles del usuario
                        User user = dbHelper.getUser(userLogin);
                        if(user != null){
                            fullName = user.getFirstName() + " " + user.getLastName();
                        }

                        List<LastKnownPosition> lastKnownPositions = apiResponse.getLastKnownPosition();
                        List<OperationalOrderAchievement> achievementsList = new ArrayList<>();
                        List<OperationalOrderAchievement> achievementsListAgenda = apiResponse.getOperationalOrderAchievements();
                        List<PlannedOrder> plannedOrders = new ArrayList<>();
                        List<PlannedOrder> plannedOrdersAgenda = new ArrayList<>();
                        List<Order> orders = new ArrayList<>();
                        List<Order> ordersAgenda = new ArrayList<>();
                        List<Geocode> geocodes = new ArrayList<>();

                        Log.d(TAG,"Last Known"+lastKnownPositions.toString());
                        // Filtrar la lista de OperationalOrderAchievement y agregar los logros a achievementsList si el stopId no es "Descanso" o "5"
                        for (OperationalOrderAchievement achievement : apiResponse.getOperationalOrderAchievements()) {
                            PlannedOrder plannedOrder = achievement.getPlannedOrder();
                            if (plannedOrder != null && !"Descanso".equals(plannedOrder.getStopId()) && !"5".equals(plannedOrder.getStopId())) {
                                achievementsList.add(achievement);
                            }
                        }

                        for (OperationalOrderAchievement achievementAgenda : achievementsListAgenda){
                            PlannedOrder plannedOrderAgenda = achievementAgenda.getPlannedOrder();
                            if (plannedOrderAgenda != null && !"Llegada".equals(plannedOrderAgenda.getStopId())){
                                plannedOrdersAgenda.add(plannedOrderAgenda);
                            }
                        }

                        for (OperationalOrderAchievement achievementAgenda : achievementsListAgenda) {
                            Order order = achievementAgenda.getOrder();
                            ordersAgenda.add(order);
                        }

                        // Obtener la lista de PlannedOrder de OperationalOrderAchievement y filtrar los elementos con stopId "Llegada"
                        for (OperationalOrderAchievement achievement : achievementsList) {
                            PlannedOrder plannedOrder = achievement.getPlannedOrder();
                            if (plannedOrder != null && !"Llegada".equals(plannedOrder.getStopId()) && !"Descanso".equals(plannedOrder.getStopId()) && !"5".equals(plannedOrder.getStopId()) ) {
                                plannedOrders.add(plannedOrder);
                            }
                        }

                        // Obtener la lista de PlannedOrder de OperationalOrderAchievement y filtrar los elementos con stopId "Llegada"
                        for (OperationalOrderAchievement achievement : achievementsList) {
                            Order order = achievement.getOrder();
                            orders.add(order);
                        }

                        for(OperationalOrderAchievement achievement : achievementsList) {
                            Geocode geocode = achievement.getGeocode();
                            if (geocode != null){
                                geocodes.add(geocode);
                            }
                        }

                        Log.d(TAG,"Orders Login"+orders.toString());

                        Log.d(TAG,"PlannedOrders"+plannedOrders.toString());

                        Log.d(TAG,"Hora"+startDate.toString());

                        Log.d(TAG,"Geocode Login "+geocodes.toString());


                        Intent intent = new Intent(Login.this, InicioActivity.class);
                        intent.putParcelableArrayListExtra("positioning",new ArrayList<>(lastKnownPositions));
                        intent.putParcelableArrayListExtra("achievements",new ArrayList<>(achievementsList));
                        intent.putParcelableArrayListExtra("plannedOrders", new ArrayList<>(plannedOrders));
                        intent.putParcelableArrayListExtra("orders", new ArrayList<>(orders));
                        intent.putParcelableArrayListExtra("geocodes",new ArrayList<>(geocodes));
                        intent.putParcelableArrayListExtra("achievementsAgenda",new ArrayList<>(achievementsListAgenda));
                        intent.putParcelableArrayListExtra("plannedOrdersAgenda",new ArrayList<>(plannedOrdersAgenda));
                        intent.putParcelableArrayListExtra("ordersAgenda",new ArrayList<>(ordersAgenda));
                        intent.putExtra("user_name", fullName);
                        intent.putExtra("user_email", userLogin);
                        intent.putExtra("hora_exacta", hour);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    Log.e(TAG, "Error en la respuesta de la API: " + response.errorBody());
                    Toast.makeText(Login.this, "Error en la respuesta de la API", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                loadingAlert.closeAlertDialog();
                Log.e(TAG, "Error en la llamada a la API", t);
                Toast.makeText(Login.this, "Error en la llamada a la API", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showErrorAndClearFields() {
        Toast.makeText(Login.this, "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show();
        correo.getEditText().setText("");
        contrasenia.getEditText().setText("");
        correo.getEditText().requestFocus();
    }

    private void saveCredentials(String email, String password) {
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("email", email);
        editor.putString("password", password);
        editor.putBoolean("rememberMe", true);
        editor.apply();
    }

    private void clearCredentials() {
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("email");
        editor.remove("password");
        editor.putBoolean("rememberMe", false);
        editor.apply();
    }

    private void loadCredentials() {
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        boolean rememberMe = sharedPreferences.getBoolean("rememberMe", false);
        if (rememberMe) {
            String email = sharedPreferences.getString("email", "");
            String password = sharedPreferences.getString("password", "");
            correo.getEditText().setText(email);
            contrasenia.getEditText().setText(password);
            rememberMeCheckBox.setChecked(true);
        }
    }
}