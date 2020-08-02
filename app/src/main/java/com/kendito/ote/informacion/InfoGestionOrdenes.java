package com.kendito.ote.informacion;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.kendito.ote.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InfoGestionOrdenes extends AppCompatActivity {


    //iniciar variables de la vista
    @BindView(R.id.idCodigoOrden)
    protected TextView idCodigoOrden;
    @BindView(R.id.rutCliente)
    protected TextView rutCliente;
    @BindView(R.id.nombreCliente)
    protected TextView nombreCliente;
    @BindView(R.id.fechaOrdenPedida)
    protected TextView fechaOrdenPedida;
    @BindView(R.id.nombreSitio)
    protected TextView nombreSitio;
    @BindView(R.id.nombreArea)
    protected TextView nombreArea;
    @BindView(R.id.descripcion)
    protected TextView descripcion;


    //main
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_gestion_ordenes);
        ButterKnife.bind(this);

        //obtener variables de la actividad anterior (RecyclerView) e incorporarlas a sus respectivos EditText
        String codigoOrden = getIntent().getExtras().getString("codigoOrden");
        String rCliente = getIntent().getExtras().getString("rutCliente");
        String nombre = getIntent().getExtras().getString("nombreCliente");
        String fecha = getIntent().getExtras().getString("fechaOrden");
        String sitio = getIntent().getExtras().getString("nombreSitio");
        String area = getIntent().getExtras().getString("nombreArea");
        String descrip = getIntent().getExtras().getString("descripcion");

        idCodigoOrden.setText(codigoOrden);
        rutCliente.setText(rCliente);
        nombreCliente.setText(nombre);
        fechaOrdenPedida.setText(fecha);
        nombreSitio.setText(sitio);
        nombreArea.setText(area);
        descripcion.setText(descrip);

    }
}
