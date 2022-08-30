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
import pl.com.wfiis.android.buddyfinder.models.Event;


public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {
    private Context context;
    private ArrayList<Event> eventModels;

    public EventAdapter(Context context, ArrayList<Event> eventModels) {
        this.context = context;
        this.eventModels = eventModels;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.item_event, parent, false);

        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        holder.offerTitle.setText(eventModels.get(position).getTitle());
        holder.offerAuthor.setText(eventModels.get(position).getAuthor().getUserName());
    }

    @Override
    public int getItemCount() {
        return eventModels.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        private TextView offerTitle;
        private TextView offerAuthor;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);

            offerTitle = itemView.findViewById(R.id.tv_event_list_item_title);
            offerAuthor = itemView.findViewById(R.id.tv_event_list_item_organizer);
        }
    }
}
