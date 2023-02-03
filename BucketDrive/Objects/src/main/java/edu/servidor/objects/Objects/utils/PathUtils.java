package edu.servidor.objects.Objects.utils;

import edu.servidor.objects.Objects.models.Bucket;

public class PathUtils {
    public static String getUrl(Bucket bucket, String path){
        if (path.startsWith("/")) path = path.substring(1);
        if (path.endsWith("/")) path = path.substring(0, path.length() - 1);
        String url =  "redirect:/objects/" + bucket.getUri() + "/" + path;
        return url.endsWith("/") ? url : url + "/";
    }

    public static String extractUriFromPath(String path){
        path = path.replaceAll("%20"," ");
        path = path.substring(1);
        return path.substring(path.indexOf("/") + 1);
    }

    public static String getPathToBack(String path){
        String[] backPathArray = path.split("/");
        StringBuilder backPath = new StringBuilder();
        for (int i = 0; i < backPathArray.length - 1; i++) {
            backPath.append(backPathArray[i]).append("/");
        }
        return String.valueOf(backPath);
    }

}
