package ru.trjoxuvw.rapidlist;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

import database.ListData;
import utils.ObjectCache;

public class EditActivity extends AppCompatActivity {
    public static final String OPERATION = "ACTIVITY_OPERATION";
    public static final int OPERATION_CREATE = 0;
    public static final int OPERATION_UPDATE = 1;

    public static final String EDIT_LIST_POS = "EDIT_LIST_POS";

    private EditText editLabel;
    private EditText editItems;

    private int operation;
    private int editListPos;

    private ArrayList<ListData> getLists() {
        return ObjectCache.getLists(getApplicationContext());
    }

    private ListData createListDataFromLayout(long id) {
        return new ListData(
                id,
                editLabel.getText().toString(),
                editItems.getText().toString()
        );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        if (savedInstanceState == null)
            operation = getIntent().getExtras().getInt(OPERATION);
        else
            operation = savedInstanceState.getInt(OPERATION);

        if (operation == OPERATION_UPDATE) {
            if (savedInstanceState == null)
                editListPos = getIntent().getExtras().getInt(EDIT_LIST_POS);
            else
                editListPos = savedInstanceState.getInt(EDIT_LIST_POS);
        }

        editLabel = findViewById(R.id.editLabel);
        editItems = findViewById(R.id.editItems);
        final Button saveButton = findViewById(R.id.saveButton);
        final Button deleteButton = findViewById(R.id.deleteButton);
        final Button closeButton = findViewById(R.id.closeButton);

        assert editLabel != null;
        assert editItems != null;
        assert saveButton != null;
        assert deleteButton != null;
        assert closeButton != null;

        if (operation == OPERATION_UPDATE) {
            final ListData list = getLists().get(editListPos);
            editLabel.setText(list.label);
            editItems.setText(list.items);
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (operation)
                {
                    case OPERATION_CREATE:
                        ObjectCache.getDbInstance(getApplicationContext()).create(createListDataFromLayout(0));
                        ObjectCache.invalidateCachedLists();
                        setResult(1);
                        finish();
                        break;

                    case OPERATION_UPDATE:
                        final long id = getLists().get(editListPos).id;
                        ObjectCache.getDbInstance(getApplicationContext()).updateList(createListDataFromLayout(id));
                        ObjectCache.invalidateCachedLists();
                        ObjectCache.invalidateCachedChecks();
                        setResult(1);
                        finish();
                        break;
                }
            }
        });

        if (operation == OPERATION_UPDATE) {
            deleteButton.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    ObjectCache.getDbInstance(getApplicationContext()).deleteList(getLists().get(editListPos).id);
                    ObjectCache.invalidateCachedLists();
                    setResult(1);
                    finish();
                    return true;
                }
            });
        } else {
            deleteButton.setEnabled(false);
        }

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        setResult(0);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(OPERATION, operation);
        if (operation == OPERATION_UPDATE)
            outState.putInt(EDIT_LIST_POS, editListPos);
    }
}
