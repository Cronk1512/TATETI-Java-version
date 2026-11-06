import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Usuario {
    private String nombre;
    private InetAddress ip;
    private int puerto;
    private String eleccion;

    public Usuario(String nombre, InetAddress ip, int puerto, String eleccion) {
        this.nombre = nombre;
        this.ip = ip;
        this.puerto = puerto;
        this.eleccion = eleccion;
    }

    public String getNombre() {
        return nombre;
    }

    public InetAddress getIp() {
        return ip;
    }

    public int getPuerto() {
        return puerto;
    }

    public String getEleccion() {
        return eleccion;
    }
}