package gt.com.metrocasas.appcenacs;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class RevisionAdapter extends RecyclerView.Adapter<RevisionAdapter.MyViewHolder>{
    private List<ItemRevision> listRevision;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
        }
    }

    public RevisionAdapter(List<ItemRevision> listRevision) {
        this.listRevision = listRevision;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_revision, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ItemRevision item = listRevision.get(position);
        holder.title.setText(item.getDate());
    }

    @Override
    public int getItemCount() {
        return listRevision.size();
    }

}
