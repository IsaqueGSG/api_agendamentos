package com.api_agendamentos.api_agendamentos.services;

import com.api_agendamentos.api_agendamentos.entity.PerfilUsuario;
import com.api_agendamentos.api_agendamentos.entity.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Gera e valida tokens JWT (HS256) que identificam o usuário e o perfil.
 * Claims: sub (id do usuário), email, perfil, iat e exp.
 */
@Service
public class JwtService {

    private static final Base64.Encoder B64 = Base64.getUrlEncoder().withoutPadding();
    private static final String HEADER = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";

    private static final Pattern SUB = Pattern.compile("\"sub\":\"(\\d+)\"");
    private static final Pattern PERFIL = Pattern.compile("\"perfil\":\"([A-Z_]+)\"");
    private static final Pattern EXP = Pattern.compile("\"exp\":(\\d+)");

    /** Dados do usuário extraídos de um token válido. */
    public record DadosToken(Long usuarioId, PerfilUsuario perfil) {}

    private final byte[] secret;
    private final long expirationMinutes;

    public JwtService(@Value("${jwt.secret}") String secret,
                      @Value("${jwt.expiration-minutes:60}") long expirationMinutes) {
        byte[] bytes = secret.getBytes(StandardCharsets.UTF_8);
        if (bytes.length < 32) {
            throw new IllegalStateException("jwt.secret precisa ter pelo menos 32 caracteres.");
        }
        this.secret = bytes;
        this.expirationMinutes = expirationMinutes;
    }

    public String gerarToken(Usuario usuario) {
        long agora = Instant.now().getEpochSecond();
        long exp = agora + expirationMinutes * 60;

        String payload = "{\"sub\":\"" + usuario.getId() + "\""
                + ",\"email\":\"" + escapar(usuario.getEmail()) + "\""
                + ",\"perfil\":\"" + usuario.getPerfil().name() + "\""
                + ",\"iat\":" + agora
                + ",\"exp\":" + exp + "}";

        String conteudo = codificar(HEADER) + "." + codificar(payload);
        return conteudo + "." + B64.encodeToString(assinar(conteudo));
    }

    public DadosToken validar(String autorizacao) {
        if (autorizacao == null) {
            throw naoAutorizado();
        }
        String token = autorizacao.trim();
        if (token.regionMatches(true, 0, "Bearer ", 0, 7)) {
            token = token.substring(7).trim();
        }

        String[] partes = token.split("\\.", -1);
        if (partes.length != 3) {
            throw naoAutorizado();
        }

        try {
            byte[] recebida = Base64.getUrlDecoder().decode(partes[2]);
            byte[] esperada = assinar(partes[0] + "." + partes[1]);
            if (!MessageDigest.isEqual(esperada, recebida)) {
                throw naoAutorizado();
            }

            String header = new String(Base64.getUrlDecoder().decode(partes[0]), StandardCharsets.UTF_8);
            if (!HEADER.equals(header)) {
                throw naoAutorizado();
            }

            String payload = new String(Base64.getUrlDecoder().decode(partes[1]), StandardCharsets.UTF_8);
            Matcher sub = SUB.matcher(payload);
            Matcher perfil = PERFIL.matcher(payload);
            Matcher exp = EXP.matcher(payload);
            if (!sub.find() || !perfil.find() || !exp.find()) {
                throw naoAutorizado();
            }
            if (Instant.now().getEpochSecond() >= Long.parseLong(exp.group(1))) {
                throw naoAutorizado();
            }

            return new DadosToken(Long.parseLong(sub.group(1)), PerfilUsuario.valueOf(perfil.group(1)));
        } catch (IllegalArgumentException e) {
            // base64 inválido, número inválido ou perfil desconhecido
            throw naoAutorizado();
        }
    }

    private static ResponseStatusException naoAutorizado() {
        return new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    private static String codificar(String texto) {
        return B64.encodeToString(texto.getBytes(StandardCharsets.UTF_8));
    }

    private byte[] assinar(String conteudo) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret, "HmacSHA256"));
            return mac.doFinal(conteudo.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new IllegalStateException("Falha ao assinar o token.", e);
        }
    }

    private static String escapar(String s) {
        StringBuilder sb = new StringBuilder();
        for (char c : s.toCharArray()) {
            if (c == '"' || c == '\\') {
                sb.append('\\').append(c);
            } else if (c < 0x20) {
                sb.append(String.format("\\u%04x", (int) c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}