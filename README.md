# Event Service - Reservation Management System

A JavaFX application for managing event reservations. This system allows users to create, edit, and delete reservations for events.

## Features

- View all reservations in a table format
- Add new reservations
- Edit existing reservations
- Delete reservations
- Track reservation status

## Requirements

- Java 17 or higher
- JavaFX 17
- Maven

## Setup and Running

1. Clone the repository:
```bash
git clone https://github.com/roudaynazini/event-service.git
```

2. Navigate to the project directory:
```bash
cd event-service
```

3. Build the project:
```bash
mvn clean install
```

4. Run the application:
```bash
mvn javafx:run
```

## Project Structure

- `src/main/java/com/roudaynazini/`
  - `EventServiceApp.java` - Main application class
  - `MainViewController.java` - Controller for the main view
  - `Reservation.java` - Reservation model class
- `src/main/resources/com/roudaynazini/`
  - `main-view.fxml` - Main view layout

## Contributing

1. Fork the repository
2. Create your feature branch
3. Commit your changes
4. Push to the branch
5. Create a new Pull Request 