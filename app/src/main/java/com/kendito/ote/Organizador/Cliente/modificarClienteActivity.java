package com.kendito.ote.Organizador.Cliente;

import android.app.ProgressDialog;
import android.content.Intent;
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
import com.kendito.ote.model.Result;
import com.kendito.ote.model.Sitio;
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

public class modificarClienteActivity extends AppCompatActivity {

    //URL de servicio web y barra de progreso
    public static final String URL = "http://kenditobd.ddns.net/ote/";
    private ProgressDialog progress;


    //VALORES PARA LOS SITIOS
    List<String> values = new ArrayList<String>();
    List<String> valuesid = new ArrayList<String>();

    //valores para el recyclerView
    List<Result> sitios;

    //iniciar variables de la vista
    @BindView(R.id.btnModifciarCliente)
    protected Button btnModifciarCliente;
    @BindView(R.id.rutClienteModificar)
    protected EditText rutClienteModificar;
    @BindView(R.id.txtEditDVM)
    protected EditText txtEditDVM;
    @BindView(R.id.nombreClienteModificar)
    protected EditText nombreClienteModificar;
    @BindView(R.id.contrasenaClienteModificar)
    protected EditText contrasenaClienteModificar;
    @BindView(R.id.SpiMCSitio)
    protected Spinner SpiMCSitio;

    //variables globales
    private String rutRecibido;
    private String nombreRecibido;
    private String contrasenaRecibido;
    private String idSitioRecibido;

    @OnClick(R.id.btnModifciarCliente)void modificarCliente(View view){
        //iniciar barra de progreso
        progress = new ProgressDialog(this);
        progress.setCancelable(false);
        progress.setMessage("Modificando cliente...");
        progress.show();

        //obtener datos del cliente a modificar
        String rut = rutClienteModificar.getText().toString();
        String dv = txtEditDVM.getText().toString();
        String rutCompleto = rut + "-" + dv;

        //datos nuevos del cliente a modificar
        String nombre = nombreClienteModificar.getText().toString();
        String contrasena = contrasenaClienteModificar.getText().toString();



        String idTipoPersonal = "3";
        int index = SpiMCSitio.getSelectedItemPosition();
        String idSitio = valuesid.get(index);

        //verificar datos vacios
        if(!TextUtils.isEmpty(nombre)) {
            if(!TextUtils.isEmpty(contrasena)){

                //preparar modificacion del cliente
                Gson gson = new GsonBuilder().setLenient().create();

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(URL)
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build();

                RegisterAPI api = retrofit.create(RegisterAPI.class);
                Call<valueMensaje> call = api.modificarCliente(rutCompleto,idTipoPersonal,idSitio,nombre,contrasena);
                //ejecutar consulta
                call.enqueue(new Callback<valueMensaje>() {
                    @Override
                    public void onResponse(Call<valueMensaje> call, Response<valueMensaje> response) {
                        //obtener datos de la consulta
                        String value = response.body().getValue();
                        String message = response.body().getMessage();
                        progress.dismiss();

                        //si el value es 1 significa que la consulta se realizo correctamente
                        if(value.equals("1")){
                            Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
                        }
                    }

                    //en caso de fallar mostrar mensaje por pantalla
                    @Override
                    public void onFailure(Call<valueMensaje> call, Throwable t) {
                        progress.dismiss();
                        Toast.makeText(getApplicationContext(),"Algo ocurrio durante la modificacion del cliente",Toast.LENGTH_SHORT).show();
                    }
                });


            }else{
                Toast.makeText(getApplicationContext(),"Ingresar contraseña nueva del cliente",Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(getApplicationContext(),"Ingresar nombre nuevo del cliente",Toast.LENGTH_SHORT).show();
        }

    }


    //main
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modificar_cliente);
        ButterKnife.bind(this);
        //obtener actionbar de la actividad
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //obtener datos de la actividad anterior
        rutRecibido = getIntent().getExtras().getString("Rutcliente");
        nombreRecibido = getIntent().getExtras().getString("nombreCliente");
        contrasenaRecibido = getIntent().getExtras().getString("contrasenaCliente");
        idSitioRecibido = getIntent().getExtras().getString("idSitio");

        //ingresar datos del rut en sus respectivos EditText
        String[] rutSeparar = rutRecibido.split("-");
        rutClienteModificar.setText(rutSeparar[0]);
        txtEditDVM.setText(rutSeparar[1]);
        nombreClienteModificar.setText(nombreRecibido);
        contrasenaClienteModificar.setText(contrasenaRecibido);

        //llenar combo con sitios
        llenarDatos();
    }



    //llenar spinner con datos de sitios
    public void llenarDatos() {
        //inciar barra de progreso
        progress = new ProgressDialog(this);
        progress.setCancelable(false);
        progress.setMessage("Cargando datos de sitios...");
        progress.show();

        //iniciar consulta
        Gson gson = new GsonBuilder().setLenient().create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        RegisterAPI api = retrofit.create(RegisterAPI.class);
        Call<Sitio> call = api.verSitios();

        //ejecutar consulta
        call.enqueue(new Callback<Sitio>() {
            @Override
            public void onResponse(Call<Sitio> call, Response<Sitio> response) {

                progress.dismiss();
                if(response.body().getValue() == 1){
                    //obtener sitios de la consulta
                    sitios = response.body().getResult();

                    //recorrer los datos para almacenar idsitio y nombre de sitio y asi discriminarlos dentro del spinner
                    for(int i =0 ; i<sitios.size();i++){
                        values.add(i,sitios.get(i).getNOMBRESITIO());
                        valuesid.add(i,sitios.get(i).getIDSITIO());
                    }

                    //modificar adapter para mostrar los sitios dentro del spinner
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.spinner_item,values);
                    SpiMCSitio.setAdapter(adapter);

                }

            }

            //en caso de fallar mostrar mensaje por pantalla y regresar a actividad anterior
            @Override
            public void onFailure(Call<Sitio> call, Throwable t) {
                progress.dismiss();
                Toast.makeText(getApplicationContext(),"Fallo conexion a internet",Toast.LENGTH_SHORT).show();
                volver();


            }
        });

    }

    //metodo que vuelve a la actividad anterior
    public void volver(){
        Intent intent = new Intent(this,gestionarClienteActivity.class);
        startActivity(intent);
    }
}
