LOAD DATA INFILE '/var/lib/mysql-files/MOCK_DATA_FULL.csv'
    INTO TABLE product
         FIELDS TERMINATED BY ','
    LINES TERMINATED BY '\n'
    IGNORE 1 ROWS
    (price,brand_id,name,stock,like_count,created_at,updated_at);