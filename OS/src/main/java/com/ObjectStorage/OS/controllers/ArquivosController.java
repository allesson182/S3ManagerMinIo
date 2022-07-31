package com.ObjectStorage.OS.controllers;

import com.ObjectStorage.OS.services.LocalStorageService;
import com.ObjectStorage.OS.services.StorageService;
import io.minio.errors.*;
import io.minio.messages.Bucket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Controller
public class ArquivosController {

    @Autowired
    StorageService storageService;

    @Autowired
    LocalStorageService localStorageService;

    @GetMapping("")
    public String home(Model model) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        model.addAttribute("listaArquivos", storageService.getObjectsFromBucket());
                return "index";
    }


    @PostMapping("/arquivo")
    public String uploadAquivo(@RequestParam MultipartFile file, Model model){
        try {
            localStorageService.salvarArquivo(file);
            storageService.uploadArquivo(file, "buckettest");
            localStorageService.limpar(file.getOriginalFilename());
        }catch (Exception e){
            model.addAttribute("message", e.getMessage());
            return "index";
        }
        return "redirect:/";
    }

        @GetMapping("/deletararquivo")
    public String deletarArquivo(@RequestParam String nome, Model model){
        try {
            storageService.deletarArquivo(nome);
        }catch (Exception e){
            model.addAttribute("message", e.getMessage());
            model.addAttribute("listaArquivos",storageService.getObjectsFromBucket());
            return "/";
        }
        return "redirect:/";
    }

    @GetMapping("/baixararquivo")
   public ResponseEntity baixar(@RequestParam String nome) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        this.storageService.baixarArquivo(nome);
        String mimeType =  URLConnection.guessContentTypeFromName(nome);
        return  ResponseEntity.ok().contentType(MediaType.parseMediaType(mimeType)).body(localStorageService.baixarArquivo(nome));

   }


}
