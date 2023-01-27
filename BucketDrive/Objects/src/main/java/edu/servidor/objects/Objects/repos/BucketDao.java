package edu.servidor.objects.Objects.repos;

import edu.servidor.objects.Objects.models.Bucket;
import edu.servidor.objects.Objects.models.User;

import java.util.List;

public interface BucketDao {
    int createBucket(User currentUser, String uri);

    List<Bucket> getBucketFromUri(String uri);

    List<Bucket> getBucketsFromUser(String username);

    int deleteBucket(int id);

    List<Bucket> getBucketsById(int id);

    List<Bucket>  getBucketByNameOwner(String name, String username);

}
