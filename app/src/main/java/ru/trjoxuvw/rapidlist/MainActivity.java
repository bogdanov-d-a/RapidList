package ru.trjoxuvw.rapidlist;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

import adapter.ListsAdapter;
import database.ListData;
import utils.ObjectCache;

public class MainActivity extends AppCompatActivity {
    public static final int VIEW_REQUEST = 0;
    public static final int EDIT_REQUEST = 1;

    private ListView listsView;

    private ArrayList<ListData> getLists() {
        return ObjectCache.getLists(getApplicationContext());
    }

    private void refreshListsView() {
        ((ListsAdapter) listsView.getAdapter()).ResetList(getLists());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listsView = findViewById(R.id.listsView);
        final Button newListButton = findViewById(R.id.newListButton);

        assert listsView != null;
        assert newListButton != null;

        listsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, ViewActivity.class);
                intent.putExtra(ViewActivity.LIST_POS, position);
                startActivityForResult(intent, VIEW_REQUEST);
            }
        });
        listsView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                intent.putExtra(EditActivity.OPERATION, EditActivity.OPERATION_UPDATE);
                intent.putExtra(EditActivity.EDIT_LIST_POS, position);
                startActivityForResult(intent, EDIT_REQUEST);
                return true;
            }
        });
        listsView.setAdapter(new ListsAdapter(this));
        refreshListsView();

        newListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                intent.putExtra(EditActivity.OPERATION, EditActivity.OPERATION_CREATE);
                startActivityForResult(intent, EDIT_REQUEST);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case VIEW_REQUEST:
                break;

            case EDIT_REQUEST:
                if (resultCode == 1)
                    refreshListsView();
                break;
        }
    }
}
