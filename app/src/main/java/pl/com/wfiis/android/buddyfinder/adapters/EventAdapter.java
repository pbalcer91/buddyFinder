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
import pl.com.wfiis.android.buddyfinder.interfaces.RecyclerViewInterface;
import pl.com.wfiis.android.buddyfinder.models.Event;
import pl.com.wfiis.android.buddyfinder.views.MainActivity;


public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {
    private final RecyclerViewInterface recyclerViewInterface;

    private final Context context;
    private final ArrayList<Event> eventModels;

    public EventAdapter(Context context,
                        ArrayList<Event> eventModels,
                        RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.eventModels = eventModels;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.item_event, parent, false);

        return new EventViewHolder(view, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        holder.eventTitle.setText(eventModels.get(position).getTitle());
        holder.eventLocation.setText(eventModels.get(position).getLocation().getAddressLine(0));
        holder.eventDate.setText(MainActivity.dateFormat.format(eventModels.get(position).getDate()));
        holder.eventTime.setText(MainActivity.timeFormat.format(eventModels.get(position).getDate()));
    }

    @Override
    public int getItemCount() {
        return eventModels.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        private final TextView eventTitle;
        private final TextView eventLocation;
        private final TextView eventDate;
        private final TextView eventTime;

        public EventViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);

            eventTitle = itemView.findViewById(R.id.tv_event_list_item_title);
            eventLocation = itemView.findViewById(R.id.tv_event_list_item_location);
            eventDate = itemView.findViewById(R.id.tv_event_list_item_date);
            eventTime = itemView.findViewById(R.id.tv_event_list_item_time);

            itemView.setOnClickListener(view -> {
                if (recyclerViewInterface != null) {
                    int position = getAdapterPosition();

                    if (position != RecyclerView.NO_POSITION) {
                        recyclerViewInterface.onItemClick(position);
                    }
                }
            });
        }
    }
}
