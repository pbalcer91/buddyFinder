package pl.com.wfiis.android.buddyfinder.views;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import pl.com.wfiis.android.buddyfinder.R;
import pl.com.wfiis.android.buddyfinder.models.User;

public class HomeFragment extends Fragment {

    private User user;

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
      //  welcomeLabel.setText("Hello, " + user.getUserName());

        FloatingActionButton addOfferButton = view.findViewById(R.id.btnAddOffer);
        addOfferButton.setOnClickListener(tempView -> startActivity(new Intent(getActivity(), AddOfferActivity.class)));

        return view;
    }
}