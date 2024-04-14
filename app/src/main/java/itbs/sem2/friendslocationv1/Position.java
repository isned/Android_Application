package itbs.sem2.friendslocationv1;

public class Position {
    int idPosition;
    String longitude, latitude,pseudo;

    public Position() {
    }

    public void setIdPosition(int idPosition) {
        this.idPosition = idPosition;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public int getIdPosition() {
        return idPosition;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getPseudo() {
        return pseudo;
    }

    public Position(int idPosition, String longitude, String latitude, String pseudo) {
        this.idPosition = idPosition;
        this.longitude = longitude;
        this.latitude = latitude;
        this.pseudo = pseudo;
    }

    @Override
    public String toString() {
        return "Position{" +
                "idPosition=" + idPosition +
                ", longitude='" + longitude + '\'' +
                ", latitude='" + latitude + '\'' +
                ", pseudo='" + pseudo + '\'' +
                '}';
    }
}