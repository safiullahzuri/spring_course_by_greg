package com.example.truedemo.services;


import com.example.truedemo.models.Image;
import com.example.truedemo.models.User;
import com.example.truedemo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;
import com.example.truedemo.repositories.ImageRepository;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class ImageService {

    private static String UPLOAD_ROOT = "C://uploaded";

    private final ImageRepository imageRepository;
    private final ResourceLoader resourceLoader;
    private final SimpMessagingTemplate simpMessagingTemplate;

    private final UserRepository userRepository;

    @Autowired
    public ImageService(ImageRepository imageRepository, ResourceLoader resourceLoader, SimpMessagingTemplate simpMessagingTemplate, UserRepository userRepository){
        this.imageRepository = imageRepository;
        this.resourceLoader = resourceLoader;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.userRepository = userRepository;
    }

    public Page<Image> findPage(Pageable pageable){
        return imageRepository.findAll(pageable);
    }

    public Resource findOneImage(String fileName){
        return resourceLoader.getResource(UPLOAD_ROOT+"/"+fileName);
    }

    public void createImage(MultipartFile file) throws IOException {
        if(!file.isEmpty()){
            Files.copy(file.getInputStream(), Paths.get(UPLOAD_ROOT, file.getOriginalFilename()));
            imageRepository.save(new Image(file.getOriginalFilename(), userRepository.findUserByUsername(SecurityContextHolder.getContext().getAuthentication().getName())));
            simpMessagingTemplate.convertAndSend("/topic/newImage", file.getOriginalFilename());
        }
    }

    @PreAuthorize("@imageRepository.findByName(#fileName)?.owner?.username == authentication?.name or hasRole('ADMIN')")
    public void deleteImage(@Param("fileName") String fileName) throws IOException{
        Image image = imageRepository.findByName(fileName);
        imageRepository.delete(image);
        Files.deleteIfExists(Paths.get(UPLOAD_ROOT, fileName));
        simpMessagingTemplate.convertAndSend("/topic/deleteImage", fileName);
    }

    //command line runners are invoked once all the beans are setup
    @Bean
    CommandLineRunner setUp(ImageRepository imageRepository){
        return (args) -> {
            FileSystemUtils.deleteRecursively(new File(UPLOAD_ROOT));
            Files.createDirectory(Paths.get(UPLOAD_ROOT));

            User user1 = userRepository.findUserByUsername("safi");
            User user2 = userRepository.findUserByUsername("greg");

            FileCopyUtils.copy("Test file", new FileWriter(UPLOAD_ROOT+"/test1"));
            imageRepository.save(new Image("test1", user1));

            FileCopyUtils.copy("Test 2", new FileWriter(UPLOAD_ROOT+"/test2"));
            imageRepository.save(new Image("test2", user1));

            FileCopyUtils.copy("Test 3", new FileWriter(UPLOAD_ROOT+"/test3"));
            imageRepository.save(new Image("test3", user2));


        };
    }


}
