package com.app.IVAS.entity;

import com.sun.istack.NotNull;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

@ToString
@Setter
@Getter
@Entity(name = "NIN")
@Table(name = "nin")
@AllArgsConstructor
@NoArgsConstructor
public class NIN {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "BIRTH_COUNTRY")
    private String birthCountry;

    @Column(name = "DOB")
    private String dob;

    @Column(name = "BIRTH_DATE")
    private String birthDate;

    @Column(name = "BIRTH_LGA")
    private String birthLGA;

    @Column(name = "BIRTH_STATE")
    private String birthState;

    @Column(name = "CENTRAL_ID")
    private Long centralId;

    @Column(name = "EDUCATIONAL_LEVEL")
    private String educationalLevel;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "GENDER")
    private String gender;

    @Column(name = "EMPLOYMENT_STATUS")
    private String employmentStatus;

    @Column(name = "FIRST_NAME")
    private String firstName;

    @Column(name = "MIDDLE_NAME")
    private String middleName;

    @Column(name = "HEIGHT")
    private Long height;

    @Column(name = "MARITAL_STATUS")
    private String maritalStatus;

    @Column(name = "NIN", unique = true)
    private String nin;

    @Column(name = "NOK_ADDRESS")
    private String nokAddress;

    @Column(name = "NOK_ADDRESS2")
    private String nokAddress2;

    @Column(name = "NOK_FIRST_NAME")
    private String nokFirstName;

    @Column(name = "NOK_LGA")
    private String nokLGA;

    @Column(name = "NOK_STATE")
    private String nokState;

    @Column(name = "NOK_SURNAME")
    private String nokSurname;

    @Column(name = "NOK_TOWN")
    private String nokTown;

    @Column(name = "NSPOKEN_LANG")
    private String nspokenLang;

    @Column(name = "PHOTO")
    private String photo;

    @Column(name = "PHOTO_FILE_CODE")
    private String photoFileCode;

    @Column(name = "PROFESSION")
    private String profession;

    @Column(name = "RELIGION")
    private String religion;

    @Column(name = "RESIDENCE_ADDRESS_LINE_ONE")
    private String residenceAddressLine1;

    @Column(name = "RESIDENCE_TOWN")
    private String residenceTown;

    @Column(name = "RESIDENCE_LGA")
    private String residenceLga;

    @Column(name = "RESIDENCE_STATE")
    private String residenceState;

    @Column(name = "RESIDENCE_STATUS")
    private String residenceStatus;

    @Column(name = "SELF_ORIGIN_LGA")
    private String selfOriginLga;

    @Column(name = "SELF_ORIGIN_PLACE")
    private String selfOriginPlace;

    @Column(name = "SELF_ORIGIN_STATE")
    private String selfOriginState;

    @Column(name = "SIGNATURE")
    private String signature;

    @Column(name = "SIGNATURE_FILE_CODE")
    private String signatureFileCode;

    @Column(name = "SURNAME")
    private String surname;

    @Column(name = "TELEPHONE", unique = true)
    private String telePhoneNumber;

    @Column(name = "TITLE")
    private String title;

    @NotNull
    @Column(name = "DATE_CREATED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreated  = new Date();

    @NotNull
    @Column(name = "DATE_UPDATED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateUpdated  = new Date();

    private String asin;

}
