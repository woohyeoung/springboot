# CREATE EVENT del_user
#     ON SCHEDULE EVERY 1 DAY
#     DO
#
# # 기호 1
# DELETE FROM tbl_user u
#     WHERE u.access_date < (
#     SELECT DATE_SUB(NOW(), INTERVAL 7 DAY) FROM DUAL
#     );
#
# # 기호 2
# INSERT INTO tbl_unused_id ui
# (
#  ui.email, ui.name, ui.access_date
# )
# SELECT u.email, u.name, u.access_date
# FROM tbl_user u
# WHERE access_date < DATE_SUB(NOW(), INTERVAL 7 DAY);
#
# INSERT INTO tbl_unused_pw up
# (
#  up.password, up.role
# )
# SELECT u.password, u.role
# FROM tbl_user u
# WHERE access_date < DATE_SUB(NOW(), INTERVAL 7 DAY);
#
# # 기호 3
# WITH tbl AS
# (
#     SELECT DATE_SUB(NOW(), INTERVAL 7 DAY) t FROM DUAL
# )
# SELECT * FROM tbl_user u WHERE u.access_date < tbl.t;
