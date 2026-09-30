package com.asiscontrol.service;

import com.asiscontrol.dto.PageResponse;
import com.asiscontrol.dto.notificacion.NotificacionDtos;
import com.asiscontrol.entity.AccesoUsuario;
import com.asiscontrol.entity.Notificacion;
import com.asiscontrol.entity.enums.TipoNotificacion;
import com.asiscontrol.exception.ResourceNotFoundException;
import com.asiscontrol.repository.AccesoUsuarioRepository;
import com.asiscontrol.repository.NotificacionRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class NotificacionService {

    private final NotificacionRepository notificacionRepository;
    private final AccesoUsuarioRepository usuarioRepository;

    public NotificacionService(
            NotificacionRepository notificacionRepository,
            AccesoUsuarioRepository usuarioRepository
    ) {
        this.notificacionRepository = notificacionRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public PageResponse<NotificacionDtos.Response> list(Jwt jwt, int page, int size) {
        AccesoUsuario user = currentUser(jwt);
        PageRequest pageable = PageRequest.of(
                Math.max(page, 0),
                Math.min(Math.max(size, 1), 100),
                Sort.by(Sort.Direction.DESC, "fechaCreacion")
        );
        return PageResponse.from(
                notificacionRepository.findAllByDestinatarioIdAndActivoTrue(user.getId(), pageable),
                this::toResponse
        );
    }

    @Transactional(readOnly = true)
    public NotificacionDtos.Summary summary(Jwt jwt) {
        long unread = notificacionRepository.countByDestinatarioIdAndLeidaFalseAndActivoTrue(
                currentUser(jwt).getId()
        );
        return new NotificacionDtos.Summary(unread);
    }

    @Transactional
    public NotificacionDtos.Response markRead(Jwt jwt, Long id) {
        AccesoUsuario user = currentUser(jwt);
        Notificacion notification = notificacionRepository
                .findByIdAndDestinatarioIdAndActivoTrue(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "NOTIFICACION_NO_ENCONTRADA",
                        "No se encontro la notificacion"
                ));
        if (!notification.isLeida()) {
            notification.setLeida(true);
            notification.setFechaLectura(Instant.now());
        }
        return toResponse(notificacionRepository.save(notification));
    }

    @Transactional
    public int markAllRead(Jwt jwt) {
        return notificacionRepository.markAllRead(currentUser(jwt).getId(), Instant.now());
    }

    @Transactional
    public NotificacionDtos.Response create(
            Long userId,
            String title,
            String message,
            TipoNotificacion type,
            String targetUrl
    ) {
        AccesoUsuario user = usuarioRepository.findByIdAndActivoTrue(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "USUARIO_NO_ENCONTRADO",
                        "No se encontro el usuario destinatario"
                ));
        Notificacion notification = new Notificacion();
        notification.setDestinatario(user);
        notification.setTitulo(title);
        notification.setMensaje(message);
        notification.setTipo(type);
        notification.setUrlDestino(targetUrl);
        return toResponse(notificacionRepository.save(notification));
    }

    private AccesoUsuario currentUser(Jwt jwt) {
        return usuarioRepository.findActiveByIdentifier(jwt.getSubject())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "USUARIO_NO_ENCONTRADO",
                        "No se encontro la cuenta de la sesion"
                ));
    }

    private NotificacionDtos.Response toResponse(Notificacion notification) {
        return new NotificacionDtos.Response(
                notification.getId(),
                notification.getTitulo(),
                notification.getMensaje(),
                notification.getTipo(),
                notification.getUrlDestino(),
                notification.isLeida(),
                notification.getFechaLectura(),
                notification.getFechaCreacion()
        );
    }
}
