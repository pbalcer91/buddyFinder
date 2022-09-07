package pl.com.wfiis.android.buddyfinder.views;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.Locale;
import java.util.Objects;

import pl.com.wfiis.android.buddyfinder.R;

public class SettingsFragment extends Fragment {

    public SettingsFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        Button languageButton = view.findViewById(R.id.btn_edit_language);
        languageButton.setOnClickListener(event -> showChangeLanguageDialog());

        return view;
    }

    private void showChangeLanguageDialog() {
        MainActivity.bottomSheetDialog = new BottomSheetDialog(requireContext(), R.style.BottomSheet);
        MainActivity.bottomSheetDialog.setContentView(R.layout.dialog_language_selector);

        Button englishButton = MainActivity.bottomSheetDialog.findViewById(R.id.btn_english);
        Objects.requireNonNull(englishButton).setOnClickListener(event -> changeLanguage("en"));

        Button polishButton = MainActivity.bottomSheetDialog.findViewById(R.id.btn_polish);
        Objects.requireNonNull(polishButton).setOnClickListener(event -> changeLanguage("pl"));

        Button spanishButton = MainActivity.bottomSheetDialog.findViewById(R.id.btn_spanish);
        Objects.requireNonNull(spanishButton).setOnClickListener(event -> changeLanguage("es"));

        MainActivity.bottomSheetDialog.show();
    }

    private void changeLanguage(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Resources resources = getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);
        //TODO: find new way
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());

        MainActivity.bottomSheetDialog.dismiss();
        MainActivity.prevFragmentIndex = 0;
        this.requireActivity().finish();
        startActivity(this.requireActivity().getIntent());
        getParentFragmentManager().beginTransaction().setCustomAnimations(R.anim.animation_from_right, R.anim.animation_to_left)
                .replace(R.id.fragment_layout,
                        MainActivity.settingsFragment).commit();
    }

}
