package com.example.myapplication.Models;

public class Users {

    private String aboutme, birthdate, education, image, name;
    private Object state;

    public Users() {
    }

    public Users(String aboutme, String birthdate, String education, String image, String name, Object state) {
        this.aboutme = aboutme;
        this.birthdate = birthdate;
        this.education = education;
        this.image = image;
        this.name = name;
        this.state = state;
    }

    public String getAboutme() {
        return aboutme;
    }

    public void setAboutme(String aboutme) {
        this.aboutme = aboutme;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getState() {
        return state;
    }

    public void setState(Object state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "Users{" +
                "aboutme='" + aboutme + '\'' +
                ", birthdate='" + birthdate + '\'' +
                ", education='" + education + '\'' +
                ", image='" + image + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
