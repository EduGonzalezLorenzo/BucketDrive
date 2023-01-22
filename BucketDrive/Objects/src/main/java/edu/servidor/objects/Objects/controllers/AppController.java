package edu.servidor.objects.Objects.controllers;

import edu.servidor.objects.Objects.forms.UserForm;
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
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@Valid UserForm name, @Valid UserForm password) {
        return "login";
    }

    @GetMapping("/signup")
    public String signUp(Model model) {
        return "signup";
    }

    @PostMapping("/signup")
    public String signUp(@Valid UserForm userForm, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            List<ObjectError> errors = bindingResult.getAllErrors();
            String errorMessage = "";
            for (ObjectError error : errors) {
                if (error instanceof FieldError) {
                    FieldError fieldError = (FieldError) error;
                    errorMessage += "-" + fieldError.getField() + ":" + fieldError.getDefaultMessage() + "\n";
                }
            }
            model.addAttribute("message", errorMessage);
            return "signup";
        }
        String message = service.addUser(userForm.getName(), userForm.getPassword());
        model.addAttribute("message", message);

        return "signup";
    }

    @GetMapping("/settings")
    public String settings(Model model) {
        return "settings";
    }

    @PostMapping("/settings")
    public String settings() {
        return "settings";
    }

    @GetMapping("/objects")
    public String objects(HttpSession session) {
        return "objects";
    }
}
