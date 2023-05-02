package edu.iu.c322.demo.invoicingservice.controller;

import edu.iu.c322.demo.invoicingservice.model.Invoice;
import edu.iu.c322.demo.invoicingservice.model.Order;
import edu.iu.c322.demo.invoicingservice.repository.InvoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import org.springframework.http.HttpStatus;

import java.util.Date;
import java.util.Map;



@RestController
@RequestMapping("/invoices")
public class InvoicingController {
    private final WebClient orderService;
    private final InvoiceRepository repository;


    public InvoicingController(WebClient.Builder webClientBuilder, InvoiceRepository repository) {
        //to connect to order service
        orderService = webClientBuilder.baseUrl("http://localhost:8083").build();
        this.repository = repository;
    }


    @PutMapping("/{orderId}")
    public Mono<Order> updateOrderStatus(@PathVariable int orderId, @RequestBody Map<String, String> requestBody) {
        String newStatus = requestBody.get("status");
        return orderService.get().uri("/orders/order/{orderId}", orderId)
                .retrieve()
                .bodyToMono(Order.class)
                .flatMap(order -> {
                    if (order != null) {
                        order.setStatus(newStatus);
                        return orderService.put().uri("/orders/{orderId}", orderId)
                                .bodyValue(order)
                                .retrieve()
                                .onStatus(status -> status.isError(), clientResponse -> Mono.error(new RuntimeException("Failed to update order")))
                                .bodyToMono(Order.class);
                    } else {
                        return Mono.error(new RuntimeException("Order not found"));
                    }
                });
    }


    @DeleteMapping("/{orderId}")
    public Mono<Void> deleteOrder(@PathVariable int orderId) {
        return orderService.delete().uri("/orders/{orderId}", orderId)
                .retrieve()
                .onStatus(status -> status.isError(), clientResponse -> Mono.error(new RuntimeException("Failed to delete order")))
                .bodyToMono(Void.class);
    }

    @GetMapping("/{orderId}")
    public Mono<Invoice> findByOrderId(@PathVariable int orderId) {
        return orderService.get().uri("/orders/order/{orderId}?includeShippingAddress=true", orderId) // add a parameter to include the shipping address
                .retrieve()
                .bodyToMono(Order.class)
                .map(order -> {
                    if (order != null) {
                        return new Invoice(order);
                    } else {
                        return null;
                    }
                });
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public int createInvoice(@RequestBody Order order) {
        Invoice invoice = new Invoice(order);
        Invoice savedInvoice = repository.save(invoice);
        return savedInvoice.getId();
    }


}
