package com.hub.tofimotia.models;

import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Type;

import java.util.List;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "users")
public class User extends BaseEntity{

    @Column(nullable = false,unique = true)
    private String email;

    @Column(name ="password_hash",nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private Map<String,Object> profileData;
}
