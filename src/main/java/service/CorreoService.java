package service;

import java.util.Properties;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

public class CorreoService {

    private static final String REMITENTE = "narvateo2021@gmail.com";
    // PILAS: Sin espacios
    private static final String CLAVE_APP = "yfmjdpiyvzqrkyfe";

    // Agregamos el parámetro "token"
    public static void enviarCorreoVerificacion(String destinatario, String nombre, String token) {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(REMITENTE, CLAVE_APP);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(REMITENTE));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            message.setSubject("Activa tu cuenta en MovieZone 🎬");

            // EL LINK MÁGICO QUE VA A PRESIONAR EL USUARIO
            // Asegúrate de que esta URL sea la correcta de tu Tomcat (o de ngrok si lo estás usando para pruebas)
            String link = "http://localhost:8080/MovieZoneWeb_war/usuario?accion=verificar&token=" + token;

            String contenido = "<div style='font-family: Arial, sans-serif; color: #333; max-width: 600px; margin: auto; border: 1px solid #ddd; border-radius: 10px; padding: 20px;'>"
                    + "<h2 style='color: #e53935; text-align: center;'>¡Hola " + nombre + "!</h2>"
                    + "<p style='font-size: 16px;'>Qué bacán que te hayas unido a <b>MovieZone</b>. Para poder iniciar sesión y empezar a guardar tus películas favoritas, necesitamos verificar tu correo electrónico.</p>"
                    + "<div style='text-align: center; margin: 30px 0;'>"
                    + "<a href='" + link + "' style='background-color: #e53935; color: white; padding: 12px 25px; text-decoration: none; border-radius: 5px; font-weight: bold; font-size: 16px;'>Verificar mi cuenta</a>"
                    + "</div>"
                    + "<p style='font-size: 14px; color: #555;'>Si el botón no funciona, copia y pega el siguiente enlace en tu navegador:</p>"
                    + "<p style='font-size: 12px; color: #0066cc; word-break: break-all;'>" + link + "</p>"
                    + "<hr style='border: none; border-top: 1px solid #eee; margin-top: 20px;'>"
                    + "<p style='font-size: 12px; color: #777; text-align: center;'>El equipo de MovieZone 🍿</p>"
                    + "</div>";

            message.setContent(contenido, "text/html; charset=utf-8");

            Transport.send(message);
            System.out.println("Correo de verificación enviado exitosamente a: " + destinatario);

        } catch (Exception e) {
            System.out.println("Error al enviar el correo: " + e.getMessage());
        }
    }
}