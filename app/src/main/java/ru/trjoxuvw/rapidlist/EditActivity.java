package ru.trjoxuvw.rapidlist;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
    private Button saveButton;
    private Button deleteButton;
    private Button closeButton;

    private int operation;
    private int editListPos;
    private ListData editList;

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

    private void updateBtnState() {
        final boolean dataChanged = operation == OPERATION_CREATE ||
                !editLabel.getText().toString().equals(editList.label) ||
                !editItems.getText().toString().equals(editList.items);

        saveButton.setEnabled(dataChanged);
        closeButton.setText(dataChanged ? "Discard" : "Close");
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
            editList = getLists().get(editListPos);
        }

        editLabel = findViewById(R.id.editLabel);
        editItems = findViewById(R.id.editItems);
        saveButton = findViewById(R.id.saveButton);
        deleteButton = findViewById(R.id.deleteButton);
        closeButton = findViewById(R.id.closeButton);

        assert editLabel != null;
        assert editItems != null;
        assert saveButton != null;
        assert deleteButton != null;
        assert closeButton != null;

        if (operation == OPERATION_UPDATE) {
            editLabel.setText(editList.label);
            editItems.setText(editList.items);
        }

        editLabel.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                updateBtnState();
            }
        });

        editItems.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                updateBtnState();
            }
        });

        saveButton.setText(operation == OPERATION_CREATE ? "Create" : "Update");
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

        updateBtnState();
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
