package adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;

import java.util.ArrayList;

import ru.trjoxuvw.rapidlist.R;
import ru.trjoxuvw.rapidlist.ViewActivity;

public class ItemsAdapter extends BaseAdapter {
    private final ArrayList<ViewActivity.ItemData> itemsList;
    private final LayoutInflater mInflater;

    public ItemsAdapter(Context context, ArrayList<ViewActivity.ItemData> itemsList) {
        this.itemsList = itemsList;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return itemsList.size();
    }

    @Override
    public Object getItem(int position) {
        return itemsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.items_item, null);
            holder = new ViewHolder((CheckBox) convertView.findViewById(R.id.checkBox));
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final ViewActivity.ItemData item = (ViewActivity.ItemData) getItem(position);
        holder.checkBox.setText(item.label);
        holder.checkBox.setTextColor(item.checked ? Color.GRAY : Color.BLACK);
        holder.checkBox.setChecked(item.checked);

        return convertView;
    }

    private static class ViewHolder {
        public final CheckBox checkBox;

        public ViewHolder(CheckBox checkBox) {
            this.checkBox = checkBox;
        }
    }
}
