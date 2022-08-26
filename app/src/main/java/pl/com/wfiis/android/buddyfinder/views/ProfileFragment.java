package pl.com.wfiis.android.buddyfinder.views;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.regex.Pattern;

import pl.com.wfiis.android.buddyfinder.R;
import pl.com.wfiis.android.buddyfinder.models.User;

public class ProfileFragment extends Fragment {

    private User user;

    private TextView userName;
    private TextView userEmail;

    private BottomSheetDialog bottomSheetDialog;

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

        userName = view.findViewById(R.id.user_name);
        userEmail = view.findViewById(R.id.user_email);
        Button editNameButton = view.findViewById(R.id.btn_edit_name);
        Button editEmailButton = view.findViewById(R.id.btn_edit_email);
        Button changePasswordButton = view.findViewById(R.id.btn_edit_password);
        Button logoutButton = view.findViewById(R.id.btn_logout);

        userName.setText(user.getUserName());
        userEmail.setText(user.getEmail());

        editNameButton.setOnClickListener(tempView -> showEditNameDialog());
        editEmailButton.setOnClickListener(tempView -> showEditEmailDialog());
        changePasswordButton.setOnClickListener(tempView -> showChangePasswordDialog());
        logoutButton.setOnClickListener(tempView -> showLogoutDialog());

        return view;
    }

    private void showEditNameDialog() {
        bottomSheetDialog = new BottomSheetDialog(this.getContext(), R.style.BottomSheet);
        bottomSheetDialog.setContentView(R.layout.dialog_edit_name);

        EditText newNameField = bottomSheetDialog.findViewById(R.id.newNameEdit);

        Button acceptButton = bottomSheetDialog.findViewById(R.id.passwordChangeConfirmButton);
        Button rejectButton = bottomSheetDialog.findViewById(R.id.passwordChangeRejectButton);

        acceptButton.setOnClickListener(tempView -> changeUserName(newNameField.getText().toString()));
        rejectButton.setOnClickListener(tempView -> bottomSheetDialog.cancel());

        bottomSheetDialog.show();
    }

    private void showEditEmailDialog() {
        bottomSheetDialog = new BottomSheetDialog(this.getContext(), R.style.BottomSheet);
        bottomSheetDialog.setContentView(R.layout.dialog_edit_email);

        EditText newEmailField = bottomSheetDialog.findViewById(R.id.newEmailEdit);

        Button acceptButton = bottomSheetDialog.findViewById(R.id.passwordChangeConfirmButton);
        Button rejectButton = bottomSheetDialog.findViewById(R.id.passwordChangeRejectButton);

        acceptButton.setOnClickListener(tempView -> changeUserEmail(newEmailField.getText().toString()));
        rejectButton.setOnClickListener(tempView -> bottomSheetDialog.cancel());

        bottomSheetDialog.show();
    }

    private void showChangePasswordDialog() {
        bottomSheetDialog = new BottomSheetDialog(this.getContext(), R.style.BottomSheet);
        bottomSheetDialog.setContentView(R.layout.dialog_change_password);

        Button acceptButton = bottomSheetDialog.findViewById(R.id.passwordChangeConfirmButton);
        Button rejectButton = bottomSheetDialog.findViewById(R.id.passwordChangeRejectButton);

        acceptButton.setOnClickListener(tempView -> changePassword());
        rejectButton.setOnClickListener(tempView -> bottomSheetDialog.cancel());

        bottomSheetDialog.show();
    }

    private void showLogoutDialog() {
        bottomSheetDialog = new BottomSheetDialog(this.getContext(), R.style.BottomSheet);
        bottomSheetDialog.setContentView(R.layout.dialog_logout);

        Button acceptButton = bottomSheetDialog.findViewById(R.id.passwordChangeConfirmButton);
        Button rejectButton = bottomSheetDialog.findViewById(R.id.passwordChangeRejectButton);

        acceptButton.setOnClickListener(tempView -> logout());
        rejectButton.setOnClickListener(tempView -> bottomSheetDialog.cancel());

        bottomSheetDialog.show();
    }

    private boolean changeUserName(String name) {
        if (name.length() == 0) {
            Toast.makeText(this.getContext(), "Name field is empty", Toast.LENGTH_SHORT).show();
            return (false);
        }

        if (name.equals(user.getUserName()))
            return (false);

        //TODO: change user name in database

        user.setUserName(name);
        userName.setText(user.getUserName());
        Toast.makeText(this.getContext(), "Name changed", Toast.LENGTH_SHORT).show();
        bottomSheetDialog.cancel();
        return (true);
    }

    private boolean changeUserEmail(String email) {
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
        Toast.makeText(this.getContext(), "Email changed", Toast.LENGTH_SHORT).show();
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

        oldPasswordField.setText("");
        newPasswordField.setText("");
        repeatNewPasswordField.setText("");
    }

    private boolean changePasswordValidation() {
        EditText oldPasswordField = bottomSheetDialog.findViewById(R.id.oldPasswordEdit);
        EditText newPasswordField = bottomSheetDialog.findViewById(R.id.newPasswordEdit);
        EditText repeatNewPasswordField = bottomSheetDialog.findViewById(R.id.repeatNewPasswordEdit);
        
        if (oldPasswordField.getText().length() == 0
            || newPasswordField.getText().length() == 0
            || repeatNewPasswordField.getText().length() == 0) {
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