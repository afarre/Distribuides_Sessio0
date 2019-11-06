import java.net.Socket;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class main {
    public static int sharedVar = 0;

    public static void main(String[] args) {
        System.out.println("Run");
        int N;
        System.out.println("Introdueix el nombre de servidors desitjat (ha de ser major o igual a 2): ");

        N = readInt();
        while (N < 2) {
            System.out.println("Opcio del menu incorrecta! Introdueix el nombre de servidors de nou:");
            N = readInt();
        }

        ArrayList<LocalServer> serversArray = new ArrayList<>(N);
        ArrayList<LocalClient> clientArray = new ArrayList<>(N);
        int port = 33333;
        for (int i = 0; i < N; i++){
            serversArray.add(i, new LocalServer(null, sharedVar, ++port, i));
            serversArray.get(i).start();
        }

        port = 33334;
        for (int i = 0; i < N; i++){
            LocalClient localClient = null;
            if (i != N - 1){
                localClient = new LocalClient(serversArray.get(i), new Socket(), ++port, i);
                clientArray.add(i, localClient);
            }else {
                localClient = new LocalClient(serversArray.get(i), new Socket(), port - i, i);
                clientArray.add(i, localClient);
            }
            serversArray.get(i).setClient(localClient);
        }

        for (int i = 0; i < N; i++){
            if (i != N - 1){
                clientArray.get(i + 1).start();
            }else {
                clientArray.get(0).start();
            }
        }

        clientArray.get(0).setToken(true);
    }

    /**
     * Comprova que l'usuari introduiex un enter
     * @return El numero introduit per l'usuari o -1 en cas de que no hagi introduit un numero
     */
    private static int readInt(){
        try {
            Scanner read = new Scanner(System.in);
            return read.nextInt();
        }catch (InputMismatchException ignored){
        }
        return -1;
    }
}
