package edu.servidor.objects.Objects.controllers;

import edu.servidor.objects.Objects.forms.UserForm;
import edu.servidor.objects.Objects.models.User;
import edu.servidor.objects.Objects.services.BucketService;
import edu.servidor.objects.Objects.services.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UserController {
    @Autowired
    UserService userService;

    @GetMapping("/index")
    public String index(Model model) {
        return "index";
    }

    @GetMapping("/login")
    public String login(HttpSession session) {
        Object login = session.getAttribute("currentUser");
        if (login == null) return "login";
        else return "objects";
    }

    @PostMapping("/login")
    public String login(HttpSession session, @Valid UserForm userForm, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("message", "Data input error");
            return "login";
        }
        User user = userService.login(userForm.getUsername(), userForm.getPassword());
        if (user != null) {
            session.setAttribute("currentUser", user);
            return "redirect:/objects/";
        }
        model.addAttribute("message", "Wrong user or password");
        return "login";
    }

    @GetMapping("/signup")
    public String signUp(Model model) {
        return "signup";
    }

    @PostMapping("/signup")
    public String signUp(@Valid UserForm userForm, BindingResult bindingResult, Model model) {
        String message = "";
        if (bindingResult.hasErrors()) {
            message = "Data input error";
        } else message = userService.addUser(userForm.getUsername(), userForm.getName(), userForm.getPassword());

        model.addAttribute("message", message);

        return "signup";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.setAttribute("currentUser", null);
        return "index";
    }

    @GetMapping("/settings")
    public String settings(Model model, HttpSession session) {
        model.addAttribute("currentUser", session.getAttribute("currentUser"));
        return "settings";
    }

    @PostMapping("/settings")
    public String settings(@Valid UserForm userForm, BindingResult bindingResult, Model model, HttpSession session) {
        String message = "";
        User user = (User) session.getAttribute("currentUser");
        if (bindingResult.hasErrors()) {
            message = "Data input error";
        } else {
            message = userService.modifyUser(userForm.getName(), userForm.getPassword(), user.getUsername());
            session.setAttribute("currentUser", userService.getUserById(user.getUsername()));
        }
        model.addAttribute("currentUser", session.getAttribute("currentUser"));
        model.addAttribute("message", message);

        return "settings";
    }

}
