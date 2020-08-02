package com.kendito.ote.verMisOrdenes;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kendito.ote.R;
import com.kendito.ote.adapter.RVAGestionOrdenes;
import com.kendito.ote.api.RegisterAPI;
import com.kendito.ote.model.GestionOrdenes;
import com.kendito.ote.model.ResultGestionOrdenes;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.kendito.ote.loginActivity.rutLoginPasado;


public class VerMisOrdenesFragment extends Fragment {


    //direccion del web service
    public static final String URL = "http://kenditobd.ddns.net/ote/";
    //barra de progreso
    private ProgressDialog progress;

    //variables globales para mostrar ordenes
    static private List<ResultGestionOrdenes> ordenes;
    static private RVAGestionOrdenes adaptador;


    //iniciar variables de la vista
    @BindView(R.id.recyclerViewVerOrdenesF)
    protected RecyclerView listaOrdenesPP;
    @BindView(R.id.txtTitulo)
    protected TextView txtTitulo;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_ver_mis_ordenes, container, false);
        ButterKnife.bind(this,view);

        //configurar layoutmanager de lista de ordenes
        LinearLayoutManager lim = new LinearLayoutManager(getContext());
        lim.setOrientation(LinearLayoutManager.VERTICAL);
        listaOrdenesPP.setLayoutManager(lim);

        obtenerOrdenes();

        return view;
    }


    //metodo obtenerOrdenes que obtiene ordenes y las despliega por pantalla
    public void obtenerOrdenes() {
        //obtener rut ingresado al sistema
        String rutRecibido = rutLoginPasado;

        //iniciar barra de progreso



        //iniciar consulta
        Gson gson = new GsonBuilder().setLenient().create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        RegisterAPI api = retrofit.create(RegisterAPI.class);
        Call<GestionOrdenes> call = api.verOrdenesEG(rutRecibido);
        //ejecutar consultar
        call.enqueue(new Callback<GestionOrdenes>() {
            @Override
            public void onResponse(Call<GestionOrdenes> call, Response<GestionOrdenes> response) {

                try{
                    int value = response.body().getValue();

                    if(value == 1){
                        ordenes = response.body().getResultGestionOrdenes();

                        inicializarAdaptador();
                    }
                }catch (Exception e){
                    Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                }

            }

            @SuppressLint("ResourceAsColor")
            @Override
            public void onFailure(Call<GestionOrdenes> call, Throwable t) {
                Snackbar.make(getView(), Html.fromHtml("<font color=\"#ffffff\">Fallo</font>"),Snackbar.LENGTH_SHORT).show();
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
