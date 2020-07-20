package com.example.truedemo.controllers;


import com.example.truedemo.models.Image;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.example.truedemo.services.ImageService;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@Controller
public class HomeController {

    private static final String BASE_PATH ="/images";
    private static final String FILENAME = "{filename:.+}";
    private static final String UPLOAD_PATH = "C://uploaded";

    private final ImageService imageService;

    @Autowired
    public HomeController(ImageService imageService) {
        this.imageService = imageService;
    }

    @RequestMapping(value = "/")
    public String index(Model model, Pageable pageable){
        final Page<Image> page = imageService.findPage(pageable);
        model.addAttribute("page", page);
        if(page.hasNext()){
            model.addAttribute("next", pageable.next());
        }
        if(page.hasPrevious()){
            model.addAttribute("prev", pageable.previousOrFirst());
        }
        return "index";
    }


    @RequestMapping(method = RequestMethod.POST, value = "/saveImage")
    public String saveImage(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes){
        try{
            imageService.createImage(file);
            redirectAttributes.addFlashAttribute("flash.message", "You successfully added an image.");
        }catch (Exception e){
            redirectAttributes.addFlashAttribute("flash.message", "Failed to upload "+file.getOriginalFilename()+"=>"+ e.getMessage());
        }
        return "redirect:/";
    }


    @RequestMapping(method = RequestMethod.GET, value = UPLOAD_PATH+"/"+ FILENAME)
    @ResponseBody
    public ResponseEntity<?> oneRawImage(@PathVariable String fileName){
        try{
            Resource file  = imageService.findOneImage(fileName);
            return ResponseEntity.ok()
                    .contentLength(file.contentLength())
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(new InputStreamResource(file.getInputStream()));
        }catch (IOException ex){
            return ResponseEntity.badRequest()
                    .body("Couldn't find "+fileName+ " => "+ex.getMessage());
        }
    }
    @RequestMapping(method = RequestMethod.POST, value = BASE_PATH)
    @ResponseBody
    public ResponseEntity<?> createFile(@RequestParam("file")MultipartFile file, HttpServletRequest httpRequest){
        try{
            imageService.createImage(file);
            final URI locationURI = new URI(httpRequest.getRequestURL().toString() + "/").resolve(file.getOriginalFilename()+"/raw");
            return ResponseEntity.created(locationURI)
                    .body("Successful update" + file.getOriginalFilename());
        }catch (IOException | URISyntaxException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload "+file.getOriginalFilename()+" => "+e.getMessage());
        }
    }

    @RequestMapping(method = RequestMethod.DELETE, value = BASE_PATH+"/"+FILENAME+"/req")
    @ResponseBody
    public ResponseEntity<?> deleteFile(@PathVariable String fileName){
        try{
            imageService.deleteImage(fileName);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Successfully deleted "+fileName);
        }catch (IOException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete "+fileName+"=> "+e.getMessage());
        }
    }


    @RequestMapping(method = RequestMethod.POST, value = "/delete")
    public String deleteMyFile(@RequestParam("fileName") String fileName, RedirectAttributes redirectAttributes){
        try{
            imageService.deleteImage(fileName);
            redirectAttributes.addFlashAttribute("flash.message", "Image was successfully deleted.");
        }catch (IOException|RuntimeException e){
            redirectAttributes.addFlashAttribute("flash.message", e.getMessage());
        }
        return "redirect:/";
    }










}
