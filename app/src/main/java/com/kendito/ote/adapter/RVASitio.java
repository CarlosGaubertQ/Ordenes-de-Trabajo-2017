package com.kendito.ote.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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
import com.kendito.ote.informacion.InfoSitio;
import com.kendito.ote.model.ResultSitioG;
import com.kendito.ote.model.valueMensaje;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.kendito.ote.Organizador.Sitio.gestionarSitioActivity.URL;

/**
 * Created by Kendo on 11-12-2017.
 */


public class RVASitio extends RecyclerView.Adapter<RVASitio.SitiosViewHolder> {

    //definir barra de progreso
    private ProgressDialog progress;

    //lista de sitios
    private List<ResultSitioG> sitios;

    //variables para pasar datos a travez de clases
    public static String idSitioPasado = null;
    public static String nombreSitioPasado = null;
    public static String nombreAreaPasado = null;

    //seleccion de item en el recyclerView
    int selected_position = -1;

    //variable para dobleclick
    String dobleClick = null;

    //constructor
    public RVASitio(List<ResultSitioG> sitios) {
        this.sitios = sitios;
    }

    //crear vista de como se vera en RecyclerView
    @Override
    public SitiosViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_sitio, parent, false);
        return new SitiosViewHolder(v);
    }

    //metodo que sirve para pasar dato del sitio con respecto a la posicion en la que esta
    @Override
    public void onBindViewHolder(SitiosViewHolder sitiosViewHolder, int position) {
        ResultSitioG sitio = sitios.get(position);

        sitiosViewHolder.tvNombreArea.setText(sitio.getNOMBREAREA());
        sitiosViewHolder.tvNombreSitio.setText(sitio.getNOMBRESITIO());
        sitiosViewHolder.tvIdSitio.setText(sitio.getIDSITIO());

        sitiosViewHolder.tablaSitios.setBackgroundResource(selected_position == position ? R.drawable.cell_shape_seleccionado : R.drawable.cell_shape);
    }

    //metodo que devuelve la cantidad de datos que reciben los sitios
    @Override
    public int getItemCount() {
        return sitios.size();
    }

    //metodo que controla transiciones del ViewHolder
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onViewAttachedToWindow(SitiosViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        animateCircularReveal(holder.itemView);
    }

    //transicion de aparicion
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void animateCircularReveal(View view) {
        int centerX = 0;
        int centerY = 0;
        int startRadius = 0;
        int endRadius = Math.max(view.getWidth(), view.getHeight());
        Animator animation = ViewAnimationUtils.createCircularReveal(view, centerX, centerY, startRadius, endRadius);
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
                sitios.remove(position);
                notifyItemRemoved(position);

            }
        });
        animation.start();
    }


    //subclase sitioViewHolder que recibe el ViewHolder de la clase madre con tal de recibir el oncreate y el onbind de la misma
    // con tal de saber a que layout redireccionar los datos y en que id depositar las mismas
    public class SitiosViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        //variables del  layout
        private TextView tvIdSitio;
        private TextView tvNombreSitio;
        private TextView tvNombreArea;
        private Button btnEliminarSitio;
        private TableLayout tablaSitios;

        //constructor de la subclase que recibe el super para poder indicar el itemView pertenece a la clase madre
        public SitiosViewHolder(final View itemView) {

            super(itemView);
            //verbo necesario para poder hacer que funcione el metodo onClick, sin este no funcionaria
            itemView.setOnClickListener(this);
            //llamar variables del layout de vista del recyclerView
            tvIdSitio = (TextView) itemView.findViewById(R.id.tvIdSitio);
            tvNombreSitio = (TextView) itemView.findViewById(R.id.tvNombreSitio);
            tvNombreArea = (TextView) itemView.findViewById(R.id.tvNombreArea);
            btnEliminarSitio = (Button) itemView.findViewById(R.id.btnEliminarSitio);
            tablaSitios = (TableLayout) itemView.findViewById(R.id.tablaSitios);


            btnEliminarSitio.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onClick(View view) {
                    //animateCircularDelete(itemView,getAdapterPosition());
                    //obtener posicion del dato seleccionado
                    int position = getAdapterPosition();
                    final ResultSitioG sitio = sitios.get(position);

                    //eliminar sitio de la bd
                    //crear alertDialog y preguntar si se desea eliminar
                    AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
                    builder.setMessage("¿ Deseas eliminar este sitio[" + sitio.getNOMBRESITIO() + "] ?\n" +
                            "[PRECAUCION]Si se elimina este sitio se eliminaran tambien los cliente relacionados a este.");
                    builder.setTitle("Eliminado");
                    //action de boton positivo en el alertDialog

                    builder.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //preparar consulta para eliminar sitio
                            progress = new ProgressDialog(itemView.getContext());
                            progress.setCancelable(false);
                            progress.setMessage("Eliminando sitio");
                            progress.show();

                            Gson gson = new GsonBuilder().setLenient().create();

                            Retrofit retrofit = new Retrofit.Builder()
                                    .baseUrl(URL)
                                    .addConverterFactory(GsonConverterFactory.create(gson))
                                    .build();

                            RegisterAPI api = retrofit.create(RegisterAPI.class);
                            Call<valueMensaje> call = api.eliminarSitio(sitio.getIDSITIO());
                            //ejecutar consulta

                            call.enqueue(new Callback<valueMensaje>() {
                                @Override
                                public void onResponse(Call<valueMensaje> call, Response<valueMensaje> response) {
                                    //obtener datos de la consulta
                                    String value = response.body().getValue();
                                    String message = response.body().getMessage();
                                    progress.dismiss();

                                    //"1" significa que la consulta se realizo correctamente
                                    if (value.equals("1")) {
                                        Toast.makeText(itemView.getContext(), message, Toast.LENGTH_SHORT).show();
                                        //actualizar datos
                                        animateCircularDelete(itemView,getAdapterPosition());
                                    } else {
                                        Toast.makeText(itemView.getContext(), message, Toast.LENGTH_SHORT).show();
                                    }

                                }

                                @Override
                                public void onFailure(Call<valueMensaje> call, Throwable t) {
                                    progress.dismiss();
                                    //en caso de fallar mostrar mensaje por pantalla
                                    Toast.makeText(itemView.getContext(), "Ocurrio un problema al conectar con el host", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    });

                    //action de boton negativo en el alertDialog
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
            tvNombreSitio.setSelected(true);
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
            ResultSitioG sitio = sitios.get(position);

            //pasar datos para poder llamarlos y utilizarlos desde la actividad anterior
            idSitioPasado = sitio.getIDSITIO();
            nombreSitioPasado = sitio.getNOMBRESITIO();
            nombreAreaPasado = sitio.getNOMBREAREA();

            //determinar si se clickeo la misma tarjeta
            if (dobleClick == sitio.getIDSITIO()){
                //crear intent para poder iniciar actividad de informacion

                Intent intent = new Intent(context.getApplicationContext(), InfoSitio.class);

                //pasar datos para mostrar
                intent.putExtra("idSitio", sitio.getIDSITIO());
                intent.putExtra("nombreSitio", sitio.getNOMBRESITIO());
                intent.putExtra("nombreArea", sitio.getNOMBREAREA());
                context.startActivity(intent);

            }else{

                Snackbar.make(itemView, Html.fromHtml("<font color=\"#ffffff\">Presiona denuevo para ver detalles</font>"),Snackbar.LENGTH_SHORT).show();
            }

            dobleClick = sitio.getIDSITIO();



        }


    }
}
