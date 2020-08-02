package com.kendito.ote.Organizador.Sitio;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kendito.ote.R;
import com.kendito.ote.adapter.RVASitio;
import com.kendito.ote.api.RegisterAPI;
import com.kendito.ote.model.ResultSitioG;
import com.kendito.ote.model.SitioG;
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

import static com.kendito.ote.adapter.RVASitio.idSitioPasado;
import static com.kendito.ote.adapter.RVASitio.nombreSitioPasado;

public class gestionarSitioActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{

    //direccion de webservices y barra de progreso
    public static final String URL = "http://kenditobd.ddns.net/ote/";
    private ProgressDialog progress;

    //adaptador RecyclerView de sitios y
    private RVASitio adaptador;
    List<ResultSitioG> sitios;

    //iniciar variables de la vista
    @BindView(R.id.btnSitioAgregar)
    protected Button btnSitioAgregar;
    @BindView(R.id.recyclerViewGestionarSitio)
    protected RecyclerView listaSitio;


    //iniciar actividad de agregar sitio
    @OnClick(R.id.btnSitioAgregar)void ingresarAgregarSitio(View view) {
        Intent intent = new Intent(this, agregarSitioActivity.class);
        startActivity(intent);
    }


    //main
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestionar_sitio);
        ButterKnife.bind(this);



        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //configurar layoutmanager de lista de sitio
        LinearLayoutManager lim = new LinearLayoutManager(this);
        lim.setOrientation(LinearLayoutManager.VERTICAL);
        listaSitio.setLayoutManager(lim);


        obtenerSitios();
    }





    public void obtenerSitios() {
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
        Call<SitioG> call = api.verSitiosG();

        //ejecutar consulta
        call.enqueue(new Callback<SitioG>() {
            @Override
            public void onResponse(Call<SitioG> call, Response<SitioG> response) {
                progress.dismiss();
                //"1" significa que la consulta se realizo correctamente
                int value = response.body().getValue();
                if(value == 1){
                    //obtener datos de los sitios e inicializar adaptador para mostrarlos en el recyclerView
                    sitios = response.body().getResultSitioG();
                    inicializarAdaptador();
                }
            }

            @Override
            public void onFailure(Call<SitioG> call, Throwable t) {
                progress.dismiss();
                Toast.makeText(getApplicationContext(),"Hubo un problema con el host",Toast.LENGTH_SHORT).show();
            }
        });


    }


    public void inicializarAdaptador() {
        adaptador = new RVASitio(sitios);
        listaSitio.setAdapter(adaptador);
    }


    //configurar  menu search para poder mostrarlo en la actividad
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        //obtener la vista del menu
        getMenuInflater().inflate(R.menu.menu_search,menu);
        //obtener item de la actividad de menu
        final MenuItem item = menu.findItem(R.id.action_search);
        //obtener searchview del menu item
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setQueryHint("Buscar por nombre o area");
        searchView.setIconified(true);
        searchView.setOnQueryTextListener(this);

        return true;
    }


    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }


    //cambiar datos del recyclerview al cambiar texto del searchView y asi discriminar sitios por nombre del sitio y area
    @Override
    public boolean onQueryTextChange(String sitioB) {

        //no mostrar recyclerView mientras se ejecuta el cambio
        listaSitio.setVisibility(View.GONE);

        //iniciar consulta
        Gson gson = new GsonBuilder().setLenient().create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        RegisterAPI api = retrofit.create(RegisterAPI.class);
        Call<SitioG> call = api.verSitioE(sitioB);
        //ejecutar consulta
        call.enqueue(new Callback<SitioG>() {
            @Override
            public void onResponse(Call<SitioG> call, Response<SitioG> response) {

                int value = response.body().getValue();
                listaSitio.setVisibility(View.VISIBLE);
                //si el value es 1 quiere decir que la consulta fue exitosa
                if(value == 1){
                    sitios = response.body().getResultSitioG();
                    inicializarAdaptador();
                }
            }

            @Override
            public void onFailure(Call<SitioG> call, Throwable t) {
                //si falla la consulta mostrar mensaje por pantalla
                listaSitio.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(),"Fallo conexion a internet",Toast.LENGTH_LONG).show();
            }
        });

        return false;

    }
}