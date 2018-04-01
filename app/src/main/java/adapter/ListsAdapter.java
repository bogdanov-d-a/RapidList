package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import database.ListData;
import ru.trjoxuvw.rapidlist.R;

public class ListsAdapter extends BaseAdapter {
    private final LayoutInflater mInflater;
    private ArrayList<ListData> lists = new ArrayList<>();

    public ListsAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    public void ResetList(ArrayList<ListData> lists) {
        this.lists = lists;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public Object getItem(int position) {
        return lists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.lists_item, null);
            holder = new ViewHolder((TextView) convertView.findViewById(R.id.label));
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final ListData list = (ListData) getItem(position);
        holder.label.setText(list.label);

        return convertView;
    }

    private static class ViewHolder {
        public final TextView label;

        public ViewHolder(TextView label) {
            this.label = label;
        }
    }
}
