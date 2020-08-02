package com.kendito.ote.Organizador.Ordenes;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kendito.ote.R;
import com.kendito.ote.adapter.RVAGestionOrdenes;
import com.kendito.ote.api.RegisterAPI;
import com.kendito.ote.model.GestionOrdenes;
import com.kendito.ote.model.Personal;
import com.kendito.ote.model.ResultGestionOrdenes;
import com.kendito.ote.model.ResultPersonal;
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

import static com.kendito.ote.adapter.RVAGestionOrdenes.codigoOrdenPasado;
import static com.kendito.ote.adapter.RVAGestionOrdenes.posicionPasado;

public class GestionarOrdenes extends AppCompatActivity {

    //URL de servicio web y barra de progreso
    public static final String URL = "http://kenditobd.ddns.net/ote/";
    private ProgressDialog progress;

    //variables globales para (lista de ordenes,y adaptador para mostrar en el recyclerView)
    List<ResultGestionOrdenes> ordenes;
    private RVAGestionOrdenes adaptador;

    //lista para llenar spinner
    List<ResultPersonal> personal;




    //valores de personales que sirven para discriminar sus rut's de acuerdo a su posicion
    List<String> values = new ArrayList<String>();
    List<String> valuesid = new ArrayList<String>();
    //adapter para modificar el spinner
    ArrayAdapter<String> adapter;

    //iniciar variables de la vista
    @BindView(R.id.recyclerViewGestionarOrdenes)
    protected RecyclerView listaOrdenes;
    @BindView(R.id.spinnerSelPersonal)
    protected Spinner spinnerSelPersonal;
    @BindView(R.id.btnAsignarOrden)
    protected Button btnAsignarOrden;
    @BindView(R.id.btnEliminarOrden)
    protected Button btnEliminarOrden;


    //evento click para boton de asignar ordenes
    @OnClick(R.id.btnAsignarOrden)
    void asignarOrden(View view) {

        //obtener rut y codigo de orden del cual se seleccion en el spinner y recyclerView
        final String rut = valuesid.get(spinnerSelPersonal.getSelectedItemPosition());
        final String codigoOrdenRecibido = codigoOrdenPasado;


        //determinar si se selecciono alguna orden de trabajo
        if (codigoOrdenRecibido != null) {
            //preguntar si se desea asignar orden de trabajo
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("¿ Deseas orden la orden[" + codigoOrdenRecibido + "] al personal " + spinnerSelPersonal.getSelectedItem() + " ?");
            builder.setTitle("Asignar");
            //accion positiva del alertDialog
            builder.setPositiveButton("Asignar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //iniciar barra de prgreso
                    progress.setMessage("Asignando orden...");
                    progress.show();


                    //iniciar consulta
                    Gson gson = new GsonBuilder().setLenient().create();

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(URL)
                            .addConverterFactory(GsonConverterFactory.create(gson))
                            .build();

                    RegisterAPI api = retrofit.create(RegisterAPI.class);
                    Call<valueMensaje> call = api.asignarOrden(rut, codigoOrdenRecibido);
                    //ejecutar consulta
                    call.enqueue(new Callback<valueMensaje>() {
                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                        @Override
                        public void onResponse(Call<valueMensaje> call, Response<valueMensaje> response) {
                            progress.dismiss();
                            //obtener valores de respuesta
                            String value = response.body().getValue();
                            String message = response.body().getMessage();

                            //si el valor es 1 entonces indica que la consulta se realizo correctamente
                            if (value.equals("1")) {
                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                                //obtenerOrdenes();

                                //eliminar orden del recyclerView
                                int posicionRecibido = posicionPasado;
                                RecyclerView.ViewHolder viewHolder = listaOrdenes.findViewHolderForAdapterPosition(posicionRecibido);
                                adaptador.animateCircularDelete(viewHolder.itemView,posicionRecibido);

                                codigoOrdenPasado = null;
                            } else {
                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                            }
                        }

                        //en caso de fallar mostrar mensaje por pantalla
                        @Override
                        public void onFailure(Call<valueMensaje> call, Throwable t) {
                            progress.dismiss();
                            Toast.makeText(getApplicationContext(), "Fallo la conexion con el host", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            });

            //accion negativa del alertDialog
            builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });

            //mostrar alertDialog
            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            Toast.makeText(getApplicationContext(), "Seleccionar una orden", Toast.LENGTH_LONG).show();
        }
    }


    //evento click para el boton eliminar orden
    @OnClick(R.id.btnEliminarOrden)
    void eliminarOrden(View view) {

        //obtener codigo de orden desde el recyclerView
        final String codigoOrdenRecibido = codigoOrdenPasado;

        //Determinar si selecciono algun codigo de orden
        if (codigoOrdenRecibido != null) {
            //iniciar alertDialog con tal de preguntar si se desea eliminar orden
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("¿ Deseas eliminar la orden[" + codigoOrdenRecibido + "] ?");
            builder.setTitle("Eliminado");
            //accion positiva para alertDialog
            builder.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //eliminar

                    //iniciar barra de progreso
                    progress.setMessage("Eliminando orden...");
                    progress.show();

                    //iniciar consulta
                    Gson gson = new GsonBuilder().setLenient().create();

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(URL)
                            .addConverterFactory(GsonConverterFactory.create(gson))
                            .build();

                    RegisterAPI api = retrofit.create(RegisterAPI.class);
                    Call<valueMensaje> call = api.eliminarOrden(codigoOrdenRecibido);
                    //ejecutar consulta
                    call.enqueue(new Callback<valueMensaje>() {
                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                        @Override
                        public void onResponse(Call<valueMensaje> call, Response<valueMensaje> response) {
                            progress.dismiss();

                            //obtener valores de respuesta a la consulta
                            String value = response.body().getValue();
                            String message = response.body().getMessage();
                            //si la respuesta del value es 1 significa que la consulta se realizo correctamente
                            if(value.equals("1")) {
                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                                //actualizar datos
                                //obtenerOrdenes();

                                //eliminar orden del recyclerView
                                int posicionRecibido = posicionPasado;
                                RecyclerView.ViewHolder viewHolder = listaOrdenes.findViewHolderForAdapterPosition(posicionRecibido);
                                adaptador.animateCircularDelete(viewHolder.itemView,posicionRecibido);

                                //dejar variable null con tal de que no se pueda hacer el mismo proceso con el mismo codigo ya eliminado
                                codigoOrdenPasado = null;
                            }else {
                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                            }

                        }

                        //en caso de fallar mostrar mensaje por pantalla
                        @Override
                        public void onFailure(Call<valueMensaje> call, Throwable t) {
                            progress.dismiss();
                            Toast.makeText(getApplicationContext(), "Fallo la conexion con el host", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            });

            //accion negativa del alertDialog
            builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });

            //mmostrar alertDialog
            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            Toast.makeText(getApplicationContext(), "Seleccionar una orden", Toast.LENGTH_LONG).show();
        }
    }

    //main
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestionar_ordenes);
        ButterKnife.bind(this);
        //actionbar de la actividad
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //configurar layoutmanager de lista de ordenes
        LinearLayoutManager lim = new LinearLayoutManager(this);
        lim.setOrientation(LinearLayoutManager.VERTICAL);
        listaOrdenes.setLayoutManager(lim);


        //cargar datos
        obtenerOrdenes();
        llenarPersonal();

    }


    //metodo para obtener todas las ordenes del sistema que no han sido asignadas a ningun personal
    public void obtenerOrdenes() {
        //iniciar barra de progreso
        progress = new ProgressDialog(this);
        progress.setCancelable(false);
        progress.setMessage("Cargando ordenes de trabajo...");
        progress.show();

        //iniciar consulta
        Gson gson = new GsonBuilder().setLenient().create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        RegisterAPI api = retrofit.create(RegisterAPI.class);
        Call<GestionOrdenes> call = api.verGestionOrdenes();
        //ejecutar consulta
        call.enqueue(new Callback<GestionOrdenes>() {
            @Override
            public void onResponse(Call<GestionOrdenes> call, Response<GestionOrdenes> response) {
                progress.dismiss();
                //obtener datos de respuesta de la consulta
                int value = response.body().getValue();

                //si el value es 1 quiere decir que la consulta se realizo con exito
                if (value == 1) {
                    ordenes = response.body().getResultGestionOrdenes();
                    //iniciar adaptador del recyclerView para poder mostrar datos por pantalla
                    inicializarAdaptador();
                }

            }

            //en caso de fallar mostrar mensaje por pantalla
            @Override
            public void onFailure(Call<GestionOrdenes> call, Throwable t) {
                progress.dismiss();
                Toast.makeText(getApplicationContext(), "Hubo un problema con el host :" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }


    //inicializar adaptador
    public void inicializarAdaptador() {
        adaptador = new RVAGestionOrdenes(ordenes);
        listaOrdenes.setAdapter(adaptador);
    }


    //metodo que llena spinner con todos los personales del sistema
    public void llenarPersonal() {
        //iniciar barra de progreso
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
                //si el valor obtenido por la respuesta de la consulta es 1
                //indica que la consulta se realizo correctamente
                if (response.body().getValuePersonal() == 1) {
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


                }
            }

            //en caso de fallar mostrar mensaje por pantalla
            @Override
            public void onFailure(Call<Personal> call, Throwable t) {
                progress.dismiss();
                Toast.makeText(getApplicationContext(), "Fallo la conexion a internet", Toast.LENGTH_SHORT).show();

            }
        });


    }
}
