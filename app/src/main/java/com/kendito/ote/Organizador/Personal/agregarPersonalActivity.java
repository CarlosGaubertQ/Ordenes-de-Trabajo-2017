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
import com.kendito.ote.model.valueMensaje;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class agregarPersonalActivity extends AppCompatActivity {

    //URL de servicio web y barra de progreso
    public static final String URL = "http://kenditobd.ddns.net/ote/";
    private ProgressDialog progress;


    //datos que serviran para introducir en el spinner de tipo personal
    String[] datos = {"Selecciona tipo personal" , "Organizador" , "Personal"};
    //iniciar variables de la vista
    @BindView(R.id.btnAgregarPersonal)
    protected Button btnAgregarPersonal;
    @BindView(R.id.rutPersonalAgregar)
    protected EditText rutPersonalAgregar;
    @BindView(R.id.txtEditDVA)
    protected EditText txtEditDVA;
    @BindView(R.id.nombrePersonalAgregar)
    protected EditText nombrePersonalAgregar;
    @BindView(R.id.contrasenaPersonalAgregar)
    protected EditText contrasenaPersonalAgregar;
    @BindView(R.id.SpiAPTipoPersonal)
    protected Spinner SpiAPTipoPersonal;



    //evento click para el boton de agregar personal
    @OnClick(R.id.btnAgregarPersonal)void agregarPersonal(View v){
        //iniciar barra de progreso
        progress = new ProgressDialog(this);
        progress.setCancelable(false);
        progress.setMessage("Intentando agregar personal...");


        //obtener datos para poder agregar nuevo personal
        String rutP = rutPersonalAgregar.getText().toString();
        String dv = txtEditDVA.getText().toString();
        String rutCompleto = rutP + "-" + dv;
        String nombreP = nombrePersonalAgregar.getText().toString();
        String contrasena = contrasenaPersonalAgregar.getText().toString();
        String idTipoPersonal = Integer.toString(SpiAPTipoPersonal.getSelectedItemPosition());
        String personalAsignador = "0";


        //determinar si espacios estan vacios
        if(!TextUtils.isEmpty(rutP) || !TextUtils.isEmpty(dv)){
            if(!TextUtils.isEmpty(nombreP)){
                if(!TextUtils.isEmpty(contrasena)){
                    if(idTipoPersonal != "0"){
                        if(verificarRut(Integer.parseInt(rutP),dv)){
                            //mostrar barra de progreso
                            progress.show();
                            //determinar el tipo de asignador
                            if(idTipoPersonal == "1"){
                                personalAsignador = "1";
                            }else if(idTipoPersonal == "2"){
                                personalAsignador = "0";
                            }

                            //iniciar consulta
                            Gson gson = new GsonBuilder().setLenient().create();


                            Retrofit retrofit = new Retrofit.Builder()
                                    .baseUrl(URL)
                                    .addConverterFactory(GsonConverterFactory.create(gson))
                                    .build();

                            RegisterAPI api = retrofit.create(RegisterAPI.class);
                            Call<valueMensaje> call = api.ingresarPersonal(rutCompleto,idTipoPersonal,
                                                                            nombreP,personalAsignador,contrasena);
                            //ejecutar consulta
                            call.enqueue(new Callback<valueMensaje>() {
                                @Override
                                public void onResponse(Call<valueMensaje> call, Response<valueMensaje> response) {

                                    progress.dismiss();
                                    String value = response.body().getValue();
                                    String message = response.body().getMessage();
                                    //si el resultado de value es 1 quiere decir que la consulta se realizo correctamente
                                    if(value == "1"){
                                        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
                                        rutPersonalAgregar.setText("");
                                        txtEditDVA.setText("");
                                        nombrePersonalAgregar.setText("");
                                        contrasenaPersonalAgregar.setText("");
                                    }else{
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
                            Toast.makeText(getApplicationContext(),"Rut no valido",Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(getApplicationContext(),"Seleccionar un tipo de personal",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(),"Ingresar contraseña para el personal",Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(getApplicationContext(),"Ingresar nombre del personal",Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(getApplicationContext(),"Ingresar rut",Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_personal);
        //obtener variable de actionbar propio
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);

        //llenar spinner con datos de tipo personal
        ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this, R.layout.spinner_item,datos);
        SpiAPTipoPersonal.setAdapter(adaptador);
    }


    //metodo para verificar si un rut fue digitado correctamente
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
