package com.fileflow.fileflowbackend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Tracks which user uploaded which file, so the file list can be
 * scoped per-user instead of showing every file on the FTP server
 * to every logged-in user.
 *
 * This is a many-to-one relationship: many FileMetadata rows point
 * to one User (the owner).
 */
@Entity
@Table(name = "file_metadata")
public class FileMetadata
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @CreationTimestamp
    private LocalDateTime uploadedAt;

    public FileMetadata()
    {

    }

    public FileMetadata(String fileName, User owner)
    {
        this.fileName = fileName;
        this.owner = owner;
    }

    public Long getId()
    {
        return id;
    }

    public String getFileName()
    {
        return fileName;
    }

    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }

    public User getOwner()
    {
        return owner;
    }

    public void setOwner(User owner)
    {
        this.owner = owner;
    }

    public LocalDateTime getUploadedAt()
    {
        return uploadedAt;
    }
}
