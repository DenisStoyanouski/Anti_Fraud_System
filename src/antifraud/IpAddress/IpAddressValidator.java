package antifraud.IpAddress;

public class IpAddressValidator {
    public static boolean isValidIpAddress(String ipAddress) {
        return ipAddress.matches("^((\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(?!$)|$)){4}$");
    }
}
