package com.kendito.ote.informacion;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.kendito.ote.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InfoSitio extends AppCompatActivity {


    //iniciar variables de la vista
    @BindView(R.id.idSitioInfo)
    protected TextView idSitioInfo;

    @BindView(R.id.nombreSitioInfo)
    protected TextView nombreSitioInfo;

    @BindView(R.id.nombreAreaInfo)
    protected TextView nombreAreaInfo;


    //main
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_sitio);
        ButterKnife.bind(this);



        //obtener variables de la actividad anterior (RecyclerView) e incorporarlas a sus respectivos EditText
        String txtidSitio = getIntent().getExtras().getString("idSitio");
        String txtnombreSitio = getIntent().getExtras().getString("nombreSitio");
        String txtnombreArea = getIntent().getExtras().getString("nombreArea");

        idSitioInfo.setText(txtidSitio);
        nombreSitioInfo.setText(txtnombreSitio);
        nombreAreaInfo.setText(txtnombreArea);


    }
}
