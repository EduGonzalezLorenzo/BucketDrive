package edu.servidor.objects.Objects.controllers;

import edu.servidor.objects.Objects.forms.UserForm;
import edu.servidor.objects.Objects.models.User;
import edu.servidor.objects.Objects.services.MyService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class AppController {

    @Autowired
    MyService service;

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
        String errorMessage = "";
        if (bindingResult.hasErrors()) {
            List<ObjectError> errors = bindingResult.getAllErrors();
            for (ObjectError error : errors) {
                if (error instanceof FieldError) {
                    FieldError fieldError = (FieldError) error;
                    errorMessage += "-" + fieldError.getField() + ":" + fieldError.getDefaultMessage();
                    model.addAttribute("message", errorMessage);
                    return "login";
                }
            }
        }
        User user = service.login(userForm.getUsername(), userForm.getPassword());
        if (user != null){
            session.setAttribute("currentUser", user);
            return "objects";
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
            List<ObjectError> errors = bindingResult.getAllErrors();
            for (ObjectError error : errors) {
                if (error instanceof FieldError) {
                    FieldError fieldError = (FieldError) error;
                    message += "-" + fieldError.getField() + ":" + fieldError.getDefaultMessage();
                }
            }
        } else message = service.addUser(userForm.getUsername(), userForm.getName(), userForm.getPassword());

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
            List<ObjectError> errors = bindingResult.getAllErrors();
            for (ObjectError error : errors) {
                if (error instanceof FieldError) {
                    FieldError fieldError = (FieldError) error;
                    message += "-" + fieldError.getField() + ":" + fieldError.getDefaultMessage();
                }
            }
        } else{
            message = service.modifyUser(userForm.getUsername(), userForm.getName(), userForm.getPassword(), user.getId());
            session.setAttribute("currentUser", service.getUserById(user.getId()));
        }
        model.addAttribute("currentUser", session.getAttribute("currentUser"));
        model.addAttribute("message", message);

        return "settings";
    }

    @GetMapping("/objects")
    public String objects(HttpSession session) {
        return "objects";
    }
}
