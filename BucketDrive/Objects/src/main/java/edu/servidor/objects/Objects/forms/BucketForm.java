package edu.servidor.objects.Objects.forms;

import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

public class BucketForm {

    @Pattern(regexp = "^[a-zA-Z0-9_-]*[^/][a-zA-Z0-9_-]*$")
    @Length(min = 1, max = 20)
    String uri;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
