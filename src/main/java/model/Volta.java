package model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Volta {
     int lap_number;
     String date_start;
     double duration_sector_1, duration_sector_2, duration_sector_3, lap_duration;
     HashMap<Integer, ArrayList<CarData>> lapCarData = new HashMap<Integer, ArrayList<CarData>>();
     boolean is_pit_out = false;
     boolean isComplete = false;
     Pit pit;
    
    public void info() {
        System.out.println("Volta " +this.lap_number +
                "\nDuração: " +this.lap_duration +
                "\nSetor 1: " +this.duration_sector_1 +
                "\nSetor 2: " +this.duration_sector_2 +
                "\nSetor 3: " +this.duration_sector_3);

        if (is_pit_out) {
            System.out.println("Pit:");
            pit.info();
        }
    }

    public void setLap_number(int lap_number) {
        this.lap_number = lap_number;
    }

    public void setPit(Pit pit) {
        this.pit = pit;
    }

    public void setIs_pit_out(boolean is_pit_out) {
        this.is_pit_out = is_pit_out;
    }

    public void setLap_duration(double lap_duration) {
        this.lap_duration = lap_duration;
    }

    public void setDuration_sector_3(double duration_sector_3) {
        this.duration_sector_3 = duration_sector_3;
    }

    public void setDuration_sector_2(double duration_sector_2) {
        this.duration_sector_2 = duration_sector_2;
    }

    public void setDuration_sector_1(double duration_sector_1) {
        this.duration_sector_1 = duration_sector_1;
    }

    public void setDate_start(String date_start) { this.date_start = date_start; }

    public void setComplete() { this.isComplete = true; }

    public void setLapCarData(HashMap<Integer, ArrayList<CarData>> lapCarData) { this.lapCarData = lapCarData; }

    public boolean isComplete() { return this.isComplete; }

    public int getLap_number() {return lap_number;}

    public double getLap_duration() {return lap_duration;}

    public HashMap<Integer, ArrayList<CarData>> getLapCarData() {return lapCarData;}

    // CALCULA E RETORNA DATETIME DO FINAL DA VOLTA, EM ISO
    public HashMap<String, String> getDates() {
        HashMap<String, String> dates = new HashMap<>();
        dates.put("start_sector_1", this.date_start);
        String end_sector_1 = sumDateTime(date_start, this.duration_sector_1);
        dates.put("start_sector_2", end_sector_1);
        String end_sector_2 = sumDateTime(end_sector_1, this.duration_sector_2);
        dates.put("start_sector_3", end_sector_2);
        dates.put("date_end", this.sumDateTime(date_start, lap_duration));

        return dates;
    }

    // SOMA UM DOUBLE DE SEGUNDOS EM UMA STRING ISO, RETORNANDO OUTRA STRING ISO
    private String sumDateTime(String start, double duration) {
        String date_end;
        OffsetDateTime dateTime = OffsetDateTime.parse(start);
        long seg = (long) duration;
        long nano = Math.round((duration-seg)*1_000_000_000);

        OffsetDateTime endDateTime = dateTime.plusSeconds(seg).plusNanos(nano);
        date_end = endDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        return date_end;
    }

    public boolean is_pit_out() {return is_pit_out;}

    public HashMap<Integer, Double> getSectorDurations() {
        HashMap<Integer, Double> durationSectors = new HashMap<Integer, Double>();
        durationSectors.put(1, duration_sector_1);
        durationSectors.put(2, duration_sector_2);
        durationSectors.put(3, duration_sector_3);
        return durationSectors;
    }
}
