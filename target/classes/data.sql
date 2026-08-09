USE disaster_alert_db;
INSERT IGNORE INTO users(id,full_name,email,password,phone,location,role,credential_status)
VALUES
(1,'System Administrator','admin@disaster.local','admin123','9999999999','Chennai','ADMIN','VERIFIED'),
(2,'Emergency Authority','authority@disaster.local','authority123','9888888888','Tirupati','AUTHORITY','VERIFIED'),
(3,'Demo Citizen','user@disaster.local','user123','9777777777','Tirupati','USER','VERIFIED');

INSERT IGNORE INTO disaster_readings(id,location,temperature,humidity,rainfall,wind_speed,water_level,seismic_magnitude,source)
VALUES
(1,'Tirupati',31.2,82,145.5,28.4,4.8,2.1,'DEMO / WEATHER API'),
(2,'Chennai',35.7,76,22.2,51.3,3.2,3.0,'DEMO / WEATHER API'),
(3,'Hyderabad',42.1,18,4.0,18.2,1.1,1.5,'DEMO / WEATHER API'),
(4,'Visakhapatnam',29.8,88,205.0,72.0,6.4,5.8,'DEMO / WEATHER + USGS');

INSERT IGNORE INTO predictions(id,reading_id,disaster_type,risk_level,probability,confidence,message,recommended_action)
VALUES
(1,1,'FLOOD','HIGH',86,91,'Heavy rainfall and elevated humidity indicate possible flood conditions.','Move to higher ground and follow local evacuation instructions.'),
(2,2,'CYCLONE','MEDIUM',64,78,'High wind speed detected near the coastal monitoring zone.','Avoid coastal travel and monitor official warnings.'),
(3,3,'HEATWAVE','HIGH',89,94,'Extreme temperature with very low humidity indicates heatwave risk.','Stay hydrated, avoid direct sun and remain indoors during peak heat.'),
(4,4,'EARTHQUAKE','HIGH',82,88,'Seismic magnitude crossed the demonstration alert threshold.','Drop, cover and hold; move away from unsafe structures.');

INSERT IGNORE INTO alerts(id,prediction_id,title,disaster_type,severity,region,message,channels,status)
VALUES
(1,1,'Flood Warning - Tirupati','FLOOD','HIGH','Tirupati','Heavy rainfall detected. Possible flooding in low-lying areas. Take precautionary action immediately.','IN_APP,EMAIL','ACTIVE'),
(2,2,'High Wind Advisory - Chennai','CYCLONE','MEDIUM','Chennai','Strong winds detected. Coastal residents should monitor official advisories.','IN_APP','ACTIVE'),
(3,3,'Heatwave Alert - Hyderabad','HEATWAVE','HIGH','Hyderabad','Extreme heat conditions detected. Avoid outdoor exposure during peak hours.','IN_APP,EMAIL,SMS','ACTIVE'),
(4,4,'Earthquake Alert - Visakhapatnam','EARTHQUAKE','HIGH','Visakhapatnam','Significant seismic activity detected. Follow emergency safety procedures.','IN_APP,SMS','ACTIVE');

INSERT IGNORE INTO credentials(id,user_id,credential_code,credential_type,issuer,status,expires_at)
VALUES
(1,2,'VC-AUTH-2026-001','Certified Emergency Responder','Disaster Authority','VALID','2027-12-31'),
(2,3,'VC-CIT-2026-014','Verified Citizen','Disaster Authority','VALID','2027-06-30');

INSERT IGNORE INTO system_logs(id,user_id,action,module,details)
VALUES
(1,1,'SYSTEM_STARTED','SYSTEM','Application initialized successfully'),
(2,2,'ALERT_ISSUED','ALERT','Flood alert issued for Tirupati'),
(3,1,'CREDENTIAL_VERIFIED','CREDENTIAL','Authority credential verified'),
(4,3,'LOGIN','AUTH','Demo citizen logged in');
