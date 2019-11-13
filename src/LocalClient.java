import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;

public class LocalClient extends Thread {
    private DataInputStream diStream;
    private DataOutputStream doStream;
    private Socket socket;
    private LocalServer localServer;
    private final int nextPORT;

    private boolean token;
    public final static String CALCULATIONS = "CALCULATIONS";
    public final static String READ = "READ";
    public final static String END_TOKEN = "END_TOKEN";
    public final static String HANDSHAKE = "HANDSHAKE";

    public LocalClient(LocalServer localServer, String token, String nextPORT){
        this.localServer = localServer;
        this.token = !token.equals("false");
        this.nextPORT = Integer.parseInt(nextPORT);
    }


    @Override
    public void run() {
        boolean wait = true;

        while (wait) {
            // Averiguem quina direccio IP hem d'utilitzar
            InetAddress iAddress;
            try {
                iAddress = InetAddress.getLocalHost();
                String IP = iAddress.getHostAddress();

                socket = new Socket(String.valueOf(IP), nextPORT);
                doStream = new DataOutputStream(socket.getOutputStream());
                diStream = new DataInputStream(socket.getInputStream());
            } catch (ConnectException ignored) {
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (socket != null) {
                    wait = false;
                }
            }
        }

        try {
            handshake();
            talk();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks connection between server and client by sendig a message
     * @throws IOException In case any data stream fails to work
     */
    private void handshake() throws IOException {
        System.out.println("[CLIENT] - Faig handshake");
        doStream.writeUTF(HANDSHAKE);
        doStream.writeInt(nextPORT);
    }

    /**
     * Communication between machines via sockets
     * @throws IOException In case the data streams fail to communicate
     */
    public void talk() throws IOException {
        if (token){
            System.out.println("---------------------------------");
            System.out.println("[CLIENT - 1].\tLa variable val " + LocalServer.sharedVar + ". Faig els calculs sobre la variable.");
            localServer.useSharedVar();
            System.out.println("[CLIENT - 2].\tPOST VALOR: " + LocalServer.sharedVar + ". Envio els resultats a traves del socket al seguent servidor");
            doStream.writeUTF(CALCULATIONS);
            doStream.writeInt(LocalServer.sharedVar);
            token = false;
            System.out.println("[CLIENT - 3].\tCom que he acabat de parlar i fer els calculs, em canvio el meu token a " + token + " i faig una petició per a que el token del seguent server sigui true");
            doStream.writeUTF(END_TOKEN);
        }
    }

    /**
     * Set the token's value. True if you must communicate, false otherwise
     * @param token Token value
     */
    public void setToken(boolean token) {
        this.token = token;
    }
}
