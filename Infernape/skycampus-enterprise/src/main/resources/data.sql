-- Red Enterprise de demostración: 4 sedes y su flota inicial.
INSERT INTO sede (codigo, universidad, activa) VALUES ('ECI', 'Escuela Colombiana de Ingenieria', TRUE);
INSERT INTO sede (codigo, universidad, activa) VALUES ('UNAL', 'Universidad Nacional', TRUE);
INSERT INTO sede (codigo, universidad, activa) VALUES ('UNIANDES', 'Universidad de los Andes', TRUE);
INSERT INTO sede (codigo, universidad, activa) VALUES ('EAFIT', 'Universidad EAFIT', TRUE);

INSERT INTO drone (id, sede_codigo, bateria, capacidad_carga_kg, disponible, express) VALUES ('D-ECI-01', 'ECI', 92, 5.0, TRUE, FALSE);
INSERT INTO drone (id, sede_codigo, bateria, capacidad_carga_kg, disponible, express) VALUES ('D-ECI-02', 'ECI', 78, 2.0, TRUE, TRUE);
INSERT INTO drone (id, sede_codigo, bateria, capacidad_carga_kg, disponible, express) VALUES ('D-ECI-03', 'ECI', 65, 15.0, TRUE, FALSE);
INSERT INTO drone (id, sede_codigo, bateria, capacidad_carga_kg, disponible, express) VALUES ('D-UNAL-01', 'UNAL', 88, 5.0, TRUE, TRUE);
INSERT INTO drone (id, sede_codigo, bateria, capacidad_carga_kg, disponible, express) VALUES ('D-UNIANDES-01', 'UNIANDES', 70, 5.0, FALSE, FALSE);
INSERT INTO drone (id, sede_codigo, bateria, capacidad_carga_kg, disponible, express) VALUES ('D-EAFIT-01', 'EAFIT', 95, 5.0, TRUE, FALSE);
