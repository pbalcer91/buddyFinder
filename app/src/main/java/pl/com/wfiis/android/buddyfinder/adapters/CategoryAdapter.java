package pl.com.wfiis.android.buddyfinder.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import pl.com.wfiis.android.buddyfinder.R;

public class CategoryAdapter extends BaseAdapter {
    private Context context;
    private final List<String> categoryList;

    public CategoryAdapter(Context context, List<String> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    @Override
    public int getCount() {
        return (categoryList != null ? categoryList.size() : 0);
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View rootView = LayoutInflater.from(context)
                .inflate(R.layout.item_category, viewGroup, false);

        TextView categoryName = rootView.findViewById(R.id.category_name);
        categoryName.setText(categoryList.get(i));

        return rootView;
    }
}
