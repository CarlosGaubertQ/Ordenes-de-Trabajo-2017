package com.kendito.ote.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kendito.ote.R;
import com.kendito.ote.informacion.InfoGestionOrdenes;
import com.kendito.ote.model.Result;
import com.kendito.ote.model.ResultGestionOrdenes;

import java.util.List;

import static com.kendito.ote.Personal_Organizador.VerOrdenesPersonal.verOrdenesFragment;

/**
 * Created by xykit on 19-01-2018.
 */

public class RVAGestionOrdenes extends RecyclerView.Adapter<RVAGestionOrdenes.OrdenesViewHolder> {

    //lista de ordenes
    private List<ResultGestionOrdenes> ordenes;

    //variables para pasar datos a travez de clases
    public static String codigoOrdenPasado = null;
    public static String ordenAceptadaPasado = null;
    public static int posicionPasado = 0;

    //seleccion de item en el recyclerView
    int selected_position = -1;

    //variable para dobleclick
    String dobleClick = null;

    //constructor
    public RVAGestionOrdenes(List<ResultGestionOrdenes> ordenes) {
        this.ordenes = ordenes;
    }

    //crear vista de como se vera en RecyclerView
    @Override
    public OrdenesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_gestion_ordenes,parent,false);
        return new OrdenesViewHolder(v);
    }

    //metodo que sirve para pasar dato del sitio con respecto a la posicion en la que esta
    @Override
    public void onBindViewHolder(OrdenesViewHolder holder, int position) {
        ResultGestionOrdenes orden = ordenes.get(position);
        holder.tvcodigoOrden.setText(orden.getCODIGOORDEN());
        holder.tvCliente.setText(orden.getNOMBRECLIENTE());
        holder.tvDescripcion.setText(orden.getDESCRIPCIONORDEN());
        holder.tvFechaOrden.setText(orden.getFECHAPEDIDAORDEN());


        holder.tvFechaAOrden.setText(orden.getFECHAACEPTADAORDEN() == null ? "Sin datos" : orden.getFECHAACEPTADAORDEN());


        //revisar estado de orden
        if(orden.getORDENACEPTADA().equals("0")){
            holder.tvEstado.setText("Orden sin aceptar" );
        }else if(orden.getORDENREALIZADA().equals("0") && orden.getORDENACEPTADA().equals("1")){
            holder.tvEstado.setText("Orden en proceso");
        }else{
            holder.tvEstado.setText("Orden terminada");
        }


        //al crear cada dato a mostrar cambiar color de acuerdo al estado de la orden
        if(orden.getORDENACEPTADA().equals("0")){
            holder.tablaOrdenes.setBackgroundResource(selected_position == position ? R.drawable.cell_shape_orden_nada_seleccionado : R.drawable.cell_shape_orden_nada);
        }else if(orden.getORDENREALIZADA().equals("0")){
            holder.tablaOrdenes.setBackgroundResource(selected_position == position ? R.drawable.cell_shape_orden_en_proceso_seleccionado : R.drawable.cell_shape_orden_en_proceso);
        }else{
            holder.tablaOrdenes.setBackgroundResource(selected_position == position ? R.drawable.cell_shape_orden_finalizada_seleccionado : R.drawable.cell_shape_orden_finalizada);
        }

    }



    //metodo que devuelve la cantidad de datos que reciben los sitios
    @Override
    public int getItemCount() {
        return ordenes.size();
    }

    //metodo que controla transiciones del ViewHolder
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onViewAttachedToWindow(OrdenesViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        animateCircularReveal(holder.itemView);
    }

    //transicion de aparicion
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void animateCircularReveal(View view){
        int centerX = 0;
        int centerY = 0;
        int startRadius = 0;
        int endRadius = Math.max(view.getWidth(), view.getHeight());
        Animator animation = ViewAnimationUtils.createCircularReveal(view,centerX,centerY,startRadius,endRadius);
        view.setVisibility(View.VISIBLE);
        animation.start();
    }

    //transicion de eliminacion
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void animateCircularDelete(final View view, final int position) {
        int centerX = view.getWidth();
        int centerY = view.getHeight();
        int startRadius = view.getWidth();
        int endRadius = 0;
        Animator animation = ViewAnimationUtils.createCircularReveal(view, centerX, centerY, startRadius, endRadius);

        animation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                view.setVisibility(View.INVISIBLE);
                ordenes.remove(position);
                notifyItemRemoved(position);
            }
        });
        animation.start();
    }

    //subclase sitioViewHolder que recibe el ViewHolder de la clase madre con tal de recibir el oncreate y el onbind de la misma
    // con tal de saber a que layout redireccionar los datos y en que id depositar las mismas
    public class OrdenesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        //variables del  layout
        private TextView tvcodigoOrden;
        private TextView tvCliente;
        private TextView tvDescripcion;
        private TextView tvFechaOrden;
        private TextView tvFechaAOrden;
        private TextView tvEstado;

        //tabla
        private TableLayout tablaOrdenes;

        //constructor de la subclase que recibe el super para poder indicar el itemView pertenece a la clase madre
        public OrdenesViewHolder(View itemView) {
            super(itemView);
            //verbo necesario para poder hacer que funcione el metodo onClick, sin este no funcionaria
            itemView.setOnClickListener(this);
            //llamar variables del layout de vista del recyclerView
            tvcodigoOrden = (TextView)itemView.findViewById(R.id.tvcodigoOrden);
            tvCliente = (TextView)itemView.findViewById(R.id.tvCliente);
            tvDescripcion = (TextView)itemView.findViewById(R.id.tvDescripcion);
            tvFechaOrden = (TextView)itemView.findViewById(R.id.tvFechaOrden);
            tablaOrdenes = (TableLayout)itemView.findViewById(R.id.tablaOrdenes);
            tvFechaAOrden = (TextView)itemView.findViewById(R.id.tvFechaAOrden);
            tvEstado = (TextView)itemView.findViewById(R.id.tvEstado);

            //auto seleccionar las variables para poder activar la propiedad 'android:ellipsize="marquee"'
            tvCliente.setSelected(true);
            tvFechaOrden.setSelected(true);

        }

        //metodo onClick que permite hacerle click a cualquier dato del recyclerView
        @SuppressLint("ResourceAsColor")
        @Override
        public void onClick(View view) {


            //obtener seleccion
            if (getAdapterPosition() == RecyclerView.NO_POSITION) return;
            //notificar seleccion
            notifyItemChanged(selected_position);
            selected_position = getAdapterPosition();
            notifyItemChanged(selected_position);

            //obtener context con tal de obtener acceso a casi la totalidad del sistema android
            //en este caso para poder crear una nueva actividad
            Context context = view.getContext();
            //obtener posicion del dato seleccionado
            int posicion = getAdapterPosition();
            ResultGestionOrdenes orden = ordenes.get(posicion);

            //pasar datos para poder llamarlos y utilizarlos desde la actividad anterior
            codigoOrdenPasado = orden.getCODIGOORDEN();
            ordenAceptadaPasado = orden.getORDENACEPTADA();

            posicionPasado = posicion;



            if(dobleClick == orden.getCODIGOORDEN()){
                //crear intent para poder iniciar actividad de informacion
                Intent intent = new Intent(context, InfoGestionOrdenes.class);

                //pasar datos para mostrar
                intent.putExtra("codigoOrden",orden.getCODIGOORDEN());
                intent.putExtra("rutCliente",orden.getRUTCLIENTE());
                intent.putExtra("nombreCliente",orden.getNOMBRECLIENTE());
                intent.putExtra("fechaOrden",orden.getFECHAPEDIDAORDEN());
                intent.putExtra("nombreSitio",orden.getNOMBRESITIO());
                intent.putExtra("nombreArea",orden.getNOMBREAREA());
                intent.putExtra("descripcion",orden.getDESCRIPCIONORDEN());
                context.startActivity(intent);
            }else{
                Snackbar.make(itemView, Html.fromHtml("<font color=\"#ffffff\">Presiona denuevo para ver detalles</font>"),Snackbar.LENGTH_SHORT).show();

            }

            dobleClick = orden.getCODIGOORDEN();

        }
    }

}
