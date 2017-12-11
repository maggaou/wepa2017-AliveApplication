package wad.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import wad.repository.FileObjectRepository;

@Controller
public class KuvaController {
    
    @Autowired
    FileObjectRepository fileObjectRepository;
    
     
    @GetMapping(path = "/kuva/{id}", produces = "image/*")
    @ResponseBody
    public byte[] haeKuva(@PathVariable Long id) {
        return fileObjectRepository.findById(id).get().getSisalto();
    }
}
