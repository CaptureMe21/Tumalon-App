package com.example.tumalonsmartdentalcare.Model;

public class ClientAccount {

    public String userId;
    public String lastName;
    public String firstName;
    public String middleName;
    public String nickname;

    public String dateBirth;
    public String occupation;
    public String civilStatus;
    public String sex;
    public String province;
    public String municipality;
    public String barangay;
    public String nationality;
    public String religion;
    public String purok;
    public String personName;
    public String personNumber;
    public String personAddress;
    public String email;

    public ClientAccount() {
    }

    public ClientAccount(String userId, String lastName, String firstName, String middleName, String nickname, String dateBirth, String occupation,
                         String civilStatus, String sex, String province, String municipality, String barangay, String nationality, String religion,
                         String purok, String personName, String personNumber, String personAddress, String email) {

        this.userId = userId;
        this.lastName = lastName;
        this.firstName = firstName;
        this.middleName = middleName;
        this.nickname = nickname;
        this.dateBirth = dateBirth;
        this.occupation = occupation;
        this.civilStatus = civilStatus;
        this.sex = sex;
        this.province = province;
        this.municipality = municipality;
        this.barangay = barangay;
        this.nationality = nationality;
        this.religion = religion;
        this.purok = purok;
        this.personName = personName;
        this.personNumber = personNumber;
        this.personAddress = personAddress;
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getDateBirth() {
        return dateBirth;
    }

    public void setDateBirth(String dateBirth) {
        this.dateBirth = dateBirth;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getCivilStatus() {
        return civilStatus;
    }

    public void setCivilStatus(String civilStatus) {
        this.civilStatus = civilStatus;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getMunicipality() {
        return municipality;
    }

    public void setMunicipality(String municipality) {
        this.municipality = municipality;
    }

    public String getBarangay() {
        return barangay;
    }

    public void setBarangay(String barangay) {
        this.barangay = barangay;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getReligion() {
        return religion;
    }

    public void setReligion(String religion) {
        this.religion = religion;
    }

    public String getPurok() {
        return purok;
    }

    public void setPurok(String purok) {
        this.purok = purok;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getPersonNumber() {
        return personNumber;
    }

    public void setPersonNumber(String personNumber) {
        this.personNumber = personNumber;
    }

    public String getPersonAddress() {
        return personAddress;
    }

    public void setPersonAddress(String personAddress) {
        this.personAddress = personAddress;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}