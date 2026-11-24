-- M3 Menu Catalogue / M4 Menu & Stock Management (owner: Pasha)
-- Categories and menu items. Run after 01_users.sql.
CREATE TABLE categories (
  id         BIGINT AUTO_INCREMENT PRIMARY KEY,
  name       VARCHAR(60) NOT NULL UNIQUE,
  sort_order INT NOT NULL DEFAULT 0
);

CREATE TABLE menu_items (
  id          BIGINT AUTO_INCREMENT PRIMARY KEY,
  category_id BIGINT NOT NULL,
  name        VARCHAR(100) NOT NULL,
  description VARCHAR(255),
  price_iqd   INT NOT NULL CHECK (price_iqd > 0),
  image_url   VARCHAR(255),
  vegetarian  BOOLEAN NOT NULL DEFAULT FALSE,
  available   BOOLEAN NOT NULL DEFAULT TRUE,
  daily_stock INT NULL,          -- NULL = unlimited
  stock_left  INT NULL,          -- what is left today; NULL = unlimited
  FOREIGN KEY (category_id) REFERENCES categories(id)
);
