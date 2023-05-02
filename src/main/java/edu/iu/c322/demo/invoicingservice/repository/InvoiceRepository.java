package edu.iu.c322.demo.invoicingservice.repository;

import edu.iu.c322.demo.invoicingservice.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceRepository extends JpaRepository<Invoice,Integer> {
}
