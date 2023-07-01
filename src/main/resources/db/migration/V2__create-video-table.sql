CREATE TABLE videos (
  id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(255) NOT NULL,
  upload_date DATETIME NOT NULL,
  user_id TEXT NOT NULL,
  blob_name VARCHAR(255) NOT NULL,
  blob_url VARCHAR(255) NOT NULL,
  FOREIGN KEY (user_id) REFERENCES users (id)
);
