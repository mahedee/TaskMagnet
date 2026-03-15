package com.mahedee.backend.models;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "users", uniqueConstraints = {
    @UniqueConstraint(columnNames = "username"),
    @UniqueConstraint(columnNames = "email")
})
public class User extends BaseEntity<Long>{

    @NotBlank
    @Size(max = 50)
    private String username;
   
    // @NotBlank: This annotation checks that the email variable is not null and not composed of whitespace. This is part of the JSR 303 Bean Validation API.
    // @Size(max = 250): This annotation checks that the email variable has a maximum length of 250 characters.
    // @Email: This annotation checks that the email variable is a valid email address.
    @NotBlank
    @Size(max = 250)
    @Email
    private String email;

    @NotBlank
    @Size(max = 250)
    private String password;

    // The @ManyToMany annotation defines a many-to-many relationship between the User and Role entities. In a many-to-many relationship, a single User can have multiple Roles, and a single Role can be associated with multiple Users.
    // The fetch = FetchType.LAZY attribute is used to specify that the associated Roles should be fetched from the database only when they are actually needed. This is known as lazy loading and is a common performance optimization technique.
    // The @JoinTable annotation defines the join table used to implement the many-to-many relationship. The join table is named "user_roles" and it has two join columns, "user_id" and "role_id".
    // The private Set<Role> roles = new HashSet<>(); line defines a field named "roles" of type Set<Role>. The Set interface represents a collection of unique elements, which in this case are the Roles associated with a User.
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    // Constructor

    public User(){
    }

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    // Getters and setters

    public String getUsername(){
        return username;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public String getEmail(){
        return email;
    }
    
    public void setEmail(String email){
        this.email = email;
    }

    public String getPassword(){
        return password;
    }
    
    public void setPassword(String password){
        this.password = password;
    }

    public Set<Role> getRoles(){
        return roles;
    }

    public void setRoles(Set<Role> roles){
        this.roles = roles;
    }
}
