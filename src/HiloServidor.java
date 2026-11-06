import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class HiloServidor extends Thread {
    private boolean finHiloServidor = false;
    private DatagramSocket socket;
    private static Usuario[] usuarios = new Usuario[2];
    private int contadorUsuarios = 0;
    private String[][] tablero = {{"-", "-", "-"}, {"-", "-", "-"}, {"-", "-", "-"}};
    private int turno = 0;

    public HiloServidor() {
        try {
            socket = new DatagramSocket(25565);
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        while (!finHiloServidor) {
            byte[] datos = new byte[1024];
            DatagramPacket dp = new DatagramPacket(datos, datos.length);
            try {
                socket.receive(dp);
                procesarMensaje(dp);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void procesarMensaje(DatagramPacket dp) {
        String msg = new String(dp.getData()).trim();
        InetAddress ipCliente = dp.getAddress();
        int puertoCliente = dp.getPort();

        if (msg.startsWith("Registro_")) {
            if (contadorUsuarios < 2) {
                String[] partes = msg.split("_");
                String nombre = partes[1];
                String eleccion = partes[2];
                usuarios[contadorUsuarios] = new Usuario(nombre, ipCliente, puertoCliente, eleccion);
                contadorUsuarios++;
                enviarMensaje("Registro_OK", ipCliente, puertoCliente);
                if (contadorUsuarios == 2) {
                    enviarMensajeATodos("Comienza_Es turno de " + usuarios[0].getNombre());
                }
            }
        } else if (msg.startsWith("movimiento_")) {
            String[] partes = msg.split("_");
            String nombre = partes[1];
            int fila = Integer.parseInt(partes[2]);
            int columna = Integer.parseInt(partes[3]);

            if (contadorUsuarios == 2) {
                int jugadorActual = -1;
                if (usuarios[0].getNombre().equals(nombre)) {
                    jugadorActual = 0;
                } else if (usuarios[1].getNombre().equals(nombre)) {
                    jugadorActual = 1;
                }

                if (jugadorActual == turno && tablero[fila][columna].equals("-")) {
                    tablero[fila][columna] = (jugadorActual == 0) ? "X" : "O";
                    turno = 1 - turno;
                    enviarMensajeATodos("tablero_Fila:" + fila + " Col:" + columna + " por " + nombre);
                    if (verificarGanador()) {
                        enviarMensajeATodos("FIN_GANADOR_" + nombre);
                    } else if (verificarEmpate()) {
                        enviarMensajeATodos("FIN_EMPATE");
                    } else {
                        enviarMensajeATodos("turnito_" + usuarios[turno].getNombre());
                    }
                }
            }
        }
    }

    private boolean verificarGanador() {
        for (int i = 0; i < 3; i++) {
            if (!tablero[i][0].equals("-") && tablero[i][0].equals(tablero[i][1]) && tablero[i][1].equals(tablero[i][2])) return true;
            if (!tablero[0][i].equals("-") && tablero[0][i].equals(tablero[1][i]) && tablero[1][i].equals(tablero[2][i])) return true;
        }
        if (!tablero[0][0].equals("-") && tablero[0][0].equals(tablero[1][1]) && tablero[1][1].equals(tablero[2][2])) return true;
        if (!tablero[0][2].equals("-") && tablero[0][2].equals(tablero[1][1]) && tablero[1][1].equals(tablero[2][0])) return true;
        return false;
    }

    private boolean verificarEmpate() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (tablero[i][j].equals("-")) return false;
            }
        }
        return true;
    }

    public void enviarMensaje(String msg, InetAddress ip, int puerto) {
        byte[] data = msg.getBytes();
        DatagramPacket dp = new DatagramPacket(data, data.length, ip, puerto);
        try {
            socket.send(dp);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void enviarMensajeATodos(String msg) {
        for (int i = 0; i < contadorUsuarios; i++) {
            if (usuarios[i] != null) {
                enviarMensaje(msg, usuarios[i].getIp(), usuarios[i].getPuerto());
            }
        }
    }
}