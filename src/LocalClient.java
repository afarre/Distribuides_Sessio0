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
    private int PORT;

    private boolean token;
    private final int ID;
    public final static String CALCULATIONS = "CALCULATIONS";
    public final static String READ = "READ";
    public final static String END_TOKEN = "END_TOKEN";
    public final static String HANDSHAKE = "HANDSHAKE";

    public LocalClient(LocalServer localServer, Socket socket, int port, int ID){
        this.localServer = localServer;
       this.socket = socket;
        this.PORT = port;
        this.ID = ID;
        token = false;

        // Averiguem quina direccio IP hem d'utilitzar
        InetAddress iAddress;
        try {
            iAddress = InetAddress.getLocalHost();
            String IP = iAddress.getHostAddress();

            socket = new Socket (String.valueOf(IP), PORT);
            doStream = new DataOutputStream(socket.getOutputStream());
            diStream = new DataInputStream(socket.getInputStream());
        } catch (ConnectException c){
            System.err.println("Error! El servidor no esta disponible! (Soc el client " + ID + " i em volia conectar a traves del socket" + port + ")");
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        try {
            handshake();
            while (true){
                talk();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks connection between server and client by sendig a message
     * @throws IOException In case any data stream fails to work
     */
    private void handshake() throws IOException {
        doStream.writeUTF(HANDSHAKE);
        doStream.writeInt(ID);
        doStream.writeInt(PORT);
    }

    /**
     * Communication between machines via sockets
     * @throws IOException In case the data streams fail to communicate
     */
    private void talk() throws IOException {
        if (token){
            System.out.println("---------------------------------");
            System.out.println("1.\tCom a client " + ID + " soc qui pot parlar (amb el servidor " + (ID + 1) + ") perque tinc el token a " + token + ". Li demano el valor de la variable");
            doStream.writeUTF(READ);
            //doStream.flush();
            LocalServer.sharedVar = diStream.readInt();
            localServer.useSharedVar();
            System.out.println("4.\tEnviant resultats del meu calcul a traves del socket.");
            doStream.writeUTF(CALCULATIONS);
            doStream.writeInt(LocalServer.sharedVar);
            token = false;
            System.out.println("6.\tSoc el client " + ID + " i com que he acabat de parlar i fer els calculs, em canvio el meu token a " + token + " i faig una petició per a que el token del seguent server sigui true");
            doStream.writeUTF(END_TOKEN);
        }
//        doStream.flush();
    }

    /**
     * Set the token's value. True if you must communicate, false otherwise
     * @param token Token value
     */
    public void setToken(boolean token) {
        this.token = token;
    }
}
