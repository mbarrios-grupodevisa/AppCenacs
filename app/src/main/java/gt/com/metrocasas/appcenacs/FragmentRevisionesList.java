package gt.com.metrocasas.appcenacs;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

public class FragmentRevisionesList extends Fragment {

    private static final int ACTIVITY_REQUEST = 1;
    private static final int VIVENTI = 3;
    private static final int CASA_ASUNCION = 4;
    private SwipeRefreshLayout swipeContainer;
    private List<ItemRevision> listRevision = new ArrayList<>();
    FloatingActionButton fab;
    private RevisionAdapter rAdapter;
    private String proyecto, userid;
    LinearLayout p, q;
    ProgressBar progreso;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View partenView = inflater.inflate(R.layout.lista_revisiones, container, false);
        p = (LinearLayout)partenView.findViewById(R.id.layoutprogress);
        q = (LinearLayout)partenView.findViewById(R.id.layoutlist);
        progreso = (ProgressBar)partenView.findViewById(R.id.progressBar);
        progreso.setProgress(0);
        userid = getArguments().getString("id");
        swipeContainer = (SwipeRefreshLayout)partenView.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                listRevision.clear();
                rAdapter.notifyDataSetChanged();
                new GetRevisiones(getActivity(), rAdapter, listRevision, swipeContainer, p, q, progreso).execute(proyecto, userid);
            }
        });
        swipeContainer.setColorSchemeResources(android.R.color.holo_green_light,
                android.R.color.holo_blue_dark,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        RecyclerView recyclerView = (RecyclerView) partenView.findViewById(R.id.recycler_view);
        /*fab = (FloatingActionButton) partenView.findViewById(R.id.btn_nuevo);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(partenView.getContext(), DetalleRevisionActivity.class);
                intent.putExtra("proyecto",proyecto);
                intent.putExtra("id", userid);
                startActivityForResult(intent,ACTIVITY_REQUEST);
            }
        });*/

        rAdapter = new RevisionAdapter(listRevision);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),3));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(rAdapter);
        proyecto = getArguments().getString("proyecto");
        new GetRevisiones(getActivity(), rAdapter, listRevision, swipeContainer, p, q, progreso).execute(proyecto, userid);
        return partenView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == ACTIVITY_REQUEST)
        {
            if(resultCode == VIVENTI)
            {
                listRevision.clear();
                rAdapter.notifyDataSetChanged();
                p.setVisibility(View.VISIBLE);
                q.setVisibility(View.GONE);
                progreso.setProgress(0);
                new GetRevisiones(getActivity(), rAdapter, listRevision, swipeContainer, p, q, progreso).execute(proyecto, userid);
                Snackbar.make(q, "Datos Cargados Correctamente", Snackbar.LENGTH_INDEFINITE)
                        .setActionTextColor(getResources().getColor(R.color.colorAccent))
                        .setAction("ACEPTAR", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                            }
                        })
                        .show();
            }
            if(resultCode == CASA_ASUNCION)
            {
                listRevision.clear();
                rAdapter.notifyDataSetChanged();
                p.setVisibility(View.VISIBLE);
                q.setVisibility(View.GONE);
                progreso.setProgress(0);
                new GetRevisiones(getActivity(), rAdapter, listRevision, swipeContainer, p, q, progreso).execute(proyecto, userid);
                Snackbar.make(q, "Datos Cargados Correctamente", Snackbar.LENGTH_INDEFINITE)
                        .setActionTextColor(getResources().getColor(R.color.colorAccent))
                        .setAction("ACEPTAR", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                            }
                        })
                        .show();
            }
        }
    }
}
