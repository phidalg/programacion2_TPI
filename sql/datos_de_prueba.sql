USE `prog2_tpi`;

-- ----------------------------
-- Inserción de 5 libros
-- ----------------------------
INSERT INTO `libros` (`eliminado`, `titulo`, `autor`, `editorial`, `anio_edicion`) VALUES
(0, 'El Aleph', 'Jorge Luis Borges', 'Emecé Editores', 2007),
(0, 'Rayuela', 'Julio Cortázar', 'Editorial Sudamericana', 2013),
(0, 'Martín Fierro', 'José Hernández', 'Editorial Losada', 2015),
(0, 'Don Segundo Sombra', 'Ricardo Güiraldes', 'Editorial Losada', 2016),
(0, 'El túnel', 'Ernesto Sabato', 'Penguin Random House', 2020);

-- ----------------------------
-- Inserción de 5 fichas bibliográficas
-- ----------------------------
INSERT INTO `fichas_bibliograficas`
(`eliminado`, `isbn`, `clasificacion_dewey`, `estanteria`, `idioma`, `id_libro`) VALUES
(0, '978-950-04-2749-1', '863.7', 'A-01', 'Español', 1),
(0, '978-950-07-6424-6', '863.7', 'A-02', 'Español', 2),
(0, '978-950-03-8560-3', '863.3', 'A-03', 'Español', 3),
(0, '978-950-03-8841-3', '863.7', 'A-04', 'Español', 4),
(0, '978-987-566-989-6', '863.7', 'A-05', 'Español', 5);
