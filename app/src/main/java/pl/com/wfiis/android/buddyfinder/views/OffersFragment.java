package pl.com.wfiis.android.buddyfinder.views;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import pl.com.wfiis.android.buddyfinder.R;
import pl.com.wfiis.android.buddyfinder.adapters.OfferAdapter;
import pl.com.wfiis.android.buddyfinder.models.Offer;
import pl.com.wfiis.android.buddyfinder.models.User;

public class OffersFragment extends Fragment {

    private ArrayList<Offer> offers = new ArrayList<>();
    private User user;

    public OffersFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            user = bundle.getParcelable("user");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_offers, container, false);

        RecyclerView offersList = view.findViewById(R.id.offersList);

        setupOffersList();

        OfferAdapter offerAdapter = new OfferAdapter(this.getContext(), offers);
        offersList.setAdapter(offerAdapter);
        offersList.setLayoutManager(new LinearLayoutManager(this.getContext()));

        return view;
    }

    private void setupOffersList() {
        for (int i = 0; i < 15; i++) {
            if (i % 2 == 0)
                offers.add(new Offer("Offer numero " + i, new User("Smith", "some_email")));
            else
                offers.add(new Offer("Offer numero " + i, user));
        }
        // TODO: implement setup offersList from database
    }
}