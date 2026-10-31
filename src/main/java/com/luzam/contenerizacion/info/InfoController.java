package com.luzam.contenerizacion.info;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.util.Map;


@RestController
public class InfoController {

    private final String version;
    private final String entorno;

    public InfoController(@Value("${app.version}") String version,
                          @Value("${app.entorno}") String entorno) {
        this.version = version;
        this.entorno = entorno;
    }

    @GetMapping("/info")
    public Map<String, String> info() {
        return Map.of(
                "aplicacion", "tareas-api",
                "version", version,
                "entorno", entorno,
                "host", nombreHost());
    }

    private static String nombreHost() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            return "desconocido";
        }
    }
}