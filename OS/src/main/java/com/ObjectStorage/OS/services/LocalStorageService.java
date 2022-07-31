package com.ObjectStorage.OS.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class LocalStorageService {
    @Value("${arquivos.raiz}")
    private String raiz;
    public void salvarArquivo(MultipartFile arquivo){

        Path diretorioPath = Paths.get(this.raiz, "arquivos");
        Path arquivoPath = diretorioPath.resolve(arquivo.getOriginalFilename());
        try {
            Files.createDirectories(diretorioPath);
            arquivo.transferTo(arquivoPath);

        } catch (IOException e) {
            throw new RuntimeException("erro ao salvar arquivo", e);
        }
    }
    public ByteArrayResource baixarArquivo(String nome) throws IOException {
        Path diretorioPath = Paths.get(this.raiz, "arquivos");
        Path arquivoPath = diretorioPath.resolve(nome);
        File file =  arquivoPath.toFile();

        Path path = Paths.get(file.getAbsolutePath());
        return  new ByteArrayResource(Files.readAllBytes(path));



    }

    public void limpar(String originalFilename) {
        Path diretorioPath = Paths.get(this.raiz, "arquivos");
        Path arquivoPath = diretorioPath.resolve(originalFilename);
        arquivoPath.toFile().delete();
    }
}
