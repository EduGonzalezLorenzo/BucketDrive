package edu.servidor.objects.Objects.repos;

import edu.servidor.objects.Objects.models.Bucket;
import edu.servidor.objects.Objects.models.User;

import java.util.List;

public interface BucketDao {
    int createBucket(User currentUser, String uri);

    boolean checkUri(String finalUri, int owner);

    List<Bucket> getBucketsFromUser(int owner);

    int deleteBucket(int id);

    Bucket getBucketById(int id);
}
