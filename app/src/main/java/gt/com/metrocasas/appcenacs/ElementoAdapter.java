package gt.com.metrocasas.appcenacs;

import android.app.Activity;
import android.graphics.Paint;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.List;

public class ElementoAdapter extends RecyclerView.Adapter<ElementoAdapter.MyViewHolder> {

    private List<Elemento> listElemento;
    private Activity actividad;

    public void setTam(int tam) {
        this.tam = tam;
    }

    private int tam = 0;

    public ElementoAdapter(List<Elemento> listElemento,Activity ac) {
        this.listElemento = listElemento;
        this.actividad = ac;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public CheckBox estado;
        public TextView elemento;
        public Button agregar_comentario;
        public Elemento e;

        public MyViewHolder(View view,Elemento it) {
            super(view);

            estado = (CheckBox) view.findViewById(R.id.element_checkBox);
            elemento = (TextView) view.findViewById(R.id.element_revision);
            agregar_comentario = (Button) view.findViewById(R.id.element_btn);
            this.e  = it;

            estado.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    if(isChecked){
                        agregar_comentario.setEnabled(false);
                        elemento.setPaintFlags(elemento.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        e.setEstado(true);
                    }else{
                        elemento.setPaintFlags(elemento.getPaintFlags() &~Paint.STRIKE_THRU_TEXT_FLAG);
                        agregar_comentario.setEnabled(true);
                        e.setEstado(false);
                    }
                }
            });

            agregar_comentario.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    comentarioDialog comentario = new comentarioDialog();
                    comentario.setElement(e);
                    comentario.setBtn_coment(agregar_comentario);
                    comentario.show(actividad.getFragmentManager(),"");
                }
            });
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_elemento, parent, false);
        Elemento item = listElemento.get(tam);
        tam++;
        return new MyViewHolder(itemView,item);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Elemento item = listElemento.get(position);
        holder.elemento.setText(item.getElemento());
    }

    @Override
    public int getItemCount() {
        return listElemento.size();
    }

    public List<Elemento> getListElemento() {
        return listElemento;
    }

    public void hidenCardViewCenacInterno()
    {
        CardView card_view = (CardView) actividad.findViewById(R.id.card_view_cenac_interno);
        card_view.setVisibility(View.GONE);
    }
    /*
    public void hidenCardViewCenacExterno()
    {
        CardView card_view = (CardView) actividad.findViewById(R.id.card_view_cenac_externo);
        card_view.setVisibility(View.GONE);
    }

    public void hidenCardViewDespensa()
    {
        CardView card_view = (CardView) actividad.findViewById(R.id.card_view_despensa);
        card_view.setVisibility(View.GONE);
    }

    public void hidenCardViewLimpieza()
    {
        CardView card_view = (CardView) actividad.findViewById(R.id.card_view_limpieza);
        card_view.setVisibility(View.GONE);
    }

    public void hidenCardViewConstruccion()
    {
        CardView card_view = (CardView) actividad.findViewById(R.id.card_view_construccion);
        card_view.setVisibility(View.GONE);
    }
    */
}
