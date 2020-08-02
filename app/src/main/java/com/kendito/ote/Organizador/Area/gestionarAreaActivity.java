package com.kendito.ote.Organizador.Area;

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
import com.kendito.ote.adapter.RVAArea;
import com.kendito.ote.api.RegisterAPI;
import com.kendito.ote.model.Area;
import com.kendito.ote.model.ResultArea;
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

import static com.kendito.ote.adapter.RVAArea.idAreaPasado;
import static com.kendito.ote.adapter.RVAArea.nombreAreaPasado;

public class gestionarAreaActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    //URL de servicio web y barra de progreso
    public static final String URL = "http://kenditobd.ddns.net/ote/";
    private ProgressDialog progress;

    //lista de areas para mostrar en recyclerView
    List<ResultArea> areas;

    //adaptador para recycleryView
    private RVAArea adaptador;


    //iniciar variables de la vista
    @BindView(R.id.btnAreaAgregar)
    protected Button btnAreaAgregar;
    @BindView(R.id.recyclerViewGestionarArea)
    protected RecyclerView listaArea;


    //evento click para el boton de ingresar areas
    @OnClick(R.id.btnAreaAgregar)void ingresarAgregarArea(View view){
        //ingresar a la actividad de agregar areas
        Intent intent = new Intent(this,agregarAreaActivity.class);
        startActivity(intent);
    }


    //main
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestionar_area);
        ButterKnife.bind(this);

        //instanciar actionbar de la actividad
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //configurar layoutmanager de lista de areas
        LinearLayoutManager lim = new LinearLayoutManager(this);
        lim.setOrientation(LinearLayoutManager.VERTICAL);
        listaArea.setLayoutManager(lim);

        //cargar areas
        obtenerAreas();
    }


    //metodo carga areas en el recyclerView
    public void obtenerAreas() {

        //iniciar barra de progreso
        progress = new ProgressDialog(this);
        progress.setCancelable(false);
        progress.setMessage("Cargando datos de areas...");
        progress.show();


        //iniciar consulta
        Gson gson = new GsonBuilder().setLenient().create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        RegisterAPI api = retrofit.create(RegisterAPI.class);
        Call<Area> call = api.verArea();

        //ejecutar consulta
        call.enqueue(new Callback<Area>() {
            @Override
            public void onResponse(Call<Area> call, Response<Area> response) {
                progress.dismiss();
                //obtener respuesta de la consulta
                int value = response.body().getValueArea();

                //si el valor de la respuesta es 1 entonces la consuta se realizo correctamente
                if(value == 1){
                    //cargar areas con datos de la consulta y cargar datos al recyclerView
                    areas = response.body().getResultArea();
                    inicializarAdaptador();
                }

            }

            @Override
            public void onFailure(Call<Area> call, Throwable t) {
                progress.dismiss();
                Toast.makeText(getApplicationContext(),"Hubo un problema con el host :" + t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    //metodo que inicializa el adaptador con tal de mostrar en el recyclerView las areas del sistema
    public void inicializarAdaptador(){
        adaptador = new RVAArea(areas);
        listaArea.setAdapter(adaptador);
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
        searchView.setQueryHint("Buscar por area");
        searchView.setIconified(true);
        searchView.setOnQueryTextListener(this);
        return true;
    }


    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    //cambiar datos del recyclerview al cambiar texto del searchView y asi discriminar areas por nombres de area
    @Override
    public boolean onQueryTextChange(String areaE) {

        //no mostrar recyclerView mientras se ejecuta el cambio
        listaArea.setVisibility(View.GONE);


        //iniciar consulta
        Gson gson = new GsonBuilder().setLenient().create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        RegisterAPI api = retrofit.create(RegisterAPI.class);
        Call<Area> call = api.verAreaE(areaE);

        //ejecutar consulta
        call.enqueue(new Callback<Area>() {
            @Override
            public void onResponse(Call<Area> call, Response<Area> response) {
                //obtener datos de la consulta
                int value = response.body().getValueArea();
                listaArea.setVisibility(View.VISIBLE);
                //si el valor de respuesta es 1 entonces la consulta se realizo correctamente
                if(value == 1){
                    //obtener areas para poder mostrarlas en el recyclerView
                    areas = response.body().getResultArea();
                    inicializarAdaptador();
                }
            }

            //en caso de fallar mostrar mensaje por pantalla
            @Override
            public void onFailure(Call<Area> call, Throwable t) {
                listaArea.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(),"Fallo conexion a internet",Toast.LENGTH_LONG).show();
            }
        });


        return true;
    }
}
