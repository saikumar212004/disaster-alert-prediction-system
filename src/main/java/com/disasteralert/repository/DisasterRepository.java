package com.disasteralert.repository;

import com.disasteralert.model.PredictionResult;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
public class DisasterRepository {

    private final JdbcTemplate db;

    public DisasterRepository(JdbcTemplate db) {
        this.db = db;
    }

    /**
     * Returns the latest disaster readings along with their predictions.
     */
    public List<Map<String, Object>> dashboard() {

        return db.queryForList("""
                SELECT
                    r.id,
                    r.location,
                    r.temperature,
                    r.humidity,
                    r.rainfall,
                    r.wind_speed,
                    r.water_level,
                    r.seismic_magnitude,
                    r.recorded_at,
                    p.disaster_type,
                    p.risk_level,
                    p.probability,
                    p.confidence,
                    p.message,
                    p.recommended_action
                FROM disaster_readings r
                LEFT JOIN predictions p
                    ON p.reading_id = r.id
                ORDER BY r.recorded_at DESC
                LIMIT 10
                """);
    }

    /**
     * Returns all alerts, newest first.
     */
    public List<Map<String, Object>> alerts() {

        return db.queryForList("""
                SELECT *
                FROM alerts
                ORDER BY issued_at DESC
                """);
    }

    /**
     * Returns all registered users.
     */
    public List<Map<String, Object>> users() {

        return db.queryForList("""
                SELECT
                    id,
                    full_name,
                    email,
                    phone,
                    location,
                    role,
                    credential_status,
                    created_at
                FROM users
                ORDER BY id
                """);
    }

    /**
     * Returns credentials together with user information.
     */
    public List<Map<String, Object>> credentials() {

        return db.queryForList("""
                SELECT
                    c.*,
                    u.full_name,
                    u.email
                FROM credentials c
                JOIN users u
                    ON u.id = c.user_id
                ORDER BY c.id
                """);
    }

    /**
     * Returns the latest system logs.
     */
    public List<Map<String, Object>> logs() {

        return db.queryForList("""
                SELECT
                    l.*,
                    u.full_name
                FROM system_logs l
                LEFT JOIN users u
                    ON u.id = l.user_id
                ORDER BY l.created_at DESC
                LIMIT 100
                """);
    }

    /**
     * Returns prediction history together with the environmental
     * conditions that produced each prediction.
     */
    public List<Map<String, Object>> history() {

        return db.queryForList("""
                SELECT
                    p.*,
                    r.location,
                    r.temperature,
                    r.humidity,
                    r.rainfall,
                    r.wind_speed,
                    r.water_level,
                    r.seismic_magnitude
                FROM predictions p
                JOIN disaster_readings r
                    ON r.id = p.reading_id
                ORDER BY p.predicted_at DESC
                """);
    }

    /**
     * Inserts an environmental reading and returns the generated ID.
     */
    public long insertReading(
            String location,
            double temp,
            double hum,
            double rain,
            double wind,
            double water,
            double seismic,
            String source) {

        db.update("""
                INSERT INTO disaster_readings
                    (
                        location,
                        temperature,
                        humidity,
                        rainfall,
                        wind_speed,
                        water_level,
                        seismic_magnitude,
                        source
                    )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """,
                location,
                temp,
                hum,
                rain,
                wind,
                water,
                seismic,
                source
        );

        Long id = db.queryForObject(
                "SELECT LAST_INSERT_ID()",
                Long.class
        );

        if (id == null) {
            throw new IllegalStateException(
                    "Unable to retrieve generated disaster reading ID"
            );
        }

        return id;
    }

    /**
     * Inserts a prediction.
     *
     * If the prediction is not NORMAL, an alert is also created.
     */
    public void insertPrediction(
            long reading,
            PredictionResult p) {

        if (p == null) {
            throw new IllegalArgumentException(
                    "PredictionResult cannot be null"
            );
        }

        /*
         * Insert prediction.
         *
         * IMPORTANT:
         * These are Java getter methods because p is a Java object.
         */
        db.update("""
                INSERT INTO predictions
                    (
                        reading_id,
                        disaster_type,
                        risk_level,
                        probability,
                        confidence,
                        message,
                        recommended_action
                    )
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """,
                reading,
                p.getDisasterType(),
                p.getRiskLevel(),
                p.getProbability(),
                p.getConfidence(),
                p.getMessage(),
                p.getRecommendation()
        );

        /*
         * Create an alert for every disaster prediction except NORMAL.
         */
        if (!"NORMAL".equalsIgnoreCase(p.getDisasterType())) {

            Long predictionId = db.queryForObject(
                    "SELECT LAST_INSERT_ID()",
                    Long.class
            );

            if (predictionId == null) {
                throw new IllegalStateException(
                        "Unable to retrieve generated prediction ID"
                );
            }

            String title = p.getDisasterType() + " Alert";

            String alertMessage =
                    p.getMessage()
                            + " "
                            + p.getRecommendation();

            db.update("""
                    INSERT INTO alerts
                        (
                            prediction_id,
                            title,
                            disaster_type,
                            severity,
                            region,
                            message,
                            channels,
                            status
                        )
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                    """,
                    predictionId,
                    title,
                    p.getDisasterType(),
                    p.getRiskLevel(),
                    "Demo Region",
                    alertMessage,
                    "IN_APP,EMAIL",
                    "ACTIVE"
            );
        }
    }

    /**
     * Returns dashboard statistics.
     */
    public Map<String, Object> stats() {

        Map<String, Object> m = new LinkedHashMap<>();

        Long users = db.queryForObject(
                "SELECT COUNT(*) FROM users",
                Long.class
        );

        Long alerts = db.queryForObject(
                "SELECT COUNT(*) FROM alerts",
                Long.class
        );

        Long activeAlerts = db.queryForObject(
                "SELECT COUNT(*) FROM alerts WHERE status = 'ACTIVE'",
                Long.class
        );

        Long predictions = db.queryForObject(
                "SELECT COUNT(*) FROM predictions",
                Long.class
        );

        m.put("users", users != null ? users : 0);
        m.put("alerts", alerts != null ? alerts : 0);
        m.put(
                "activeAlerts",
                activeAlerts != null ? activeAlerts : 0
        );
        m.put(
                "predictions",
                predictions != null ? predictions : 0
        );

        return m;
    }
}