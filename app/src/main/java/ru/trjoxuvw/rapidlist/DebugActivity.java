package ru.trjoxuvw.rapidlist;

import android.os.Environment;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import database.DatabaseHelper;
import utils.Utils;

public class DebugActivity extends AppCompatActivity {
    private static final String dbPath = "/data/data/ru.trjoxuvw.rapidlist/databases/" + DatabaseHelper.DATABASE_NAME;
    private static final String defaultBackupName = "rl-bk.db";

    private EditText backupPathText;

    private String getBackupPath() {
        return backupPathText.getText().toString();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);

        backupPathText = findViewById(R.id.backupPathText);
        assert backupPathText != null;
        backupPathText.setText(Environment.getExternalStorageDirectory() + "/" + defaultBackupName);

        final Button backupButton = findViewById(R.id.backupButton);
        assert backupButton != null;
        backupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Utils.copyFile(dbPath, getBackupPath());
                    Toast.makeText(DebugActivity.this, "Success", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Toast.makeText(DebugActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        final Button restoreButton = findViewById(R.id.restoreButton);
        assert restoreButton != null;
        restoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Utils.copyFile(getBackupPath(), dbPath);
                    Toast.makeText(DebugActivity.this, "Success", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Toast.makeText(DebugActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
