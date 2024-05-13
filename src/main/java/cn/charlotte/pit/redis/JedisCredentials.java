package cn.charlotte.pit.redis;

public class JedisCredentials {

    private final String address, password;
    private final int port;

    public JedisCredentials(String address, String password, int port) {
        this.address = address;
        this.password = password;
        this.port = port;
    }

    /**
     * @return
     */
    public boolean isAuth() {
        return password != null && !password.isEmpty();
    }

    public String getAddress() {
        return this.address;
    }

    public String getPassword() {
        return this.password;
    }

    public int getPort() {
        return this.port;
    }
}
