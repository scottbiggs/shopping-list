package sleepfuriously.com.biggsshoppinglist;

/**
 * Simple data class for a shopping list item.
 */
public class ShoppingItem {

    private int id;
    private String name;
    private boolean checked = false;

    //-------------------------------

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

}
