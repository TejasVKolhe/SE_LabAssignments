import time
import random




def tcp_tahoe():
    cwnd = 1  # Congestion window size
    ssthresh = 16  # Slow start threshold
    max_cwnd = 50  # Maximum congestion window size
    rtt = 1  # Simulated round-trip time

    packets_sent = 0  # Total packets sent so far
    total_packets = 20 # Total packets to send
    packets_to_send = total_packets  # Remaining packets to be sent
    lost_packet = None  # To track the lost packet for retransmission

    while cwnd < max_cwnd and packets_sent < total_packets:
        print(f"\nCurrent Congestion Window: {cwnd}")

        for i in range(cwnd):
            if packets_to_send > 0 or lost_packet is not None:
                # If there's a lost packet, retransmit it
                if lost_packet is not None:
                    print(f"Retransmitting lost packet {lost_packet}")
                    packet_no = lost_packet
                    lost_packet = None  # Clear the lost packet after retransmission
                else:
                    packet_no = packets_sent + 1
                    print(f"Sending packet {packet_no}")
                    packets_sent += 1
                    packets_to_send -= 1

                # Simulate a packet loss with 10% probability
                if random.random() < 0.1:
                    print(f"Packet {packet_no} Lost! TimeOut occurred.")
                    ssthresh = cwnd // 2  # Update the threshold to half of the current congestion window
                    print(f"Threshold reset to {ssthresh}")
                    cwnd = 1  # Reset congestion window to 1 after packet loss
                    lost_packet = packet_no  # Track the lost packet for retransmission
                    break  # Break out of the for loop to simulate timeout and retransmission
            
        else:  # This else block is executed when there is no packet loss
            # Adjust congestion window based on slow start or congestion avoidance phase
            if cwnd < ssthresh:
                cwnd *= 2  # Exponential growth during slow start
            else:
                cwnd += 1  # Linear growth during congestion avoidance
        
        time.sleep(rtt)  # Simulate round-trip time

    print(f"\nDone! Total packets sent: {packets_sent}")

tcp_tahoe()
