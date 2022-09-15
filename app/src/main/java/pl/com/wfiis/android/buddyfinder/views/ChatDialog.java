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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_chat);

        Event event1 = getIntent().getParcelableExtra("event");

        ImageView backButton = this.findViewById(R.id.btn_back);
        backButton.setOnClickListener(event -> this.finish());

        TextView title = this.findViewById(R.id.tv_event_title);
        title.setText(event1.getTitle());

        ImageView sendButton = this.findViewById(R.id.btn_send);
        sendButton.setOnClickListener(event -> {

        });

        EditText messageField = this.findViewById(R.id.et_message_field);
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
