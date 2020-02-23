//Sebastian Åkerlund
// Server, multi-threaded, accepting several simultaneous clients.

package Assignment4;

import java.net.Socket;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.io.PrintWriter;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

class ChatServer implements Runnable {
	private final static int PORT = 8000;
	private final static int MAX_CLIENTS = 5;
	private final static Executor executor = Executors.newFixedThreadPool(MAX_CLIENTS);

	private final Socket clientSocket;
	private String clientName = "";
	public PrintWriter socketWriter = null;

	private static String allNames = "";

	private ChatServer(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}

	// static LinkedList<ChatServer> chatServers = new LinkedList<>();
	static CopyOnWriteArrayList<ChatServer> chatServers = new CopyOnWriteArrayList<>();

	public static BlockingQueue<String> messageQueue = new ArrayBlockingQueue<String>(MAX_CLIENTS);

	public void run() {
		SocketAddress remoteSocketAddress = clientSocket.getRemoteSocketAddress();
		SocketAddress localSocketAddress = clientSocket.getLocalSocketAddress();
		System.out.println("Accepted client " + remoteSocketAddress + " (" + localSocketAddress + ").");

		BufferedReader socketReader = null;
		try {
			socketWriter = new PrintWriter(clientSocket.getOutputStream(), true);
			socketReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

			String threadInfo = " (" + Thread.currentThread().getName() + ").";
			String inputLine = socketReader.readLine();
			System.out.println("Received: \"" + inputLine + "\" from " + remoteSocketAddress + threadInfo);

			// First message is client name.
			clientName = inputLine;
			while (inputLine != null) {
				if(inputLine.compareTo("exit")){
					chatServers.remove(clientSocket);
					clientSocket.close();
				}

				inputLine = clientName + ": " + inputLine;
				messageQueue.put(inputLine);

				System.out.println(
						"Sent: \"" + inputLine + "\" to " + allNames + " " + remoteSocketAddress + threadInfo);

				inputLine = socketReader.readLine();
				allNames = "";
				for (ChatServer chatServer : chatServers) {
					allNames += chatServer.clientName + ", ";

				}

				System.out.println(
						"Received: \"" + inputLine + "\" from " + clientName + " " + remoteSocketAddress + threadInfo);



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


					} catch (InterruptedException e) {
						System.out.println(e);
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
