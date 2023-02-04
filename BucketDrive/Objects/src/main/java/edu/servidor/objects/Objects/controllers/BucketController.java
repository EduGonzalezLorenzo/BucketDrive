package edu.servidor.objects.Objects.controllers;

import edu.servidor.objects.Objects.forms.BucketForm;
import edu.servidor.objects.Objects.models.*;
import edu.servidor.objects.Objects.services.BucketService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class BucketController {
    @Autowired
    BucketService bucketService;

    @GetMapping("/objects/")
    public String showBuckets(HttpSession session, Model model) {
        User user = (User) session.getAttribute("currentUser");
        model.addAttribute("buckets", bucketService.getBuckets(user));
        return "objects";
    }

    @PostMapping("/objects")
    public String createBucket(@Valid BucketForm bucketForm, BindingResult bindingResult, Model model, HttpSession session) {
        String message;
        User user = (User) session.getAttribute("currentUser");
        if (bindingResult.hasErrors()) {
            message = "Data input error";
        } else {
            message = bucketService.createBucket(user, bucketForm.getUri());
        }
        model.addAttribute("message", message);
        model.addAttribute("buckets", bucketService.getBuckets(user));
        return "objects";
    }

    @PostMapping("/deletebucket/{id}")
    public String deleteBucket(@PathVariable int id, Model model, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        model.addAttribute("message", bucketService.deleteBucket(id, user.getUsername()));
        model.addAttribute("buckets", bucketService.getBuckets(user));
        return "objects";
    }

    @GetMapping("/deletebucket/{id}")
    public String accessDeleteError(){
        return "objects";
    }

}
