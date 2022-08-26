package pl.com.wfiis.android.buddyfinder.views;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import pl.com.wfiis.android.buddyfinder.R;
import pl.com.wfiis.android.buddyfinder.adapters.OfferAdapter;
import pl.com.wfiis.android.buddyfinder.models.Offer;
import pl.com.wfiis.android.buddyfinder.models.User;

public class HomeFragment extends Fragment {

    private User user;
    private ArrayList<Offer> joinedOffersList = new ArrayList<>();
    private ArrayList<Offer> createdOffersList = new ArrayList<>();

    public HomeFragment() {
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
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        TextView welcomeLabel = view.findViewById(R.id.welcome_label);
        welcomeLabel.setText("Hello, " + user.getUserName());

        //TODO: usunac po testach
        OfferDetailsDialog dialog = new OfferDetailsDialog(requireContext(), R.style.Dialog);
        dialog.setContentView(R.layout.dialog_offer_details);

        FloatingActionButton addOfferButton = view.findViewById(R.id.btnAddOffer);
        //addOfferButton.setOnClickListener(tempView -> startActivity(new Intent(getActivity(), AddOfferActivity.class)));
        addOfferButton.setOnClickListener(tempView -> dialog.show());

        RecyclerView joinedOffersListView = view.findViewById(R.id.joinedOffers);
        RecyclerView createdOffersListView = view.findViewById(R.id.createdOffersList);

        setupOffersList();

        OfferAdapter joinedOfferAdapter = new OfferAdapter(this.getContext(), joinedOffersList);
        OfferAdapter createdOfferAdapter = new OfferAdapter(this.getContext(), createdOffersList);

        joinedOffersListView.setAdapter(joinedOfferAdapter);
        joinedOffersListView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        createdOffersListView.setAdapter(createdOfferAdapter);
        createdOffersListView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        return view;
    }

    private void setupOffersList() {
        for (int i = 0; i < 15; i++) {
            if (i % 2 == 0)
                joinedOffersList.add(new Offer("Offer numero " + i, new User("Smith", "some_email")));
            else
                createdOffersList.add(new Offer("Offer numero " + i, user));
        }
        // TODO: implement setup offersList from database
    }
}