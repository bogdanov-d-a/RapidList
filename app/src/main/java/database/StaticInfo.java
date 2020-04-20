package database;

import java.util.Arrays;
import java.util.List;

public final class StaticInfo {
    public enum Type {
        LIST,
        CHECK
    }

    public static String getTableName(Type type) {
        switch (type) {
            case LIST:
                return "lists";
            case CHECK:
                return "checks";
            default:
                return null;
        }
    }

    public static String getListTableName() {
        return getTableName(Type.LIST);
    }

    public static String getCheckTableName() {
        return getTableName(Type.CHECK);
    }

    protected static final String ID_ROW = "id";

    private static final List<String> listRows = Arrays.asList("label", "items", "use_long_click", "show_flip_prompt");
    private static final List<String> checkRows = Arrays.asList("list_id", "item_index");

    private static final List<String> listRowTypes = Arrays.asList("text", "text", "integer", "integer");
    private static final List<String> checkRowTypes = Arrays.asList("integer references " + getListTableName() + "(" + getListRowName(0) + ")", "integer");

    public static final class ListRowId {
        public static final int LABEL = 1;
        public static final int ITEMS = 2;
        public static final int USE_LONG_CLICK = 3;
        public static final int SHOW_FLIP_PROMPT = 4;
    }

    public static final class CheckRowId {
        public static final int LIST_ID = 1;
        public static final int ITEM_INDEX = 2;
    }

    private static int getRowCountWithoutId(Type type) {
        switch (type) {
            case LIST:
                return listRows.size();
            case CHECK:
                return checkRows.size();
            default:
                return 0;
        }
    }

    public static int getRowCount(Type type) {
        return getRowCountWithoutId(type) + 1;
    }

    public static int getListRowCount() {
        return getRowCount(Type.LIST);
    }

    public static int getCheckRowCount() {
        return getRowCount(Type.CHECK);
    }

    private static String getRowNameImpl(Type type, int id) {
        if (id == 0)
            return ID_ROW;

        switch (type) {
            case LIST:
                return listRows.get(id - 1);
            case CHECK:
                return checkRows.get(id - 1);
            default:
                return null;
        }
    }

    public static String getRowName(Type type, int id) {
        return getTableName(type) + "__" + getRowNameImpl(type, id);
    }

    public static String getListRowName(int id) {
        return getRowName(Type.LIST, id);
    }

    public static String getCheckRowName(int id) {
        return getRowName(Type.CHECK, id);
    }

    public static String getRowType(Type type, int id) {
        if (id == 0)
            return "integer primary key";

        switch (type) {
            case LIST:
                return listRowTypes.get(id - 1);
            case CHECK:
                return checkRowTypes.get(id - 1);
            default:
                return null;
        }
    }
}
