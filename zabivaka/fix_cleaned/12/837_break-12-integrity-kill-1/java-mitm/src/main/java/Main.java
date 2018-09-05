import commandserver.CommandServer;
import models.Done;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;
import picocli.CommandLine;
import team11.BruteForce;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Main implements Runnable {

    private static final int BUFFER_SIZE = 1024;

    @CommandLine.Option(names = "-p", defaultValue = "4000")
    int listeningPort;

    @CommandLine.Option(names = "-s", defaultValue = "127.0.0.1")
    String forwardHost;

    @CommandLine.Option(names = "-q", defaultValue = "3000")
    int forwardPort;

    @CommandLine.Option(names = "-c", defaultValue = "127.0.0.1")
    String commandHost;

    @CommandLine.Option(names = "-d", defaultValue = "5000")
    int commandPort;

    private CommandServer commandServer;

    private Interceptor provideInterceptor() {
        return new LoggingInterceptor() {

            private int accepted = 0;

            @Override public void onAccepted() {
                accepted++;
            }

            @Override public boolean shouldAcceptNextSocket() {
                return accepted < 1;
            }

            @Override public void onProxyClosed() {
                if (accepted == 1) {
                    BruteForce.bruteForce(commandServer, "ted");
                }
            }
        };
    }

    @Override public void run() {
        commandServer = new CommandServer(commandHost, commandPort);

        Thread timeoutThread = new Thread(() -> {
            try {
                Thread.sleep(TimeUnit.MINUTES.toMillis(25));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            sendDoneCommand();
        });
        timeoutThread.setDaemon(true);
        timeoutThread.start();

        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(listeningPort, 100, InetAddress.getByName("0.0.0.0"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));

        try {
            Interceptor interceptor = provideInterceptor();

            System.out.println("started");
            while (interceptor.shouldAcceptNextSocket()) {
                interceptor.onBeforeNextAccept();
                try {
                    try (Socket s = serverSocket.accept();
                         Socket forwardSocket = new Socket(forwardHost, forwardPort);
                         BufferedSource in = Okio.buffer(Okio.source(s));
                         BufferedSink out = Okio.buffer(Okio.sink(s));
                         BufferedSource forwardIn = Okio.buffer(Okio.source(forwardSocket));
                         BufferedSink forwardOut = Okio.buffer(Okio.sink(forwardSocket))) {
                        interceptor.onAccepted();

                        List<Thread> threads = Arrays.asList(
                            new Thread(() -> copyAndIntercept(interceptor::clientToServer, in, forwardOut)),
                            new Thread(() -> copyAndIntercept(interceptor::serverToClient, forwardIn, out))
                        );
                        threads.forEach(Thread::start);
                        for (Thread thread : threads) {
                            thread.join();
                        }
                    }
                    interceptor.onSocketClosed();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            interceptor.onProxyClosed();
            sendDoneCommand();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendDoneCommand() {
        commandServer.send(new Done());
    }

    public static void main(String[] args) {
        CommandLine.run(new Main(), System.out, args);
    }

    interface OneWayInterceptor {
        boolean intercept(byte[] bytes, BufferedSink sink) throws IOException;
    }

    private static void copyAndIntercept(OneWayInterceptor interceptor, BufferedSource in, BufferedSink out) {
        try {
            byte[] buffer = new byte[BUFFER_SIZE];
            while (!in.exhausted()) {
                int readBytes = in.read(buffer);
                byte[] forInterceptor;
                if (readBytes == -1) {
                    break;
                } else if (readBytes != buffer.length) {
                    forInterceptor = Arrays.copyOf(buffer, readBytes);
                } else {
                    forInterceptor = buffer;
                }

                boolean keepSocketOpen = interceptor.intercept(forInterceptor, out);
                if (!keepSocketOpen) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}