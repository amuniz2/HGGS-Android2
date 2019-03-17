package amuniz.com.honeygogroceryshoppping.model;

/**
 * Created by amuni on 4/1/2018.
 */

public class PantryItemQuantity {
    private double mQuantity;
    private String mUnit;

    public double getQuantity() {
        return mQuantity;
    }

    public void setQuantity(double quantity) {
        mQuantity = quantity;
    }

    public String getUnit() {
        return mUnit;
    }

    public void setUnit(String unit) {
        mUnit = unit;
    }

    public PantryItemQuantity(double quantity, String unit)
    {
        mUnit = unit;
        mQuantity = quantity;
    }

    public String toString(){
        return String.format("%.2f %s", getQuantity(), getUnit());
    }

}
