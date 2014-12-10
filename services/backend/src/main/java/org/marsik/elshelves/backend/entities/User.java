package org.marsik.elshelves.backend.entities;

import org.hibernate.validator.constraints.Email;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.UUID;

@NodeEntity
public class User {
    @Indexed(unique = true)
    UUID id;

    @NotNull
    String name;

    @NotNull
    @Email
    String email;
    String password;

    String verificationCode;
    Date registrationDate;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public static User fromDto(org.marsik.elshelves.api.entities.User dto) {
        User u = new User();
        u.setEmail(dto.getEmail());
        u.setName(dto.getName());
        u.setPassword(dto.getPassword());
        u.setId(dto.getId());
        return u;
    }

    public org.marsik.elshelves.api.entities.User toDto() {
        org.marsik.elshelves.api.entities.User user = new org.marsik.elshelves.api.entities.User();
        user.setId(getId());
        user.setEmail(getEmail());
        user.setName(getName());
        return user;
    }
}
