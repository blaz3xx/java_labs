package workshop.task_2_2;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class Order {

    private static int nextOrderNumber = 1;

    private final int orderNumber;
    private final List<LineItem> lineItems = new ArrayList<>();
    private boolean isDelivered;

    public Order() {
        this.orderNumber = nextOrderNumber;
        nextOrderNumber += 1;
    }

    public static void resetNextOrderNumber() {
        nextOrderNumber = 1;
    }

    public void deliver() {
        this.isDelivered = true;
    }

    public boolean isDelivered() {
        return this.isDelivered;
    }

    public void addLineItem(LineItem aLineItem) {
        this.lineItems.add(aLineItem);
    }

    public void addLineItem(String name, double value, int quantity) {
        IntStream.range(0, quantity)
                .forEach(i -> this.lineItems.add(new LineItem(name, value)));
    }

    public List<LineItem> getLineItems() {
        return this.lineItems;
    }

    @Override
    public String toString() {
        return "order " + this.orderNumber + " items: " + this.lineItems.size();
    }

    public double getValue() {
        return this.lineItems.stream()
                .mapToDouble(LineItem::getValue)
                .sum();
    }

    public double getMostExpensiveItemValue() {
        return this.lineItems.stream()
                .mapToDouble(LineItem::getValue)
                .max()
                .orElse(0.0);
    }
}
