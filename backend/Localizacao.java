package backend;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Localizacao {
    private int id;
    private double latitude;
    private double longitude;
    private LocalDateTime timestamp;
    private String status;
    private Animal animal;

    public Localizacao() {}

    public Localizacao(int id, double latitude, double longitude, Animal animal) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = LocalDateTime.now();
        this.animal = animal;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Animal getAnimal() { return animal; }
    public void setAnimal(Animal animal) { this.animal = animal; }

    public String getTimestampFormatado() {
        return timestamp.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
    }
}
