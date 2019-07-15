package com.pancarte.microservice.service;


import com.pancarte.microservice.model.Book;
import com.pancarte.microservice.model.Borrow;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookService {


   public List<Book> findAll();
   public List<Book> borrowBook();
   public List<Borrow> findAllBorrowBook();
   List<Book> findByTitle(@Param("name") String name);



}
