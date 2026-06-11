DbConfig() {

    String host = System.getenv("MYSQLHOST");
    String port = System.getenv("MYSQLPORT");
    String database = System.getenv("MYSQLDATABASE");
    String user = System.getenv("MYSQLUSER");
    String pass = System.getenv("MYSQLPASSWORD");

    if (host != null && !host.isBlank()) {
        url = "jdbc:mysql://" + host + ":" + port + "/" + database
                + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

        username = user;
        password = pass;
    } else {
        url = DEFAULT_URL;
        username = "root";
        password = "root";
    }
}