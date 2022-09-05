package pl.com.wfiis.android.buddyfinder.views;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.Objects;
import java.util.regex.Pattern;

import pl.com.wfiis.android.buddyfinder.R;
import pl.com.wfiis.android.buddyfinder.models.User;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileFragment extends Fragment {

    private User user;

    private TextView userName;
    private TextView userEmail;

    private BottomSheetDialog bottomSheetDialog;

    FirebaseFirestore db;

    public ProfileFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            user = bundle.getParcelable("user");
        }
        else{
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        userName = view.findViewById(R.id.user_name);
        userEmail = view.findViewById(R.id.user_email);
        Button editUser = view.findViewById(R.id.btn_edit_user);
        Button changePasswordButton = view.findViewById(R.id.btn_edit_password);
        Button logoutButton = view.findViewById(R.id.btn_logout);

        userName.setText(user.getUserName());
        userEmail.setText(user.getEmail());

        editUser.setOnClickListener(tempView -> showEditUserDialog());
        changePasswordButton.setOnClickListener(tempView -> showChangePasswordDialog());
        logoutButton.setOnClickListener(tempView -> showLogoutDialog());


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            //pobieranie uzytkownika do wyrzucenia potem
            //db.collection("Users").document(user.getUid())
        }

        return view;
    }

    private void showEditUserDialog() {
        bottomSheetDialog = new BottomSheetDialog(requireContext(), R.style.BottomSheet);
        bottomSheetDialog.setContentView(R.layout.dialog_edit_user);

        EditText newNameField = bottomSheetDialog.findViewById(R.id.et_new_name);
        newNameField.setText(user.getUserName());

        EditText newEmailField = bottomSheetDialog.findViewById(R.id.et_new_email);
        newEmailField.setText(user.getEmail());

        Button acceptButton = bottomSheetDialog.findViewById(R.id.btn_dialog_accept);
        Button rejectButton = bottomSheetDialog.findViewById(R.id.btn_dialog_reject);

        Objects.requireNonNull(acceptButton).setOnClickListener(
                tempView -> {
                    changeUserName(Objects.requireNonNull(newNameField)
                            .getText().toString());
                    changeUserEmail(Objects.requireNonNull(newEmailField)
                            .getText().toString());

                    Toast.makeText(this.getContext(), "User updated", Toast.LENGTH_SHORT).show();
                });
        Objects.requireNonNull(rejectButton).setOnClickListener(
                tempView -> bottomSheetDialog.cancel());

        bottomSheetDialog.show();
    }

    private void showChangePasswordDialog() {
        bottomSheetDialog = new BottomSheetDialog(requireContext(), R.style.BottomSheet);
        bottomSheetDialog.setContentView(R.layout.dialog_change_password);

        Button acceptButton = bottomSheetDialog.findViewById(R.id.btn_dialog_accept);
        Button rejectButton = bottomSheetDialog.findViewById(R.id.btn_dialog_reject);

        Objects.requireNonNull(acceptButton).setOnClickListener(
                tempView -> changePassword());
        Objects.requireNonNull(rejectButton).setOnClickListener(
                tempView -> bottomSheetDialog.cancel());

        bottomSheetDialog.show();
    }

    private void showLogoutDialog() {
        bottomSheetDialog = new BottomSheetDialog(requireContext(), R.style.BottomSheet);
        bottomSheetDialog.setContentView(R.layout.dialog_message);

        TextView message = bottomSheetDialog.findViewById(R.id.tv_dialog_message);
        message.setText(R.string.logout_info);

        Button acceptButton = bottomSheetDialog.findViewById(R.id.btn_dialog_accept);
        acceptButton.setText(R.string.logout);

        Button rejectButton = bottomSheetDialog.findViewById(R.id.btn_dialog_reject);
        rejectButton.setText(R.string.reject);

        Objects.requireNonNull(acceptButton).setOnClickListener(
                tempView -> logout());
        Objects.requireNonNull(rejectButton).setOnClickListener(
                tempView -> bottomSheetDialog.cancel());

        bottomSheetDialog.show();
    }

    private boolean changeUserName(@NonNull String name) {
        if (name.length() == 0) {
            Toast.makeText(this.getContext(), "Name field is empty", Toast.LENGTH_SHORT).show();
            return (false);
        }

        if (name.equals(user.getUserName()))
            return (false);

        //TODO: change user name in database

        user.setUserName(name);
        userName.setText(user.getUserName());

        bottomSheetDialog.cancel();
        return (true);
    }

    private boolean changeUserEmail(@NonNull String email) {
        if (email.length() == 0) {
            Toast.makeText(this.getContext(), "Email field is empty", Toast.LENGTH_SHORT).show();
            return (false);
        }

        if (!changeEmailValidation(email)) {
            Toast.makeText(this.getContext(), "Wrong email format", Toast.LENGTH_SHORT).show();
            return (false);
        }

        if (email.equals(user.getUserName()))
            return (false);

        //TODO: change user email in database
        //TODO: check if email exists in database

        user.setEmail(email);
        userEmail.setText(user.getEmail());
        bottomSheetDialog.cancel();
        return (true);
    }

    private boolean changeEmailValidation(String email) {
        String regex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";

        return (Pattern.compile(regex).matcher(email).matches());
    }

    private boolean changePassword() {
        if (!changePasswordValidation()) {
            clearPasswordsFields();
            return (false);
        }

        //TODO: check old password and change password in database

        Toast.makeText(this.getContext(), "Password changed", Toast.LENGTH_SHORT).show();
        bottomSheetDialog.cancel();
        return (true);
    }

    private void clearPasswordsFields() {
        EditText oldPasswordField = bottomSheetDialog.findViewById(R.id.oldPasswordEdit);
        EditText newPasswordField = bottomSheetDialog.findViewById(R.id.newPasswordEdit);
        EditText repeatNewPasswordField = bottomSheetDialog.findViewById(R.id.repeatNewPasswordEdit);

        Objects.requireNonNull(oldPasswordField).setText("");
        Objects.requireNonNull(newPasswordField).setText("");
        Objects.requireNonNull(repeatNewPasswordField).setText("");
    }

    private boolean changePasswordValidation() {
        EditText oldPasswordField = bottomSheetDialog.findViewById(R.id.oldPasswordEdit);
        EditText newPasswordField = bottomSheetDialog.findViewById(R.id.newPasswordEdit);
        EditText repeatNewPasswordField = bottomSheetDialog.findViewById(R.id.repeatNewPasswordEdit);

        if (Objects.requireNonNull(oldPasswordField).getText().length() == 0
            || Objects.requireNonNull(newPasswordField).getText().length() == 0
            || Objects.requireNonNull(repeatNewPasswordField).getText().length() == 0) {
            Toast.makeText(this.getContext(), "All of fields need to be filled", Toast.LENGTH_SHORT).show();
            return (false);
        }

        if (!newPasswordField.getText().equals(repeatNewPasswordField.getText())) {
            Toast.makeText(this.getContext(), "Wrong password confirmation", Toast.LENGTH_SHORT).show();
            return (false);
        }

        return (true);
    }

    private boolean logout() {
        //TODO: logout
        Toast.makeText(this.getContext(), "Logged out", Toast.LENGTH_SHORT).show();
        bottomSheetDialog.cancel();
        return (true);
    }
}