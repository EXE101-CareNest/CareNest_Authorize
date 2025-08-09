@echo off
echo.
echo === Common Redis Commands ===
echo KEYS *           - Liệt kê tất cả key
echo GET key_name     - Xem giá trị key
echo SET key_name val - Tạo key với giá trị
echo DEL key_name     - Xóa 1 key
echo FLUSHALL         - Xóa tất cả key trong tất cả DB
echo FLUSHDB          - Xóa tất cả key trong DB hiện tại
echo EXPIRE key sec   - Đặt thời gian sống cho key
echo TTL key          - Xem thời gian sống còn lại
echo.
echo === Connecting to Redis in Docker container ===
docker exec -it 71bb78eea68a redis-cli
pause
