package com.app.IVAS.entity;

import com.app.IVAS.Enum.DataStorageConstant;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@Entity
public class ImageResolution implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;

    private String name;

    private String code;

    private int width;

    private int height;

    private boolean original;

    private String url;

    @Column(name = "file_code")
    private String fileCode;

    @Enumerated(EnumType.STRING)
    private DataStorageConstant dataStorageConstant;

    private java.sql.Timestamp dateCreated;

    private java.sql.Timestamp dateUpdated;
}
