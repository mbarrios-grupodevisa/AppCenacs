package gt.com.metrocasas.appcenacs;

public class ItemRevision {
    String date = "";

    public ItemRevision(){
    }

    public ItemRevision(String date)
    {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
