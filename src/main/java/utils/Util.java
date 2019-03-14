package utils;

public class Util {
    public static boolean isIPv4(String ip) {
        String[] nums = ip.split("\\.");
        if (nums.length != 4) {
            return false;
        }

        for (String num : nums) {
            int iNum = Integer.parseInt(num);
            if (iNum < 0 || iNum > 255 || !num.equals(String.valueOf(iNum))) {
                return false;
            }
        }
        return true;
    }
}
