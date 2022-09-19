package pl.com.wfiis.android.buddyfinder.views;

import static android.content.Context.MODE_PRIVATE;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.Objects;
import java.util.regex.Pattern;

import pl.com.wfiis.android.buddyfinder.DBServices.Callback;
import pl.com.wfiis.android.buddyfinder.DBServices.DBServices;
import pl.com.wfiis.android.buddyfinder.R;
import pl.com.wfiis.android.buddyfinder.models.User;

public class ProfileFragment extends Fragment {

    private TextView userName;
    private TextView userEmail;
    private DBServices dbServices;

    LinearLayout profileViewLogged;
    RelativeLayout profileViewNotLogged;

    public ProfileFragment() {
        dbServices = new DBServices();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        profileViewLogged = view.findViewById(R.id.view_profile_logged);
        profileViewNotLogged = view.findViewById(R.id.view_profile_not_logged);

        if (MainActivity.currentUser == null) {
            profileViewNotLogged.setVisibility(View.VISIBLE);

            Button signIn = view.findViewById(R.id.btn_sign_in);
            signIn.setOnClickListener(event -> MainActivity.showLoginDialog(this.getContext()));

            Button signUp = view.findViewById(R.id.btn_sign_up);
            signUp.setOnClickListener(event -> MainActivity.showRegisterDialog(this.getContext()));

            return view;
        }


        profileViewLogged.setVisibility(View.VISIBLE);

        userName = view.findViewById(R.id.user_name);
        userEmail = view.findViewById(R.id.user_email);

        Button editUser = view.findViewById(R.id.btn_edit_user);
        Button changePasswordButton = view.findViewById(R.id.btn_edit_password);
        Button logoutButton = view.findViewById(R.id.btn_logout);

        userName.setText(MainActivity.currentUser.getUserName());
        userEmail.setText(MainActivity.currentUser.getEmail());

        editUser.setOnClickListener(tempView -> showEditUserDialog());
        changePasswordButton.setOnClickListener(tempView -> showChangePasswordDialog());
        logoutButton.setOnClickListener(tempView -> showLogoutDialog());

        return view;
    }

    private void showEditUserDialog() {
        MainActivity.bottomSheetDialog = new BottomSheetDialog(requireContext(), R.style.BottomSheet);
        MainActivity.bottomSheetDialog.setContentView(R.layout.dialog_edit_user);

        EditText newNameField = MainActivity.bottomSheetDialog.findViewById(R.id.et_new_name);
        Objects.requireNonNull(newNameField).setText(MainActivity.currentUser.getUserName());

        EditText newEmailField = MainActivity.bottomSheetDialog.findViewById(R.id.et_new_email);
        Objects.requireNonNull(newEmailField).setText(MainActivity.currentUser.getEmail());

        Button acceptButton = MainActivity.bottomSheetDialog.findViewById(R.id.btn_dialog_accept);
        Button rejectButton = MainActivity.bottomSheetDialog.findViewById(R.id.btn_dialog_reject);

        Objects.requireNonNull(acceptButton).setOnClickListener(
                tempView -> {
                    changeUserName(Objects.requireNonNull(newNameField)
                            .getText().toString());
                    changeUserEmail(Objects.requireNonNull(newEmailField)
                            .getText().toString());
                });
        Objects.requireNonNull(rejectButton).setOnClickListener(
                tempView -> MainActivity.bottomSheetDialog.cancel());

        MainActivity.bottomSheetDialog.show();
    }

    private void showChangePasswordDialog() {
        MainActivity.bottomSheetDialog = new BottomSheetDialog(requireContext(), R.style.BottomSheet);
        MainActivity.bottomSheetDialog.setContentView(R.layout.dialog_change_password);

        Button acceptButton = MainActivity.bottomSheetDialog.findViewById(R.id.btn_dialog_accept);
        Button rejectButton = MainActivity.bottomSheetDialog.findViewById(R.id.btn_dialog_reject);

        Objects.requireNonNull(acceptButton).setOnClickListener(
                tempView -> changePassword());
        Objects.requireNonNull(rejectButton).setOnClickListener(
                tempView -> MainActivity.bottomSheetDialog.cancel());

        MainActivity.bottomSheetDialog.show();
    }

    private void showLogoutDialog() {
        MainActivity.bottomSheetDialog = new BottomSheetDialog(requireContext(), R.style.BottomSheet);
        MainActivity.bottomSheetDialog.setContentView(R.layout.dialog_message);

        TextView message = MainActivity.bottomSheetDialog.findViewById(R.id.tv_dialog_message);
        Objects.requireNonNull(message).setText(R.string.logout_info);

        Button acceptButton = MainActivity.bottomSheetDialog.findViewById(R.id.btn_dialog_accept);
        Objects.requireNonNull(acceptButton).setText(R.string.logout);

        Button rejectButton = MainActivity.bottomSheetDialog.findViewById(R.id.btn_dialog_reject);
        Objects.requireNonNull(rejectButton).setText(R.string.reject);

        Objects.requireNonNull(acceptButton).setOnClickListener(
                tempView -> logout());
        Objects.requireNonNull(rejectButton).setOnClickListener(
                tempView -> MainActivity.bottomSheetDialog.cancel());

        MainActivity.bottomSheetDialog.show();
    }

    private void changeUserName(@NonNull String name) {
        if (name.length() == 0) {
            Toast.makeText(this.getContext(), R.string.name_field_required, Toast.LENGTH_SHORT).show();
            return;
        }

        if (name.equals(MainActivity.currentUser.getUserName())) {
            MainActivity.bottomSheetDialog.cancel();
            return;
        }

        Toast.makeText(this.getContext(), R.string.user_updated, Toast.LENGTH_SHORT).show();

        MainActivity.currentUser.setUserName(name);
        userName.setText(MainActivity.currentUser.getUserName());

        dbServices.updateUserData("username", name, requireContext());

        MainActivity.bottomSheetDialog.cancel();
    }

    private void changeUserEmail(@NonNull String email) {
        if (email.length() == 0) {
            Toast.makeText(this.getContext(), R.string.email_field_required, Toast.LENGTH_SHORT).show();
            return;
        }

        if (!changeEmailValidation(email)) {
            Toast.makeText(this.getContext(), R.string.email_format_error, Toast.LENGTH_SHORT).show();
            return;
        }

        if (email.equals(MainActivity.currentUser.getEmail())) {
            MainActivity.bottomSheetDialog.cancel();
            return;
        }

        Toast.makeText(this.getContext(), R.string.user_updated, Toast.LENGTH_SHORT).show();

        dbServices.isEmailInDB(email, result -> {
            if (result) {
                Toast.makeText(requireContext(), "Exists account connected to this email", Toast.LENGTH_SHORT).show();
                return;
            }

            MainActivity.currentUser.setEmail(email);
            userEmail.setText(MainActivity.currentUser.getEmail());

            dbServices.updateUserData("email", email, requireContext());
        });

        MainActivity.bottomSheetDialog.cancel();
    }

    private boolean changeEmailValidation(String email) {
        String regex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";

        return (Pattern.compile(regex).matcher(email).matches());
    }

    private void changePassword() {
        if (!changePasswordValidation()) {
            clearPasswordsFields();
            return;
        }

        EditText oldPasswordField = MainActivity.bottomSheetDialog.findViewById(R.id.oldPasswordEdit);
        EditText newPasswordField = MainActivity.bottomSheetDialog.findViewById(R.id.newPasswordEdit);

        if(MainActivity.currentUser.getPassword().equals(oldPasswordField.getText().toString())){
            dbServices.updateUserData("password", newPasswordField.getText().toString(), requireContext());
            Toast.makeText(this.getContext(), R.string.password_changed, Toast.LENGTH_SHORT).show();
            MainActivity.bottomSheetDialog.cancel();
        }
        else
            Toast.makeText(this.getContext(), "Old password is incorrect", Toast.LENGTH_SHORT).show();

    }

    private void clearPasswordsFields() {
        EditText oldPasswordField = MainActivity.bottomSheetDialog.findViewById(R.id.oldPasswordEdit);
        EditText newPasswordField = MainActivity.bottomSheetDialog.findViewById(R.id.newPasswordEdit);
        EditText repeatNewPasswordField = MainActivity.bottomSheetDialog.findViewById(R.id.repeatNewPasswordEdit);


        Objects.requireNonNull(oldPasswordField).setText("");
        Objects.requireNonNull(newPasswordField).setText("");
        Objects.requireNonNull(repeatNewPasswordField).setText("");
    }

    private boolean changePasswordValidation() {
        EditText oldPasswordField = MainActivity.bottomSheetDialog.findViewById(R.id.oldPasswordEdit);
        EditText newPasswordField = MainActivity.bottomSheetDialog.findViewById(R.id.newPasswordEdit);
        EditText repeatNewPasswordField = MainActivity.bottomSheetDialog.findViewById(R.id.repeatNewPasswordEdit);

        if (Objects.requireNonNull(oldPasswordField).getText().length() == 0
            || Objects.requireNonNull(newPasswordField).getText().length() == 0
            || Objects.requireNonNull(repeatNewPasswordField).getText().length() == 0) {
            Toast.makeText(this.getContext(), R.string.all_fields_required, Toast.LENGTH_SHORT).show();
            return (false);
        }

        if (!newPasswordField.getText().toString().equals(repeatNewPasswordField.getText().toString())) {
            Toast.makeText(this.getContext(), R.string.wrong_re_password, Toast.LENGTH_SHORT).show();
            return (false);
        }

        return (true);
    }

    private void logout() {
        dbServices.logoutUser(requireContext());
        MainActivity.currentUser = null;

        Toast.makeText(this.getContext(), R.string.logged_out, Toast.LENGTH_SHORT).show();

        MainActivity.bottomSheetDialog.dismiss();

        MainActivity.prevFragmentIndex = 1;
        MainActivity.bottomNavigation.setSelectedItemId(R.id.menu_item_home);

        getParentFragmentManager().beginTransaction().
                setCustomAnimations(R.anim.animation_from_right, R.anim.animation_to_left)
                .replace(R.id.fragment_layout,
                        MainActivity.homeFragment).commit();

    }
}