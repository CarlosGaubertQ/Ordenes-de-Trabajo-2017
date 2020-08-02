package com.kendito.ote.Cliente;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kendito.ote.R;
import com.kendito.ote.api.RegisterAPI;
import com.kendito.ote.model.valueMensaje;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.kendito.ote.Cliente.OrdenesYCliente.verMisOrdenesFragment;


public class SolicitarOrdenFragment extends Fragment {


    //direccion de webservices y barra de progreso
    public static final String URL = "http://kenditobd.ddns.net/ote/";
    private ProgressDialog progress;

    //iniciar variables de la vista
    @BindView(R.id.nombreCliente)
    protected TextView nombreCliente;
    @BindView(R.id.btnSolicitarOrden)
    protected Button btnSolicitarOrden;
    @BindView(R.id.txtOrden)
    protected EditText txtOrden;

    //variables globales
    String rutCliente = null;
    String nombre = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_solicitar_orden, container, false);
        ButterKnife.bind(this,view);
        rutCliente = getActivity().getIntent().getExtras().getString("rut");
        nombre = getActivity().getIntent().getExtras().getString("nombre");
        nombreCliente.setText(nombre);
        return view;

    }

    //evento click del boton de ingresar nueva orden
    @OnClick(R.id.btnSolicitarOrden)void ingresarOrden(View view){

        //obtener fecha de hoy
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//dd/MM/yyyy
        Date now = new Date();
        final String strDate = sdfDate.format(now);



        //verificar espacios en blancos
        if(!TextUtils.isEmpty(txtOrden.getText())){
            //crear alertDialog para preguntar si se esta seguro de pedir orden
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage("¿ Desea pedir esta orden ?");
            builder.setTitle("Orden de trabajo");
            //accion positiva de alertDialog
            builder.setPositiveButton("Pedir orden", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //iniciar barra de progreso
                    progress = new ProgressDialog(getContext());
                    progress.setCancelable(false);
                    progress.setMessage("Ingresando orden...");
                    progress.show();

                    //iniciar consulta
                    Gson gson = new GsonBuilder().setLenient().create();

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(URL)
                            .addConverterFactory(GsonConverterFactory.create(gson))
                            .build();

                    RegisterAPI api = retrofit.create(RegisterAPI.class);
                    Call<valueMensaje> call = api.ingresarSolicitud(rutCliente,strDate,txtOrden.getText().toString());
                    //ejecutar consulta
                    call.enqueue(new Callback<valueMensaje>() {
                        @SuppressLint("ResourceAsColor")
                        @Override
                        public void onResponse(Call<valueMensaje> call, Response<valueMensaje> response) {
                            //obtener datos de la consulta
                            String value = response.body().getValue();
                            progress.dismiss();
                            //si la respuesta ala consulta es 1 entonces se ejecuto correctamente
                            if(value.equals("1")){
                                Toast.makeText(getContext(),response.body().getMessage(),Toast.LENGTH_LONG).show();
                                txtOrden.setText("");
                                verMisOrdenesFragment.obtenerOrdenes();
                                Snackbar.make(getView(), Html.fromHtml("<font color=\"#ffffff\">Revisa nueva orden en la pestaña de Ordenes</font>"),Snackbar.LENGTH_SHORT).show();
                            }else if(value.equals("2")){
                                Toast.makeText(getContext(),response.body().getMessage(),Toast.LENGTH_LONG).show();
                            }else if(value.equals("0")){
                                Toast.makeText(getContext(),response.body().getMessage(),Toast.LENGTH_LONG).show();
                            }
                        }

                        //en caso de fallar mostrar mensaje por pantalla
                        @Override
                        public void onFailure(Call<valueMensaje> call, Throwable t) {
                            progress.dismiss();
                            Toast.makeText(getContext(),"Fallo conexion a internet",Toast.LENGTH_LONG).show();
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

            //mostrar Alerta para poder eliminar
            AlertDialog dialog = builder.create();
            dialog.show();
        }else{
            Toast.makeText(getContext(),"Llenar solicitud para pedir una orden de trabajo",Toast.LENGTH_LONG).show();
        }


    }

}
