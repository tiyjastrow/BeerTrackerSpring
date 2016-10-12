package com.theironyard.controllers;

import com.theironyard.PasswordStorage;
import com.theironyard.entities.Beer;
import com.theironyard.entities.User;
import com.theironyard.services.BeerRepository;
import com.theironyard.services.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * Created by zach on 11/10/15.
 */
@RestController
public class BeerTrackerController {
    @Autowired
    BeerRepository beers;

    @Autowired
    UserRepository users;

    @PostConstruct
    public void init() throws InvalidKeySpecException, NoSuchAlgorithmException, PasswordStorage.CannotPerformOperationException {
        User user = users.findOneByName("John");
        if (user == null) {
            user = new User();
            user.name = "John";
            user.password = PasswordStorage.createHash("Biker1");
            users.save(user);
        }
    }

    @RequestMapping("/")
    public String home(
            HttpSession session,
            Model model,
            String type,
            Integer calories,
            String search
    ) {
        String username = (String) session.getAttribute("username");

        if (username == null) {
            return "login";
        }

        if (search != null) {
            model.addAttribute("beers", beers.searchByName(search));
        }
        else if (type != null && calories != null) {
            model.addAttribute("beers", beers.findByTypeAndCaloriesIsLessThanEqual(type, calories));
        }
        else if (type != null) {
            model.addAttribute("beers", beers.findByTypeOrderByNameAsc(type));
        }
        else {
            model.addAttribute("beers", beers.findAll());
        }
        return "home";
    }

    @RequestMapping("/add-beer")
    public Beer addBeer(String beername, String beertype, int beercalories, HttpSession session, HttpServletResponse response) throws Exception {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            throw new Exception("Not logged in.");
        }

        User user = users.findOneByName(username);

        Beer beer = new Beer();
        beer.name = beername;
        beer.type = beertype;
        beer.calories = beercalories;
        beer.user = user;
        beers.save(beer);
        response.sendRedirect("/");
        return beer;
    }

    @RequestMapping("/edit-beer")
    public Beer editBeer(int id, String name, String type, HttpSession session, HttpServletResponse response) throws Exception {
        if (session.getAttribute("username") == null) {
            throw new Exception("Not logged in.");
        }
        Beer beer = (Beer) beers.findOne(id);
        beer.name = name;
        beer.type = type;
        beers.save(beer);
        response.sendRedirect("/");
        return beer;
    }

    @RequestMapping("/login")
    public User login(String username, String password, HttpSession session, HttpServletResponse response) throws Exception {
        session.setAttribute("username", username);

        User user = users.findOneByName(username);
        if (user == null) {
            user = new User();
            user.name = username;
            user.password = PasswordStorage.createHash(password);
            users.save(user);
        }
        else if (!PasswordStorage.verifyPassword(user.password, password)) {
            throw new Exception("Wrong password");
        }

        response.sendRedirect("/");
        return user;
    }

    @RequestMapping("/logout")
    public void logout(HttpSession session, HttpServletResponse response) throws Exception  {
        session.invalidate();
        response.sendRedirect("/");
    }
}
