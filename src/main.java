public class main {
    public static int sharedVar = 0;
    public static void main(String[] args) {
        LocalServer server = new LocalServer(sharedVar, args[1]);
        server.start();
        LocalClient client = new LocalClient(server, args[0], args[2]);
        client.start();
        server.registerClient(client);
    }
}

