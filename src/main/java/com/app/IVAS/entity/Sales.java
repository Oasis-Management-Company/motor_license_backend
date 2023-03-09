package com.app.IVAS.entity;

import com.app.IVAS.entity.userManagement.StatusEntity;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "SALES_TABLE")
public class Sales extends StatusEntity {
}
