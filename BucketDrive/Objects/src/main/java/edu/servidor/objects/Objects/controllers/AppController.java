package edu.servidor.objects.Objects.controllers;

import edu.servidor.objects.Objects.forms.BucketForm;
import edu.servidor.objects.Objects.forms.UserForm;
import edu.servidor.objects.Objects.models.Bucket;
import edu.servidor.objects.Objects.models.ObjectFile;
import edu.servidor.objects.Objects.models.User;
import edu.servidor.objects.Objects.services.MyService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
        if (bindingResult.hasErrors()) {
            model.addAttribute("message", "Data input error");
            return "login";
        }
        User user = service.login(userForm.getUsername(), userForm.getPassword());
        if (user != null) {
            session.setAttribute("currentUser", user);
            return "redirect:/objects";
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
            message = "Data input error";
        } else {
            message = service.modifyUser(userForm.getName(), userForm.getPassword(), user.getUsername());
            session.setAttribute("currentUser", service.getUserById(user.getUsername()));
        }
        model.addAttribute("currentUser", session.getAttribute("currentUser"));
        model.addAttribute("message", message);

        return "settings";
    }

    @GetMapping("/objects")
    public String objects(HttpSession session, Model model) {
        User user = (User) session.getAttribute("currentUser");
        model.addAttribute("buckets", service.getBuckets(user));
        return "objects";
    }

    @PostMapping("/objects")
    public String objects(@Valid BucketForm bucketForm, BindingResult bindingResult, Model model, HttpSession session) {
        String message = "";
        User user = (User) session.getAttribute("currentUser");
        if (bindingResult.hasErrors()) {
            message = "Data input error";
        } else {
            message = service.createBucket(user, bucketForm.getUri());
        }
        model.addAttribute("message", message);
        model.addAttribute("buckets", service.getBuckets(user));
        return "objects";
    }

    @GetMapping("/objects/{name}/")
    public String showBucket(@PathVariable String name, HttpSession session, Model model) {
        User user = (User) session.getAttribute("currentUser");
        int bucketID = service.getBucketID(name, user.getUsername());
        List<ObjectFile> objects = service.getObjectsFromBucket(bucketID);
        model.addAttribute("bucket", name);
        model.addAttribute("objects", objects);
        return "bucket";
    }

    @PostMapping("/objects/{name}/")
    public String createObject(@PathVariable String name, @RequestParam("file") MultipartFile file, @RequestParam("path") String path, Model model, HttpSession session) throws IOException {
        User user = (User) session.getAttribute("currentUser");
        Bucket bucket = service.getBucketByNameOwner(name, user.getUsername());
        String message = service.createObject(file, path, bucket, user);
        List<ObjectFile> objects = service.getObjectsFromBucket(bucket.getId());

        model.addAttribute("message", message);
        model.addAttribute("bucket", bucket);
        model.addAttribute("objects", objects);

        return ("redirect:/objects/" + bucket.getUri() + "/");
    }

    @PostMapping("/deletebucket/{id}")
    public String deleteBucket(@PathVariable int id, Model model, HttpSession session) {
        model.addAttribute("message", service.deleteBucket(id));
        model.addAttribute("buckets", service.getBuckets((User) session.getAttribute("currentUser")));
        return "objects";
    }

    @GetMapping("/download/{objid}/{fid}")
    public ResponseEntity<byte[]> download(@PathVariable int objid, @PathVariable int fid) {
        ObjectFile objectFile = new ObjectFile(); //TODO obtener el objeto Y versión solicitadas por le usuario
        byte[] fileContent = objectFile.getBody();
        String name = objectFile.getUri(); //TODO hacer service que extraiga el nombre del archivo, no hay que devolver bucket/carpeta/archivo.txt hay que devolver archivo.txt
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf(objectFile.getContentType()));
        headers.setContentLength(objectFile.getContentLength());
        headers.set("Content-disposition", "attachment;filename=" + name);
        return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);
    }
}
