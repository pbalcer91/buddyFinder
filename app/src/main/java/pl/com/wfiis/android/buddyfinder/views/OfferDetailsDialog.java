package pl.com.wfiis.android.buddyfinder.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import pl.com.wfiis.android.buddyfinder.R;

public class OfferDetailsDialog extends Dialog {
    private ImageView backButton;
    private ImageView editButton;

    public OfferDetailsDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        backButton = this.findViewById(R.id.btn_back);
        editButton = this.findViewById(R.id.btn_offer_edit);

        backButton.setOnClickListener(event -> System.out.println("Back clicked"));
        editButton.setOnClickListener(event -> System.out.println("Edit clicked"));
    }
}
