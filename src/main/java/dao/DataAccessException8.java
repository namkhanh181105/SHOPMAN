package dao;

/**
 * Ngoại lệ truy xuất dữ liệu dùng chung cho các lớp DAO, giúp không nuốt lỗi SQL
 * đồng thời giữ nguyên chữ ký công khai của các phương thức DAO theo sơ đồ lớp.
 */
public class DataAccessException8 extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public DataAccessException8(String message, Throwable cause) {
        super(message, cause);
    }

    public DataAccessException8(String message) {
        super(message);
    }
}

