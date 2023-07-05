CREATE TABLE videos (
  id TEXT PRIMARY KEY UNIQUE NOT NULL,
  name TEXT NOT NULL,
  upload_date TIMESTAMP NOT NULL,
  user_id TEXT NOT NULL,
  blob_name TEXT NOT NULL,
  blob_url TEXT NOT NULL,
  FOREIGN KEY (user_id) REFERENCES users (id)
);

