package com.app.IVAS.entity.userManagement;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;


@Data
@Entity
@Table(
        name = "portal_user"
)
public class PortalUser extends StatusEntity {
    @Basic(
            optional = false
    )
    @Column(
            name = "first_name",
            table = "portal_user",
            nullable = false,
            length = 1024
    )
    private String firstName;
    @Basic(
            optional = false
    )
    @Column(
            name = "last_name",
            table = "portal_user",
            nullable = false,
            length = 1024
    )
    private String lastName;
    @Basic
    @Column(
            name = "other_names",
            table = "portal_user",
            length = 1024
    )
    private String otherNames;
    @Basic(
            optional = false
    )
    @Column(
            name = "email",
            table = "portal_user",
            length = 1024,
            unique = true
    )
    private String email;
    @Basic
    @Column(
            name = "phone_number",
            table = "portal_user",
            nullable = false,
            length = 1024
    )
    private String phoneNumber;
    @Basic
    @Column(
            name = "generated_password",
            table = "portal_user",
            length = 1024
    )
    private String generatedPassword;
    @Basic(
            optional = false
    )
    @Column(
            name = "username",
            table = "portal_user",
            nullable = false,
            unique = true,
            length = 1024
    )
    private String username;
    @Basic
    @Column(
            name = "date_of_birth",
            table = "portal_user"
    )
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateOfBirth;
    @Basic(
            optional = false
    )
    @Column(
            name = "display_name",
            table = "portal_user",
            nullable = false,
            length = 1024
    )
    private String displayName;
    @Basic
    @Column(
            name = "email_verified",
            table = "portal_user"
    )
    private Boolean emailVerified;
    @Basic
    @Column(
            name = "preferred_name",
            table = "portal_user",
            length = 1024
    )
    private String preferredName;
    @Basic
    private Boolean userVerified;
    @Basic
    private String nationalIdentificationNumber;
    @Column(
            name = "image",
            table = "portal_user",
            nullable = true,
            length = 1024
    )
    private String image;
    @Column(name = "STATE_ID")
    private Long stateId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private Lga lga;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private Area area;

    @OneToOne
    private Role role;
    private String address;

    @ManyToOne
    private ZonalOffice office;

}