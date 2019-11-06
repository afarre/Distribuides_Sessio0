import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class LocalServer extends  Thread {
    private int PORT;
    private DataInputStream diStream;
    private DataOutputStream doStream;
    private final int ID;
    private LocalClient localClient;
    public static int sharedVar;

    public LocalServer(LocalClient localClient, int sharedVar, int port, int id){
        this.PORT = port;
        this.ID = id;
        LocalServer.sharedVar = sharedVar;
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
            case LocalClient.READ:
                System.out.println("2.\tVALOR: " + sharedVar + " (I'm server " + ID + ")\n[CALCULATING]");
                doStream.writeInt(sharedVar);
                break;

            case LocalClient.END_TOKEN:
                System.out.println("7.\tCanviant el token del client " + ID + " en base a la peticio del client anterior...");
                localClient.setToken(true);
                System.out.println("-----------------------------------------");
                break;

            case LocalClient.CALCULATIONS:
                sharedVar = diStream.readInt();
                System.out.println("5.\tActualitzant la variable de sharedVar en el servidor " + ID + ". Ara sharedVal val: " + sharedVar);
                break;

            case LocalClient.HANDSHAKE:
                int id = diStream.readInt();
                int port = diStream.readInt();
                System.out.println("[HANDSHAKE]\tSoc el servidor " + ID + " i rebo missatges del client " + id + " a traves del port " + port + ".");
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
        System.out.println("3.\tPOST VALOR: " + sharedVar + " (I'm server " + ID + ")");
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
        this.localClient = localClient;
    }
}
