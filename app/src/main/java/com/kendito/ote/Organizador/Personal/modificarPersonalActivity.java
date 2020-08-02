package com.kendito.ote.Organizador.Personal;

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
import com.kendito.ote.model.Personal;
import com.kendito.ote.model.ResultPersonal;
import com.kendito.ote.model.valueMensaje;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class modificarPersonalActivity extends AppCompatActivity {

    //URL de servicio web y barra de progreso
    public static final String URL = "http://kenditobd.ddns.net/ote/";
    private ProgressDialog progress;

    //iniciar variables de la vista
    @BindView(R.id.rutPersonalModificar)
    protected EditText rutPersonalModificar;
    @BindView(R.id.txtEditDVM)
    protected EditText txtEditDVM;
    @BindView(R.id.nombrePersonalModifiar)
    protected EditText nombrePersonalModifiar;
    @BindView(R.id.contrasenaPersonalModificar)
    protected EditText contrasenaPersonalModificar;
    @BindView(R.id.btnModificarPersonal)
    protected Button btnModificarPersonal;
    @BindView(R.id.SpiMPTipoPersonal)
    protected Spinner SpiMPTipoPersonal;
    String[] datos = {"Selecciona tipo personal" , "Organizador" , "Personal"};


    @OnClick(R.id.btnModificarPersonal)void modificarPersonal(View view){
        progress = new ProgressDialog(this);
        progress.setCancelable(false);
        progress.setMessage("Intentando modificar personal...");

        //variables de la actividad
        String rut = rutPersonalModificar.getText().toString();
        String dv = txtEditDVM.getText().toString();

        //dar valores a modificar
        String rutCompleto = rut + "-" + dv;
        String nombre = nombrePersonalModifiar.getText().toString();
        String contrasena = contrasenaPersonalModificar.getText().toString();
        String idTipoPersonal = Integer.toString(SpiMPTipoPersonal.getSelectedItemPosition());
        String personalAsignador = "0";
        if(idTipoPersonal == "1"){
            personalAsignador = "1";
        }else if(idTipoPersonal == "2"){
            personalAsignador = "0";
        }


        //verificar espacios en blanco
        if(!TextUtils.isEmpty(nombre)){
            if(!TextUtils.isEmpty(contrasena)){
                if(idTipoPersonal != "0"){
                    //verificar codigo verificador
                    if(verificarRut(Integer.parseInt(rut),dv)){

                        //iniciar consulta
                        Gson gson = new GsonBuilder().setLenient().create();

                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl(URL)
                                .addConverterFactory(GsonConverterFactory.create(gson))
                                .build();

                        RegisterAPI api = retrofit.create(RegisterAPI.class);
                        Call<valueMensaje> call = api.modificarPersonal(rutCompleto,idTipoPersonal,nombre,personalAsignador,contrasena);
                        //ejecutar consulta
                        call.enqueue(new Callback<valueMensaje>() {
                            @Override
                            public void onResponse(Call<valueMensaje> call, Response<valueMensaje> response) {
                                //obtener valores de respuesta
                                String value = response.body().getValue();
                                String message = response.body().getMessage();

                                //si el valor es 1 entonces quiere decir que la consulta se ejecuto correctamente
                                if(value.equals("1")){
                                    Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
                                }

                            }

                            @Override
                            public void onFailure(Call<valueMensaje> call, Throwable t) {
                                //en caso de fallar consulta mostrar mensaje por pantalla
                                Toast.makeText(getApplicationContext(),"Algo ocurrio durante la modificacion del personal",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else{
                        Toast.makeText(getApplicationContext(),"El rut ingresado no es valido",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(),"Selecciona un tipo de personal",Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(getApplicationContext(),"Ingresar contrasena nueva",Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(getApplicationContext(),"Ingresar nuevo nombre del personal",Toast.LENGTH_SHORT).show();
        }

    }


    //main
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modificar_personal);
        ButterKnife.bind(this);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


        //llenar spinner con datos de tipo personal
        ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this, R.layout.spinner_item,datos);
        SpiMPTipoPersonal.setAdapter(adaptador);

        //obtener rut de la actividad anterior
        String rutPeronal = getIntent().getExtras().getString("rutPersonal");

        //separar rur e ingresar en los editText
        String[] rutSeparar = rutPeronal.split("-");
        rutPersonalModificar.setText(rutSeparar[0]);
        txtEditDVM.setText(rutSeparar[1]);


        llamarDatosModificar(rutPeronal);

    }

    //metodo que obtiene datos del personal seleccionado
    public void llamarDatosModificar(String rut){
        //iniciar barra de progreso
        progress = new ProgressDialog(this);
        progress.setCancelable(false);
        progress.setMessage("Cargando datos de personal...");
        progress.show();

        //iniciar consulta
        Gson gson = new GsonBuilder().setLenient().create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        RegisterAPI api = retrofit.create(RegisterAPI.class);
        Call<Personal> call = api.verPersonalEspecifico(rut);
        //ejecutar consulta
        call.enqueue(new Callback<Personal>() {
            @Override
            public void onResponse(Call<Personal> call, Response<Personal> response) {
                int value = response.body().getValuePersonal();
                //obtener lista del personal
                List<ResultPersonal> personal = response.body().getResultPersonal();

                progress.dismiss();

                //si el valor es 1 entonces la consulta sse realizo correctamente y los datos seran llenados con
                //con los datos correspondientes
                if(value == 1){
                    nombrePersonalModifiar.setText(personal.get(0).getNOMBRE());
                    contrasenaPersonalModificar.setText(personal.get(0).getCONTRASENA());

                    if(personal.get(0).getIDTIPOPERSONAL().equals("1")){
                        SpiMPTipoPersonal.setSelection(1);
                    }else{
                        SpiMPTipoPersonal.setSelection(2);
                    }
                }

            }

            //en caso de fallar la consulta mostrar mensaje por pantalla
            @Override
            public void onFailure(Call<Personal> call, Throwable t) {
                progress.dismiss();
                Toast.makeText(getApplicationContext(),"Algo ocurrio durante la modificacion",Toast.LENGTH_SHORT).show();
            }
        });
    }


    //metodo para verificar rut si es correctamente digitado por pantalla
    public boolean verificarRut(int rut,String digitoVerificador){
        boolean exito = false;

        String digitoFinal;
        int digito = 0;
        int contador = 0;
        int multiplo = 0;
        int acomulador = 0;
        int rut1 = rut;
        contador = 2;
        acomulador = 0;


        if(digitoVerificador.equals("k")) digitoVerificador = "K";

        for (int i = 0; i < 8; i++) {
            multiplo = (rut1 % 10) * contador;
            acomulador = acomulador + multiplo;
            rut1 = rut1 / 10;
            contador += 1;
            if(contador == 8){
                contador = 2;
            }
        }
        digito = 11 - (acomulador % 11);

        digitoFinal = Integer.toString(digito);
        if("10".equals(digitoFinal)) digitoFinal = "K";
        if("11".equals(digitoFinal)) digitoFinal = "0";
        exito = digitoFinal.equals(digitoVerificador);


        return exito;
    }
}
