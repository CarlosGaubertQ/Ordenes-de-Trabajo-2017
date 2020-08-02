package com.kendito.ote.Organizador.Cliente;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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
import com.kendito.ote.adapter.RVACliente;
import com.kendito.ote.api.RegisterAPI;
import com.kendito.ote.model.Cliente;
import com.kendito.ote.model.ResultCliente;
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

import static com.kendito.ote.adapter.RVACliente.contrasenaClientePasado;
import static com.kendito.ote.adapter.RVACliente.nombreClientePasado;
import static com.kendito.ote.adapter.RVACliente.rutClientePasado;
import static com.kendito.ote.adapter.RVACliente.sitioClientePasado;

public class gestionarClienteActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    //URL de servicio web y barra de progreso
    public static final String URL = "http://kenditobd.ddns.net/ote/";
    private ProgressDialog progress;

    //lista de clientes para mostrar en recyclerView
    List<ResultCliente> clientes;

    //adaptador para recyclerView
    private RVACliente adaptador;


    //iniciar variables de la vista
    @BindView(R.id.btnClienteAgregar)
    protected Button btnClienteAgregar;
    @BindView(R.id.btnClienteModificar)
    protected Button btnClienteModificar;
    @BindView(R.id.recyclerViewGestionarCliente)
    protected RecyclerView listaCliente;




    //evento click del boton agregar cliente
    @OnClick(R.id.btnClienteAgregar)void AgregarCliente(View view){
        Intent intent = new Intent(this,agregarClienteActivity.class);
        startActivity(intent);
    }

    //evento click del boton modificar cliente
    @OnClick(R.id.btnClienteModificar)void ModificarCliente(View view){
        //verificar si se selecciono algun cliente
        if(rutClientePasado == null){
            Toast.makeText(this,"Selecciona un cliente",Toast.LENGTH_SHORT).show();
        }else{
            //obtener valores del recyclerView
            String rutClienteRecibido = rutClientePasado;
            String nombreClienteRecibido = nombreClientePasado;
            String contrasenaClienteRecibido = contrasenaClientePasado;
            String idSiticioCliente = sitioClientePasado;

            //crear intent pasando parametros del cliente para poder modificarlo
            Intent intent = new Intent(this,modificarClienteActivity.class);
            intent.putExtra("Rutcliente",rutClienteRecibido);
            intent.putExtra("nombreCliente",nombreClienteRecibido);
            intent.putExtra("contrasenaCliente",contrasenaClienteRecibido);
            intent.putExtra("idSitio",idSiticioCliente);
            startActivity(intent);
        }


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestionar_cliente);
        //obtener actionbar de la actividad
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);

        //configurar layoutmanager de lista de clientes
        LinearLayoutManager lim = new LinearLayoutManager(this);
        lim.setOrientation(LinearLayoutManager.VERTICAL);
        listaCliente.setLayoutManager(lim);

        //cargar clientes
        obtenerCliente();

    }


    //metodo que muestra cliente en recyclerView
    public void obtenerCliente(){
        progress = new ProgressDialog(this);
        progress.setCancelable(false);
        progress.setMessage("Cargando datos de clientes...");
        progress.show();

        //iniciar consulta
        Gson gson = new GsonBuilder().setLenient().create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        RegisterAPI api = retrofit.create(RegisterAPI.class);
        Call<Cliente> call = api.verCliente();

        //ejecutar consulta
        call.enqueue(new Callback<Cliente>() {
            @Override
            public void onResponse(Call<Cliente> call, Response<Cliente> response) {
                progress.dismiss();
                //obtener valores de la consulta
                int value = response.body().getValueCliente();
                clientes = response.body().getResultCliente();
                //si el value es 1 significa que la consulta se realizo correctamente
                if(value == 1){
                    inicializarAdaptador();
                }
            }

            //en caso de fallar mandar un mensaje por pantalla
            @Override
            public void onFailure(Call<Cliente> call, Throwable t) {
                progress.dismiss();
                Toast.makeText(getApplicationContext(),"Algo ocurrio mientras se cargaban datos",Toast.LENGTH_SHORT).show();
            }
        });

    }


    //metodo que inicializa el adaptador con tal de mostrar en el recyclerView los clientes del sistema
    public void inicializarAdaptador(){
        adaptador = new RVACliente(clientes);
        listaCliente.setAdapter(adaptador);
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



    //cambiar datos del recyclerview al cambiar texto del searchView y asi discriminar clientes por nombre del cliente,nombre del sitio y area
    @Override
    public boolean onQueryTextChange(String s) {

        //no mostrar recyclerView mientras se ejecuta el cambio
        listaCliente.setVisibility(View.GONE);

        //iniciar consulta
        Gson gson = new GsonBuilder().setLenient().create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        RegisterAPI api = retrofit.create(RegisterAPI.class);
        Call<Cliente> call = api.verClienteE(s);
        //ejecutar consulta
        call.enqueue(new Callback<Cliente>() {
            @Override
            public void onResponse(Call<Cliente> call, Response<Cliente> response) {
                //mostrar recyclerView
                listaCliente.setVisibility(View.VISIBLE);
                //obtener datos de la consulta
                int value = response.body().getValueCliente();
                clientes = response.body().getResultCliente();
                //si el value es igual a 1 entonces la consulta sali correctamente
                if(value == 1){
                    inicializarAdaptador();
                }
            }

            //en caso de fallar mostrar mensaje por pantala
            @Override
            public void onFailure(Call<Cliente> call, Throwable t) {
                listaCliente.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(),"Fallo conexion a internet",Toast.LENGTH_LONG).show();
            }
        });


        return true;
    }
}
