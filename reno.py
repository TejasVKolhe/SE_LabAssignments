import time
import random

# TCP Reno simulation
def tcp_reno(total_packets=50):
    cwnd = 1  # Start congestion window at 1
    ssthresh = 16  # Slow start threshold
    max_cwnd = 50  # Maximum window size
    rtt = 1  # Round trip time
    packet_number = 1 # Starting packet number
    packets_sent = []  # List to track sent packets

    while packet_number < total_packets + 128:  # Sending packets until reaching total_packets
        print(f"\n[INFO] Congestion Window (cwnd): {cwnd}")

        # Determine how many packets to send based on the congestion window
        packets_to_send = list(range(packet_number, packet_number + cwnd))
        print(f"Sending packets: {packets_to_send}")

        for packet in packets_to_send:
            # Simulate packet loss
            if random.random() < 0.1:  # Simulate packet loss
                print(f"Packet {packet} lost! Timeout detected.")
                print("Packet Loss Detected! Performing Slow Start.")
                ssthresh = cwnd // 2  # Multiplicative decrease
                cwnd = max(1, ssthresh)  # Reset congestion window
                print(f"New ssthresh: {ssthresh}, cwnd reset to: {cwnd}")

                packet_number = packet  # Start sending from the lost packet
                break  

            # Simulate successful acknowledgment
            print(f"Packet {packet} acknowledged.")

            # Simulate receiving duplicate ACKs for the last packet
            if packet == packets_to_send[-1] and random.random() < 0.2:  # Simulate receiving a duplicate ACK
                print(f"Received 3 Duplicate ACKs for packet {packet}!")
                ssthresh = cwnd // 2  # Update ssthresh
                cwnd = max(1, (ssthresh // 2) + 3)  # New cwnd after 3 duplicate ACKs
                print(f"New ssthresh: {ssthresh}, cwnd: {cwnd}")

                
                packet_number = packet  # Start sending from the duplicated ACK packet
                break  

        else:
            # If no break occurs, proceed to the next packet
            packet_number += cwnd  # Increment packet number based on cwnd

            # Adjust cwnd based on slow start or congestion avoidance
            if cwnd < ssthresh:
                cwnd *= 2  # Slow start (exponential increase)
            else:
                cwnd += 1  # Congestion avoidance (linear increase)

        time.sleep(rtt)  # Simulate RTT

    print("TCP Reno Congestion Control Complete.")

tcp_reno()
