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

    public List<Map<String, Object>> alerts() {

        return db.queryForList("""
                SELECT *
                FROM alerts
                ORDER BY issued_at DESC
                """);
    }

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

        return db.queryForObject(
                "SELECT LAST_INSERT_ID()",
                Long.class
        );
    }

    public void insertPrediction(
            long reading,
            PredictionResult p) {

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
                p.disasterType(),
                p.riskLevel(),
                p.probability(),
                p.confidence(),
                p.message(),
                p.recommendedAction()
        );

        // Create an alert when the prediction is not NORMAL
        if (!"NORMAL".equals(p.disasterType())) {

            Long predictionId = db.queryForObject(
                    "SELECT LAST_INSERT_ID()",
                    Long.class
            );

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
                    p.disasterType() + " Alert",
                    p.disasterType(),
                    p.riskLevel(),
                    "Demo Region",
                    p.message() + " " + p.recommendedAction(),
                    "IN_APP,EMAIL",
                    "ACTIVE"
            );
        }
    }

    public Map<String, Object> stats() {

        Map<String, Object> m = new LinkedHashMap<>();

        m.put(
                "users",
                db.queryForObject(
                        "SELECT COUNT(*) FROM users",
                        Long.class
                )
        );

        m.put(
                "alerts",
                db.queryForObject(
                        "SELECT COUNT(*) FROM alerts",
                        Long.class
                )
        );

        m.put(
                "activeAlerts",
                db.queryForObject(
                        "SELECT COUNT(*) FROM alerts WHERE status = 'ACTIVE'",
                        Long.class
                )
        );

        m.put(
                "predictions",
                db.queryForObject(
                        "SELECT COUNT(*) FROM predictions",
                        Long.class
                )
        );

        return m;
    }
}