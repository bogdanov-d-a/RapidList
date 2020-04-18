package database;

public final class ListData extends AbstractData {
    public String label;
    public String items;
    public boolean useLongClick;

    public ListData(long id, String label, String items, boolean useLongClick) {
        super(id);
        this.label = label;
        this.items = items;
        this.useLongClick = useLongClick;
    }

    @Override
    public StaticInfo.Type getType() {
        return StaticInfo.Type.LIST;
    }

    @Override
    protected String getDataStringAux(int id) {
        switch (id) {
            case 1:
                return label;
            case 2:
                return items;
            case 3:
                return Long.toString(useLongClick ? 1 : 0);
            default:
                return null;
        }
    }
}
