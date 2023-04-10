package com.app.IVAS.nin.search_by_nin;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class FindByNinDataResponsePojo {

    private String birthCountry;

    private String birthDate;

    private String birthLGA;

    private String birthState;

    private Long centralId;

    private String educationalLevel;

    private String email;

    private String employmentStatus;

    private String firstName;

    private String middleName;

    private String gender;

    private Long height;

    private String maritalStatus;

    private String nin;

    private String nokAddress;

    private String nokAddress2;

    private String nokFirstName;

    private String nokLGA;

    private String nokState;

    private String nokSurname;

    private String nokTown;

    private String nspokenLang;

    private String photo;

    private String profession;

    private String religion;

    private String residenceAddressLine1;

    private String residenceTown;

    private String residenceLga;

    private String residenceState;

    private String residenceStatus;

    private String selfOriginLga;

    private String selfOriginPlace;

    private String selfOriginState;

    private String signature;

    private String surname;

    private String telePhoneNumber;

    private String title;

    @XmlElement(name="birthdate")
    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    @XmlElement(name="birthlga")
    public String getBirthLGA() {
        return birthLGA;
    }

    public void setBirthLGA(String birthLGA) {
        this.birthLGA = birthLGA;
    }

    @XmlElement(name="birthstate")
    public String getBirthState() {
        return birthState;
    }

    public void setBirthState(String birthState) {
        this.birthState = birthState;
    }

    @XmlElement(name="centralID")
    public Long getCentralId() {
        return centralId;
    }

    public void setCentralId(Long centralId) {
        this.centralId = centralId;
    }

    @XmlElement(name="educationallevel")
    public String getEducationalLevel() {
        return educationalLevel;
    }

    public void setEducationalLevel(String educationalLevel) {
        this.educationalLevel = educationalLevel;
    }

    @XmlElement(name="email")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @XmlElement(name="emplymentstatus")
    public String getEmploymentStatus() {
        return employmentStatus;
    }

    public void setEmploymentStatus(String employmentStatus) {
        this.employmentStatus = employmentStatus;
    }

    @XmlElement(name="firstname")
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @XmlElement(name="middlename")
    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }


    public void setGender(String gender) {
        this.gender = gender;
    }

    @XmlElement(name="heigth")
    public Long getHeight() {
        return height;
    }

    public void setHeight(Long height) {
        this.height = height;
    }

    @XmlElement(name="maritalstatus")
    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public String getGender() {
        return gender;
    }

    public void setResidenceState(String residenceState) {
        this.residenceState = residenceState;
    }

    @XmlElement(name="nin")
    public String getNin() {
        return nin;
    }

    public void setNin(String nin) {
        this.nin = nin;
    }

    @XmlElement(name="nok_address1")
    public String getNokAddress() {
        return nokAddress;
    }

    public void setNokAddress(String nokAddress) {
        this.nokAddress = nokAddress;
    }

    @XmlElement(name="nok_address2")
    public String getNokAddress2() {
        return nokAddress2;
    }

    public void setNokAddress2(String nokAddress2) {
        this.nokAddress2 = nokAddress2;
    }

    @XmlElement(name="nok_firstname")
    public String getNokFirstName() {
        return nokFirstName;
    }

    public void setNokFirstName(String nokFirstName) {
        this.nokFirstName = nokFirstName;
    }

    @XmlElement(name="nok_lga")
    public String getNokLGA() {
        return nokLGA;
    }

    public void setNokLGA(String nokLGA) {
        this.nokLGA = nokLGA;
    }

    @XmlElement(name="nok_state")
    public String getNokState() {
        return nokState;
    }

    public void setNokState(String nokState) {
        this.nokState = nokState;
    }

    @XmlElement(name="nok_surname")
    public String getNokSurname() {
        return nokSurname;
    }

    public void setNokSurname(String nokSurname) {
        this.nokSurname = nokSurname;
    }

    @XmlElement(name="nok_town")
    public String getNokTown() {
        return nokTown;
    }

    public void setNokTown(String nokTown) {
        this.nokTown = nokTown;
    }

    @XmlElement(name="nspokenlang")
    public String getNspokenLang() {
        return nspokenLang;
    }

    public void setNspokenLang(String nspokenLang) {
        this.nspokenLang = nspokenLang;
    }

    @XmlElement(name="photo")
    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    @XmlElement(name="profession")
    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    @XmlElement(name="religion")
    public String getReligion() {
        return religion;
    }

    public void setReligion(String religion) {
        this.religion = religion;
    }

    @XmlElement(name="residence_AdressLine1")
    public String getResidenceAddressLine1() {
        return residenceAddressLine1;
    }

    public void setResidenceAddressLine1(String residenceAddressLine1) {
        this.residenceAddressLine1 = residenceAddressLine1;
    }

    @XmlElement(name="residence_Town")
    public String getResidenceTown() {
        return residenceTown;
    }

    public void setResidenceTown(String residenceTown) {
        this.residenceTown = residenceTown;
    }

    @XmlElement(name="residence_lga")
    public String getResidenceLga() {
        return residenceLga;
    }

    public void setResidenceLga(String residenceLga) {
        this.residenceLga = residenceLga;
    }

    @XmlElement(name="residence_state")
    public String getResidenceState() {
        return residenceState;
    }

    public void setResidence_state(String residence_state) {
        this.residenceState = residence_state;
    }

    @XmlElement(name="residencestatus")
    public String getResidenceStatus() {
        return residenceStatus;
    }

    public void setResidenceStatus(String residenceStatus) {
        this.residenceStatus = residenceStatus;
    }

    @XmlElement(name="self_origin_lga")
    public String getSelfOriginLga() {
        return selfOriginLga;
    }

    public void setSelfOriginLga(String selfOriginLga) {
        this.selfOriginLga = selfOriginLga;
    }

    @XmlElement(name="self_origin_place")
    public String getSelfOriginPlace() {
        return selfOriginPlace;
    }

    public void setSelfOriginPlace(String selfOriginPlace) {
        this.selfOriginPlace = selfOriginPlace;
    }

    @XmlElement(name="self_origin_state")
    public String getSelfOriginState() {
        return selfOriginState;
    }

    public void setSelfOriginState(String selfOriginState) {
        this.selfOriginState = selfOriginState;
    }

    @XmlElement(name="signature")
    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    @XmlElement(name="surname")
    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    @XmlElement(name="telephoneno")
    public String getTelePhoneNumber() {
        return telePhoneNumber;
    }

    public void setTelePhoneNumber(String telePhoneNumber) {
        this.telePhoneNumber = telePhoneNumber;
    }

    @XmlElement(name="title")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @XmlElement(name="birthcountry")
    public String getBirthCountry() {
        return birthCountry;
    }

    public void setBirthCountry(String birthCountry) {
        this.birthCountry = birthCountry;
    }
}
