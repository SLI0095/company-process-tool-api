package com.semestral_project.company_process_tool.entities;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="item_type",
        discriminatorType = DiscriminatorType.STRING)

public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;
    @Column(columnDefinition="LONGTEXT")
    private String briefDescription;
    @Column(columnDefinition="LONGTEXT")
    private String mainDescription;
    private String version;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate changeDate;
    @Column(columnDefinition="LONGTEXT")
    private String changeDescription;

    @ManyToMany
    @JoinTable(name = "item_user_access",
            joinColumns = {@JoinColumn(name = "element_id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id")})
    private List<UserType> hasAccess = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "item_user_edit",
            joinColumns = {@JoinColumn(name = "element_id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id")})
    private List<UserType> canEdit = new ArrayList<>();

    @ManyToOne
    private User owner;

    public Item() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBriefDescription() {
        return briefDescription;
    }

    public void setBriefDescription(String briefDescription) {
        this.briefDescription = briefDescription;
    }

    public String getMainDescription() {
        return mainDescription;
    }

    public void setMainDescription(String mainDescription) {
        this.mainDescription = mainDescription;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public LocalDate getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(LocalDate changeDate) {
        this.changeDate = changeDate;
    }

    public String getChangeDescription() {
        return changeDescription;
    }

    public void setChangeDescription(String changeDescription) {
        this.changeDescription = changeDescription;
    }

    public List<UserType> getHasAccess() {
        return hasAccess;
    }

    public void setHasAccess(List<UserType> hasAccess) {
        this.hasAccess = hasAccess;
    }

    public List<UserType> getCanEdit() {
        return canEdit;
    }

    public void setCanEdit(List<UserType> canEdit) {
        this.canEdit = canEdit;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }
}
