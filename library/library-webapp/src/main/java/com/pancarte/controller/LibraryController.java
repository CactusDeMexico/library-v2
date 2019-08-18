package com.pancarte.controller;

import com.pancarte.Model.*;
import com.pancarte.proxy.MicroserviceLibraryProxy;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Controller
public class LibraryController {

    @Autowired
    private MicroserviceLibraryProxy Library;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public String user = "";
    public String role = "";

    @RequestMapping(value = {"/content"}, method = RequestMethod.GET)
    public ModelAndView content() {
        ModelAndView model = new ModelAndView();
        User user = new User();
        List<Book_List> book = new ArrayList<>();
        List<Borrow> borrow = new ArrayList<>();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        int  userId=0;
        if (!auth.getName().equals("anonymousUser")) {
            model.addObject("userName", user.getName() + " " + user.getLastName());
        } else {
            model.addObject("userName", "0");
        }

        List<Book_Reservation> bookRes = reservation();

        model.addObject("bookRes", bookRes);

        String borrowedBookByTitle = "xxx";
        String reservedBook = "__";
        String resaString = "__";
        model.addObject("userId",userId);
        model.addObject("booklist",book);
        model.addObject("resaString", resaString);
        model.addObject("reservedBook", reservedBook);
        model.addObject("borrow", borrow);
        model.addObject("user", user);
        model.addObject("book", book);
        model.addObject("now", Date.valueOf(LocalDate.now()));
        model.addObject("borrowedBookByTitle", borrowedBookByTitle);
        model.setViewName("fragment/content");

        return model;
    }

    @RequestMapping(value = {"/header"}, method = RequestMethod.GET)
    public ModelAndView header() {
        ModelAndView model = new ModelAndView();
        model.addObject("userName", "0");

        model.setViewName("fragment/header");
        return model;
    }

    @RequestMapping(value = {"/index", "/"}, method = RequestMethod.GET)
    public ModelAndView index() {
        ModelAndView model = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = Library.findUserByEmail(auth.getName());
        List<Book_List> book = Library.getAllBooks();
        String borrowedBookByTitle = "__";
        String reservedBook = "__";
        String resaString = "_";
        int  userId=0;
        List<Reservation> reservation = Library.getAllReservation();
        List<Borrow> borrowed = Library.getallborrowedBook();
        if (!auth.getName().equals("anonymousUser")) {
             borrowed = Library.getborrowedBook(user.getId());


            for (Borrow borrowedBook : borrowed) {
                if (borrowedBook.getIdUser() == user.getId()) {
                    for (Book_List books : book
                    ) {
                        if (books.getIdBook() == borrowedBook.getIdBook()) {
                            borrowedBookByTitle += books.getTitle() + ",";
                        }
                    }
                }
            }

            for (Reservation reservations : reservation) {
                resaString =resaString+reservations.getTitle();
                if (reservations.getIdUser() == user.getId()) {
                    for (Book_List books : book
                    ) {
                        System.out.println(books.getTitle());
                        if (books.getIdBook() == reservations.getIdBook()) {
                            reservedBook += books.getTitle() + ",";
                        }
                    }
                }
                System.out.println(resaString+" RESATRING 1");
            }
            userId=user.getId();
            model.addObject("userName", user.getName() + " " + user.getLastName());
            model.addObject("userId", userId);

        } else {
            model.addObject("userName", "0");
            model.addObject("user", userId);
        }
        List<Book_Reservation> bookRes = reservation();

        for (Book_Reservation resa : bookRes
        ) {
            System.out.println("______________________");
            System.out.println(resa.getAvalaibleDate() + " date disp");
            System.out.println(resa.getIdUser() + " id user ");
            System.out.println(resa.getNbMaxRes() + " max ");
            System.out.println(resa.getTitle() + " titre ");
            System.out.println(resa.getRanking() + " rank");
        }
        for (Reservation reservations : reservation) {
            resaString =resaString+reservations.getTitle();

            System.out.println(resaString+" RESATRING 1");
        }
        model.addObject("borrow", borrowed);
        model.addObject("bookRes", bookRes);
        model.addObject("resaString", resaString);
        model.addObject("borrowedBookByTitle", borrowedBookByTitle);
        model.addObject("reservedBook", reservedBook);
        model.addObject("view", "home");
        model.addObject("book", book);

        model.setViewName("index");
        return model;
    }

    @RequestMapping(value = {"/search"}, method = RequestMethod.POST)
    public String search(String search) {

        return "redirect:/search/?search=" + search;
    }

    @RequestMapping(value = {"/returnbook"}, method = RequestMethod.POST)
    public String returnBook(@RequestParam("idborrow") int idborrow) {
        Library.returnBook(idborrow);
        return "redirect:/loggedhome";
    }

    @RequestMapping(value = {"/search"}, method = RequestMethod.GET)
    public ModelAndView searchIt(@RequestParam("search") String search) {
        ModelAndView model = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = Library.findUserByEmail(auth.getName());
        if (!auth.getName().equals("anonymousUser")) {
            model.addObject("userName", user.getName() + " " + user.getLastName());
        } else {
            model.addObject("userName", "0");
        }

        String cap = search.substring(0, 1).toUpperCase() + search.substring(1);
        String caps = search.toUpperCase();

        List<Book_List> book = Library.searchBooks(search);
        book.addAll(Library.searchBooks(cap));
        book.addAll(Library.searchBooks(caps));
        model.addObject("book", book);

        model.addObject("search", search);

        model.addObject("view", "search");
        model.setViewName("index");

        return model;
    }

    @RequestMapping(value = {"/login"}, method = RequestMethod.GET)
    public ModelAndView login() {
        ModelAndView model = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = Library.findUserByEmail(auth.getName());
        if (!auth.getName().equals("anonymousUser")) {
            model.addObject("userName", user.getName() + " " + user.getLastName());
        } else {
            model.addObject("userName", "0");
        }
        User userv = new User();
        model.addObject("view", "login");

        model.addObject("userName", "0");
        model.addObject("user", userv);

        model.setViewName("index");
        return model;
    }

    @RequestMapping(value = {"/signup"}, method = RequestMethod.GET)
    public ModelAndView signup() {
        ModelAndView model = new ModelAndView();
        User user = new User();
        model.addObject("user", user);
        // model.setViewName("user/signup");
        model.addObject("view", "signup");
        model.setViewName("index");

        model.addObject("userName", "0");

        return model;
    }

    @RequestMapping(value = {"/signup"}, method = RequestMethod.POST)
    public ModelAndView createUser(@Valid User user, BindingResult bindingResult) {
        ModelAndView model = new ModelAndView();
        User userExists = Library.findUserByEmail(user.getEmail());

        if (userExists != null) {
            bindingResult.rejectValue("email", "error.user", "L'email existe déja");
        }
        if (bindingResult.hasErrors()) {
            // model.setViewName("user/signup");
            model.addObject("view", "signup");
            model.setViewName("index");
        } else {
            //bCryptPasswordEncoder.encode(user.getPassword());
            Library.createUser(user.getLastName(), user.getName(), user.getEmail(), bCryptPasswordEncoder.encode(user.getPassword()));
            model.addObject("msg", "L'utilisateur à été enregistré");
            model.addObject("user", new User());

            model.addObject("userName", "0");

            //model.setViewName("user/signup");
            model.addObject("view", "login");
            model.setViewName("index");
        }

        return model;
    }

    public List<Book_Reservation> reservation()  {
        List<Book> book = Library.getBook();
        List<Borrow> borrow = Library.getallborrowedBook();
        List<Reservation> reservation = Library.getAllReservation();
        List<Book_Reservation> bookRes = new ArrayList<>();

        for (Reservation reservations : reservation
        ) {

            int nbres = 0;
            Book_Reservation res = new Book_Reservation();
            Date now = Date.valueOf(LocalDate.now());
            LocalDate init = Instant.ofEpochMilli(now.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate next2Day = init.minus(1, ChronoUnit.YEARS);
            Date dummy = Date.valueOf(next2Day);


            int nbMax = 0;
            int rank = 1;

            for (Book books : book
            ) {

                if (reservations.getTitle().equals(books.getTitle())) {
                    nbMax++;

                    for (Borrow borrows : borrow
                    ) {
                        dummy.setTime(borrows.getReturnDate().getTime());
                        if (borrows.getIdBook() == books.getIdBook()) {
                            System.out.println(dummy.compareTo(borrows.getReturnDate()) < 0);
                            if (dummy.compareTo(borrows.getReturnDate()) < 0) {

                                dummy.setTime(borrows.getReturnDate().getTime());
                            }
                        }
                    }
                }
            }
            //rand positionner
            for (Reservation reser : reservation
            ) {
                if (reservations.getTitle().equals(reser.getTitle())) {

                    if (reservations.getIdUser() != reser.getIdUser()) {
                        rank++;
                    } else {
                        break;
                    }
                }
            }
            //nb resa
            for (Reservation reser : reservation
            ) {
                if (reservations.getTitle().equals(reser.getTitle())) {
                    nbres++;
                }
            }

            res.setRanking(rank);
            res.setAvalaibleDate(dummy);
            System.out.println("DUMMY DUMMY" + dummy);
            res.setIdUser(reservations.getIdUser());
            res.setNbres(nbres);
            res.setNbMaxRes(nbMax * 2);
            res.setTitle(reservations.getTitle());
            bookRes.add(res);
        }

        return bookRes;
    }

    @RequestMapping(value = {"/loggedhome"}, method = RequestMethod.GET)
    public ModelAndView loggedHome() {
        ModelAndView model = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = Library.findUserByEmail(auth.getName());
        List<Book> book = Library.getBook();
        List<Borrow> borrow = Library.getborrowedBook(user.getId());
        List<Book_List> booklist = Library.getAllBooks();
        String borrowedBookByTitle = "__";
        if (!auth.getName().equals("anonymousUser")) {
            model.addObject("userName", user.getName() + " " + user.getLastName());
        } else {
            model.addObject("userName", "0");
        }
        for (Borrow borrowedBook : borrow) {

                for (Book books : book
                ) {

                    if (books.getIdBook() == borrowedBook.getIdBook()) {
                        borrowedBookByTitle += books.getTitle() + ",";
                    }
                }

        }

        List<Book_Reservation> bookRes = reservation();

        for (Book_Reservation res : bookRes
        ) {
            System.out.println("_____max=" + res.getNbMaxRes());
            System.out.println("_____ res act " + res.getNbres());
        }
        model.addObject("bookRes", bookRes);
        model.addObject("borrowedBookByTitle", borrowedBookByTitle);
        model.addObject("borrow", borrow);
        model.addObject("user", user);
        model.addObject("booklist", booklist);
        model.addObject("book", book);
        model.addObject("now", Date.valueOf(LocalDate.now()));

        model.addObject("view", "loggedhome");

        model.setViewName("index");
        return model;
    }

    @RequestMapping(value = {"/reserv"}, method = RequestMethod.POST)
    public String reserv(@RequestParam("idbook") int idBook) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = Library.findUserByEmail(auth.getName());
        Library.reserv(idBook, user.getId());

        return "redirect:/loggedhome";
    }

    @RequestMapping(value = {"/cancel"}, method = RequestMethod.POST)
    public String cancel(@RequestParam("title") String title) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = Library.findUserByEmail(auth.getName());
        Library.cancel(title, user.getId());

        return "redirect:/loggedhome";
    }

    @RequestMapping(value = {"/borrow"}, method = RequestMethod.POST)
    public String borrow(@RequestParam("idbook") int idBook) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = Library.findUserByEmail(auth.getName());
        List<Reservation> reservation = Library.getAllReservation();
        for (Reservation reservations : reservation){
            if (reservations.getIdUser() == user.getId() && reservations.getIdBook() == idBook) {
                Library.deleteReservations(reservations.getIdservation());
            }
        }
        Library.borrow(idBook, user.getId());

        return "redirect:/loggedhome";
    }

    @RequestMapping(value = {"/borrowed"}, method = RequestMethod.POST)
    public String borrowed(@RequestParam("idborrow") int idBorrow) {

        Library.extendBorrow(idBorrow);
        return "redirect:/loggedhome";
    }
}
