// Peter Idestam-Almquist, 2017-03-10.
// Server, multi-threaded, accepting several simultaneous clients.

package Assignment4;

import java.net.Socket;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.io.PrintWriter;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

class ChatServer implements Runnable {
	private final static int PORT = 8000;
	private final static int MAX_CLIENTS = 5;
	private final static Executor executor = Executors.newFixedThreadPool(MAX_CLIENTS);

	private final Socket clientSocket;
	private String clientName = "";
	public PrintWriter socketWriter = null;

	private ChatServer(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}

	// Map<Socket, PrintWriter> clients = new HashMap<Socket, PrintWriter>();
	static LinkedList<Socket> sockets = new LinkedList<>();
	static LinkedList<ChatServer> chatServers = new LinkedList<>();

	LinkedList<PrintWriter> writers = new LinkedList<>();

	public static BlockingQueue<String> messageQueue = new ArrayBlockingQueue<String>(MAX_CLIENTS);

	private Runnable message(String message) {
		return () -> socketWriter.println(message);
	}

	public void run() {
		SocketAddress remoteSocketAddress = clientSocket.getRemoteSocketAddress();
		SocketAddress localSocketAddress = clientSocket.getLocalSocketAddress();
		System.out.println("Accepted client " + remoteSocketAddress + " (" + localSocketAddress + ").");

		BufferedReader socketReader = null;
		try {
			socketWriter = new PrintWriter(clientSocket.getOutputStream(), true);
			socketReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			writers.add(socketWriter);

			String threadInfo = " (" + Thread.currentThread().getName() + ").";
			String inputLine = socketReader.readLine();
			System.out.println("Received: \"" + inputLine + "\" from " + remoteSocketAddress + threadInfo);

			// First message is client name.
			clientName = inputLine;

			while (inputLine != null) {
				inputLine = clientName + ": " + inputLine;
				messageQueue.put(inputLine);

				System.out.println(
						"Sent: \"" + inputLine + "\" to " + clientName + " " + remoteSocketAddress + threadInfo);

				inputLine = socketReader.readLine();

				System.out.println(
						"Received: \"" + inputLine + "\" from " + clientName + " " + remoteSocketAddress + threadInfo);

				// executor.execute(message(socketWriter, messageQueue.take()));
				// socketWriter.println(inputLine);

			}
			System.out.println("Closing connection " + remoteSocketAddress + " (" + localSocketAddress + ").");
		} catch (Exception exception) {
			System.out.println(exception);
		} finally {
			try {
				if (socketWriter != null)
					socketWriter.close();
				if (socketReader != null)
					socketReader.close();
				if (clientSocket != null)
					clientSocket.close();
			} catch (Exception exception) {
				System.out.println(exception);
			}
		}
	}

	public static class MessageThread implements Runnable {
		BlockingQueue<String> queue = new ArrayBlockingQueue<>(5);
		String names;

		@Override
		public void run() {
			queue = ChatServer.messageQueue;

			while (true) {
				names = "";
				if (!queue.isEmpty()) {
					try {

						String message = queue.take();
						for (ChatServer chatServer : chatServers) {
							names += chatServer.clientName + ", ";

							executor.execute(() -> {
								chatServer.socketWriter.println(message);
							});

						}

						System.out.println(names);

					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}
		}

	}

	public static void main(String[] args) {
		System.out.println("Server2 started.");

		ServerSocket serverSocket = null;
		Socket clientSocket = null;
		try {
			serverSocket = new ServerSocket(PORT);
			SocketAddress serverSocketAddress = serverSocket.getLocalSocketAddress();
			System.out.println("Listening (" + serverSocketAddress + ").");

			// MessageThread messageThread = new MessageThread();
			executor.execute(new MessageThread());

			while (true) {
				clientSocket = serverSocket.accept();
				sockets.add(clientSocket);
				ChatServer chatServer = new ChatServer(clientSocket);
				chatServers.add(chatServer);
				executor.execute(chatServer);
			}
		} catch (Exception exception) {
			System.out.println(exception);
		} finally {
			try {
				if (serverSocket != null)
					serverSocket.close();
			} catch (Exception exception) {
				System.out.println(exception);
			}
		}
	}
}
