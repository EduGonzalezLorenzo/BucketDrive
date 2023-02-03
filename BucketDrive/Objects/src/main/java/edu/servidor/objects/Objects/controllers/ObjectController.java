package edu.servidor.objects.Objects.controllers;

import edu.servidor.objects.Objects.models.*;
import edu.servidor.objects.Objects.services.BucketService;
import edu.servidor.objects.Objects.services.ObjectService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
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

import static edu.servidor.objects.Objects.utils.FileUtils.fileBuilder;
import static edu.servidor.objects.Objects.utils.PathUtils.*;

@Controller
public class ObjectController {
    @Autowired
    BucketService bucketService;
    @Autowired
    ObjectService objectService;

    @GetMapping("/objects/{path}/")
    public String showBucketContent(@PathVariable String path, HttpSession session, Model model) {
        User user = (User) session.getAttribute("currentUser");
        if (!bucketService.checkBucketOwner(path, user)) {
            model.addAttribute("message", "You are not the owner of this bucket");

            return "objects";
        }
        model.addAttribute("message", session.getAttribute("message"));
        session.setAttribute("message", null);
        model.addAttribute("bucket", path);
        model.addAttribute("objects",
                objectService.getObjectsFromBucketAndPath(bucketService.getBucketID(path, user.getUsername()), path));

        return "bucket";
    }

    @PostMapping("/objects/{bucketName}/")
    public String createObject(@PathVariable String bucketName, @RequestParam("file") MultipartFile file, @RequestParam("path") String path, Model model, HttpSession session) throws IOException, NoSuchAlgorithmException {
        String message;
        User user = (User) session.getAttribute("currentUser");
        Bucket bucket = bucketService.getBucketByNameOwner(bucketName, user.getUsername());

        if (Objects.equals(file.getOriginalFilename(), "")) message = "You need to upload a file";
        else {
            message = objectService.createObject(file, path, bucket, user);
            model.addAttribute("objects", objectService.getObjectsFromBucket(bucket.getId()));
        }
        session.setAttribute("message", message);

        return getUrl(bucket, path);
    }

    @GetMapping("/objects/{bucket}/**")
    public String navigateAmongObjects(@PathVariable String bucket, HttpServletRequest request, HttpSession session, Model model) {
        String path = extractUriFromPath((String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE));
        User user = (User) session.getAttribute("currentUser");

        model.addAttribute("message", session.getAttribute("message"));
        model.addAttribute("bucket", bucket);
        session.setAttribute("message", null);

        if (!bucketService.checkBucketOwner(bucket, user)) {
            model.addAttribute("message", "You are not the owner of this bucket");

            return "objects";
        }
        if (path.endsWith("/")) {
            int bucketID = bucketService.getBucketID(path.split("/")[0], user.getUsername());
            path = path.substring(0, path.length() - 1);

            model.addAttribute("objects", objectService.getObjectsFromBucketAndPath(bucketID, path));
            model.addAttribute("path", path);
            model.addAttribute("creationPath", path.substring(path.indexOf("/")) + "/");
            model.addAttribute("backPath", getPathToBack(path));

            return "bucket";
        } else {
            List<ReferenceObjectToFile> objectVersions = objectService.getObjectVersions(objectService.getObjectId(path));
            String[] splitPath = path.split("/");

            model.addAttribute("fileName", splitPath[splitPath.length - 1]);
            model.addAttribute("objectId", objectVersions.get(0).getObjectId());
            model.addAttribute("versions", objectVersions);
            model.addAttribute("backPath", getPathToBack(path));

            return "object";
        }
    }

    @GetMapping("/download/{objid}/{fid}")
    public ResponseEntity<byte[]> downloadObjectVersion(@PathVariable int objid, @PathVariable int fid) {
        ObjectFile objectFile = objectService.getObjectFromId(objid);
        FileData fileData = objectService.getFileById(objectService.getObjectFromVersion(objid, fid).getFileId());

        return fileBuilder(objectFile, fileData);
    }

    @PostMapping("/deleteobj/{bucket}/{object}")
    public String deleteObject(@PathVariable int object, Model model, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        model.addAttribute("message", objectService.deleteObject(object, user));
        model.addAttribute("buckets", bucketService.getBuckets(user));

        return "objects";
    }

    @GetMapping("/deleteobj/{bucket}/{object}")
    public String accessDeleteError(){
        return "objects";
    }
}
