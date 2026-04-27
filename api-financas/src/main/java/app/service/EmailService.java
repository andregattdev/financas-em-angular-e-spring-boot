package app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void enviarEmailConfirmacao(String emailDestino, String token) {
        SimpleMailMessage mensagem = new SimpleMailMessage();
        mensagem.setFrom("andregatt.dev@gmail.com");
        mensagem.setTo(emailDestino);
        mensagem.setSubject("Confirme seu Cadastro - App Financeiro");
        mensagem.setText("Olá! Clique no link abaixo para ativar sua conta:\n\n" +
                        "http://localhost:8080/api/auth/confirmar?token=" + token);

        mailSender.send(mensagem);
    }
}
