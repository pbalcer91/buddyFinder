package pl.com.wfiis.android.buddyfinder.views;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import pl.com.wfiis.android.buddyfinder.R;
import pl.com.wfiis.android.buddyfinder.models.Event;

public class ChatDialog extends AppCompatActivity {

    private Event event;

    private ImageView backButton;
    private TextView title;

    private EditText messageField;
    private ImageView sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_chat);

        event = getIntent().getParcelableExtra("event");

        backButton = this.findViewById(R.id.btn_back);
        backButton.setOnClickListener(event -> this.finish());

        title = this.findViewById(R.id.tv_event_title);
        title.setText(event.getTitle());

        sendButton = this.findViewById(R.id.btn_send);
        sendButton.setOnClickListener(event -> {

        });

        messageField = this.findViewById(R.id.et_message_field);
        messageField.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND
                    || actionId == EditorInfo.IME_ACTION_DONE
                    || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                    || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {

                //send message
            }
            return false;
        });
    }
}
