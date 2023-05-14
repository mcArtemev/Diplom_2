package ApiTests.Order;

public class OrderSerializer {
    public OrderSerializer(){};
    private String[] ingredients;

    public OrderSerializer(String[] ingredients) {
        this.ingredients = ingredients;
    }

    public String[] getIngredients() {
        return ingredients;
    }

    public void setIngredients(String[] ingredients) {
        this.ingredients = ingredients;
    }


}
