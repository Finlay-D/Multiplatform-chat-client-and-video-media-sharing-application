import org.junit.jupiter.api.Test;
import java.io.*;
import java.net.Socket;
import java.util.concurrent.*;

class serverTestHJ2 {
    @Test
    void testLobbes() {
        final int CLIENT_COUNT = 50;
        final CountDownLatch latch = new CountDownLatch(CLIENT_COUNT);

        try (PrintWriter logWriter = new PrintWriter(new FileWriter("server_log.txt"))) {
            for (int i = 0; i < CLIENT_COUNT; i++) {
                Thread t = new Thread(() -> {
                    try {
                        Socket socket = new Socket("localhost", 9000);
                        PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                        writer.println("user" + Thread.currentThread().getId());
                        reader.readLine(); // ignoring the response

                        String message = "/message nonExistent Hello from " + Thread.currentThread().getId();

                        latch.countDown();
                        try {
                            latch.await();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        writer.println("/newLobby TESTLOBBY");
                        logWriter.println(message);

                        writer.println("/JoinLobby TESTLOBBY");
                        reader.readLine();

                        writer.println("/startGame Hangman");
                        reader.readLine();

                        socket.close();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                t.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
