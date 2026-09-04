package model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CarData {
    int brake, throttle, speed;
    String date;

    public void info() {
        System.out.println("Data: " +this.date +
                "\nAcelerador: " +this.throttle +
                "\nFreios: " +this.brake +
                "\nVelocidade: " +this.speed);
    }

    public void setBrake(int brake) { this.brake = brake; }

    public void setThrottle(int throttle) { this.throttle = throttle; }

    public void setSpeed(int speed) { this.speed = speed; }

    public void setDate(String date) { this.date = date; }

    public int getBrake() { return this.brake; }

    public int getThrottle() { return this.throttle; }

    public int getSpeed() { return this.speed; }

    public String getDate() { return this.date; }

    // RETORNA UM BOOLEANO TRUE SE O DATE ESTIVER DENTRO DO INTERVALO DE TEMPO
    public boolean isInInterval(String start, String end) {
        OffsetDateTime startTime = OffsetDateTime.parse(start);
        OffsetDateTime endTime = OffsetDateTime.parse(end);
        OffsetDateTime now = OffsetDateTime.parse(date);

        if (now.isAfter(startTime) && now.isBefore(endTime)) {
            return true;
        } else {
            return false;
        }
    }

    // RETORNA UM DOUBLE DE SEGUNDOS ENTRE O DATE E O HORARIO FORNECIDO
    public double timePassed(String start) {
        OffsetDateTime startTime = OffsetDateTime.parse(start);
        OffsetDateTime now = OffsetDateTime.parse(date);

        double duration = ChronoUnit.NANOS.between(startTime, now) / 1_000_000_000.0;
        return duration;
    }

}
