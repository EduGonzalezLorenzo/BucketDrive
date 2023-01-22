package edu.servidor.objects.Objects.forms;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

public class UserForm {
    @Length(min = 3, max = 10)
    String name;

    @Length(min = 3, max = 10)
    String password;

    @Length(min = 3, max = 30)
    String realName;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }
}
