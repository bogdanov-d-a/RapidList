package database;

public final class ListData extends AbstractData {
    public String label;
    public String items;

    public ListData(long id, String label, String items) {
        super(id);
        this.label = label;
        this.items = items;
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
            default:
                return null;
        }
    }
}
