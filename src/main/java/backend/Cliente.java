package backend;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;

public class Cliente {
    // CRIA CLIENTE HTTP
    private HttpClient cliente = HttpClient.newHttpClient();

    // CRIA OBJETO DE REQUEST
    private HttpRequest requisicao;

    // METODO PARA RECEBER INFORMAÇOES
    public HashMap<String, String> getResposta(String url) {
        try {
            // TEMPO DE ESPERA PARA RESPEITAR A API
            Thread.sleep(300);

            // ATRIBUI VALOR A REQUISIÇÃO
            requisicao = HttpRequest.newBuilder().uri(URI.create(url))
                    .GET()
                    .build();

            // ENVIA A REQUISIÇÃO E RECEBE A RESPOSTA
            HttpResponse<String> resposta = cliente.send(requisicao, HttpResponse.BodyHandlers.ofString());

            // EXIBE STATUSCODE NO TERMINAL
            System.out.println(resposta.statusCode());

            // CRIA HASHMAP COM STATUS E BODY
            HashMap<String, String> map = new HashMap<>();
            map.put("body", resposta.body());
            map.put("status", Integer.toString(resposta.statusCode()) );

            // RETORNA STRING DE RESPOSTA
            return map;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
