# Vehicle Entry Exit - Android Frontend (Kotlin)

This Android application serves as the frontend for the Vehicle Entry Exit system, connecting to a Spring Boot backend via REST APIs. It provides user-friendly interfaces for vehicle management, QR code scanning, and information access.

## Features

-   **User Authentication:** Secure login functionality to access the application.
-   **Vehicle Management:**
    -      Add, edit, and view vehicle details.
    -      Generate and download QR codes for vehicle numbers (using ZXing and ML Kit).
-   **QR Code Scanning:** Scan QR codes to log vehicle entry/exit.
-   **Log Viewing:** View vehicle entry/exit logs.
-   **Announcements:** View announcements posted by the manager.
-   **Profile Management:** View and manage personal profile information.

## Technologies Used

-   **Kotlin:** Primary programming language.
-   **Android SDK:** For Android application development.
-   **Retrofit:** For handling REST API calls to the backend.
-   **ZXing (Zebra Crossing):** For generating QR codes.
-   **ML Kit (Google Machine Learning Kit):** For scanning QR codes.

## QR Code Generation and Scanning

-   **Generation:** The application uses ZXing to generate QR codes from vehicle numbers.
-   **Scanning:** ML Kit is used for scanning QR codes and extracting vehicle numbers.

## API Communication

-   Retrofit is used to handle all API communication with the backend.
-   Data models are defined to represent API responses.
-   Repositories are used to abstract API calls and provide data to the UI.
