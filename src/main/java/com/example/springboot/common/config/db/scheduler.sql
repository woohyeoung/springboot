CREATE EVENT del_user
    ON SCHEDULE EVERY 1 DAY
    DO
    DELETE FROM tbl_user u WHERE u.access_date < (
        SELECT DATE_SUB(NOW(), INTERVAL 7 DAY) FROM DUAL
        );