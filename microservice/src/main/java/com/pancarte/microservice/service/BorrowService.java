package com.pancarte.microservice.service;

import com.pancarte.microservice.model.Borrow;
import com.pancarte.microservice.model.Borrowed;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BorrowService {

    int findFirstBorrowById(@Param("id_book") int id_book);

    List<Borrow> findAllBorrowBook();

    Borrow findBorrowedBook(@Param("id_borrow") int id_borrow);

    List<Borrow> findBorrowedBookByIUser(@Param("id_user") int id_user);
}
