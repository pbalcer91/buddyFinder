package pl.com.wfiis.android.buddyfinder.views;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import pl.com.wfiis.android.buddyfinder.R;
import pl.com.wfiis.android.buddyfinder.models.Event;

public class ChatDialog extends AppCompatActivity {

    private Event event;

    private ImageView backButton;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_chat);

        event = getIntent().getParcelableExtra("event");

        backButton = this.findViewById(R.id.btn_back);
        backButton.setOnClickListener(event -> this.finish());

        title = this.findViewById(R.id.tv_event_title);
        title.setText(event.getTitle());
    }
}
