package com.example.securebusiness.service.impl;

import com.example.securebusiness.model.Image;
import com.example.securebusiness.repository.DbStorageRepository;
import com.example.securebusiness.utils.ImageUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

@Service
@AllArgsConstructor
public class StorageService {
    private DbStorageRepository storageRepository;

    public String uploadImage(MultipartFile file) throws IOException {
        Image imageData;
        try {
            imageData = storageRepository.save(Image.builder()
                    .name(file.getOriginalFilename()
                    )
                    .type(file.getContentType())
                    .imageData(ImageUtils.compressImage(file.getBytes())).build());
        } catch (IOException exception) {
            throw new IOException(exception.getMessage());
        }
        return imageData.getName();
    }

    public byte[] downloadImage(String fileName) {
        Optional<Image> image = storageRepository.findByName(fileName);
        return ImageUtils.decompressImage(image.map(Image::getImageData).orElse(null));
    }
}
