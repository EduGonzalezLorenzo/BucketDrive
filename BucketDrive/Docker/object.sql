-- phpMyAdmin SQL Dump
-- version 5.2.0
-- https://www.phpmyadmin.net/
--
-- Servidor: mysql
-- Tiempo de generación: 25-01-2023 a las 17:51:39
-- Versión del servidor: 8.0.31
-- Versión de PHP: 8.0.19

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `object`
--
CREATE DATABASE IF NOT EXISTS `object` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE `object`;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `bucket`
--

DROP TABLE IF EXISTS `bucket`;
CREATE TABLE `bucket` (
  `id` int NOT NULL,
  `uri` varchar(100) NOT NULL,
  `owner` varchar(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Volcado de datos para la tabla `bucket`
--

INSERT INTO `bucket` (`id`, `uri`, `owner`) VALUES
(34, 'hola', 'Edu'),
(35, 'pere', 'Edu'),
(36, 'bjhj', 'Edu'),
(37, 'DiscoDeJames', 'James');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `File`
--

DROP TABLE IF EXISTS `File`;
CREATE TABLE `File` (
  `id` int NOT NULL,
  `body` longblob NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `object`
--

DROP TABLE IF EXISTS `object`;
CREATE TABLE `object` (
  `id` int NOT NULL,
  `uri` varchar(1000) NOT NULL,
  `bucketId` int NOT NULL,
  `owner` varchar(10) NOT NULL,
  `contentType` varchar(10) NOT NULL,
  `lastModified` timestamp NOT NULL,
  `created` timestamp NOT NULL,
  `metadataId` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `ObjectToFile`
--

DROP TABLE IF EXISTS `ObjectToFile`;
CREATE TABLE `ObjectToFile` (
  `objectId` int NOT NULL,
  `fileId` int NOT NULL,
  `uploadDate` timestamp NOT NULL,
  `versionId` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `user`
--

DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `username` varchar(10) NOT NULL,
  `name` varchar(30) NOT NULL,
  `password` varchar(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Volcado de datos para la tabla `user`
--

INSERT INTO `user` (`username`, `name`, `password`) VALUES
('Edu', 'Eduardo González Lorenzo', '48690'),
('Fran', 'Francisco', '1509442'),
('James', 'James Davies', '1509442');

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `bucket`
--
ALTER TABLE `bucket`
  ADD PRIMARY KEY (`id`),
  ADD KEY `bucketToUsers` (`owner`);

--
-- Indices de la tabla `File`
--
ALTER TABLE `File`
  ADD PRIMARY KEY (`id`);

--
-- Indices de la tabla `object`
--
ALTER TABLE `object`
  ADD PRIMARY KEY (`id`),
  ADD KEY `object to user` (`owner`),
  ADD KEY `object to bucket` (`bucketId`);

--
-- Indices de la tabla `ObjectToFile`
--
ALTER TABLE `ObjectToFile`
  ADD KEY `ToFile` (`fileId`),
  ADD KEY `ToObject` (`objectId`);

--
-- Indices de la tabla `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`username`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `bucket`
--
ALTER TABLE `bucket`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=38;

--
-- AUTO_INCREMENT de la tabla `File`
--
ALTER TABLE `File`
  MODIFY `id` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `object`
--
ALTER TABLE `object`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=20;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `bucket`
--
ALTER TABLE `bucket`
  ADD CONSTRAINT `bucketToUsers` FOREIGN KEY (`owner`) REFERENCES `user` (`username`) ON DELETE RESTRICT ON UPDATE RESTRICT;

--
-- Filtros para la tabla `object`
--
ALTER TABLE `object`
  ADD CONSTRAINT `object to bucket` FOREIGN KEY (`bucketId`) REFERENCES `bucket` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  ADD CONSTRAINT `object to user` FOREIGN KEY (`owner`) REFERENCES `user` (`username`) ON DELETE RESTRICT ON UPDATE RESTRICT;

--
-- Filtros para la tabla `ObjectToFile`
--
ALTER TABLE `ObjectToFile`
  ADD CONSTRAINT `ToFile` FOREIGN KEY (`fileId`) REFERENCES `File` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  ADD CONSTRAINT `ToObject` FOREIGN KEY (`objectId`) REFERENCES `object` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
