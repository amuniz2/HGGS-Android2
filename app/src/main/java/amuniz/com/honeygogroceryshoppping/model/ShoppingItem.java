package amuniz.com.honeygogroceryshoppping.model;

public class ShoppingItem  {
    private PantryItemQuantity mQuantity;
    private boolean mSelected;
    private boolean mIsInCart;

    private long mPantryItemId;

    public ShoppingItem(long pantryItemId)
    {
        this(pantryItemId,  false, 1, "", false);
    }

    public ShoppingItem(long pantryItemId, boolean isInCart, int quantity, String unit, boolean selected)
    {
        mPantryItemId = pantryItemId;
        mSelected = selected;
        mIsInCart = isInCart;
        mQuantity = new PantryItemQuantity(quantity, unit);

    }
    public PantryItemQuantity getQuantity() {
        return mQuantity;
    }

    public void setQuantity(PantryItemQuantity quantity) {
        mQuantity = quantity;
    }

    public boolean getIsInCart() { return mIsInCart; }

    public void setIsInCart(boolean value) { mIsInCart = value; }

    public boolean getSelected() { return mSelected; }

    public void setSelected(boolean value) { mSelected = value; }

    public long getPantryItemId() {
        return mPantryItemId;
    }
}
