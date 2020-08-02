package com.kendito.ote.Organizador;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.kendito.ote.Organizador.Area.gestionarAreaActivity;
import com.kendito.ote.Organizador.Cliente.gestionarClienteActivity;
import com.kendito.ote.Organizador.Ordenes.GestionarOrdenes;
import com.kendito.ote.Organizador.Personal.gestionarPersonalActivity;
import com.kendito.ote.Organizador.Sitio.gestionarSitioActivity;
import com.kendito.ote.Personal_Organizador.VerOrdenesPersonal;
import com.kendito.ote.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class menuOrganizadorActivity extends AppCompatActivity {

    //iniciar variables de la vista
    @BindView(R.id.btnAGestionarPersonal)
    protected Button btnAGestionarPersonal;
    @BindView(R.id.btnAGestionarClientes)
    protected Button btnAGestionarClientes;
    @BindView(R.id.btnAGestionarAreas)
    protected Button btnAGestionarAreas;
    @BindView(R.id.btnAGestionarSitios)
    protected Button btnAGestionarSitios;
    @BindView(R.id.btnAGestionarOrdenes)
    protected Button btnAGestionarOrdenes;
    @BindView(R.id.btnAVerOrdenes)
    protected Button btnAVerOrdenes;

    //boton para ingresar a actividad "ver ordenes"
    @OnClick(R.id.btnAVerOrdenes)void ingresarVerOrdenes(View view){
        Intent intent = new Intent(this, VerOrdenesPersonal.class);
        startActivity(intent);
    }


    //boton para ingresar a actividad "Gestionar Ordenes"
    @OnClick(R.id.btnAGestionarOrdenes)void ingresarGestionarOrdenes(View view){
        Intent intent = new Intent(this, GestionarOrdenes.class);
        startActivity(intent);
    }

    //boton para ingresar a actividad "Gestionar personal"
    @OnClick(R.id.btnAGestionarPersonal)void ingresarGestionarPersonal(View v){
        Intent intent = new Intent(this,gestionarPersonalActivity.class);
        startActivity(intent);
    }

    //boton para ingresar a actividad "Gestionar clientes"
    @OnClick(R.id.btnAGestionarClientes)void ingresarGestionarCliente(View view){
        Intent intent = new Intent(this,gestionarClienteActivity.class);
        startActivity(intent);
    }


    //boton para ingresar a actividad "Gestionar area"
    @OnClick(R.id.btnAGestionarAreas)void ingresarGestionarArea(View view){
        Intent intent = new Intent(this, gestionarAreaActivity.class);
        startActivity(intent);
    }

    //boton para ingresar a actividad "Gestionar sitio"
    @OnClick(R.id.btnAGestionarSitios)void ingresarGestionarSitio(View view){
        Intent intent = new Intent(this,gestionarSitioActivity.class);
        startActivity(intent);
    }

    //main
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_administrador);
        ButterKnife.bind(this);
        
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

    }



}