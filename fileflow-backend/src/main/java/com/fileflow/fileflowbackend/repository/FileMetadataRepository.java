package com.fileflow.fileflowbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.fileflow.fileflowbackend.entity.FileMetadata;
import com.fileflow.fileflowbackend.entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileMetadataRepository
    extends JpaRepository<FileMetadata, Long>
{
    List<FileMetadata> findByOwner(User owner);

    Optional<FileMetadata> findByFileNameAndOwner(
        String fileName,
        User owner
    );

    @Transactional
    void deleteByFileNameAndOwner(
        String fileName,
        User owner
    );
}