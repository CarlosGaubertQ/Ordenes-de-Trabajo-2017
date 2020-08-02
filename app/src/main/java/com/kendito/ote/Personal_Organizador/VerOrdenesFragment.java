package com.kendito.ote.Personal_Organizador;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kendito.ote.R;
import com.kendito.ote.adapter.RVAGestionOrdenes;
import com.kendito.ote.api.RegisterAPI;
import com.kendito.ote.model.GestionOrdenes;
import com.kendito.ote.model.ResultGestionOrdenes;
import com.kendito.ote.model.valueMensaje;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.kendito.ote.Personal_Organizador.VerOrdenesPersonal.verMisOrdenesFragment;
import static com.kendito.ote.adapter.RVAGestionOrdenes.codigoOrdenPasado;
import static com.kendito.ote.adapter.RVAGestionOrdenes.ordenAceptadaPasado;
import static com.kendito.ote.adapter.RVAGestionOrdenes.posicionPasado;
import static com.kendito.ote.loginActivity.rutLoginPasado;


public class VerOrdenesFragment extends Fragment {


    //direccion del web service
    public static final String URL = "http://kenditobd.ddns.net/ote/";
    //barra de progreso
    private ProgressDialog progress;

    //variables globales para mostrar ordenes
    List<ResultGestionOrdenes> ordenes;
    private RVAGestionOrdenes adaptador;


    //iniciar variables de la vista
    @BindView(R.id.recyclerViewVerOrdenes)
    protected RecyclerView listaOrdenesPP;
    @BindView(R.id.btnAceptarOrden)
    protected Button btnAceptarOrden;
    @BindView(R.id.btnVerificarOrdenTerminada)
    protected Button btnVerificarOrdenTerminada;

    //main
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_ver_ordenes, container, false);

        //inicializar butterknife
        ButterKnife.bind(this,view);

        //configurar layoutmanager de lista de ordenes
        LinearLayoutManager lim = new LinearLayoutManager(getContext());
        lim.setOrientation(LinearLayoutManager.VERTICAL);
        listaOrdenesPP.setLayoutManager(lim);

        //obtener datos de ordenes del personal ingresado y mostrarlos por pantalla
        obtenerOrdenes();

        return view;
    }

    //evento onclick de boton verificar orden como terminada
    @OnClick(R.id.btnVerificarOrdenTerminada)
    void finalizarOrden(View view) {
        //obtener codigo de orden seleccionado
        final String codigoOrdenRecibido = codigoOrdenPasado;
        final String ordenAceptadaRecibido = ordenAceptadaPasado;


        //verificar si se selecciono alguna orden
        if (codigoOrdenRecibido != null) {

            //verificar si se acepto la orden
            if (ordenAceptadaRecibido.equals("1")) {


                //crear alertDialog para poder preguntar si finalizar orden
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("¿ Deseas finalizar la orden[" + codigoOrdenRecibido + "] ?");
                builder.setTitle("Orden de trabajo");
                builder.setPositiveButton("Finalizar orden", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //iniciar barra de progrso
                        progress.setMessage("Finalizando orden...");
                        progress.show();

                        //iniciar consulta
                        Gson gson = new GsonBuilder().setLenient().create();

                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl(URL)
                                .addConverterFactory(GsonConverterFactory.create(gson))
                                .build();

                        RegisterAPI api = retrofit.create(RegisterAPI.class);
                        Call<valueMensaje> call = api.terminarOrden(codigoOrdenRecibido);
                        //ejecutar consulta
                        call.enqueue(new Callback<valueMensaje>() {
                            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                            @Override
                            public void onResponse(Call<valueMensaje> call, Response<valueMensaje> response) {
                                progress.dismiss();
                                String value = response.body().getValue();
                                String message = response.body().getMessage();

                                //"1" significa que la consulta se realizo correctamente
                                if (value.equals("1")) {
                                    //refrescar datos actualizados
                                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                                    //obtenerOrdenes();
                                    int posicionRecibido = posicionPasado;
                                    RecyclerView.ViewHolder viewHolder = listaOrdenesPP.findViewHolderForAdapterPosition(posicionRecibido);

                                    adaptador.animateCircularDelete(viewHolder.itemView,posicionRecibido);
                                    codigoOrdenPasado = null;
                                    verMisOrdenesFragment.obtenerOrdenes();
                                } else {
                                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                                }
                            }

                            //en caso de fallar la consulta arrojar mensaje
                            @Override
                            public void onFailure(Call<valueMensaje> call, Throwable t) {
                                progress.dismiss();
                                Toast.makeText(getContext(), "Fallo la conexion con el host", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });

                //mostrar alertDialog
                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();

            }else{
                Toast.makeText(getContext(),"Esta orden no se ha aceptado",Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(getContext(), "Seleccionar una orden", Toast.LENGTH_LONG).show();
        }
    }

    //evento onclick para boton aceptar orden
    @OnClick(R.id.btnAceptarOrden)
    void aceptarOrden(View view) {

        //obtener fecha de hoy
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//dd/MM/yyyy
        Date now = new Date();
        final String strDate = sdfDate.format(now);


        //obtener codigo de orden seleccionado
        final String codigoOrdenRecibido = codigoOrdenPasado;

        //verificar si se selecciono alguna orden
        if (codigoOrdenRecibido != null) {
            //crear alertDialog para poder preguntar si se desea aceptar orden
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage("¿ Deseas aceptar la orden[" + codigoOrdenRecibido + "] ?");
            builder.setTitle("Orden de trabajo");
            builder.setPositiveButton("Aceptar orden", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //iniciar barra de progreso
                    progress.setMessage("Aceptando orden...");
                    progress.show();


                    //iniciar consulta
                    Gson gson = new GsonBuilder().setLenient().create();

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(URL)
                            .addConverterFactory(GsonConverterFactory.create(gson))
                            .build();

                    RegisterAPI api = retrofit.create(RegisterAPI.class);
                    Call<valueMensaje> call = api.aceptarOrden(codigoOrdenRecibido,strDate);
                    //ejecutar consulta
                    call.enqueue(new Callback<valueMensaje>() {
                        @Override
                        public void onResponse(Call<valueMensaje> call, Response<valueMensaje> response) {
                            progress.dismiss();
                            String value = response.body().getValue();
                            String message = response.body().getMessage();

                            //"1" significa que la consulta se ejecuto correctamente
                            if (value.equals("1")) {
                                //refrescar datos actuales
                                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                                obtenerOrdenes();
                                codigoOrdenPasado = null;
                                verMisOrdenesFragment.obtenerOrdenes();
                            } else {
                                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                            }
                        }

                        //en caso de fallar mostrar mensaje por pantalla
                        @Override
                        public void onFailure(Call<valueMensaje> call, Throwable t) {
                            progress.dismiss();
                            Toast.makeText(getContext(), "Fallo la conexion con el host", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

            //mostrar alertDialog
            builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            Toast.makeText(getContext(), "Seleccionar una orden", Toast.LENGTH_LONG).show();
        }


    }




    //metodo obtenerOrdenes que obtiene ordenes y las despliega por pantalla
    public void obtenerOrdenes() {
        //obtener rut ingresado al sistema
        String rutRecibido = rutLoginPasado;

        //iniciar barra de progreso
        progress = new ProgressDialog(getContext());
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
        Call<GestionOrdenes> call = api.verOrdenesE(rutRecibido);
        //ejecutar consultar
        call.enqueue(new Callback<GestionOrdenes>() {
            @Override
            public void onResponse(Call<GestionOrdenes> call, Response<GestionOrdenes> response) {
                progress.dismiss();
                int value = response.body().getValue();

                //"1" significa que la consulta se ejecuto correctamente
                if (value == 1) {
                    ordenes = response.body().getResultGestionOrdenes();
                    //metodo que inicializa el recyclerView y muestra datos por pantalla
                    inicializarAdaptador();
                }
            }

            //en caso de fallar mostrar mensaje por pantalla
            @Override
            public void onFailure(Call<GestionOrdenes> call, Throwable t) {
                progress.dismiss();
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    //metodo inicializarAdaptador que inicializa un adapter con los datos obtenidos de la consulta
    //de ordenes de personal
    public void inicializarAdaptador() {
        adaptador = new RVAGestionOrdenes(ordenes);
        listaOrdenesPP.setAdapter(adaptador);
    }
}
