package pl.com.wfiis.android.buddyfinder.views;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import pl.com.wfiis.android.buddyfinder.R;
import pl.com.wfiis.android.buddyfinder.models.User;

public class ProfileFragment extends Fragment {

    private User user;

    public ProfileFragment() {

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
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        TextView userName = view.findViewById(R.id.user_name);
        TextView userEmail = view.findViewById(R.id.user_email);
        Button editProfileButton = view.findViewById(R.id.btn_edit_profile);
        Button changePasswordButton = view.findViewById(R.id.btn_edit_password);
        Button logoutButton = view.findViewById(R.id.btn_logout);

        userName.setText(user.getUserName());
        userEmail.setText(user.getEmail());

        editProfileButton.setOnClickListener(tempView -> System.out.println("Edit Profile"));

        changePasswordButton.setOnClickListener(tempView -> System.out.println("Edit Password"));

        logoutButton.setOnClickListener(tempView -> System.out.println("Logout"));

        return view;
    }
}