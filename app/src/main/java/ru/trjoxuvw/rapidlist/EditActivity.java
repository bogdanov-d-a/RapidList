package ru.trjoxuvw.rapidlist;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

import database.CheckData;
import database.ListData;
import utils.ObjectCache;

public class EditActivity extends AppCompatActivity {
    public static final String OPERATION = "ACTIVITY_OPERATION";
    public static final int OPERATION_CREATE = 0;
    public static final int OPERATION_UPDATE = 1;

    public static final String EDIT_LIST_POS = "EDIT_LIST_POS";

    private EditText editLabel;
    private EditText editItems;
    private CheckBox useLongClickCheckBox;
    private CheckBox showFlipPromptCheckBox;
    private Button saveButton;
    private Button deleteButton;
    private Button closeButton;

    private int operation;
    private int editListPos;
    private ListData editList;
    private String editListItemsProcessed;
    private Set<Integer> editCheckedItems;

    private ArrayList<ListData> getLists() {
        return ObjectCache.getLists(getApplicationContext());
    }

    private ArrayList<CheckData> getChecks() {
        return ObjectCache.getChecks(getApplicationContext());
    }

    private static class ListDataWithChecks {
        public ListData listData;
        public Set<Integer> checks;
    }

    private ListDataWithChecks createListDataFromLayout(long id) {
        ListDataWithChecks result = new ListDataWithChecks();
        result.checks = new TreeSet<>();

        StringBuilder editItemsCleaned = new StringBuilder();
        int lineIndex = 0;
        for (String line : editItems.getText().toString().split("\n")) {
            if (lineIndex > 0) {
                editItemsCleaned.append('\n');
            }
            boolean checked = line.startsWith("#c ");
            if (checked) {
                result.checks.add(lineIndex);
            }
            editItemsCleaned.append(checked ? line.substring(3) : line);
            ++lineIndex;
        }

        result.listData = new ListData(
                id,
                editLabel.getText().toString(),
                editItemsCleaned.toString(),
                useLongClickCheckBox.isChecked(),
                showFlipPromptCheckBox.isChecked()
        );

        return result;
    }

    private void addChecks(long listId, Set<Integer> itemChecks) {
        for (Integer itemCheck : itemChecks) {
            ObjectCache.getDbInstance(getApplicationContext()).addListCheck(listId, itemCheck);
        }
    }

    private void updateBtnState() {
        final boolean dataChanged = operation == OPERATION_CREATE ||
                !editLabel.getText().toString().equals(editList.label) ||
                !editItems.getText().toString().equals(editListItemsProcessed) ||
                useLongClickCheckBox.isChecked() != editList.useLongClick ||
                showFlipPromptCheckBox.isChecked() != editList.showFlipPrompt;

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

            editCheckedItems = new TreeSet<>();
            for (CheckData checkData : getChecks()) {
                if (checkData.listId == editList.id) {
                    editCheckedItems.add(checkData.itemIndex);
                }
            }
        }

        editLabel = findViewById(R.id.editLabel);
        editItems = findViewById(R.id.editItems);
        useLongClickCheckBox = findViewById(R.id.useLongClickCheckBox);
        showFlipPromptCheckBox = findViewById(R.id.showFlipPromptCheckBox);
        saveButton = findViewById(R.id.saveButton);
        deleteButton = findViewById(R.id.deleteButton);
        closeButton = findViewById(R.id.closeButton);

        assert editLabel != null;
        assert editItems != null;
        assert useLongClickCheckBox != null;
        assert showFlipPromptCheckBox != null;
        assert saveButton != null;
        assert deleteButton != null;
        assert closeButton != null;

        if (operation == OPERATION_UPDATE) {
            editLabel.setText(editList.label);

            StringBuilder editItemsText = new StringBuilder();
            int lineIndex = 0;
            for (String line : editList.items.split("\n")) {
                if (lineIndex > 0) {
                    editItemsText.append('\n');
                }
                if (editCheckedItems.contains(lineIndex)) {
                    editItemsText.append("#c ");
                }
                editItemsText.append(line);
                ++lineIndex;
            }
            editListItemsProcessed = editItemsText.toString();
            editItems.setText(editListItemsProcessed);

            useLongClickCheckBox.setChecked(editList.useLongClick);
            showFlipPromptCheckBox.setChecked(editList.showFlipPrompt);
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

        useLongClickCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateBtnState();
            }
        });

        showFlipPromptCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateBtnState();
            }
        });

        saveButton.setText(operation == OPERATION_CREATE ? "Create" : "Update");
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (operation)
                {
                    case OPERATION_CREATE: {
                        ListDataWithChecks data = createListDataFromLayout(0);
                        final long newId = ObjectCache.getDbInstance(getApplicationContext()).create(data.listData);
                        addChecks(newId, data.checks);
                        ObjectCache.invalidateCachedLists();
                        ObjectCache.invalidateCachedChecks();
                        setResult(1);
                        finish();
                        break;
                    }

                    case OPERATION_UPDATE: {
                        final long id = getLists().get(editListPos).id;
                        ListDataWithChecks data = createListDataFromLayout(id);
                        ObjectCache.getDbInstance(getApplicationContext()).updateList(data.listData);
                        addChecks(id, data.checks);
                        ObjectCache.invalidateCachedLists();
                        ObjectCache.invalidateCachedChecks();
                        setResult(1);
                        finish();
                        break;
                    }
                }
            }
        });

        if (operation == OPERATION_UPDATE) {
            deleteButton.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    ObjectCache.getDbInstance(getApplicationContext()).deleteList(getLists().get(editListPos).id);
                    ObjectCache.invalidateCachedLists();
                    ObjectCache.invalidateCachedChecks();
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
