package com.kendito.ote;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kendito.ote.Cliente.OrdenesYCliente;
import com.kendito.ote.Organizador.menuOrganizadorActivity;
import com.kendito.ote.Personal_Organizador.VerOrdenesPersonal;
import com.kendito.ote.api.RegisterAPI;
import com.kendito.ote.informacion.InfoOTE;
import com.kendito.ote.model.valueLogin;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class loginActivity extends AppCompatActivity{

    //URL de servicio web
    public static final String URL = "http://kenditobd.ddns.net/ote/";

    //variables globales publicas
    public static String rutLoginPasado;
    public static String idTipoPersonalPasado;


    //variables globales
    private ProgressDialog progress;
    String rut;
    String contrasena;

    //iniciar variables de la vista
    @BindView(R.id.btnIngresar)
    protected Button btnIngresar;
    @BindView(R.id.txtEditRut)
    protected EditText txtEditRut;
    @BindView(R.id.txtEditDigitoVerificador)
    protected EditText txtEditDigitoVerificador;
    @BindView(R.id.txtEditContrasena)
    protected EditText txtEditContrasena;
    @BindView(R.id.btnSalir)
    protected Button btnSalir;

    //boton para salir del programa
    @OnClick(R.id.btnSalir)void salir(View v){
        finish();
    }

    @OnClick(R.id.btnIngresar)void IngresarSistema(View v){

        //validar datos vacios
        if(TextUtils.isEmpty(txtEditRut.getText()) || TextUtils.isEmpty(txtEditDigitoVerificador.getText())){
            Toast msjToast = Toast.makeText(getApplicationContext(),"Ingresar rut",Toast.LENGTH_SHORT);
            msjToast.show();
        }else {

            if (TextUtils.isEmpty(txtEditContrasena.getText())) {
                Toast msjToast = Toast.makeText(getApplicationContext(), "Ingresar contraseña", Toast.LENGTH_SHORT);
                msjToast.show();
            }else {
                
                //buscar rut y contraseña correspondiente en la base de datos
                //crear barra de progreso
                progress = new ProgressDialog(this);
                progress.setCancelable(false);
                progress.setMessage("Intentando ingresar porfavor espere");
                progress.show();

                //datos temporales
                rut = txtEditRut.getText().toString() + "-" + txtEditDigitoVerificador.getText().toString();
                contrasena = txtEditContrasena.getText().toString();

                //llamar consulta
                Gson gson = new GsonBuilder().setLenient().create();
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(URL)
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build();

                RegisterAPI api = retrofit.create(RegisterAPI.class);
                Call<valueLogin> call = api.verificarLoginPersonal(rut,contrasena);
                
                //ejecutar consulta
                call.enqueue(new Callback<valueLogin>() {
                    @Override
                    public void onResponse(Call<valueLogin> call, Response<valueLogin> response) {
                        String value = response.body().getValue();
                        String nombre = response.body().getNombre();

                        progress.dismiss();
                        
                        //determinar que clase de personal es al que se esta teniendo acceso mediante al rut
                        //organizador
                        if(value.equals("1")){
                            rutLoginPasado = rut;
                            idTipoPersonalPasado = value;
                            Toast.makeText(getApplicationContext(),"Bienvenido " + nombre,Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(loginActivity.this, menuOrganizadorActivity.class);
                            startActivity(intent);

                        //personal
                        }else if(value.equals("2")){
                            rutLoginPasado = rut;
                            idTipoPersonalPasado = value;
                            Toast.makeText(getApplicationContext(),"Bienvenido " + nombre,Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(loginActivity.this, VerOrdenesPersonal.class);
                            startActivity(intent);
                        //cliente
                        }else if(value.equals("3")){
                            rutLoginPasado = rut;
                            Toast.makeText(getApplicationContext(),"Bienvenido " + nombre,Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(loginActivity.this, OrdenesYCliente.class);
                            intent.putExtra("rut",rut);
                            intent.putExtra("nombre",nombre);
                            startActivity(intent);
                        //no existe
                        }else if(value.equals("4")){
                            Toast.makeText(getApplicationContext(),"No existe este usuario",Toast.LENGTH_SHORT).show();
                        }else if(value.equals("6")){
                            Toast.makeText(getApplicationContext(),"Contraseña incorrecta",Toast.LENGTH_SHORT).show();
                        }
                    }

                    //en caso de fallar mostrar error por pantalla
                    @Override
                    public void onFailure(Call<valueLogin> call, Throwable t) {
                        Log.e("Error",t.toString());
                        progress.dismiss();
                        Toast.makeText(getApplicationContext(),"Fallo conexion a internet",Toast.LENGTH_SHORT).show();
                    }
                });


            }
        }
    }

    //main
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        ActionBar actionBar = getSupportActionBar();
        try{
            Common.currentToken = FirebaseInstanceId.getInstance().getToken();
            Log.d("MY TOKEN",Common.currentToken);

            Vibrator vibrator = (Vibrator)this.getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(1000);
        }catch(Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_login,menu);
        return super.onCreateOptionsMenu(menu);
    }



    //metodo que da acceso a ajustes del programa
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.ajustes:
                Toast.makeText(getApplicationContext(),"Ir a ajustes",Toast.LENGTH_SHORT).show();
                break;

            case R.id.action_info:
                Intent intent = new Intent(this,InfoOTE.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }


}