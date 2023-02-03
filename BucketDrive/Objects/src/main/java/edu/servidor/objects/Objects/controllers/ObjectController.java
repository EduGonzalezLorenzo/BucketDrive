package edu.servidor.objects.Objects.controllers;

import edu.servidor.objects.Objects.models.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.HandlerMapping;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Objects;

@Controller
public class ObjectAndFileController {


    @GetMapping("/objects/{path}/")
    public String showBucketContent(@PathVariable String path, HttpSession session, Model model) {
        User user = (User) session.getAttribute("currentUser");
        if (!bucketService.checkOwner(path, user)) {
            model.addAttribute("message", "You are not the owner of this bucket");
            return "objects";
        }
        int bucketID = bucketService.getBucketID(path, user.getUsername());
        List<String> objects = bucketService.getObjectsFromBucketFromUri(bucketID, path);
        model.addAttribute("message", session.getAttribute("message"));
        session.setAttribute("message", null);
        model.addAttribute("bucket", path);
        model.addAttribute("objects", objects);
        return "bucket";
    }

    @PostMapping("/objects/{bucketName}/")
    public String createObject(@PathVariable String bucketName, @RequestParam("file") MultipartFile file, @RequestParam("path") String path, Model model, HttpSession session) throws IOException, NoSuchAlgorithmException {
        String message;
        User user = (User) session.getAttribute("currentUser");
        Bucket bucket = bucketService.getBucketByNameOwner(bucketName, user.getUsername());
        if (Objects.equals(file.getOriginalFilename(), "")) message = "You need to upload a file";
        else {
            message = bucketService.createObject(file, path, bucket, user);
            List<ObjectFile> objects = bucketService.getObjectsFromBucket(bucket.getId());
            model.addAttribute("objects", objects);
        }
        session.setAttribute("message", message);
        if (path.startsWith("/")) path = path.substring(1);
        if (path.endsWith("/")) path = path.substring(0, path.length() - 1);
        String url = "redirect:/objects/" + bucket.getUri() + "/" + path;
        return url.endsWith("/") ? url : url + "/";
    }

    @GetMapping("/objects/{bucket}/**")
    public String navigateAmongObjects(@PathVariable String bucket, HttpServletRequest request, HttpSession session, Model model) {
        String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        path = path.substring(1);
        path = path.substring(path.indexOf("/") + 1);
        User user = (User) session.getAttribute("currentUser");

        model.addAttribute("message", session.getAttribute("message"));
        session.setAttribute("message", null);
        model.addAttribute("bucket", bucket);

        if (!bucketService.checkOwner(bucket, user)) {
            model.addAttribute("message", "You are not the owner of this bucket");
            return "objects";
        }
        if (path.endsWith("/")) {
            int bucketID = bucketService.getBucketID(path.split("/")[0], user.getUsername());
            List<String> objects = bucketService.getObjectsFromBucketFromUri(bucketID, path);
            model.addAttribute("objects", objects);
            path = path.substring(0, path.length() - 1);
            model.addAttribute("path", path);
            model.addAttribute("creationPath", path.substring(path.indexOf("/")) + "/");
            String[] backPathArray = path.split("/");
            StringBuilder backPath = new StringBuilder();
            for (int i = 0; i < backPathArray.length - 1; i++) {
                backPath.append(backPathArray[i]).append("/");
            }
            model.addAttribute("backPath", backPath);

            return "bucket";
        } else {
            List<ReferenceObjectToFile> objectVersions = bucketService.getObjectVersions(bucketService.getObjectId(path));
            String[] splitPath = path.split("/");
            int objectId = objectVersions.get(0).getObjectId();
            model.addAttribute("fileName", splitPath[splitPath.length - 1]);
            model.addAttribute("objectId", objectId);
            model.addAttribute("versions", objectVersions);

            String[] backPathArray = path.split("/");
            StringBuilder backPath = new StringBuilder();
            for (int i = 0; i < backPathArray.length - 1; i++) {
                backPath.append(backPathArray[i]).append("/");
            }
            model.addAttribute("backPath", backPath);

            return "object";
        }
    }

    @GetMapping("/download/{objid}/{fid}")
    public ResponseEntity<byte[]> download(@PathVariable int objid, @PathVariable int fid) {
        ReferenceObjectToFile reference = bucketService.getObjectFromVersion(objid, fid);
        FileData fileData = bucketService.getFileById(reference.getFileId());
        ObjectFile objectFile = bucketService.getObjectFromId(objid);

        byte[] fileContent = fileData.getBody();
        String name = bucketService.getFileName(objectFile);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf(objectFile.getContentType()));
        headers.setContentLength(fileContent.length);
        headers.set("Content-disposition", "attachment;filename=" + name);
        return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);
    }

    @GetMapping("/deleteobj/{bucket}/{object}")
    public String deleteObject(@PathVariable int object, Model model, HttpSession session) {
        model.addAttribute("message", bucketService.deleteObject(object));
        model.addAttribute("buckets", bucketService.getBuckets((User) session.getAttribute("currentUser")));
        return "objects";
    }
}