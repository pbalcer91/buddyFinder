package pl.com.wfiis.android.buddyfinder.views;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import java.util.Locale;

import pl.com.wfiis.android.buddyfinder.DBServices.DBServices;
import pl.com.wfiis.android.buddyfinder.R;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DBServices dbServices = new DBServices();

        SharedPreferences sharedPreferences = this.getSharedPreferences("login", MODE_PRIVATE);
        String savedEmail = sharedPreferences.getString("email", null);
        String savedPassword = sharedPreferences.getString("password", null);

        if (savedEmail != null && savedPassword != null)
            dbServices.SignInUser(savedEmail, savedPassword, this);

        sharedPreferences = this.getSharedPreferences("language", MODE_PRIVATE);
        String savedLanguage = sharedPreferences.getString("languageCode", null);

        if (savedLanguage != null) {
            Locale locale = new Locale(savedLanguage);
            Locale.setDefault(locale);
            Resources resources = getResources();
            Configuration configuration = resources.getConfiguration();
            configuration.setLocale(locale);
            //TODO: find new way
            resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        }

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        }, 2000);
    }
}