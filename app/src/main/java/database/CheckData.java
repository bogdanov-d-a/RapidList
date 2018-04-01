package database;

public final class CheckData extends AbstractData {
    public long listId;
    public long itemIndex;

    public CheckData(long id, long listId, long itemIndex) {
        super(id);
        this.listId = listId;
        this.itemIndex = itemIndex;
    }

    @Override
    public StaticInfo.Type getType() {
        return StaticInfo.Type.CHECK;
    }

    @Override
    protected String getDataStringAux(int id) {
        switch (id) {
            case 1:
                return Long.toString(listId);
            case 2:
                return Long.toString(itemIndex);
            default:
                return null;
        }
    }
}
