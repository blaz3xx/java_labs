package workshop.task_2_2;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class Exercise2OrdersTest extends CompanyDomain
{
    /**
     *  Aggregate the total order values by city.
     */
    @Test
    public void totalOrderValuesByCity()
    {
        Map<String, Double> map = company.getCustomers().stream()
                .collect(Collectors.groupingBy(
                        Customer::getCity,
                        Collectors.summingDouble(Customer::getTotalOrderValue)));

        assertEquals(2, map.size());
        assertEquals(446.25, map.get("London"), 0.0);
        assertEquals(857.0, map.get("Liphook"), 0.0);
    }

    /**
     * Extra credit. Create a map where the values are customers and the key is the price of
     * the most expensive item that the customer ordered.
     */

    @Test
    public void mostExpensiveItem()
    {
        Map<Double, List<Customer>> map = company.getCustomers().stream()
                .collect(Collectors.groupingBy(Customer::getMostExpensiveItemValue));

        Assert.assertEquals(2, map.size());
        Assert.assertEquals(2, map.entrySet().size());
        Assert.assertEquals(
                Arrays.asList(
                        this.company.getCustomerNamed("Fred"),
                        this.company.getCustomerNamed("Bill")),
                map.get(50.0));
    }

    @Test
    public void allOrdersOfTheCompany()
    {
        assertEquals(5, company.getOrders().size());
    }

    @Test
    public void orderValueAndMostExpensiveItem()
    {
        Order order = company.getCustomerNamed("Mary").getOrders().get(0);

        assertEquals(857.0, order.getValue(), 0.0);
        assertEquals(500.0, order.getMostExpensiveItemValue(), 0.0);
    }

    @Test
    public void addLineItemWithQuantity()
    {
        Order order = new Order();
        order.addLineItem("cup", 1.5, 3);

        assertEquals(3, order.getLineItems().size());
        assertEquals(4.5, order.getValue(), 0.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void unknownCustomer()
    {
        company.getCustomerNamed("Nobody");
    }

}
