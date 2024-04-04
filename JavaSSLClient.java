import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.*;
import java.security.KeyStore;
import javax.net.ssl.*;

public class JavaSSLServer {

    public static void main(String[] args) {
        try {
            // Load server keystore
            char[] keystorePassword = "password".toCharArray(); 
            KeyStore keystore = KeyStore.getInstance("JKS");
            FileInputStream keystoreFile = new FileInputStream("serverkeystore.jks"); 
            keystore.load(keystoreFile, keystorePassword);

            // Create SSL context
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keystore, keystorePassword);
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagerFactory.getKeyManagers(), null, null);

            // Create SSL server socket
            SSLServerSocketFactory sslServerSocketFactory = sslContext.getServerSocketFactory();
            SSLServerSocket sslServerSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(4242);
			System.out.println("SSL server socket is created successfully");
            while (true) {
                SSLSocket sslSocket = (SSLSocket) sslServerSocket.accept();
                new Thread(() -> {
                    try {
								 // Create a Scanner object to read input from the user
					Scanner scanner = new Scanner(System.in);
					
					// Prompt the user to enter the video filename
					System.out.println("Enter the video filename:");
					
					// Read the filename entered by the user
					String filename = scanner.nextLine().trim(); // Trim any leading/trailing whitespace
					
					// Check if the specified file exists
					File videofile = new File(filename);
					if (!videofile.exists()) {
						System.out.println("File not found: " + filename);
						scanner.close();
						return;
					}

					// Inform the user that the client is connecting to the server
					System.out.println("Connected to server");

					// Read the contents of the video file into a byte array
					byte[] videobytes = new byte[(int) videofile.length()];
					FileInputStream fis = new FileInputStream(videofile);
					fis.read(videobytes);
					fis.close();

					// Connect to the server
					Socket socket = new Socket("localhost", 4242);
					
					// Get the output stream of the socket and send the video file bytes
					OutputStream os = socket.getOutputStream();
					os.write(videobytes);
					os.flush();
					
					// Close the output stream and socket
					os.close();
					socket.close();
					
					// Close the scanner
					scanner.close();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}