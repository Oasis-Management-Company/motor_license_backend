package com.app.IVAS.entity;


import com.app.IVAS.Enum.ActivityStatusConstant;
import com.app.IVAS.entity.userManagement.PortalUser;
import com.app.IVAS.entity.userManagement.StatusEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "ACTIVITY_LOG")
public class ActivityLog extends StatusEntity {

    private String description;

    @Enumerated(EnumType.STRING)
    private ActivityStatusConstant action;
}
