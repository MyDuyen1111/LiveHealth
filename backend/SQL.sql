-- MySQL dump 10.13  Distrib 8.0.29, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: livehealth
-- ------------------------------------------------------
-- Server version	8.0.29

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `address`
--

DROP TABLE IF EXISTS `address`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `address` (
  `id` binary(16) NOT NULL,
  `company_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8_general_ci DEFAULT NULL,
  `country` varchar(255) NOT NULL,
  `first_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8_general_ci NOT NULL,
  `last_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8_general_ci NOT NULL,
  `state` varchar(255) NOT NULL,
  `street_address` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8_general_ci NOT NULL,
  `zip_code` int NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `address`
--

LOCK TABLES `address` WRITE;
/*!40000 ALTER TABLE `address` DISABLE KEYS */;
/*!40000 ALTER TABLE `address` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `brand`
--

DROP TABLE IF EXISTS `brand`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `brand` (
  `id` binary(16) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `logo_url` varchar(255) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKrdxh7tq2xs66r485cc8dkxt77` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `brand`
--

LOCK TABLES `brand` WRITE;
/*!40000 ALTER TABLE `brand` DISABLE KEYS */;
/*!40000 ALTER TABLE `brand` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cart`
--

DROP TABLE IF EXISTS `cart`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cart` (
  `id` binary(16) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `coupon_id` binary(16) DEFAULT NULL,
  `payment_method_id` binary(16) DEFAULT NULL,
  `shipping_method_id` binary(16) DEFAULT NULL,
  `user_id` binary(16) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK9emlp6m95v5er2bcqkjsw48he` (`user_id`),
  KEY `FKhceorppmmggrjeh724uyfqwxf` (`coupon_id`),
  KEY `FK672lbhtgmx2wevhbdou5wlrq9` (`payment_method_id`),
  KEY `FKfq1tve121bfe6v9l7j4au65ck` (`shipping_method_id`),
  CONSTRAINT `FK672lbhtgmx2wevhbdou5wlrq9` FOREIGN KEY (`payment_method_id`) REFERENCES `payment_method` (`id`),
  CONSTRAINT `FKfq1tve121bfe6v9l7j4au65ck` FOREIGN KEY (`shipping_method_id`) REFERENCES `shipping_method` (`id`),
  CONSTRAINT `FKhceorppmmggrjeh724uyfqwxf` FOREIGN KEY (`coupon_id`) REFERENCES `product_promotion` (`id`),
  CONSTRAINT `FKl70asp4l4w0jmbm1tqyofho4o` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cart`
--

LOCK TABLES `cart` WRITE;
/*!40000 ALTER TABLE `cart` DISABLE KEYS */;
/*!40000 ALTER TABLE `cart` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cart_item`
--

DROP TABLE IF EXISTS `cart_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cart_item` (
  `id` binary(16) NOT NULL,
  `price` double NOT NULL,
  `quantity` int NOT NULL,
  `cart_id` binary(16) NOT NULL,
  `product_id` binary(16) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK1uobyhgl1wvgt1jpccia8xxs3` (`cart_id`),
  KEY `FKjcyd5wv4igqnw413rgxbfu4nv` (`product_id`),
  CONSTRAINT `FK1uobyhgl1wvgt1jpccia8xxs3` FOREIGN KEY (`cart_id`) REFERENCES `cart` (`id`),
  CONSTRAINT `FKjcyd5wv4igqnw413rgxbfu4nv` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cart_item`
--

LOCK TABLES `cart_item` WRITE;
/*!40000 ALTER TABLE `cart_item` DISABLE KEYS */;
/*!40000 ALTER TABLE `cart_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `category`
--

DROP TABLE IF EXISTS `category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `category` (
  `id` binary(16) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK46ccwnsi9409t36lurvtyljak` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `category`
--

LOCK TABLES `category` WRITE;
/*!40000 ALTER TABLE `category` DISABLE KEYS */;
/*!40000 ALTER TABLE `category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `client_testimonial`
--

DROP TABLE IF EXISTS `client_testimonial`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `client_testimonial` (
  `id` binary(16) NOT NULL,
  `avatar_url` varchar(255) NOT NULL,
  `content` varchar(255) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `name` varchar(255) NOT NULL,
  `position` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `client_testimonial`
--

LOCK TABLES `client_testimonial` WRITE;
/*!40000 ALTER TABLE `client_testimonial` DISABLE KEYS */;
/*!40000 ALTER TABLE `client_testimonial` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `invalidated_token`
--

DROP TABLE IF EXISTS `invalidated_token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `invalidated_token` (
  `id` varchar(255) NOT NULL,
  `expiry_time` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `invalidated_token`
--

LOCK TABLES `invalidated_token` WRITE;
/*!40000 ALTER TABLE `invalidated_token` DISABLE KEYS */;
/*!40000 ALTER TABLE `invalidated_token` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `news`
--

DROP TABLE IF EXISTS `news`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `news` (
  `id` binary(16) NOT NULL,
  `content` text NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `thumbnail_url` varchar(255) DEFAULT NULL,
  `title` varchar(255) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `author_id` binary(16) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKrxsvhjf5khl0mf6jf3au0vqaf` (`author_id`),
  CONSTRAINT `FKrxsvhjf5khl0mf6jf3au0vqaf` FOREIGN KEY (`author_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `news`
--

LOCK TABLES `news` WRITE;
/*!40000 ALTER TABLE `news` DISABLE KEYS */;
/*!40000 ALTER TABLE `news` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `news_comment`
--

DROP TABLE IF EXISTS `news_comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `news_comment` (
  `id` binary(16) NOT NULL,
  `content` varchar(1000) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `news_id` binary(16) NOT NULL,
  `user_id` binary(16) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKt2n600d9p35y9ytg8e0x13h17` (`news_id`),
  KEY `FK8k9qjl86y0no1pt1emokbli02` (`user_id`),
  CONSTRAINT `FK8k9qjl86y0no1pt1emokbli02` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FKt2n600d9p35y9ytg8e0x13h17` FOREIGN KEY (`news_id`) REFERENCES `news` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `news_comment`
--

LOCK TABLES `news_comment` WRITE;
/*!40000 ALTER TABLE `news_comment` DISABLE KEYS */;
/*!40000 ALTER TABLE `news_comment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `news_tag`
--

DROP TABLE IF EXISTS `news_tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `news_tag` (
  `id` binary(16) NOT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKqohx23n1hi25r7jxqn1fx9uvs` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `news_tag`
--

LOCK TABLES `news_tag` WRITE;
/*!40000 ALTER TABLE `news_tag` DISABLE KEYS */;
/*!40000 ALTER TABLE `news_tag` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `news_tag_mapping`
--

DROP TABLE IF EXISTS `news_tag_mapping`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `news_tag_mapping` (
  `news_id` binary(16) NOT NULL,
  `tag_id` binary(16) NOT NULL,
  KEY `FKke2cxr829jmstpkjlbp23m2g9` (`tag_id`),
  KEY `FK3isbngplyhhej06f7ehgh5ka5` (`news_id`),
  CONSTRAINT `FK3isbngplyhhej06f7ehgh5ka5` FOREIGN KEY (`news_id`) REFERENCES `news` (`id`),
  CONSTRAINT `FKke2cxr829jmstpkjlbp23m2g9` FOREIGN KEY (`tag_id`) REFERENCES `news_tag` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `news_tag_mapping`
--

LOCK TABLES `news_tag_mapping` WRITE;
/*!40000 ALTER TABLE `news_tag_mapping` DISABLE KEYS */;
/*!40000 ALTER TABLE `news_tag_mapping` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_item`
--

DROP TABLE IF EXISTS `order_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_item` (
  `id` binary(16) NOT NULL,
  `price` double NOT NULL,
  `quantity` int NOT NULL,
  `subtotal` double NOT NULL,
  `order_id` binary(16) NOT NULL,
  `product_id` binary(16) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKt4dc2r9nbvbujrljv3e23iibt` (`order_id`),
  KEY `FK551losx9j75ss5d6bfsqvijna` (`product_id`),
  CONSTRAINT `FK551losx9j75ss5d6bfsqvijna` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`),
  CONSTRAINT `FKt4dc2r9nbvbujrljv3e23iibt` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_item`
--

LOCK TABLES `order_item` WRITE;
/*!40000 ALTER TABLE `order_item` DISABLE KEYS */;
/*!40000 ALTER TABLE `order_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
  `id` binary(16) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `discount_percentage` double NOT NULL,
  `note` varchar(1000) DEFAULT NULL,
  `order_date` datetime(6) NOT NULL,
  `order_number` varchar(255) NOT NULL,
  `shipping_fee` double NOT NULL,
  `status` enum('CANCELLED','COMPLETED','DELIVERED','ON_THE_WAY','ORDER_RECEIVED','PROCESSING') NOT NULL,
  `subtotal` double NOT NULL,
  `total_amount` double NOT NULL,
  `contact_email` varchar(255) DEFAULT NULL,
  `contact_phone` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) NOT NULL,
  `billing_address_id` binary(16) DEFAULT NULL,
  `payment_method_id` binary(16) DEFAULT NULL,
  `shipping_address_id` binary(16) DEFAULT NULL,
  `user_id` binary(16) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKnthkiu7pgmnqnu86i2jyoe2v7` (`order_number`),
  KEY `FKqraecqgbbr1p37ic9dr44e2dr` (`billing_address_id`),
  KEY `FKgeqwl6x0iadp9e2459uh3o8fv` (`payment_method_id`),
  KEY `FKh0uue95ltjysfmkqb5abgk7tj` (`shipping_address_id`),
  KEY `FKel9kyl84ego2otj2accfd8mr7` (`user_id`),
  CONSTRAINT `FKel9kyl84ego2otj2accfd8mr7` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FKgeqwl6x0iadp9e2459uh3o8fv` FOREIGN KEY (`payment_method_id`) REFERENCES `payment_method` (`id`),
  CONSTRAINT `FKh0uue95ltjysfmkqb5abgk7tj` FOREIGN KEY (`shipping_address_id`) REFERENCES `address` (`id`),
  CONSTRAINT `FKqraecqgbbr1p37ic9dr44e2dr` FOREIGN KEY (`billing_address_id`) REFERENCES `address` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payment_method`
--

DROP TABLE IF EXISTS `payment_method`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payment_method` (
  `id` binary(16) NOT NULL,
  `is_active` bit(1) NOT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKai10u6l9urufl9pidfuhnmmcj` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payment_method`
--

LOCK TABLES `payment_method` WRITE;
/*!40000 ALTER TABLE `payment_method` DISABLE KEYS */;
/*!40000 ALTER TABLE `payment_method` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product`
--

DROP TABLE IF EXISTS `product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product` (
  `id` binary(16) NOT NULL,
  `created_at` varchar(255) NOT NULL,
  `description` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `old_price` double NOT NULL,
  `sku` varchar(255) NOT NULL,
  `stock` int NOT NULL,
  `updated_at` varchar(255) NOT NULL,
  `brand_id` binary(16) DEFAULT NULL,
  `category_id` binary(16) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKs6cydsualtsrprvlf2bb3lcam` (`brand_id`),
  KEY `FK1mtsbur82frn64de7balymq9s` (`category_id`),
  CONSTRAINT `FK1mtsbur82frn64de7balymq9s` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`),
  CONSTRAINT `FKs6cydsualtsrprvlf2bb3lcam` FOREIGN KEY (`brand_id`) REFERENCES `brand` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product`
--

LOCK TABLES `product` WRITE;
/*!40000 ALTER TABLE `product` DISABLE KEYS */;
/*!40000 ALTER TABLE `product` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_attribute`
--

DROP TABLE IF EXISTS `product_attribute`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_attribute` (
  `id` binary(16) NOT NULL,
  `attribute_key` varchar(255) NOT NULL,
  `attribute_value` varchar(255) NOT NULL,
  `product_id` binary(16) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKlefs59y5kmsbu017n1wp10gf2` (`product_id`),
  CONSTRAINT `FKlefs59y5kmsbu017n1wp10gf2` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_attribute`
--

LOCK TABLES `product_attribute` WRITE;
/*!40000 ALTER TABLE `product_attribute` DISABLE KEYS */;
/*!40000 ALTER TABLE `product_attribute` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_image`
--

DROP TABLE IF EXISTS `product_image`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_image` (
  `product_id` binary(16) NOT NULL,
  `image_url` varchar(255) NOT NULL,
  KEY `FK6oo0cvcdtb6qmwsga468uuukk` (`product_id`),
  CONSTRAINT `FK6oo0cvcdtb6qmwsga468uuukk` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_image`
--

LOCK TABLES `product_image` WRITE;
/*!40000 ALTER TABLE `product_image` DISABLE KEYS */;
/*!40000 ALTER TABLE `product_image` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_promotion`
--

DROP TABLE IF EXISTS `product_promotion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_promotion` (
  `id` binary(16) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `discount_type` varchar(255) NOT NULL,
  `discount_value` double NOT NULL,
  `end_date` datetime(6) NOT NULL,
  `is_active` bit(1) NOT NULL,
  `name` varchar(255) NOT NULL,
  `start_date` datetime(6) NOT NULL,
  `product_id` binary(16) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK7acf9c0rxy50sulckwfkskyna` (`product_id`),
  CONSTRAINT `FKmpv6380a8ouxwxa99taru2luq` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_promotion`
--

LOCK TABLES `product_promotion` WRITE;
/*!40000 ALTER TABLE `product_promotion` DISABLE KEYS */;
/*!40000 ALTER TABLE `product_promotion` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_tag`
--

DROP TABLE IF EXISTS `product_tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_tag` (
  `product_id` binary(16) NOT NULL,
  `tag_id` binary(16) NOT NULL,
  KEY `FK3b3a7hu5g2kh24wf0cwv3lgsm` (`tag_id`),
  KEY `FK2rf7w3d88x20p7vuc2m9mvv91` (`product_id`),
  CONSTRAINT `FK2rf7w3d88x20p7vuc2m9mvv91` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`),
  CONSTRAINT `FK3b3a7hu5g2kh24wf0cwv3lgsm` FOREIGN KEY (`tag_id`) REFERENCES `tag` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_tag`
--

LOCK TABLES `product_tag` WRITE;
/*!40000 ALTER TABLE `product_tag` DISABLE KEYS */;
/*!40000 ALTER TABLE `product_tag` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `professional_member`
--

DROP TABLE IF EXISTS `professional_member`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `professional_member` (
  `id` binary(16) NOT NULL,
  `avatar_url` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `position` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `professional_member`
--

LOCK TABLES `professional_member` WRITE;
/*!40000 ALTER TABLE `professional_member` DISABLE KEYS */;
/*!40000 ALTER TABLE `professional_member` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `review`
--

DROP TABLE IF EXISTS `review`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `review` (
  `id` binary(16) NOT NULL,
  `comment` varchar(2000) DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `rating` int NOT NULL,
  `product_id` binary(16) NOT NULL,
  `user_id` binary(16) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKiyof1sindb9qiqr9o8npj8klt` (`product_id`),
  KEY `FKiyf57dy48lyiftdrf7y87rnxi` (`user_id`),
  CONSTRAINT `FKiyf57dy48lyiftdrf7y87rnxi` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FKiyof1sindb9qiqr9o8npj8klt` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `review`
--

LOCK TABLES `review` WRITE;
/*!40000 ALTER TABLE `review` DISABLE KEYS */;
/*!40000 ALTER TABLE `review` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shipping_method`
--

DROP TABLE IF EXISTS `shipping_method`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shipping_method` (
  `id` binary(16) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `is_active` bit(1) NOT NULL,
  `name` varchar(255) NOT NULL,
  `price` double NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shipping_method`
--

LOCK TABLES `shipping_method` WRITE;
/*!40000 ALTER TABLE `shipping_method` DISABLE KEYS */;
/*!40000 ALTER TABLE `shipping_method` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tag`
--

DROP TABLE IF EXISTS `tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tag` (
  `id` binary(16) NOT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK1wdpsed5kna2y38hnbgrnhi5b` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tag`
--

LOCK TABLES `tag` WRITE;
/*!40000 ALTER TABLE `tag` DISABLE KEYS */;
/*!40000 ALTER TABLE `tag` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` binary(16) NOT NULL,
  `avatar_public_id` varchar(255) DEFAULT NULL,
  `created_at` date NOT NULL,
  `email` varchar(255) NOT NULL,
  `first_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8_general_ci DEFAULT NULL,
  `last_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8_general_ci DEFAULT NULL,
  `link_avatar` varchar(255) DEFAULT NULL,
  `password` varchar(255) NOT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `role` enum('ADMIN','USER') NOT NULL,
  `address_id` binary(16) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKddefmvbrws3hvl5t0hnnsv8ox` (`address_id`),
  CONSTRAINT `FKddefmvbrws3hvl5t0hnnsv8ox` FOREIGN KEY (`address_id`) REFERENCES `address` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_health`
--

DROP TABLE IF EXISTS `user_health`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_health` (
  `id` binary(16) NOT NULL,
  `tdee` double NOT NULL,
  `age` int NOT NULL,
  `basal_metabolic_rate` double NOT NULL,
  `gender` enum('FEMALE','MALE') NOT NULL,
  `height` int NOT NULL,
  `maximum_heart_rate` int NOT NULL,
  `weight` int NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `FKf3mkl4r5trdnnsqqqo1umdv80` FOREIGN KEY (`id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_health`
--

LOCK TABLES `user_health` WRITE;
/*!40000 ALTER TABLE `user_health` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_health` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `web_information`
--

DROP TABLE IF EXISTS `web_information`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `web_information` (
  `id` binary(16) NOT NULL,
  `address` varchar(255) NOT NULL,
  `company_name` varchar(255) NOT NULL,
  `copyright_text` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `happy_customers` int NOT NULL,
  `logo_url` varchar(255) NOT NULL,
  `monthly_orders` int NOT NULL,
  `phone` varchar(255) NOT NULL,
  `qualified_team_members` int NOT NULL,
  `slogan` varchar(255) NOT NULL,
  `year_of_hard_work` int NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `web_information`
--

LOCK TABLES `web_information` WRITE;
/*!40000 ALTER TABLE `web_information` DISABLE KEYS */;
/*!40000 ALTER TABLE `web_information` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'livehealth'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-03-18 13:03:04
