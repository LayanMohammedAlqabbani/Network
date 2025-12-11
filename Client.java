import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {

    static final String ServerHost = "192.168.100.24";
    static final int ServerPort = 8080;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        try {
            // Establish connection to the server
            Socket Socket = new Socket(ServerHost, ServerPort);
            System.out.println("Connected to the server at " + ServerHost + " on port " + ServerPort);

            // Create input/output streams
            BufferedReader reader = new BufferedReader(new InputStreamReader(Socket.getInputStream()));
            PrintWriter writer = new PrintWriter(Socket.getOutputStream(), true);

            // Start client interaction
            startClientSession(scanner, reader, writer);

        } catch (IOException e) {
            System.err.println("Error connecting to server: " + e.getMessage());
        }
    }

    // Method to handle client interaction
    private static void startClientSession(Scanner scanner, BufferedReader reader, PrintWriter writer)
            throws IOException {
        while (true) {
            String input = getUserInput(scanner);
            if (shouldExit(input)) {
                break;
            }

            String[] requestData = parseInput(input);
            if (requestData == null) {
                continue;
            }

            double base = Double.parseDouble(requestData[0]);
            double exponent = Double.parseDouble(requestData[1]);

            // Create request string
            String request = String.format("%.2f,%.2f", base, exponent);

            // Record start time for RTT measurement
            long startTime = System.nanoTime();

            // Send request
            sendRequest(writer, request);

            // Receive response from server
            String response = receiveResponse(reader);

            // Record end time for RTT measurement
            long endTime = System.nanoTime();

            // Calculate round-trip time in milliseconds
            double rtt = (endTime - startTime) / 1000000.0;

            if (response.startsWith("ERROR")) {
                System.out.println("Server Error: " + response);
            } else {
                displayResult(base, exponent, response);
                // Display RTT
                System.out.printf("Round-Trip Time: %.4f ms %n", rtt);
            }
        }
        System.out.println("Disconnecting from server...");
    }

    // Method to get user input
    private static String getUserInput(Scanner scanner) {
        System.out.print("Enter base and exponent in format (e.g. 3,4) (or 'exit' to EXIT): ");
        return scanner.nextLine().trim();
    }

    // Method to check if the user wants to quit
    private static boolean shouldExit(String input) {
        return "exit".equalsIgnoreCase(input);
    }

    // Method to parse the input into base and exponent
    private static String[] parseInput(String input) {
        String[] parts = input.split(",");
        if (parts.length != 2) {
            System.out.println("Invalid input! Please enter base and exponent separated by ','.");
            return null;
        }
        return parts;
    }

    // Method to send the request to the server
    private static void sendRequest(PrintWriter writer, String request) {
        writer.println(request);
    }

    // Method to receive the response from the server
    private static String receiveResponse(BufferedReader reader) throws IOException {
        return reader.readLine();
    }

    // Method to display the result
    private static void displayResult(double base, double exponent, String response) {
        double result = Double.parseDouble(response);
        System.out.printf("Result: %.2f ^ %.2f = %.4f%n", base, exponent, result);
    }
}
