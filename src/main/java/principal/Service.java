package principal;

import backend.Cliente;
import backend.Formatter;
import model.*;

import java.util.ArrayList;
import java.util.HashMap;

public class Service {
    private String json;
    private Cliente cliente = new Cliente();
    private Formatter formatter = new Formatter();
    private HashMap<String, String> endpoints =  new HashMap<String, String>();
    private HashMap<String, String> resposta;

    // CONSTRUTOR POVOA HASHMAP DE ENDPOINTS
    Service() {
        this.setUrls();
    }

    // METODO PARA ATRIBUIR ENDPOINTS
    void setUrls() {
        endpoints.put("sessao", "https://api.openf1.org/v1/sessions?");
        endpoints.put("piloto", "https://api.openf1.org/v1/drivers?");
        endpoints.put("voltas", "https://api.openf1.org/v1/laps?");
        endpoints.put("pits", "https://api.openf1.org/v1/pit?");
        endpoints.put("cardata", "https://api.openf1.org/v1/car_data?");
    }

    // BUSCA E RETORNA A SESSAO
    Sessao getSessao(String ano, String tipo, String pais) {
        resposta = cliente.getResposta(endpoints.get("sessao") +"&year=" +ano +"&session_name=" +tipo +"&country_name=" +pais);
        json = resposta.get("body");
        Sessao sessao = formatter.getObjeto(json, Sessao.class);
        sessao.setPilotos(getPilotos(sessao));
        return sessao;
    }

    // BUSCA E RETORNA HASHMAP DE PILOTOS
    private HashMap<Integer, Piloto> getPilotos(Sessao sessao) {
        HashMap<Integer, Piloto> pilotos = new HashMap<Integer, Piloto>();
        resposta = cliente.getResposta(endpoints.get("piloto") +"&session_key=" +sessao.getSession_key());
        json = resposta.get("body");
        ArrayList<Piloto> resposta = formatter.getArrayObjetos(json, Piloto.class);
        for (Piloto p : resposta) {
            pilotos.put(p.getDriver_number(), p);
        }
        return pilotos;
    }

    // BUSCA E ATRIBUI VOLTAS AO PILOTO
    void fetchVoltas(Sessao sessao, Piloto piloto) {
        resposta = cliente.getResposta(endpoints.get("voltas") +
                "&session_key=" +sessao.getSession_key() +
                "&driver_number=" +piloto.getDriver_number());
        json = resposta.get("body");
        ArrayList<Volta> voltas = formatter.getArrayObjetos(json, Volta.class);
        HashMap<Integer, Volta> voltasMap = new HashMap<Integer, Volta>();
        for (Volta v : voltas) {
            v.fixSectorDuration();
            voltasMap.put(v.getLap_number(), v);
        }

        piloto.setVoltas(voltasMap);
        piloto.calculateBestAndWorst();
    }

    // BUSCA E ATRIBUI PITS AO PILOTO
    void fetchPits(Sessao sessao, Piloto piloto) {
        resposta = cliente.getResposta(endpoints.get("pits") +
                "&session_key=" +sessao.getSession_key() +
                "&driver_number=" +piloto.getDriver_number());
        json = resposta.get("body");
        piloto.setPits(formatter.getArrayObjetos(json, Pit.class));
    }

    // BUSCA CARDATA DE DETERMINADA VOLTA DO PILOTO
    void fetchCarData(Sessao sessao, Piloto piloto, int volta) {
        resposta = cliente.getResposta(endpoints.get("cardata") +
                "&session_key=" +sessao.getSession_key() +
                "&driver_number=" +piloto.getDriver_number() +
                "&date%3E" +piloto.getVoltas().get(volta).getDates().get("start_sector_1") +
                "&date%3C" +piloto.getVoltas().get(volta).getDates().get("date_end"));
        json = resposta.get("body");
        ArrayList<CarData> carData = formatter.getArrayObjetos(json, CarData.class);

        HashMap<String, String> datas = piloto.getVoltas().get(volta).getDates();

        HashMap<Integer, ArrayList<CarData>> carDataMap = new HashMap<Integer, ArrayList<CarData>>();
        carDataMap.put(1, new ArrayList<CarData>());
        carDataMap.put(2, new ArrayList<CarData>());
        carDataMap.put(3, new ArrayList<CarData>());

        for (CarData cd : carData) {
            // SE A DATA DO DADO ESTIVER DENTRO DO INTERVALO DE DATAS DO SETOR 1, EDICIONE AO SEU ARRAYLIST DO HASHMAP
            if (cd.isInInterval(datas.get("start_sector_1"), datas.get("start_sector_2"))) {
                carDataMap.get(1).add(cd);
            } else if (cd.isInInterval(datas.get("start_sector_2"), datas.get("start_sector_3"))) {
                carDataMap.get(2).add(cd);
            } else if (cd.isInInterval(datas.get("start_sector_3"), datas.get("date_end"))) {
                carDataMap.get(3).add(cd);
            }
        }

        piloto.getVoltas().get(volta).setLapCarData(carDataMap);
    }

}
