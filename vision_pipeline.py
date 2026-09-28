import cv2
import numpy as np

def main():
    # 1. Initialize camera feed
    cap = cv2.VideoCapture(0)
    cap.set(cv2.CAP_PROP_FRAME_WIDTH, 640)
    cap.set(cv2.CAP_PROP_FRAME_HEIGHT, 480)

    # 2. Define HSV ranges and display colors for target detection
    # Format: "Color Name": (Lower HSV, Upper HSV, BGR Overlay Color)
    COLOR_RANGES = {
        "ORANGE": (np.array([5, 150, 150]), np.array([25, 255, 255]), (0, 165, 255)),
        "YELLOW": (np.array([20, 100, 100]), np.array([35, 255, 255]), (0, 255, 255)),
        "GREEN":  (np.array([35, 80, 80]), np.array([85, 255, 255]), (0, 255, 0)),
        "BLUE":   (np.array([100, 150, 50]), np.array([140, 255, 255]), (255, 0, 0)),
        "RED":    (np.array([0, 150, 100]), np.array([10, 255, 255]), (0, 0, 255))
    }

    print("Multi-Color Detector Started. Press 'q' on the camera window to quit.")

    while True:
        ret, frame = cap.read()
        if not ret:
            print("Failed to grab frame from camera.")
            break

        height, width, _ = frame.shape
        frame_center_x = width // 2

        # Convert frame from BGR to HSV color space
        hsv_frame = cv2.cvtColor(frame, cv2.COLOR_BGR2HSV)
        
        # Draw center reference crosshair line
        cv2.line(frame, (frame_center_x, 0), (frame_center_x, height), (150, 150, 150), 1)

        detected_color = None
        max_overall_area = 0
        best_target_info = None

        # Check each color range
        for color_name, (lower_bound, upper_bound, box_color) in COLOR_RANGES.items():
            mask = cv2.inRange(hsv_frame, lower_bound, upper_bound)
            
            # Clean up noise
            kernel = np.ones((5, 5), np.uint8)
            mask = cv2.morphologyEx(mask, cv2.MORPH_OPEN, kernel)

            contours, _ = cv2.findContours(mask, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)

            for cnt in contours:
                area = cv2.contourArea(cnt)
                # Filter out small noise particles (must be larger than 800 px)
                if area > 800 and area > max_overall_area:
                    max_overall_area = area
                    detected_color = color_name
                    x, y, w, h = cv2.boundingRect(cnt)
                    delta_x = (x + w // 2) - frame_center_x
                    best_target_info = (x, y, w, h, delta_x, box_color)

        # Render detected object and large text banner
        if best_target_info:
            x, y, w, h, delta_x, box_color = best_target_info
            
            # Draw bounding box and target center dot
            cv2.rectangle(frame, (x, y), (x + w, y + h), box_color, 3)
            cv2.circle(frame, (x + w // 2, y + h // 2), 6, (0, 0, 255), -1)

            # --- LARGE ON-SCREEN DISPLAY (NO SMALL TERMINAL TEXT) ---
            # Top banner box
            cv2.rectangle(frame, (0, 0), (640, 70), (20, 20, 20), -1)
            
            # Big color name text
            cv2.putText(frame, f"COLOR: {detected_color}", (20, 45),
                        cv2.FONT_HERSHEY_SIMPLEX, 1.2, box_color, 3)
            
            # Big Delta X offset text
            cv2.putText(frame, f"OFFSET: {delta_x} px", (370, 45),
                        cv2.FONT_HERSHEY_SIMPLEX, 0.9, (255, 255, 255), 2)
        else:
            # Display searching banner when no target is present
            cv2.rectangle(frame, (0, 0), (640, 50), (20, 20, 20), -1)
            cv2.putText(frame, "STATUS: SEARCHING FOR COLOR...", (20, 35),
                        cv2.FONT_HERSHEY_SIMPLEX, 0.8, (180, 180, 180), 2)

        # Show camera feed
        cv2.imshow("FRC Multi-Color Vision Detector", frame)

        # Press 'q' to quit
        if cv2.waitKey(1) & 0xFF == ord('q'):
            break

    cap.release()
    cv2.destroyAllWindows()

if __name__ == "__main__":
    main()