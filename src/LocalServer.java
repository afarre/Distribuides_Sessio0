import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class LocalServer extends  Thread {
    private final int PORT;
    private DataInputStream diStream;
    private DataOutputStream doStream;
    private LocalClient client;
    public static int sharedVar;


    public LocalServer(int sharedVar, String arg){
        LocalServer.sharedVar = sharedVar;
        PORT = Integer.parseInt(arg);
    }


    @Override
    public void run(){
        try {
            //creem el nostre socket
            ServerSocket serverSocket = new ServerSocket(PORT);
            while (true){
                //esperem a la conexio d'algun client dins d'un bucle infinit
                Socket socket = serverSocket.accept();
                generaNouServidorDedicat(socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Generates a new server dedicated to the client or clients that wish to connect
     * @param socket Socket belonging to the client that wishes to connect
     */
    private void generaNouServidorDedicat(Socket socket){
        try {
            doStream = new DataOutputStream(socket.getOutputStream());
            diStream = new DataInputStream(socket.getInputStream());
            while (true){
                String request = diStream.readUTF();
                readRequest(request);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads and treats the requests sent by clients
     * @param request String identifying the type of request
     * @throws IOException In case any data stream fails to work
     */
    private void readRequest(String request) throws IOException {
        switch (request){
            case LocalClient.HANDSHAKE:
                int port = diStream.readInt();
                System.out.println("[HANDSHAKE]\tSoc el servidor i rebo missatges del client a traves del port " + port + ".");
                break;

            case LocalClient.CALCULATIONS:
                LocalServer.sharedVar = diStream.readInt();
                System.out.println("[SERVER - 1].\tActualitzant la variable de sharedVar. Ara sharedVal val: " + sharedVar);
                break;

            case LocalClient.END_TOKEN:
                System.out.println("[SERVER - 2].\tCanviant el token del meu client en base a la peticio del client anterior...");
                client.setToken(true);
                client.talk();
                System.out.println("-----------------------------------------");
                break;
        }
    }

    /**
     * Modifies the shared variable
     */
    public void useSharedVar() {
        for (int i = 0; i < 10; i++){
            int valor = getCurrentValue();
            updateCurrentValue(valor + 1);

            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Updates (addition) the shared variable
     * @param i Number to be added to the current value of the shared variable
     */
    private synchronized void updateCurrentValue(int i) {
        sharedVar = i;
    }

    /**
     * Retrieves the shared variable's value
     * @return The shared variable's value
     */
    private synchronized int getCurrentValue() {
        return sharedVar;
    }

    /**
     * Sets the client associated to this server
     * @param localClient Client to be associated
     */
    public void setClient(LocalClient localClient) {
        this.client = localClient;
    }

    public void registerClient(LocalClient client) {
        this.client = client;
    }
}
