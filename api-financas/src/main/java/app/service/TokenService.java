package app.service;

import java.util.Date;

import org.springframework.stereotype.Service;

import app.model.Usuario;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class TokenService {

    // A chave precisa ter pelo menos 32 caracteres para o HS256
    private final String secret = "minha-chave-secreta-muito-longa-e-segura-123456789-asig-dev";
    private final java.security.Key key = Keys.hmacShaKeyFor(secret.getBytes());

    public String gerarToken(Usuario usuario) {
        return Jwts.builder()
                .setSubject(usuario.getEmail()) 
                .claim("id", usuario.getId())
                .claim("nome", usuario.getNome())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(key) // Remova o SignatureAlgorithm daqui, ele deduz pela chave
                .compact();
    }

    public String validarToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (Exception e) {
            // Adicione este print para ver o erro real no console do STS/VS Code
            System.err.println("Erro na validação do JWT: " + e.getMessage());
            return null;
        }
    }
}