package pl.com.wfiis.android.buddyfinder.DBServices;

import java.util.ArrayList;

import pl.com.wfiis.android.buddyfinder.models.Event;

public interface CallbackEvents {
    public void onCallbackGetAllEvents(ArrayList<Event> list);

}
