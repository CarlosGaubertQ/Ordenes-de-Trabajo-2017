package com.kendito.ote.informacion;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.kendito.ote.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InfoArea extends AppCompatActivity {

    //iniciar variables de la vista
    @BindView(R.id.idAreaInfo)
    protected TextView idAreaInfo;
    @BindView(R.id.nombreAreaInfo)
    protected TextView nombreAreaInfo;


    //main
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_area);
        ButterKnife.bind(this);
        //obtener variables de la actividad anterior (RecyclerView) e incorporarlas a sus respectivos EditText
        String txtidAreaInfo = getIntent().getExtras().getString("idAreaInfo");
        String txtnombreAreaInfo = getIntent().getExtras().getString("nombreAreaInfo");

        idAreaInfo.setText(txtidAreaInfo);
        nombreAreaInfo.setText(txtnombreAreaInfo);


    }
}
