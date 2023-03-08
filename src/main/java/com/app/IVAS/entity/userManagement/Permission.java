package com.app.IVAS.entity.userManagement;


import com.app.IVAS.Enum.PermissionTypeConstant;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "PERMISSION")
public class Permission {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private PermissionTypeConstant permissionTypeConstant;

    @ManyToOne
    @JoinColumn(name = "_ID", referencedColumnName = "id")
    private Role role;

}
