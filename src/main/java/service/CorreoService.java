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

    // PON TU CORREO Y TU CLAVE DE 16 LETRAS AQUÍ (sin espacios)
    private static final String REMITENTE = "narvateo2021@gmail.com";
    private static final String CLAVE_APP = "yfmj dpiy vzqr kyfe";

    public static void enviarCorreoBienvenida(String destinatario, String nombre) {
        // Configuramos los servidores de Gmail
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        // Nos autenticamos con la nueva librería Jakarta
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(REMITENTE, CLAVE_APP);
            }
        });

        try {
            // Armamos el mensaje
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(REMITENTE));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            message.setSubject("¡Bienvenido a MovieZone, " + nombre + "! 🎬");

            // Diseñamos el correo con HTML
            String contenido = "<div style='font-family: Arial, sans-serif; color: #333;'>"
                    + "<h2 style='color: #0d6efd;'>¡Hola " + nombre + "!</h2>"
                    + "<p>Qué bacán que te hayas unido a <b>MovieZone</b>. Tu cuenta está listita.</p>"
                    + "<p>Ya puedes iniciar sesión en la web, buscar cualquier película y armar tu lista de favoritos a tu gusto.</p>"
                    + "<hr>"
                    + "<p style='font-size: 12px; color: #777;'>Saludos,<br>El equipo de desarrollo de MovieZone 🍿</p>"
                    + "</div>";

            message.setContent(contenido, "text/html; charset=utf-8");

            // Enviamos el correo
            Transport.send(message);
            System.out.println("Correo enviado exitosamente a: " + destinatario);

        } catch (Exception e) {
            System.out.println("Error al enviar el correo con Jakarta: " + e.getMessage());
        }
    }
}