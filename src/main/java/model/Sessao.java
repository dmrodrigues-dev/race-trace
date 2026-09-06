package model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.HashMap;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Sessao {
    private int session_key, year;
    private String circuit_short_name, country_name, session_name;
    private HashMap<Integer, Piloto> pilotos = new HashMap<>();
    private ArrayList<SessionResult> resultado = new ArrayList<>();
    private ArrayList<RaceControl> race_controls = new ArrayList<>();

    public void info() {
        System.out.println("Circuito: " +this.circuit_short_name +
                "\nPaís: " +this.country_name +
                "\nTipo de sessão: " +this.session_name +
                "\nAno: " +this.year);
    }

    public void setSession_key(int session_key) {
        this.session_key = session_key;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setCircuit_short_name(String circuit_short_name) {
        this.circuit_short_name = circuit_short_name;
    }

    public void setCountry_name(String country_name) {
        this.country_name = country_name;
    }

    public void setSession_name(String session_name) {
        this.session_name = session_name;
    }

    public void setResultado(ArrayList<SessionResult> resultado) { this.resultado = resultado; }

    public void setRace_controls(ArrayList<RaceControl> race_controls) { this.race_controls = race_controls; }

    public void setPilotos(HashMap<Integer, Piloto> pilotos) { this.pilotos = pilotos; }

    public int  getSession_key() { return this.session_key; }

    public HashMap<Integer, Piloto> getPilotos() { return this.pilotos; }

    public ArrayList<SessionResult> getResultado() { return this.resultado; }

    public ArrayList<Integer> getInterrupted() {
        ArrayList<Integer> interrupt = new ArrayList<>();
        for (RaceControl rc : this.race_controls) {
            if (rc.getMessage() != null && rc.getMessage().equals("RED FLAG - RACE SUSPENDED")) {
                interrupt.add(rc.getLap_number());
            }
        }
        return interrupt;
    }
}
