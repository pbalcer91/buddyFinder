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
import pl.com.wfiis.android.buddyfinder.models.User;

public class EventMemberAdapter extends RecyclerView.Adapter<EventMemberAdapter.MemberViewHolder> {
    private final Context context;
    private final ArrayList<User> membersModels;

    public EventMemberAdapter(Context context, ArrayList<User> membersModels) {
        this.context = context;
        this.membersModels = membersModels;
    }

    @NonNull
    @Override
    public EventMemberAdapter.MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.item_member, parent, false);

        return new EventMemberAdapter.MemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventMemberAdapter.MemberViewHolder holder, int position) {
        holder.eventMember.setText(membersModels.get(position).getUserName());
    }

    @Override
    public int getItemCount() {
        return membersModels.size();
    }

    public static class MemberViewHolder extends RecyclerView.ViewHolder {
        private final TextView eventMember;

        public MemberViewHolder(@NonNull View itemView) {
            super(itemView);

            eventMember = itemView.findViewById(R.id.tv_member_item_name);
        }
    }
}
