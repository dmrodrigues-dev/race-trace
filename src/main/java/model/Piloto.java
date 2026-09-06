package model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

@JsonIgnoreProperties( ignoreUnknown = true)
public class Piloto {
    private int driver_number;
    private String full_name, team_name;
    private HashMap<Integer, Volta> voltas;
    private HashMap<Integer, Volta> voltasValidas = new HashMap<Integer, Volta>();
    private ArrayList<Pit> pits;
    private Volta fastest_lap, fastest_sector_1, fastest_sector_2, fastest_sector_3, slowest_lap;
    private boolean isComplete = false;
    private boolean dnf = false;
    private boolean dns = false;
    private double fastest_sector;

    public void info() {
        System.out.println("Número do piloto: " +this.driver_number +
                "\nNome do piloto: " +this.full_name +
                "\nEquipe do piloto: " +this.team_name);
    }

    @Override
    public String toString() {return this.full_name;}

    public void setDriver_number(int driver_number) {
        this.driver_number = driver_number;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public void setTeam_name(String team_name) {
        this.team_name = team_name;
    }

    public void setVoltas(HashMap<Integer, Volta> voltas) {
        this.voltas = voltas;
        this.voltasValidas.clear();
        for (Volta v : voltas.values()) {
            v.fixSectorDuration();
            if (v.isValid()) {
                voltasValidas.put(v.getLap_number(), v);
            }
        }
    }

    public void setPits(ArrayList<Pit> pits) { this.pits = pits; }

    public void setComplete() { this.isComplete = true; }

    public void setDns() {
        this.dns = true;
    }

    public void setDnf() {
        this.dnf = true;
    }

    public void calculateBestAndWorst() {
        if(voltasValidas.isEmpty()){return;}

        Comparator<Volta> comparator = Comparator.comparing(l -> l.lap_duration);
        this.fastest_lap = Collections.min(voltasValidas.values(), comparator);
        this.slowest_lap = Collections.max(voltasValidas.values(), comparator);
        comparator = Comparator.comparing(l -> l.duration_sector_1);
        this.fastest_sector_1 = Collections.min(voltasValidas.values(), comparator);
        comparator = Comparator.comparing(l -> l.duration_sector_2);
        this.fastest_sector_2 = Collections.min(voltasValidas.values(), comparator);
        comparator = Comparator.comparing(l -> l.duration_sector_3);
        this.fastest_sector_3 = Collections.min(voltasValidas.values(), comparator);

        fastest_sector = fastest_sector_1.getSectorDurations().get(1);
        for (Volta volta : getFastest_sectors().values()) {
            for (double sector : volta.getSectorDurations().values()) {
                if (sector < fastest_sector) {
                    this.fastest_sector = sector;
                }
            }
        }
    }

    public CarData calculateHighestSpeed(int lap_number) {
        Volta lap = this.voltasValidas.get(lap_number);
        CarData highestSpeed = lap.getHighSpeed(1);
        for (int setor = 2; setor < 4; setor++) {
            if (lap.getHighSpeed(setor).getSpeed() > highestSpeed.getSpeed()) {
                highestSpeed = lap.getHighSpeed(setor);
            }
        }
        return highestSpeed;
    }

    public int getDriver_number() { return this.driver_number; }

    public HashMap<Integer, Volta> getVoltas() { return this.voltas; }

    public Volta getFastest_lap() { return this.fastest_lap; }

    public Volta getSlowest_lap() { return this.slowest_lap; }

    public double getFastest_sector() { return this.fastest_sector; }

    public HashMap<Integer, Volta> getFastest_sectors() {
        HashMap<Integer, Volta> setores = new HashMap<Integer, Volta>();
        setores.put(1, this.fastest_sector_1);
        setores.put(2, this.fastest_sector_2);
        setores.put(3,this.fastest_sector_3);
        return setores;
    }

    public ArrayList<Pit> getPits() { return this.pits; }

    public boolean isDnf() {
        return dnf;
    }

    public boolean isDns() {
        return dns;
    }

    public boolean isComplete() { return this.isComplete; }
}
