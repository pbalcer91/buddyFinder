package pl.com.wfiis.android.buddyfinder.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import pl.com.wfiis.android.buddyfinder.R;
import pl.com.wfiis.android.buddyfinder.models.Offer;


public class OfferAdapter extends RecyclerView.Adapter<OfferAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<Offer> offerModels;

    public OfferAdapter(Context context, ArrayList<Offer> offerModels) {
        this.context = context;
        this.offerModels = offerModels;
    }

    @NonNull
    @Override
    public OfferAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.offer_row_item, parent, false);

        return new OfferAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OfferAdapter.MyViewHolder holder, int position) {
        holder.offerTitle.setText(offerModels.get(position).getTitle());
        holder.offerAuthor.setText(offerModels.get(position).getAuthor().getUserName());
    }

    @Override
    public int getItemCount() {
        return offerModels.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView offerTitle;
        private TextView offerAuthor;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            offerTitle = itemView.findViewById(R.id.offerListItemTitle);
            offerAuthor = itemView.findViewById(R.id.offerListItemAuthor);
        }
    }
}
