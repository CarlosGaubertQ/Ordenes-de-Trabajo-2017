package com.kendito.ote.informacion;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.kendito.ote.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InfoOTE extends AppCompatActivity {


    @BindView(R.id.programador)
    protected TextView programador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_ote);
        ButterKnife.bind(this);

        programador.setSelected(true);
    }
}
