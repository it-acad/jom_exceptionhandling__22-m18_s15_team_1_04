package com.softserve.itacademy.controller;

import com.softserve.itacademy.exception.EntityNotFoundException;
import com.softserve.itacademy.exception.NullEntityReferenceException;
import com.softserve.itacademy.model.User;
import com.softserve.itacademy.service.RoleService;
import com.softserve.itacademy.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.text.AttributedString;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final RoleService roleService;

    public UserController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", roleService.getAll());
        return "create-user";
    }

    @PostMapping("/create")
    public String create(@Validated @ModelAttribute("user") User user, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("roles", roleService.getAll());
            return "create-user";
        }
//        throw new RuntimeException("Testing 500 Internal Server Error");
        try {

        user.setPassword(user.getPassword());
        user.setRole(roleService.readById(2));

        User newUser = userService.create(user);
        return "redirect:/todos/all/users/" + newUser.getId();
        } catch (NullEntityReferenceException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("roles", roleService.getAll());
            return "create-user";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "create-user";
        }
    }

    @GetMapping("/{id}/read")
    public String read(@PathVariable long id, Model model) {
        try {

            User user = userService.readById(id);
            model.addAttribute("user", user);
            return "user-info";
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "404-page";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "500-page";
        }
    }

    @GetMapping("/{id}/update")
    public String update(@PathVariable long id, Model model) {
        try {


        User user = userService.readById(id);
        model.addAttribute("user", user);
        model.addAttribute("roles", roleService.getAll());
        return "update-user";
    } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "404-page";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "500-page";
        }
    }

    @PostMapping("/{id}/update")
    public String update(@Validated @ModelAttribute("user") User user, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("roles", roleService.getAll());
            return "update-user";
        }

        try {
            user.setRole(roleService.readById(user.getRole().getId()));
            userService.update(user);
            return "redirect:/users/" + user.getId() + "/read";
        } catch (NullEntityReferenceException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("roles", roleService.getAll());
            return "update-user";
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "404-page";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "500-page";
        }
    }


    @GetMapping("/{id}/delete")
    public String delete(@PathVariable("id") long id, Model model) {
        try {

        userService.delete(id);
        return "redirect:/users/all";
    } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "404-page";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "500-page";
        }
    }

    @GetMapping("/all")
    public String getAll(Model model) {
        model.addAttribute("users", userService.getAll());
        return "users-list";
    }
}
