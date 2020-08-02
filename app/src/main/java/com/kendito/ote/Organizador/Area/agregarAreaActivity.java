package com.kendito.ote.Organizador.Area;

import android.app.ProgressDialog;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kendito.ote.R;
import com.kendito.ote.api.RegisterAPI;
import com.kendito.ote.model.valueMensaje;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class agregarAreaActivity extends AppCompatActivity {

    //URL de servicio web y barra de progreso
    public static final String URL = "http://kenditobd.ddns.net/ote/";
    private ProgressDialog progress;


    //iniciar variables de la vista
    @BindView(R.id.txtNombreAreaAgregar)
    protected TextView txtNombreAreaAgregar;
    @BindView(R.id.btnAgregarArea)
    protected Button btnAgregarArea;

    //evento click para ingresar areas nuevss
    @OnClick(R.id.btnAgregarArea)void ingresarArea(View view){
        //iniciar barra de progreso
        progress = new ProgressDialog(this);
        progress.setCancelable(false);
        progress.setMessage("Agregando area...");


        //obtener datos para agregar area nueva
        String nombreArea = txtNombreAreaAgregar.getText().toString();

        //verificar que los espacios no esten vacios
        if(!TextUtils.isEmpty(nombreArea)){
            //mostrar barra de proceso
            progress.show();

            //iniciar consulta
            Gson gson = new GsonBuilder().setLenient().create();


            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();

            RegisterAPI api = retrofit.create(RegisterAPI.class);
            Call<valueMensaje> call = api.ingresarArea(nombreArea);

            //ejecutar consulta
            call.enqueue(new Callback<valueMensaje>() {
                @Override
                public void onResponse(Call<valueMensaje> call, Response<valueMensaje> response) {
                    progress.dismiss();
                    //obtener datos de la consulta
                    String value = response.body().getValue();
                    String message = response.body().getMessage();

                    //si el valor de respuesta es 1 entonces la consulta se realizo correctamente
                    if(value.equals("1")){
                        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
                    }else if(value.equals("2")){
                        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
                    }

                }

                //en caso de fallar mostrar mensaje por pantalla
                @Override
                public void onFailure(Call<valueMensaje> call, Throwable t) {
                    progress.dismiss();
                    Toast.makeText(getApplicationContext(),"Algo ocurrio durante la grabacion",Toast.LENGTH_SHORT).show();
                }
            });



        }else{
            Toast.makeText(getApplicationContext(),"Ingresar nombre de area",Toast.LENGTH_SHORT).show();
        }

    }

    //main
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_area);
        ButterKnife.bind(this);

        //instanciar actionbar de la actividad
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }
}
