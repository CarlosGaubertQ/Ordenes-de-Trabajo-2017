package com.kendito.ote.Organizador.Personal;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kendito.ote.Organizador.menuOrganizadorActivity;
import com.kendito.ote.R;
import com.kendito.ote.api.RegisterAPI;
import com.kendito.ote.model.Personal;
import com.kendito.ote.model.ResultPersonal;
import com.kendito.ote.model.valueMensaje;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class gestionarPersonalActivity extends AppCompatActivity {

    //URL de servicio web y barra de progreso
    public static final String URL = "http://kenditobd.ddns.net/ote/";
    private ProgressDialog progress;

    //iniciar variables de la vista
    @BindView(R.id.btnPersonalModificar)
    protected Button btnPersonalModificar;
    @BindView(R.id.btnPersonalEliminar)
    protected Button btnPersonalEliminar;
    @BindView(R.id.btnPersonalAgregar)
    protected Button btnPersonalAgregar;
    @BindView(R.id.spinnerSelPersonal)
    protected Spinner spinnerSelPersonal;
    @BindView(R.id.rutPersonal)
    protected TextView rutPersonal;
    @BindView(R.id.nombrePersonal)
    protected TextView nombrePersonal;
    @BindView(R.id.tipoPersonal)
    protected TextView tipoPersonal;


    //lista de personales
    List<ResultPersonal> personal;
    //valores de personales que sirven para discriminar sus rut's de acuerdo a su posicion
    List<String> values = new ArrayList<String>();
    List<String> valuesid = new ArrayList<String>();

    //adapter para modificar el spinner
    ArrayAdapter<String> adapter;


    //evento click para el boton de modificar personal
    @OnClick(R.id.btnPersonalModificar)
    void modificarPersonal(View view) {

        Intent intent = new Intent(this, modificarPersonalActivity.class);
        intent.putExtra("rutPersonal", valuesid.get(spinnerSelPersonal.getSelectedItemPosition()));
        startActivity(intent);

    }

    //evento click para el boton de agregar nuevo personal
    @OnClick(R.id.btnPersonalAgregar)
    void agregarPersonal(View view) {
        Intent intent = new Intent(this, agregarPersonalActivity.class);
        intent.putExtra("spinerPersonal", spinnerSelPersonal.getSelectedItem().toString());
        startActivity(intent);
    }

    //evento click para el boton de eliminar personal
    @OnClick(R.id.btnPersonalEliminar)
    void eliminarPersonal(View view) {
        mensajeEliminar();
    }


    //modificar datos mediante el cambio de personal por medio del spinner
    @OnItemSelected(R.id.spinnerSelPersonal)
    void onItemSelected(int position) {
        rutPersonal.setText(personal.get(position).getRUTPERSONAL());
        nombrePersonal.setText(personal.get(position).getNOMBRE());

        if (personal.get(position).getIDTIPOPERSONAL().equals("1")) {
            tipoPersonal.setText("Organizador");
        } else {
            tipoPersonal.setText("Personal");
        }
    }

    //main
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestionar_personal);
        ButterKnife.bind(this);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);




        //llenar combo con personal
        llenarPersonal();
        nombrePersonal.setSelected(true);
    }


    //llenar spinner con todos los personales del sistema
    public void llenarPersonal() {
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
        Call<Personal> call = api.verPersonal();

        //ejecutar consulta
        call.enqueue(new Callback<Personal>() {
            @Override
            public void onResponse(Call<Personal> call, Response<Personal> response) {
                progress.dismiss();
                //si el valor de respuesta es 1 quiere decir que la consulta se realizo correctamente
                if (response.body().getValuePersonal() == 1) {
                    //obtener personales en una lista
                    personal = response.body().getResultPersonal();

                    //recorrer valores del personal (rutPersonal,nombrePersonal) para poder discriminar valores de acuerdo a la posicion
                    for (int i = 0; i < personal.size(); i++) {
                        values.add(i, personal.get(i).getNOMBRE());
                        valuesid.add(i, personal.get(i).getRUTPERSONAL());
                    }

                    //iniciar adapter en el spinner con tal de mostrar los datos obtenidos por la consulta
                    adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, values);
                    adapter.notifyDataSetChanged();
                    spinnerSelPersonal.setAdapter(adapter);
                    nombrePersonal.setSelected(true);
                }
            }

            @Override
            public void onFailure(Call<Personal> call, Throwable t) {
                //en caso de fallar mostrar mensaje por pantalla y volver a la actividad anterior
                progress.dismiss();
                Toast.makeText(getApplicationContext(), "Fallo la conexion a internet", Toast.LENGTH_SHORT).show();
                volver();
            }
        });


    }


    //metodo que elimina personal
    public void mensajeEliminar() {
        //mostrar alertDialog para preguntar si se desea eliminar personal
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("¿ Deseas eliminar este personal ?");
        builder.setTitle("Eliminado");
        //evento al boton positivo de alerdialog
        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //obtener index del personal seleccionado
                final int index = spinnerSelPersonal.getSelectedItemPosition();

                //obtener rut del personal de acuerdo al index del cual se esta eliminado
                String personalRut = valuesid.get(index);
                //obtener index en variable string para poder eliminar del spinner
                final String indexf = spinnerSelPersonal.getSelectedItem().toString();


                //iniciar barra de progreso
                progress.setMessage("Eliminando personal...");
                progress.show();

                //iniciar consulta
                Gson gson = new GsonBuilder().setLenient().create();

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(URL)
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build();

                RegisterAPI api = retrofit.create(RegisterAPI.class);
                Call<valueMensaje> call = api.eliminarPersonal(personalRut);
                //ejecutar consulta
                call.enqueue(new Callback<valueMensaje>() {
                    @Override
                    public void onResponse(Call<valueMensaje> call, Response<valueMensaje> response) {
                        //obtener valores de la respuesta
                        String value = response.body().getValue();
                        String message = response.body().getMessage();

                        progress.dismiss();
                        //si la respuesta es 1 entonces quiere decir que la consulta se efectuo correctamente
                        if(value.equals("1")) {
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                            //eliminar personal del spinner
                            adapter.remove(indexf);
                            adapter.notifyDataSetChanged();
                        }else if(value.equals("2")) {
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        }
                    }

                    //en caso de fallar mostrar mensaje por pantalla
                    @Override
                    public void onFailure(Call<valueMensaje> call, Throwable t) {
                        progress.dismiss();
                        Toast.makeText(getApplicationContext(), "Fallo la conexion a internet", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        //evento al boton negativo del alertDialog
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        //mostrar alertDialog
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    //metodo para volver a la actividad anterior a esta
    public void volver() {
        Intent intent = new Intent(this, menuOrganizadorActivity.class);
        startActivity(intent);
    }
}
