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

public class agregarClienteActivity extends AppCompatActivity {

    //URL de servicio web y barra de progreso
    public static final String URL = "http://kenditobd.ddns.net/ote/";
    private ProgressDialog progress;

    //iniciar variables de la vista
    @BindView(R.id.SpiACSitio)
    protected Spinner SpiACSitio;
    @BindView(R.id.btnAgregarCliente)
    protected Button btnAgregarCliente;
    @BindView(R.id.rutClienteAgregar)
    protected EditText rutClienteAgregar;
    @BindView(R.id.txtEditDVA)
    protected EditText txtEditDVA;
    @BindView(R.id.nombreClienteAgregar)
    protected EditText nombreClienteAgregar;
    @BindView(R.id.contrasenaClienteAgregar)
    protected EditText contrasenaClienteAgregar;


    //VALORES PARA LOS SITIOS
    List<String> values = new ArrayList<String>();
    List<String> valuesid = new ArrayList<String>();

    //valores para el recyclerView
    List<Result> sitios;


    @OnClick(R.id.btnAgregarCliente)
    void ingresarCliente(View view) {
        //obtener id del sitio mediante el index de la posicion del spinner
        int index = SpiACSitio.getSelectedItemPosition();
        String idSitioSel = valuesid.get(index);

        //obtener datos de la actividad para ingresar el nuevo cliente
        String rut = rutClienteAgregar.getText().toString();
        String dv = txtEditDVA.getText().toString();
        String rutCompleto = rut + "-" + dv;
        String nombre = nombreClienteAgregar.getText().toString();
        String contrasena = contrasenaClienteAgregar.getText().toString();
        String idtipoPersonal = "3";


        //verificar espacios en blanco
        if (!TextUtils.isEmpty(rut) || !TextUtils.isEmpty(dv)) {
            if (!TextUtils.isEmpty(nombre)) {
                if (!TextUtils.isEmpty(contrasena)) {
                    if (verificarRut(Integer.parseInt(rut), dv)) {
                        progress = new ProgressDialog(this);
                        progress.setCancelable(false);
                        progress.setMessage("Agregando cliente...");
                        progress.show();

                        //preparar ingreso del cliente
                        Gson gson = new GsonBuilder().setLenient().create();
                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl(URL)
                                .addConverterFactory(GsonConverterFactory.create(gson))
                                .build();
                        RegisterAPI api = retrofit.create(RegisterAPI.class);
                        Call<valueMensaje> call = api.ingresarCliente(rutCompleto, idtipoPersonal, idSitioSel, nombre, contrasena);
                        //ejecutar consulta
                        call.enqueue(new Callback<valueMensaje>() {
                            @Override
                            public void onResponse(Call<valueMensaje> call, Response<valueMensaje> response) {
                                progress.dismiss();
                                //obtener datos de la consulta
                                String value = response.body().getValue();
                                String message = response.body().getMessage();
                                //si el value es igual a 1 entonces la consulta se realizo correctamente
                                if (value.equals("1")) {
                                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                                }


                            }

                            //en caso de fallar mostrar mensaje por pantalla
                            @Override
                            public void onFailure(Call<valueMensaje> call, Throwable t) {
                                progress.dismiss();
                                Toast.makeText(getApplicationContext(), "Ocurrio un problema al ingresar el cliente", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(this, "Rut invalido", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Ingresar contraseña del cliente", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Ingresar nombre del cliente", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Ingresar rut del cliente", Toast.LENGTH_SHORT).show();
        }

    }


    //main
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_cliente);
        //instanciar actionbar de la actividad
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);
        //llenar spinner con sitios
        llenarComboSitio();

    }

    //metodo que llena spinner con los sitios del sistema
    public void llenarComboSitio() {
        //iniciar barra de progreso
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
                //si el value que recibe la respuesta de la consulta entonces se realizo correctamente
                if (response.body().getValue() == 1) {
                    //obtener sitios recibidos
                    sitios = response.body().getResult();
                    //recorrer los datos para almacenar idsitio y nombre de sitio y asi discriminarlos dentro del spinner
                    for (int i = 0; i < sitios.size(); i++) {
                        values.add(i, sitios.get(i).getNOMBRESITIO());
                        valuesid.add(i, sitios.get(i).getIDSITIO());
                    }

                    //crear adapter para poder modificar spinner deacuerdo a los datos obtenidos en la consulta
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, values);
                    SpiACSitio.setAdapter(adapter);
                }

            }

            //en caso de fallar, mandar mensaje por pantalla y volver a la actividad anterior
            @Override
            public void onFailure(Call<Sitio> call, Throwable t) {
                progress.dismiss();
                Toast.makeText(getApplicationContext(), "Fallo conexion a internet", Toast.LENGTH_SHORT).show();
                volver();
            }
        });
    }

    //metodo para volver a la actividad gestionar cliente
    public void volver() {
        Intent intent = new Intent(this, gestionarClienteActivity.class);
        startActivity(intent);
    }


    //metodo para verificar si un rut esta bien ingresado
    public boolean verificarRut(int rut, String digitoVerificador) {
        boolean exito = false;

        String digitoFinal;
        int digito = 0;
        int contador = 0;
        int multiplo = 0;
        int acomulador = 0;
        int rut1 = rut;
        contador = 2;
        acomulador = 0;
        if (digitoVerificador.equals("k")) digitoVerificador = "K";
            for (int i = 0; i < 8; i++) {
                multiplo = (rut1 % 10) * contador;
                acomulador = acomulador + multiplo;
                rut1 = rut1 / 10;
                contador += 1;
                if (contador == 8) {
                    contador = 2;
                }
            }
        digito = 11 - (acomulador % 11);

        digitoFinal = Integer.toString(digito);
        if ("10".equals(digitoFinal)) digitoFinal = "K";
        if ("11".equals(digitoFinal)) digitoFinal = "0";
        exito = digitoFinal.equals(digitoVerificador);

        return exito;
    }
}
