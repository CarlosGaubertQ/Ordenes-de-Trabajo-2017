package com.kendito.ote.Organizador.Sitio;

import android.app.ProgressDialog;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kendito.ote.R;
import com.kendito.ote.api.RegisterAPI;
import com.kendito.ote.model.Area;
import com.kendito.ote.model.ResultArea;
import com.kendito.ote.model.valueMensaje;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class agregarSitioActivity extends AppCompatActivity {

    //direccion de webservices y barra de progreso
    public static final String URL = "http://kenditobd.ddns.net/ote/";
    private ProgressDialog progress;

    //iniciar variables de la vista
    @BindView(R.id.txtnombreSitioAgregar)
    protected EditText txtnombreSitioAgregar;
    @BindView(R.id.SpiASarea)
    protected Spinner SpiASarea;
    @BindView(R.id.btnAgregarSitio)
    protected Button btnAgregarSitio;


    //valores del area que sirven para discriminar el id de area de acuerdo a su posicion
    List<String> values = new ArrayList<String>();
    List<String> valuesid = new ArrayList<String>();

    //lista de areas
    List<ResultArea> areas;

    //evento click que ingresa area nueva
    @OnClick(R.id.btnAgregarSitio) void ingresarArea(View view){


        //obtiene index, area y nombre del area de acuerdo a la posicion del spinner
        int index = SpiASarea.getSelectedItemPosition();
        String idArea = valuesid.get(index);
        String nombreSitio = txtnombreSitioAgregar.getText().toString();


        //verificar espacios en blanco
        if(!TextUtils.isEmpty(nombreSitio)){
            //iniciar barra de progreso
            progress.setMessage("Agregando sitio nuevo...");
            progress.show();

            //preparar consulta
            Gson gson = new GsonBuilder().setLenient().create();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();

            RegisterAPI api = retrofit.create(RegisterAPI.class);
            Call<valueMensaje> call = api.ingresarSitio(nombreSitio,idArea);
            //ejecutar consulta
            call.enqueue(new Callback<valueMensaje>() {
                @Override
                public void onResponse(Call<valueMensaje> call, Response<valueMensaje> response) {
                    //valores obtenidos
                    String value = response.body().getValue();
                    String message = response.body().getMessage();
                    //detener barra de progreso
                    progress.dismiss();

                    //"1" significa que la consulta se realizo correctamente
                    if(value.equals("1")){
                        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
                    }else if(value.equals("2")){
                        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailure(Call<valueMensaje> call, Throwable t) {
                    progress.dismiss();
                    //en caso de fallar mostrar mensaje por pantalla
                    Toast.makeText(getApplicationContext(),"Error al conectar con el host",Toast.LENGTH_SHORT).show();
                }
            });


        }else{
            Toast.makeText(getApplicationContext(),"Ingresar nombre del sitio nuevo",Toast.LENGTH_SHORT).show();
        }

    }

    //main
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_sitio);
        ButterKnife.bind(this);
        //iniciar actionbar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        //llenar spinner con areas
        llenarComboArea();

    }

    //metodo para llenar spinner con areas
    public void llenarComboArea(){
        //iniciar barra de progreso
        progress = new ProgressDialog(this);
        progress.setCancelable(false);
        progress.setMessage("Cargando datos de areas...");
        progress.show();

        //preparar consulta
        Gson gson = new GsonBuilder().setLenient().create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        RegisterAPI api = retrofit.create(RegisterAPI.class);
        Call<Area> call = api.verArea();

        //ejecutar consulta
        call.enqueue(new Callback<Area>() {
            @Override
            public void onResponse(Call<Area> call, Response<Area> response) {
                //detener barra de progreso
                progress.dismiss();

                //si el valor obtenido de la consulta es 1 entonces significa que la consulta se realizo correctamente
                if(response.body().getValueArea() == 1){
                    //obtener areas de la consulta
                    areas = response.body().getResultArea();

                    //recorrer valores del area (idArea,nombreArea) para poder discriminar valores de acuerdo a la posicion
                    for(int i = 0; i< areas.size() ; i++){
                        values.add(i,areas.get(i).getNOMBREAREA());
                        valuesid.add(i,areas.get(i).getIDAREA());
                    }

                    //iniciar adapter en el spinner con tal de mostrar los datos obtenidos por la consulta
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.spinner_item,values);
                    SpiASarea.setAdapter(adapter);

                }

            }

            @Override
            public void onFailure(Call<Area> call, Throwable t) {
                progress.dismiss();
                //en caso de fallar mostrar mensaje por pantalla
                Toast.makeText(getApplicationContext(),"Fallo conexion con el host",Toast.LENGTH_SHORT).show();
            }
        });

    }
}
