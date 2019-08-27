package com.pancarte.microservice.Repository;

import com.pancarte.microservice.MicroserviceApplication;
import com.pancarte.microservice.model.Borrow;

import com.pancarte.microservice.repository.BorrowRepository;

import com.pancarte.microservice.service.BookService;
import com.pancarte.microservice.service.BorrowService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static junit.framework.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestBorrowRepository {
    @Autowired
    private BorrowService borrowService;

    @MockBean
    BorrowRepository borrowRepository;



    SimpleDateFormat format=new SimpleDateFormat("dd-MM-yyyy");
    private Date date1=format. parse("02-06-2018");
    private Date date2=format. parse("12-08-2019");

    public TestBorrowRepository() throws ParseException {
    }


    @Test
    public void testGetAllBorrow() throws ParseException {

        //new Borrow_List(1,1,1,"titre 1 "," résumer","url","dgd5",date1,25,date2,date2,6,2),
        //                new Borrow_List(2,1,1,"titre 5 "," résumer","url","dgd5",date1,25,date2,date2,6,2)
        Mockito.when(borrowRepository.findAllBorrowBook()).thenReturn(
                Stream.of(
                        new Borrow(1,1,1,true,date1,date2,false),
                        new Borrow(2,4,3,true,date1,date2,false)).collect(Collectors.toList()));
       assertEquals(2,borrowService.findAllBorrowBook().size());

    }

    @Test
    public void testGetBorrowByIdUser() {
       int idUser =2;
        Mockito.when(borrowRepository.findBorrowedBookByIUser(2)).thenReturn(
                Stream.of(new Borrow(1,1,2,true,date1,date2,false)).collect(Collectors.toList()));
        assertEquals(1,borrowService.findBorrowedBookByIUser(idUser).size());
    }
    @Test
    public void testGetBorrowByIdUserWrongId() {
       int idUser =2;
        List<Borrow> borrow = new ArrayList<>();
        Mockito.when(borrowRepository.findBorrowedBookByIUser(2)).thenReturn(borrow);
        assertEquals(0,borrowService.findBorrowedBookByIUser(idUser).size());
    }
}
