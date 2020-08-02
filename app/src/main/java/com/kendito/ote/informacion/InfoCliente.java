package com.kendito.ote.informacion;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kendito.ote.Organizador.Cliente.gestionarClienteActivity;
import com.kendito.ote.Organizador.Sitio.agregarSitioActivity;
import com.kendito.ote.R;
import com.kendito.ote.api.RegisterAPI;
import com.kendito.ote.model.ClienteG;
import com.kendito.ote.model.ResultClienteG;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class InfoCliente extends AppCompatActivity {

    //URL de servicio web y barra de progreso
    public static final String URL = "http://kenditobd.ddns.net/ote/";
    private ProgressDialog progress;

    //iniciar variables de la vista
    @BindView(R.id.rutClienteInfo)
    protected TextView rutClienteInfo;
    @BindView(R.id.nombreClienteInfo)
    protected TextView nombreClienteInfo;
    @BindView(R.id.nombresitioInfo)
    protected TextView nombresitioInfo;
    @BindView(R.id.nombreAreaInfo)
    protected TextView nombreAreaInfo;
    @BindView(R.id.tipoPersonalInfo)
    protected TextView tipoPersonalInfo;
    @BindView(R.id.contrasenaInfo)
    protected TextView contrasenaInfo;


    //main
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_cliente);
        ButterKnife.bind(this);

        //obtener datos
        String rut = getIntent().getExtras().getString("rutCliente");

        //cargar datos con respecto al rut recibido
        cargarDato(rut);
    }



    //metodo que carga todos los datos con respecto al rut recibido
    public void cargarDato(String rut){

        //iniciar barra de progreso
        progress = new ProgressDialog(this);
        progress.setCancelable(false);
        progress.setMessage("Cargando datos del cliente seleccionado...");
        progress.show();

        //inciar consulta
        Gson gson = new GsonBuilder().setLenient().create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        RegisterAPI api = retrofit.create(RegisterAPI.class);
        Call<ClienteG> call = api.verClienteG(rut);

        //ejecutar consulta
        call.enqueue(new Callback<ClienteG>() {
            @Override
            public void onResponse(Call<ClienteG> call, Response<ClienteG> response) {
                progress.dismiss();
                //obtener datos de consulta
                Integer value = response.body().getValueCliente();
                //si el resultado de la conulta es 1 entonces significa que se realizo correctamente
                if(value.equals(1)){
                    //obtener datos de cliente
                    List<ResultClienteG> cliente = response.body().getResultClienteG();

                    //llenar EditText con datos de la consulta
                    rutClienteInfo.setText(cliente.get(0).getRUTCLIENTE());
                    nombreClienteInfo.setText(cliente.get(0).getNOMBRECLIENTE());
                    nombresitioInfo.setText(cliente.get(0).getNOMBRESITIO());
                    nombreAreaInfo.setText(cliente.get(0).getNOMBREAREA());
                    tipoPersonalInfo.setText(cliente.get(0).getCARGO());
                    contrasenaInfo.setText(cliente.get(0).getCONTRASENA());

                }
            }

            //en caso de fallar mostrar mensaje por pantalla y regresar a la actividad anterior
            @Override
            public void onFailure(Call<ClienteG> call, Throwable t) {
                progress.dismiss();
                Toast.makeText(getApplicationContext(),"Fallo conexion a internet",Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getApplicationContext(), gestionarClienteActivity.class);
                startActivity(intent);

            }
        });



    }
}
