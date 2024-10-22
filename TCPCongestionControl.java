package tcpCongestionControl;

import java.util.Random;

public class TCPCongestionControl {
    private static final int MAX_WINDOW_SIZE = 16; // Maximum congestion window size
    private static final int MAX_PACKETS = 40; // Total packets to send
    private static final Random random = new Random();

    private int cwnd; // Congestion window size
    private int sentPackets; // Total packets sent
    private int duplicatedAcks; // Count of duplicate ACKs
    private int ssthresh; // Slow start threshold

    public TCPCongestionControl() {
        this.cwnd = 1; // Start with a congestion window size of 1
        this.sentPackets = 0; // No packets sent yet
        this.duplicatedAcks = 0; // No duplicate ACKs yet
        this.ssthresh = MAX_WINDOW_SIZE; // Initialize slow start threshold
    }

    // Start the simulation
    public void startSimulation() {
        System.out.println("\n--- TCP Tahoe Simulation ---");
        tcpTahoe();
        System.out.println("\n--- TCP Reno Simulation ---");
        tcpReno();
    }

    // Simulate TCP Tahoe
    public void tcpTahoe() {
        reset();

        int lastSentPacket; // Track the last packet sent
        while (sentPackets < MAX_PACKETS) {
            // Determine how many packets to send based on cwnd
            int packetsToSend = Math.min(cwnd, MAX_PACKETS - sentPackets);
            lastSentPacket = sentPackets + packetsToSend; // Update last sent packet (1-indexed)
            sentPackets += packetsToSend;
            System.out.println("Sent " + packetsToSend + " packets. Total sent: " + sentPackets);

            if (simulateAckReception()) {
                System.out.println("ACK received for packets: " + (lastSentPacket - packetsToSend + 1) + " to " + lastSentPacket);
                // Increase cwnd but do not exceed ssthresh
                if (cwnd < ssthresh) {
                    cwnd *= 2; // Double the cwnd on ACK if in slow start
                } else {
                    cwnd++; // Increase by 1 if in congestion avoidance
                }
                if (cwnd > MAX_WINDOW_SIZE) cwnd = MAX_WINDOW_SIZE; // Cap cwnd
                System.out.println("cwnd increased to: " + cwnd);
            } else {
                // ACK is lost; reset cwnd to 1
                System.out.println("ACK lost for packets: " + (lastSentPacket - packetsToSend + 1) + " to " + lastSentPacket);
                System.out.println("Packet loss detected, resetting cwnd to 1.");

                // Update ssthresh
                ssthresh = Math.max(cwnd / 2, 2); // Set ssthresh to half of cwnd
                System.out.println("ssthresh set to: " + ssthresh);
                cwnd = 1; // Reset cwnd to 1
                System.out.println("cwnd reset to: " + cwnd);
                sentPackets -= packetsToSend; // Adjust sentPackets to account for lost packets
            }

            simulateRtt();
        }
    }

    // Simulate TCP Reno
    public void tcpReno() {
        reset();

        while (sentPackets < MAX_PACKETS) {
            // Determine how many packets to send based on cwnd
            int packetsToSend = Math.min(cwnd, MAX_PACKETS - sentPackets);
            sentPackets += packetsToSend;
            System.out.println("Sent " + packetsToSend + " packets. Total sent: " + sentPackets);

            if (simulateAckReception()) {
                // ACK received
                System.out.println("ACK received for packets: " + (sentPackets - packetsToSend + 1) + " to " + sentPackets);
                cwnd = Math.min(cwnd + 1, MAX_WINDOW_SIZE); // Increment cwnd by 1 and cap it
                System.out.println("cwnd increased to: " + cwnd);
                duplicatedAcks = 0; // Reset duplicate ACK count
            } else {
                // ACK is lost; check for duplicate ACKs
                System.out.println("ACK lost for packets: " + (sentPackets - packetsToSend + 1) + " to " + sentPackets);
                duplicatedAcks++;
                System.out.println("Duplicate ACK count: " + duplicatedAcks);

                if (duplicatedAcks < 3) {
                    // Ignore packet loss on duplicate ACK, continue
                    continue; // Just continue to the next iteration
                } else {
                    // Three duplicate ACKs received
                    System.out.println("Three duplicate ACKs received, entering fast recovery.");
                    int lostPacketNumber = sentPackets - packetsToSend + 1; // First lost packet number
                    System.out.println("Retransmitting lost packet: " + lostPacketNumber);

                    // Set the slow start threshold
                    ssthresh = cwnd / 2;
                    cwnd = Math.max(cwnd / 2, 1); // Enter fast recovery
                    System.out.println("cwnd decreased to: " + cwnd);
                    duplicatedAcks = 0; // Reset duplicate ACK count

                    // Adjust sentPackets to reflect the retransmission
                    sentPackets--; // Decrease sent packets count to reflect the retransmission
                }
            }

            // Send more packets based on the new congestion window
            int additionalPacketsToSend = Math.min(cwnd, MAX_PACKETS - sentPackets);
            if (additionalPacketsToSend > 0) {
                sentPackets += additionalPacketsToSend;
                System.out.println("Sent additional " + additionalPacketsToSend + " packets. Total sent: " + sentPackets);
            }

            simulateRtt();
        }
    }

    // Simulate the reception of ACK
    private boolean simulateAckReception() {
        // Simulate a 30% chance of losing an ACK
        return random.nextInt(10) >= 3;
    }

    // Simulate Round Trip Time (RTT) with random delays
    private void simulateRtt() {
        int rtt = 50 + random.nextInt(101); // Random RTT between 50 and 150 ms
        try {
            Thread.sleep(rtt); // Simulate the delay for RTT
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("Simulated RTT: " + rtt + " ms");
    }

    // Reset state for a new simulation
    private void reset() {
        cwnd = 1; // Reset congestion window size
        sentPackets = 0; // Reset total sent packets
        duplicatedAcks = 0; // Reset duplicate ACK count
        ssthresh = MAX_WINDOW_SIZE; // Reset slow start threshold
    }

    // Main method to run the simulations
    public static void main(String[] args) {
        TCPCongestionControl tcp = new TCPCongestionControl();
        tcp.startSimulation();
    }
}
