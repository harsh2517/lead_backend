package com.accountooze.model;


import java.util.Date;


import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Data;

@MappedSuperclass
@Data
public class Auditable {


    public long creationDate;

    public long lastModifiedDate;

    public Auditable() {
    }

    @PrePersist
    public void onCreate() {
        this.creationDate = new Date().getTime();
        this.onUpdate();
    }

    @PreUpdate
    public void onUpdate() {
        this.lastModifiedDate = new Date().getTime();
    }

}
