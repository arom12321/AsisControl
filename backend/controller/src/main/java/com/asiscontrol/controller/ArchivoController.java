package com.asiscontrol.controller;

import com.asiscontrol.dto.archivo.ArchivoDtos.ArchivoResponse;
import com.asiscontrol.service.ArchivoStorageService;
import com.asiscontrol.service.ArchivoStorageService.DescargaArchivo;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/archivos")
@PreAuthorize("isAuthenticated()")
public class ArchivoController {

    private final ArchivoStorageService archivoService;

    public ArchivoController(ArchivoStorageService archivoService) {
        this.archivoService = archivoService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('ARCHIVOS_ESCRIBIR')")
    public ResponseEntity<ArchivoResponse> subir(@RequestParam("archivo") MultipartFile archivo) {
        return ResponseEntity.status(201).body(archivoService.almacenar(archivo));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@moduloAccessGuard.puedeAccederArchivo(#id)")
    public ArchivoResponse obtener(@PathVariable Long id) {
        return archivoService.obtener(id);
    }

    @GetMapping("/{id}/descarga")
    @PreAuthorize("@moduloAccessGuard.puedeAccederArchivo(#id)")
    public ResponseEntity<org.springframework.core.io.Resource> descargar(@PathVariable Long id) {
        DescargaArchivo descarga = archivoService.descargar(id);
        ContentDisposition disposicion = ContentDisposition.attachment()
                .filename(descarga.nombreOriginal(), StandardCharsets.UTF_8)
                .build();
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(descarga.tipoMime()))
                .contentLength(descarga.tamanioBytes())
                .header(HttpHeaders.CONTENT_DISPOSITION, disposicion.toString())
                .header("X-Content-Type-Options", "nosniff")
                .body(descarga.recurso());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ARCHIVOS_ESCRIBIR') and "
            + "@moduloAccessGuard.puedeEliminarArchivo(#id)")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        archivoService.eliminarLogicamente(id);
        return ResponseEntity.noContent().build();
    }
}
