# disaster-alert-prediction-system
Disaster Alert and Prediction System is a Java-based web application designed to monitor, analyze, and predict potential natural disasters such as earthquakes, floods, cyclones, wildfires, and other environmental hazards. The system collects real-time data from publicly available weather, seismic, and other monitoring APIs, processes .
# Disaster Alert and Prediction System 

## Tech Stack
- Java 17
- Spring Boot
- MySQL 8
- JDBC / JdbcTemplate
- HTML5
- CSS3
- JavaScript / Fetch API
- Maven

The report specifies Java 17+, MySQL/JDBC, real-time weather/seismic APIs, role-based users, alert history, settings, an admin panel, credential verification, and a dashboard with live weather/seismic data and color-coded risk zones. This project implements those concepts as a browser application.

## Main modules
1. Authentication & role-based access — ADMIN, AUTHORITY, USER
2. Real-time monitoring dashboard
3. Environmental data collection storage
4. Rule-based disaster prediction engine
5. Alert generation and in-app alert center
6. Alert history and analytics
7. Prediction simulator for viva/demo
8. Credential verification
9. User alert preferences
10. Admin users/credentials/system logs
11. MySQL persistence
12. Responsive UI

## Demo rules
- Seismic magnitude >= 5.5 → Earthquake risk
- Rainfall > 100 mm/hr OR water level >= 5 m → Flood risk
- Wind >= 55 km/h and humidity >= 70% → Cyclone risk
- Temperature > 40°C and humidity < 25% → Heatwave risk
- Otherwise → Normal

These are demonstration rules aligned with the threshold-based logic described in the report; they are NOT a certified disaster-warning model.

## Run
### 1. Install
- JDK 17+
- Maven 3.9+
- MySQL 8+

### 2. Database
Create a MySQL user/password and update:
`src/main/resources/application.properties`

The included schema/data scripts create the database/tables and seed demo data.

### 3. Start
```bash
mvn clean package
java -jar target/disaster-alert-system-1.0.0.jar
```
Open http://localhost:8080

## Demo credentials
- Admin: admin@disaster.local / admin123
- Authority: authority@disaster.local / authority123
- User: user@disaster.local / user123

## Viva demonstration flow
1. Login as USER
2. Open Dashboard → explain active alerts, risk zones and environmental signals
3. Open Active Alerts → explain severity, region and communication channels
4. Open Prediction Simulator
5. Enter rainfall 350, humidity 85 → Flood HIGH
6. Enter seismic 6.2 → Earthquake HIGH
7. Enter temperature 42, humidity 15 → Heatwave HIGH
8. Open Prediction History → show persisted predictions
9. Open Credential Verify → verify `VC-AUTH-2026-001`
10. Logout and login as ADMIN
11. Open Admin Panel → Users, Credentials, System Logs
12. Explain MySQL tables and Java service/controller/repository layers

## Project structure
```
DisasterAlertSystem/
├── pom.xml
├── README.md
└── src/main/
    ├── java/com/disasteralert/
    │   ├── DisasterAlertApplication.java
    │   ├── controller/ApiController.java
    │   ├── model/PredictionResult.java
    │   ├── repository/DisasterRepository.java
    │   └── service/PredictionService.java
    └── resources/
        ├── application.properties
        ├── schema.sql
        ├── data.sql
        └── static/
            ├── index.html
            ├── styles.css
            └── app.js
```

## UI screens
- Secure Login
- Real-time Dashboard
- Risk Map
- Environmental Signals
- Active Alerts
- Prediction History
- Prediction Simulator
- Credential Verification
- Alert Settings
- Admin Panel: Users
- Admin Panel: Credentials
- Admin Panel: System Logs
- Responsive mobile layout

## Important note
The supplied report describes OpenWeatherMap and USGS integration and multi-channel SMS/email notifications. For a dependable final-year demo, this starter uses seeded demo readings and a Java rule engine. External API keys and SMS/email provider credentials can be added later without changing the UI flow.

### MySQL first-time setup
Before starting Spring Boot, run:
```sql
SOURCE database-setup.sql;
```
or create the database manually:
```sql
CREATE DATABASE disaster_alert_db;
```
Then update the username/password in `application.properties`.
