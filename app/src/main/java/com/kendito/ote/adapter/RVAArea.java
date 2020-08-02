package com.kendito.ote.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kendito.ote.R;
import com.kendito.ote.api.RegisterAPI;
import com.kendito.ote.informacion.InfoArea;
import com.kendito.ote.model.ResultArea;
import com.kendito.ote.model.valueMensaje;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.kendito.ote.Organizador.Area.gestionarAreaActivity.URL;

/**
 * Created by Kendo on 12-12-2017.
 */

public class RVAArea extends RecyclerView.Adapter<RVAArea.AreaViewHolder>{

    //definir barra de progreso
    private ProgressDialog progress;

    //lista de areas
    private List<ResultArea> areas;

    //variables para pasar datos a travez de clases
    public static String idAreaPasado = null;
    public static String nombreAreaPasado = null;

    //seleccion de item en el recyclerView
    int selected_position = -1;

    //variable para dobleclick
    String dobleClick = null;

    //constructor
    public RVAArea(List<ResultArea> areas) {
        this.areas = areas;
    }

    //crear vista de como se vera en RecyclerView
    @Override
    public AreaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_area,parent,false);
        return new AreaViewHolder(v);
    }

    //metodo que sirve para pasar dato del sitio con respecto a la posicion en la que esta
    @Override
    public void onBindViewHolder(AreaViewHolder areaViewHolder, int position) {
        ResultArea area = areas.get(position);
        areaViewHolder.tvIdArea.setText(area.getIDAREA());
        areaViewHolder.tvNombreArea.setText(area.getNOMBREAREA());

        areaViewHolder.tablaArea.setBackgroundResource(selected_position == position ? R.drawable.cell_shape_seleccionado : R.drawable.cell_shape);
    }

    //metodo que devuelve la cantidad de datos que reciben los sitios
    @Override
    public int getItemCount() {
        return areas.size();
    }


    //metodo que controla transiciones del ViewHolder
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onViewAttachedToWindow(AreaViewHolder holder) {
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
                areas.remove(position);
                notifyItemRemoved(position);
            }
        });
        animation.start();
    }

    //subclase sitioViewHolder que recibe el ViewHolder de la clase madre con tal de recibir el oncreate y el onbind de la misma
    // con tal de saber a que layout redireccionar los datos y en que id depositar las mismas
    public class AreaViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        //variables del  layout
        private TextView tvIdArea;
        private TextView tvNombreArea;
        private Button btnEliminarCliente;
        private TableLayout tablaArea;

        //constructor de la subclase que recibe el super para poder indicar el itemView pertenece a la clase madre
        public AreaViewHolder(final View itemView){
            super(itemView);
            //verbo necesario para poder hacer que funcione el metodo onClick, sin este no funcionaria
            itemView.setOnClickListener(this);
            //llamar variables del layout de vista del recyclerView
            tvIdArea = (TextView)itemView.findViewById(R.id.tvIdArea);
            tvNombreArea = (TextView)itemView.findViewById(R.id.tvNombreArea);
            btnEliminarCliente = (Button)itemView.findViewById(R.id.btnEliminarCliente);
            tablaArea = (TableLayout)itemView.findViewById(R.id.tablaArea);


            btnEliminarCliente.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //obtener posicion del dato seleccionado
                    int position = getAdapterPosition();
                    final ResultArea area = areas.get(position);

                    //iniciar alertDialog para preguntar si se desea eliminar
                    AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
                    builder.setMessage("¿ Deseas eliminar esta area  [" + area.getNOMBREAREA() + "] ?\n" +
                            "[PRECAUCION]Si se elimina esta area tambien se eliminaran los cliente y sitios relacionados a esta.");
                    builder.setTitle("Eliminado");
                    //accion positiva del alertDialog
                    builder.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //iniciar barra de progreso
                            progress = new ProgressDialog(itemView.getContext());
                            progress.setCancelable(false);
                            progress.setMessage("Eliminando area");
                            progress.show();

                            //iniciar consulta
                            Gson gson = new GsonBuilder().setLenient().create();

                            Retrofit retrofit = new Retrofit.Builder()
                                    .baseUrl(URL)
                                    .addConverterFactory(GsonConverterFactory.create(gson))
                                    .build();

                            RegisterAPI api = retrofit.create(RegisterAPI.class);
                            Call<valueMensaje> call = api.eliminarArea(area.getIDAREA());
                            //ejecutar consulta
                            call.enqueue(new Callback<valueMensaje>() {
                                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                                @Override
                                public void onResponse(Call<valueMensaje> call, Response<valueMensaje> response) {
                                    progress.dismiss();
                                    //obtener datos respuesta de la consulta
                                    String value = response.body().getValue();
                                    String message = response.body().getMessage();

                                    //si la respuesta del value es 1 entonces la consulta se realizo correctamente
                                    if(value.equals("1")){
                                        Toast.makeText(itemView.getContext(),message,Toast.LENGTH_SHORT).show();
                                        animateCircularDelete(itemView,getAdapterPosition());
                                    }else{
                                        Toast.makeText(itemView.getContext(),message,Toast.LENGTH_SHORT).show();
                                    }
                                }

                                //en caso de fallar mostrar mensaje por pantalla
                                @Override
                                public void onFailure(Call<valueMensaje> call, Throwable t) {
                                    progress.dismiss();
                                    Toast.makeText(itemView.getContext(),"Hubo un problema con el host",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });

                    builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    //mostrar Alerta para poder eliminar
                    AlertDialog dialog = builder.create();
                    dialog.show();

                }
            });

            //auto seleccionar las variables para poder activar la propiedad 'android:ellipsize="marquee"'
            tvNombreArea.setSelected(true);
        }

        //metodo onClick que permite hacerle click a cualquier dato del recyclerView
        @Override
        public void onClick(View v) {

            //obtener seleccion
            if (getAdapterPosition() == RecyclerView.NO_POSITION) return;
            //notificar seleccion
            notifyItemChanged(selected_position);
            selected_position = getAdapterPosition();
            notifyItemChanged(selected_position);

            //obtener context con tal de obtener acceso a casi la totalidad del sistema android
            //en este caso para poder crear una nueva actividad
            Context context = v.getContext();
            //obtener posicion del dato seleccionado
            int position = getAdapterPosition();
            ResultArea area = areas.get(position);

            //pasar datos para poder llamarlos y utilizarlos desde la actividad anterior
            idAreaPasado = area.getIDAREA();
            nombreAreaPasado = area.getNOMBREAREA();

            if(dobleClick == area.getIDAREA()){
                //crear intent para poder iniciar actividad de informacion
                Intent intent = new Intent(context, InfoArea.class);

                //pasar datos para mostrar
                intent.putExtra("idAreaInfo",area.getIDAREA());
                intent.putExtra("nombreAreaInfo",area.getNOMBREAREA());
                context.startActivity(intent);
            }else{
                Snackbar.make(itemView, Html.fromHtml("<font color=\"#ffffff\">Presiona denuevo para ver detalles</font>"),Snackbar.LENGTH_SHORT).show();
            }

            dobleClick = area.getIDAREA();


        }
    }

}
