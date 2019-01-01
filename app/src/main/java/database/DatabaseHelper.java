package database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Lists.db";

    private static String createGen(StaticInfo.Type type) {
        StringBuilder sb = new StringBuilder();
        sb.append("create table " + StaticInfo.getTableName(type) + " (");

        for (int i = 0; i < StaticInfo.getRowCount(type); ++i) {
            if (i != 0)
                sb.append(",");
            sb.append(StaticInfo.getRowName(type, i) + " " + StaticInfo.getRowType(type, i));
        }

        sb.append(");");
        return sb.toString();
    }

    private static String insertGen(AbstractData data) {
        StringBuilder sb = new StringBuilder();
        sb.append("insert into " + data.getTableName() + " (");

        for (int i = 1; i < data.getRowCount(); ++i) {
            if (i != 1)
                sb.append(",");
            sb.append(data.getRowName(i));
        }

        sb.append(") values (");

        for (int i = 1; i < data.getRowCount(); ++i) {
            if (i != 1)
                sb.append(",");
            sb.append(escapeStr(data.getDataString(i)));
        }

        sb.append(");");
        return sb.toString();
    }

    private static String updateGen(AbstractData data) {
        StringBuilder sb = new StringBuilder();
        sb.append("update " + data.getTableName() + " set ");

        for (int i = 1; i < data.getRowCount(); ++i) {
            if (i != 1)
                sb.append(",");
            sb.append(data.getRowName(i) + "=" + escapeStr(data.getDataString(i)));
        }

        sb.append(" where " + data.getRowName(0) + "=" + escapeStr(data.getDataString(0)) + ";");
        return sb.toString();
    }

    private static ListData createListDataFromCursor(Cursor cursor) {
        return new ListData(
                cursor.getLong(cursor.getColumnIndexOrThrow(StaticInfo.getListRowName(0))),
                cursor.getString(cursor.getColumnIndexOrThrow(StaticInfo.getListRowName(1))),
                cursor.getString(cursor.getColumnIndexOrThrow(StaticInfo.getListRowName(2)))
        );
    }

    private static CheckData createCheckDataFromCursor(Cursor cursor) {
        return new CheckData(
                cursor.getLong(cursor.getColumnIndexOrThrow(StaticInfo.getCheckRowName(0))),
                cursor.getLong(cursor.getColumnIndexOrThrow(StaticInfo.getCheckRowName(1))),
                cursor.getInt(cursor.getColumnIndexOrThrow(StaticInfo.getCheckRowName(2)))
        );
    }

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createGen(StaticInfo.Type.LIST));
        db.execSQL(createGen(StaticInfo.Type.CHECK));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        int curVersion = oldVersion;

        while (curVersion != newVersion) {
            switch (curVersion) {
                default:
                    break;
            }

            ++curVersion;
        }
    }

    private static long getLastInsertRowid(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("select last_insert_rowid();", null);
        try {
            if (cursor.moveToFirst())
                return cursor.getLong(0);
            return 0;
        } finally {
            cursor.close();
        }
    }

    private static String escapeStr(String str) {
        StringBuilder result = new StringBuilder();
        result.append('\'');

        for (char c : str.toCharArray()) {
            if (c == '\'')
                result.append('\'');
            result.append(c);
        }

        result.append('\'');
        return result.toString();
    }

    public ArrayList<ListData> getLists() {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery("select * from " + StaticInfo.getListTableName() +
                    " order by " + StaticInfo.getListRowName(StaticInfo.ListRowId.LABEL) + " asc;", null);
            try {
                ArrayList<ListData> result = new ArrayList<>();

                if (cursor.moveToFirst()) {
                    do {
                        result.add(createListDataFromCursor(cursor));
                    }
                    while (cursor.moveToNext());
                }

                return result;
            } finally {
                cursor.close();
            }
        } finally {
            db.close();
        }
    }

    public ArrayList<CheckData> getChecks() {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery("select * from " + StaticInfo.getCheckTableName() + ";", null);
            try {
                ArrayList<CheckData> result = new ArrayList<>();

                if (cursor.moveToFirst()) {
                    do {
                        result.add(createCheckDataFromCursor(cursor));
                    }
                    while (cursor.moveToNext());
                }

                return result;
            } finally {
                cursor.close();
            }
        } finally {
            db.close();
        }
    }

    public long create(AbstractData data) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            db.execSQL(insertGen(data));
            return getLastInsertRowid(db);
        } finally {
            db.close();
        }
    }

    public void update(AbstractData data) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            db.execSQL(updateGen(data));
        } finally {
            db.close();
        }
    }

    public void addListCheck(long listId, int itemIndex) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            create(new CheckData(0, listId, itemIndex));
        } finally {
            db.close();
        }
    }

    public void deleteListCheck(long listId, int itemIndex) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            db.execSQL("delete from " + StaticInfo.getCheckTableName() +
                    " where (" + StaticInfo.getCheckRowName(StaticInfo.CheckRowId.LIST_ID) + "=" + escapeStr(Long.toString(listId)) +
                    ") and (" + StaticInfo.getCheckRowName(StaticInfo.CheckRowId.ITEM_INDEX) + "=" + escapeStr(Integer.toString(itemIndex)) + ");");
        } finally {
            db.close();
        }
    }

    private void deleteListChecks(SQLiteDatabase db, long id) {
        db.execSQL("delete from " + StaticInfo.getCheckTableName() +
                " where " + StaticInfo.getCheckRowName(StaticInfo.CheckRowId.LIST_ID) + "=" + escapeStr(Long.toString(id)) + ";");
    }

    public void updateList(ListData list) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            deleteListChecks(db, list.id);
            update(list);
        } finally {
            db.close();
        }
    }

    public void deleteList(long id) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            deleteListChecks(db, id);
            db.execSQL("delete from " + StaticInfo.getListTableName() +
                    " where " + StaticInfo.getListRowName(0) + "=" + escapeStr(Long.toString(id)) + ";");
        } finally {
            db.close();
        }
    }
}
