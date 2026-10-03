package com.asiscontrol.service;

import com.asiscontrol.entity.enums.SeveridadError;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.*;

@Component
public class CorreoRecuperacionListener {
    private final JavaMailSender sender;
    private final AuditoriaService audit;
    private final boolean enabled;
    private final String from, base;

    public CorreoRecuperacionListener(
            JavaMailSender s,
            AuditoriaService a,
            @Value("${app.mail.enabled:false}") boolean e,
            @Value("${app.mail.from:}") String f,
            @Value("${app.frontend-url:http://localhost:3000}") String b) {
        sender = s;
        audit = a;
        enabled = e;
        from = f;
        base = b.replaceAll("/+$", "");
        if (!base.matches("https?://[^\\s?#]+"))
            throw new IllegalArgumentException(
                    "APP_FRONTEND_URL debe ser una URL http(s) sin consulta ni fragmento");
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void enviar(RecuperacionService.CorreoRecuperacion event) {
        try {
            if (!enabled || from.isBlank()) throw new IllegalStateException();
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setFrom(from);
            mail.setTo(event.correo());
            mail.setSubject("AsisControl · Recuperar contraseña");
            mail.setText(
                    "Se solicitó recuperar su contraseña de AsisControl.\n\n"
                            + base
                            + "/recuperar-contrasena#token="
                            + event.token()
                            + "\n\n"
                            + "El enlace vence en 15 minutos y solo puede utilizarse una vez. Si no"
                            + " realizó esta solicitud, ignore este correo. Su contraseña permanece"
                            + " igual hasta completar la recuperación.");
            sender.send(mail);
        } catch (Exception failure) {
            audit.registrarError(
                    SeveridadError.ERROR,
                    "CorreoRecuperacion",
                    "No se pudo enviar un correo de recuperación",
                    "Revise la configuración SMTP y su disponibilidad. No se registran"
                        + " destinatarios, credenciales ni enlaces.");
        }
    }
}
