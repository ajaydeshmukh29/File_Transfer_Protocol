package com.fileflow.fileflowbackend.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.fileflow.fileflowbackend.entity.FileMetadata;
import com.fileflow.fileflowbackend.entity.User;
import com.fileflow.fileflowbackend.repository.FileMetadataRepository;
import com.fileflow.fileflowbackend.repository.UserRepository;
import com.fileflow.fileflowbackend.security.UserPrincipal;
import com.fileflow.fileflowbackend.service.FtpService;

import java.util.List;
import java.util.stream.Collectors;

// CORS is configured centrally in SecurityConfig.
// Every endpoint here requires a valid JWT.
// File operations are scoped to the authenticated user.
@RestController
@RequestMapping("/api/files")
public class FileController
{
    private final FtpService ftpService;
    private final FileMetadataRepository fileMetadataRepository;
    private final UserRepository userRepository;

    public FileController(
        FtpService ftpService,
        FileMetadataRepository fileMetadataRepository,
        UserRepository userRepository
    )
    {
        this.ftpService = ftpService;
        this.fileMetadataRepository = fileMetadataRepository;
        this.userRepository = userRepository;
    }

    private User currentUser(Authentication authentication)
    {
        UserPrincipal principal =
            (UserPrincipal) authentication.getPrincipal();

        return userRepository.findByEmail(principal.getUsername());
    }

    // LIST — only files belonging to the logged-in user.
    @GetMapping
    public String getFiles(Authentication authentication)
    {
        User user = currentUser(authentication);

        List<FileMetadata> ownedFiles =
            fileMetadataRepository.findByOwner(user);

        if (ownedFiles.isEmpty())
        {
            return "";
        }

        return ownedFiles.stream()
            .map(FileMetadata::getFileName)
            .collect(Collectors.joining("\n"));
    }

    // DELETE
    @DeleteMapping("/{fileName}")
    public String deleteFile(
        @PathVariable String fileName,
        Authentication authentication
    )
    {
        User user = currentUser(authentication);

        boolean owns =
            fileMetadataRepository
                .findByFileNameAndOwner(fileName, user)
                .isPresent();

        if (!owns)
        {
            return "You do not have permission to delete this file";
        }

        String result = ftpService.deleteFile(fileName);

        // Remove database metadata only when the FTP deletion succeeds.
        if ("File deleted successfully".equals(result))
        {
            fileMetadataRepository.deleteByFileNameAndOwner(
                fileName,
                user
            );
        }

        return result;
    }

    // RENAME
    @PutMapping("/rename")
    public String renameFile(
        @RequestParam String oldFileName,
        @RequestParam String newFileName,
        Authentication authentication
    )
    {
        User user = currentUser(authentication);

        FileMetadata metadata =
            fileMetadataRepository
                .findByFileNameAndOwner(oldFileName, user)
                .orElse(null);

        if (metadata == null)
        {
            return "You do not have permission to rename this file";
        }

        String result =
            ftpService.renameFile(oldFileName, newFileName);

        // Update database only when FTP rename succeeds.
        if ("File renamed successfully".equals(result))
        {
            metadata.setFileName(newFileName);
            fileMetadataRepository.save(metadata);
        }

        return result;
    }

    // EXISTS
    @GetMapping("/{fileName}/exists")
    public String checkFileExists(
        @PathVariable String fileName,
        Authentication authentication
    )
    {
        User user = currentUser(authentication);

        boolean owns =
            fileMetadataRepository
                .findByFileNameAndOwner(fileName, user)
                .isPresent();

        if (!owns)
        {
            return "You do not have permission to check this file";
        }

        return ftpService.checkFileExists(fileName);
    }

    // INFO
    @GetMapping("/{fileName}/info")
    public String getFileInfo(
        @PathVariable String fileName,
        Authentication authentication
    )
    {
        User user = currentUser(authentication);

        boolean owns =
            fileMetadataRepository
                .findByFileNameAndOwner(fileName, user)
                .isPresent();

        if (!owns)
        {
            return "You do not have permission to view this file";
        }

        return ftpService.getFileInfo(fileName);
    }

    // SIZE
    @GetMapping("/{fileName}/size")
    public String getFileSize(
        @PathVariable String fileName,
        Authentication authentication
    )
    {
        User user = currentUser(authentication);

        boolean owns =
            fileMetadataRepository
                .findByFileNameAndOwner(fileName, user)
                .isPresent();

        if (!owns)
        {
            return "You do not have permission to view this file";
        }

        return ftpService.getFileSize(fileName);
    }

    // DOWNLOAD / GET
    @GetMapping("/{fileName}/download")
    public ResponseEntity<?> downloadFile(
        @PathVariable String fileName,
        Authentication authentication
    )
    {
        User user = currentUser(authentication);

        boolean owns =
            fileMetadataRepository
                .findByFileNameAndOwner(fileName, user)
                .isPresent();

        if (!owns)
        {
            return ResponseEntity
                .status(403)
                .body("You do not have permission to download this file");
        }

        FtpService.DownloadResult result =
            ftpService.downloadFile(fileName);

        if (!result.isSuccess())
        {
            return ResponseEntity
                .status(404)
                .body(result.getMessage());
        }

        return ResponseEntity.ok()
            .header(
                HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + fileName + "\""
            )
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(result.getData());
    }

    // UPLOAD / PUT
    @PostMapping("/upload")
    public String uploadFile(
        @RequestParam("file") MultipartFile file,
        Authentication authentication
    )
    {
        User user = currentUser(authentication);

        String result = ftpService.uploadFile(file);

        // Save metadata only when FTP upload succeeds.
        if ("File uploaded successfully".equals(result))
        {
            fileMetadataRepository.save(
                new FileMetadata(
                    file.getOriginalFilename(),
                    user
                )
            );
        }

        return result;
    }
}