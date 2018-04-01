package utils;

import android.content.Context;

import java.util.ArrayList;

import database.CheckData;
import database.DatabaseHelper;
import database.ListData;

public class ObjectCache {
    private static DatabaseHelper dbInstance = null;
    private static ArrayList<ListData> lists = null;
    private static ArrayList<CheckData> checks = null;

    public static DatabaseHelper getDbInstance(Context context) {
        if (dbInstance == null) {
            dbInstance = new DatabaseHelper(context);
        }
        return dbInstance;
    }

    public static ArrayList<ListData> getLists(Context context) {
        if (lists == null) {
            lists = getDbInstance(context).getLists();
        }
        return lists;
    }

    public static void invalidateCachedLists() {
        lists = null;
    }

    public static ArrayList<CheckData> getChecks(Context context) {
        if (checks == null) {
            checks = getDbInstance(context).getChecks();
        }
        return checks;
    }

    public static void invalidateCachedChecks() {
        checks = null;
    }
}
