package edu.iu.c322.demo.invoicingservice.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(value = {"id"})
@Table(name = "invoices")
public class Invoice {
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getOrderPlaced() {
        return orderPlaced;
    }

    public void setOrderPlaced(Date orderPlaced) {
        this.orderPlaced = orderPlaced;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    public List<OrderItem> getInvoiceItems() {
        return invoiceItems;
    }

    public void setInvoiceItems(List<OrderItem> invoiceItems) {
        this.invoiceItems = invoiceItems;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private Date orderPlaced;
    private float total;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "shipping_address_id")
    private Address shippingAddress;

    public Address getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(Address shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItem> invoiceItems;

    @OneToOne(mappedBy = "invoice", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Payment payment;

    public Invoice(){

    }

    public Invoice(Date orderPlaced, float total, List<OrderItem> invoiceItems, Payment payment) {
        this.orderPlaced = orderPlaced;
        this.total = total;
        this.invoiceItems = invoiceItems;
        this.payment = payment;
    }


    public Invoice(Order order){
        this.orderPlaced = new Date();
        this.total = order.getTotal();
        this.invoiceItems = order.getItems();
        this.payment = order.getPayment();
        this.id = order.getId();
        this.shippingAddress = order.getShippingAddress();
    }

}
