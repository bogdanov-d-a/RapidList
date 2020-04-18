package ru.trjoxuvw.rapidlist;

import android.app.Dialog;
import android.content.DialogInterface;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
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
import utils.Utils;

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

    private enum Status {
        ALL_CHECKED,
        ALL_UNCHECKED,
        MIXED
    }

    private Button fButton;
    private ListView itemsView;

    private int listPos;
    private ListData listData;
    private ArrayList<ItemData> items;

    private Status status = Status.MIXED;

    private void updateStatInfo() {
        final int totalCount = items.size();
        int checkedCount = 0;
        for (ItemData item : items) {
            if (item.checked) {
                ++checkedCount;
            }
        }

        status = Status.MIXED;
        if (checkedCount == 0) {
            status = Status.ALL_UNCHECKED;
        } else if (totalCount == checkedCount) {
            status = Status.ALL_CHECKED;
        }

        fButton.setText(listData.label + " - " + Integer.toString(checkedCount) + " / " + Integer.toString(totalCount - checkedCount) + " (" + Integer.toString(totalCount) + ")");
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

        fButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (status == Status.ALL_CHECKED) {
                    for (int itemPosition = 0; itemPosition < items.size(); ++itemPosition) {
                        setItemCheck(itemPosition, false);
                    }
                } else if (status == Status.ALL_UNCHECKED) {
                    for (int itemPosition = 0; itemPosition < items.size(); ++itemPosition) {
                        setItemCheck(itemPosition, true);
                    }
                } else {
                    MixedStateResetFragment.createAndShow(getSupportFragmentManager());
                }
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
            items.add(new ItemData(Utils.ParseLine(itemLabel), false));
        }
        for (CheckData checkData : ObjectCache.getChecks(getApplicationContext())) {
            if (checkData.listId == listData.id) {
                items.get(checkData.itemIndex).checked = true;
            }
        }

        itemsView.setAdapter(new ItemsAdapter(this, items));

        if (listData.useLongClick)
        {
            itemsView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    flipItemCheck(position);
                    return true;
                }
            });
        } else {
            itemsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    flipItemCheck(position);
                }
            });
        }

        updateStatInfo();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(LIST_POS, listPos);
    }

    public static class MixedStateResetFragment extends DialogFragment {
        public static void createAndShow(FragmentManager manager) {
            new MixedStateResetFragment().show(manager, "MixedStateResetFragment");
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            super.onCreateDialog(savedInstanceState);

            final ViewActivity parent = (ViewActivity) getActivity();

            final AlertDialog.Builder builder = new AlertDialog.Builder(parent);
            builder.setMessage("Check mark state is mixed. Reset all to unchecked?")
                    .setPositiveButton("Reset", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            for (int itemPosition = 0; itemPosition < parent.items.size(); ++itemPosition) {
                                parent.setItemCheck(itemPosition, false);
                            }
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dismiss();
                        }
                    });
            return builder.create();
        }
    }
}
