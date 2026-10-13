package workshop.task_2_2;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Company {

    private final String name;
    private final List<Customer> customers = new ArrayList<>();
    // suppliers are array based.
    private Supplier[] suppliers = new Supplier[0];

    public Company(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void addCustomer(Customer aCustomer) {
        this.customers.add(aCustomer);
    }

    public List<Customer> getCustomers() {
        return this.customers;
    }

    public List<Order> getOrders() {
        return this.customers.stream()
                .flatMap(customer -> customer.getOrders().stream())
                .collect(Collectors.toList());
    }

    public Customer getMostRecentCustomer() {
        return this.customers.get(customers.size()-1);
    }

    public void addSupplier(Supplier supplier) {
        // need to replace the current array of suppliers with another, larger array

        final Supplier[] currentSuppliers = this.suppliers;
        this.suppliers = new Supplier[currentSuppliers.length + 1];
        System.arraycopy(currentSuppliers, 0, this.suppliers, 0, currentSuppliers.length);
        this.suppliers[this.suppliers.length - 1] = supplier;
    }

    public Supplier[] getSuppliers() {
        return this.suppliers;
    }

    public Customer getCustomerNamed(String name) {
        return this.customers.stream()
                .filter(customer -> customer.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No customer named " + name));
    }
}
