package model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Pit {
    String date;
    int lap_number;
    double lane_duration, stop_duration;

    public void info(){
        System.out.println("Volta: " +this.lap_number +", Duração:" +this.lane_duration);
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setLane_duration(double lane_duration) {
        this.lane_duration = lane_duration;
    }

    public void setLap_number(int lap_number) {
        this.lap_number = lap_number;
    }

    public void setStop_duration(double stop_duration) {
        this.stop_duration = stop_duration;
    }
}
