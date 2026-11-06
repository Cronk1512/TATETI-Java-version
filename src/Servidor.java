public class Servidor {
    public static HiloServidor hs;
    public static void main(String[] args) {
        System.out.println("Arranca");
        hs = new HiloServidor();
        hs.start();
    }
}