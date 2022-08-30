package pl.com.wfiis.android.buddyfinder.views;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import pl.com.wfiis.android.buddyfinder.R;
import pl.com.wfiis.android.buddyfinder.adapters.CategoryAdapter;
import pl.com.wfiis.android.buddyfinder.components.CustomSpinner;

public class CreateEventActivity extends AppCompatActivity implements CustomSpinner.OnSpinnerEventsListener {

    private ImageView toolBarBackButton;
    private ImageView toolBarAcceptButton;
    private TextView toolBarTitle;

    private CustomSpinner categorySpinner;

    private CategoryAdapter categoryAdapter;

    private List<String> categoriesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        toolBarBackButton = findViewById(R.id.tool_bar_back);
        toolBarAcceptButton = findViewById(R.id.tool_bar_accept);
        toolBarTitle = findViewById(R.id.tool_bar_title);

        categorySpinner = findViewById(R.id.category_spinner);
        categorySpinner.setSpinnerEventsListener(this);

        categoriesList = new ArrayList<>();
        categoriesList.add("Category 1");
        categoriesList.add("Category 2");
        categoriesList.add("Category 3");
        categoriesList.add("Category 4");

        categoryAdapter = new CategoryAdapter(CreateEventActivity.this, categoriesList);
        categorySpinner.setAdapter(categoryAdapter);

        toolBarTitle.setText("Add new event");

        toolBarAcceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO
                // create and add event

                finish();
            }
        });

        toolBarBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void onPopupWindowOpened(Spinner spinner) {
        categorySpinner.setSelected(true);
    }

    @Override
    public void onPopupWindowClosed(Spinner spinner) {
        categorySpinner.setSelected(false);
    }
}