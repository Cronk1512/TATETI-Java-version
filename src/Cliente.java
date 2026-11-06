import java.util.Scanner;

public class Cliente {
    public static HiloCliente hc;
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Arrancando");
        System.out.print("Ingrese su nombre: ");
        String nombre = scanner.nextLine();
        System.out.print("Elija cara o cruz: ");
        String eleccion = scanner.nextLine();
        hc = new HiloCliente(nombre, eleccion);
        hc.start();

        while (true) {
            String entrada = scanner.nextLine();
            if (entrada.startsWith("m_")) {
                try {
                    String[] partes = entrada.split("_");
                    int fila = Integer.parseInt(partes[1]);
                    int columna = Integer.parseInt(partes[2]);
                    hc.hacerMovimiento(fila, columna);
                } catch (Exception e) {
                    System.out.println("Formato incorrecto. Usa m_fila_columna (ej: m_0_0)");
                }
            }
        }
    }
}