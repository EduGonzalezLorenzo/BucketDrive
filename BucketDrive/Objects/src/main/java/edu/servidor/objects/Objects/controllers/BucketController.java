package edu.servidor.objects.Objects.controllers;

import edu.servidor.objects.Objects.forms.BucketForm;
import edu.servidor.objects.Objects.models.Bucket;
import edu.servidor.objects.Objects.models.ObjectFile;
import edu.servidor.objects.Objects.models.ReferenceObjectToFile;
import edu.servidor.objects.Objects.models.User;
import edu.servidor.objects.Objects.services.BucketService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.HandlerMapping;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Controller
public class BucketController {
    @Autowired
    BucketService bucketService;

    @GetMapping("/objects")
    public String objects(HttpSession session, Model model) {
        User user = (User) session.getAttribute("currentUser");
        model.addAttribute("buckets", bucketService.getBuckets(user));
        return "objects";
    }

    @PostMapping("/objects")
    public String objects(@Valid BucketForm bucketForm, BindingResult bindingResult, Model model, HttpSession session) {
        String message = "";
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

    @GetMapping("/objects/{path}/")
    public String showBucketContent(@PathVariable String path, HttpSession session, Model model) {
        User user = (User) session.getAttribute("currentUser");
        int bucketID = bucketService.getBucketID(path, user.getUsername());
        List<String> objects = bucketService.getObjectsFromBucketFromUri(bucketID, path);
        model.addAttribute("bucket", path);
        model.addAttribute("objects", objects);
        return "bucket";
    }

    @PostMapping("/objects/{bucketName}/")
    public String createObject(@PathVariable String bucketName, @RequestParam("file") MultipartFile file, @RequestParam("path") String path, Model model, HttpSession session) throws IOException {
        //TODO añadir ya hay algo con el mismo path Y nombre de fichero añadirlo como versión de ese objeto en la tabla objectToFile
        String message = "";
        User user = (User) session.getAttribute("currentUser");
        Bucket bucket = bucketService.getBucketByNameOwner(bucketName, user.getUsername());
        if (Objects.equals(file.getOriginalFilename(), "")) message = "You need to upload a file";
        else {
            message = bucketService.createObject(file, path, bucket, user);
            List<ObjectFile> objects = bucketService.getObjectsFromBucket(bucket.getId());
            model.addAttribute("objects", objects);
        }
        model.addAttribute("bucket", bucket);
        model.addAttribute("message", message);
        //Message se pierde al hacer redirect pero si no se hace da error, revisar
        return ("redirect:/objects/" + bucket.getUri() + "/");
    }

    @GetMapping("/objects/{bucket}/**")
    public String setObject(HttpServletRequest request, HttpSession session, Model model) {
        String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        path = path.substring(1);
        path = path.substring(path.indexOf("/")+1);

        if (path.endsWith("/")){
            User user = (User) session.getAttribute("currentUser");
            int bucketID = bucketService.getBucketID(path.split("/")[0], user.getUsername());
            List<String> objects = bucketService.getObjectsFromBucketFromUri(bucketID, path);

            model.addAttribute("bucket", path.substring(0,path.length()-1));
            model.addAttribute("objects", objects);

            return "bucket";
        }else {
            List<ReferenceObjectToFile> objectVersions = bucketService.getObjectVersions(bucketService.getObjectId(path));
            String[] splitPath = path.split("/");
            model.addAttribute("fileName", splitPath[splitPath.length-1]);
            model.addAttribute("versions", objectVersions);

            return "object";//template de opciones de un objeto
        }
    }

//    @GetMapping("/download/{objid}/{fid}")
//    public ResponseEntity<byte[]> download(@PathVariable int objid, @PathVariable int fid) {
//        ObjectFile objectFile = new ObjectFile(); //TODO obtener el objeto Y versión solicitadas por le usuario
//        byte[] fileContent = objectFile.get;
//        String name = objectFile.getUri(); //TODO hacer service que extraiga el nombre del archivo, no hay que devolver bucket/carpeta/archivo.txt hay que devolver archivo.txt
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.valueOf(objectFile.getContentType()));
//        headers.setContentLength(fileContent.length);
//        headers.set("Content-disposition", "attachment;filename=" + name);
//        return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);
//    }

    @PostMapping("/deletebucket/{id}")
    public String deleteBucket(@PathVariable int id, Model model, HttpSession session) {
        model.addAttribute("message", bucketService.deleteBucket(id));
        model.addAttribute("buckets", bucketService.getBuckets((User) session.getAttribute("currentUser")));
        return "objects";
    }
}
