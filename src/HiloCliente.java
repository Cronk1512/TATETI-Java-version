import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class HiloCliente extends Thread {
    private DatagramSocket conexion;
    private InetAddress ipServer;
    private int puerto = 25565;
    private boolean finHiloCliente;
    private String nombre;
    private String eleccion;
    private String[][] tablero = {{"-", "-", "-"}, {"-", "-", "-"}, {"-", "-", "-"}};

    public HiloCliente(String nombre, String eleccion) {
        this.nombre = nombre;
        this.eleccion = eleccion;
        finHiloCliente = false;
        try {
            System.out.println("Intentando conectar");
            ipServer = InetAddress.getByName("127.0.0.1");
            conexion = new DatagramSocket();
        } catch (SocketException e) {
            throw new RuntimeException(e);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        enviarMensaje("Registro_" + nombre + "_" + eleccion);
    }

    private void procesarMensaje(DatagramPacket dp) {
        String msg = (new String(dp.getData())).trim();

        if (msg.startsWith("tablero_")) {
            String[] partes = msg.split("_");
            String info = partes[1];
            String[] subPartes = info.split(" ");
            int fila = Integer.parseInt(subPartes[0].split(":")[1]);
            int col = Integer.parseInt(subPartes[1].split(":")[1]);
            String jugadorMovimiento = subPartes[3]; // Corregido al índice 3

            String simbolo = jugadorMovimiento.equals(nombre) ? "X" : "O";
            tablero[fila][col] = simbolo;
            mostrarTablero();
        } else if (msg.startsWith("turnito_") || msg.startsWith("Comienza_")) {
            String jugadorTurno = "";
            if (msg.startsWith("turnito_")) {
                jugadorTurno = msg.split("_")[1];
            } else {
                String[] partes = msg.split("Es turno de ");
                if (partes.length > 1) {
                    jugadorTurno = partes[1];
                }
            }

            System.out.println("\nturno de: " + jugadorTurno);
            if (jugadorTurno.trim().equals(nombre)) {
                System.out.println("Escribe tu jugada con el formato: m_fila_columna (ej: m_0_0)");
            }
        } else if (msg.startsWith("FIN_")) {
            System.out.println("\n Termino " + msg);
            finHiloCliente = true;
        } else {
            System.out.println("Servidor: " + msg);
        }
    }
    public void mostrarTablero() {
        System.out.println("\nTablero actual:");
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                System.out.print(tablero[i][j] + " ");
            }
            System.out.println();
        }
    }

    public void enviarMensaje(String msg) {
        byte[] data = msg.getBytes();
        DatagramPacket dp = new DatagramPacket(data, data.length, ipServer, puerto);
        try {
            conexion.send(dp);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        while (!finHiloCliente) {
            byte[] data = new byte[1024];
            DatagramPacket dp = new DatagramPacket(data, data.length);
            try {
                conexion.receive(dp);
            } catch (IOException e) {
                break;
            }
            procesarMensaje(dp);
        }
    }

    public void hacerMovimiento(int fila, int columna) {
        enviarMensaje("movimiento_" + nombre + "_" + fila + "_" + columna);
    }
}