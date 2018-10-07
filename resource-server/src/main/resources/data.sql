INSERT INTO todo(description, created_at, deadline, done) VALUES('牛乳を買う', CURRENT_TIMESTAMP, DATEADD('DAY', 3, CURRENT_DATE), TRUE);
INSERT INTO todo(description, created_at, deadline, done) VALUES('メールを送る', CURRENT_TIMESTAMP, DATEADD('DAY', 4, CURRENT_DATE), FALSE);
INSERT INTO todo(description, created_at, deadline, done) VALUES('本を買う', CURRENT_TIMESTAMP, DATEADD('DAY', 5, CURRENT_DATE), FALSE);