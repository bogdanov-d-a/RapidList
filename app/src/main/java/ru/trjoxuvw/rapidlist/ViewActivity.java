package ru.trjoxuvw.rapidlist;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

import adapter.ItemsAdapter;
import database.CheckData;
import database.DatabaseHelper;
import database.ListData;
import utils.ObjectCache;

public class ViewActivity extends AppCompatActivity {
    public static final String LIST_POS = "LIST_POS";

    public static class ItemData {
        public String label;
        public boolean checked;

        public ItemData(String label, boolean checked) {
            this.label = label;
            this.checked = checked;
        }
    }

    private Button fButton;
    private ListView itemsView;

    private int listPos;
    private ListData listData;
    private ArrayList<ItemData> items;

    private void updateStatInfo() {
        final int totalCount = items.size();
        int checkedCount = 0;
        for (ItemData item : items) {
            if (item.checked) {
                ++checkedCount;
            }
        }

        final String countText = Integer.toString(checkedCount) + " / " + Integer.toString(totalCount);
        if (totalCount == 0) {
            fButton.setText("Empty");
        } else if (checkedCount == 0) {
            fButton.setText("Clear (" + countText + ")");
        } else if (checkedCount == totalCount) {
            fButton.setText("Full (" + countText + ")");
        } else {
            fButton.setText(countText);
        }
    }

    private void setItemCheck(int position, boolean newChecked) {
        final ItemData item = items.get(position);
        if (item.checked != newChecked) {
            item.checked = newChecked;
            ((ItemsAdapter) itemsView.getAdapter()).notifyDataSetChanged();
            updateStatInfo();

            final DatabaseHelper dbInstance = ObjectCache.getDbInstance(getApplicationContext());
            if (newChecked) {
                dbInstance.addListCheck(listData.id, position);
            } else {
                dbInstance.deleteListCheck(listData.id, position);
            }
            ObjectCache.invalidateCachedChecks();
        }
    }

    private void flipItemCheck(int position) {
        setItemCheck(position, !items.get(position).checked);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        fButton = findViewById(R.id.fButton);
        itemsView = findViewById(R.id.itemsView);

        assert fButton != null;
        assert itemsView != null;

        fButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                for (int itemPosition = 0; itemPosition < items.size(); ++itemPosition) {
                    setItemCheck(itemPosition, false);
                }
                return true;
            }
        });

        if (savedInstanceState == null) {
            listPos = getIntent().getExtras().getInt(LIST_POS);
        } else {
            listPos = savedInstanceState.getInt(LIST_POS);
        }
        listData = ObjectCache.getLists(getApplicationContext()).get(listPos);

        items = new ArrayList<>();
        for (String itemLabel : listData.items.split("\n")) {
            items.add(new ItemData(itemLabel, false));
        }
        for (CheckData checkData : ObjectCache.getChecks(getApplicationContext())) {
            if (checkData.listId == listData.id) {
                items.get(checkData.itemIndex).checked = true;
            }
        }

        itemsView.setAdapter(new ItemsAdapter(this, items));
        itemsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                flipItemCheck(position);
            }
        });

        updateStatInfo();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(LIST_POS, listPos);
    }
}
