package com.app.IVAS.entity;

import com.app.IVAS.Enum.RegType;
import com.app.IVAS.entity.userManagement.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(
        name = "edit_portal_user"
)
public class EditPortalUser extends StatusEntity {
    @Basic(
            optional = false
    )
    @Column(
            name = "first_name",
            table = "edit_portal_user",
            nullable = false,
            length = 1024
    )
    private String firstName;
    @Basic(
            optional = false
    )
    @Column(
            name = "last_name",
            table = "edit_portal_user",
            nullable = false,
            length = 1024
    )
    private String lastName;
    @Basic
    @Column(
            name = "other_names",
            table = "edit_portal_user",
            length = 1024
    )
    private String otherNames;
    @Basic(
            optional = false
    )
    @Column(
            name = "email",
            table = "edit_portal_user",
            length = 1024,
            unique = true
    )
    private String email;
    @Basic
    @Column(
            name = "phone_number",
            table = "edit_portal_user",
            nullable = false,
            length = 1024,
            unique = true
    )
    private String phoneNumber;

    private String address;

    private String parentPhoneNumber;
    private Long parentId;
    private String displayName;
    private String dateCreated;

}