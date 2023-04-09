package com.app.IVAS.entity;

import com.app.IVAS.Enum.DataStorageConstant;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "file_upload")
public class FileModel implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;

    private String name;

    private String code;

    @OneToMany(
            cascade = {CascadeType.ALL},
            fetch = FetchType.LAZY
    )
    @JoinColumn(name = "file_code", referencedColumnName = "code")
    private List<ImageResolution> imageResolutions;

    private String url;

    @Enumerated(EnumType.STRING)
    private DataStorageConstant dataStorageConstant;

    private Timestamp dateCreated;

    private Timestamp dateUpdated;
}
