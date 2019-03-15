package utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;

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

    public static String getCellStringValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        if (cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
            return String.valueOf((int) (cell.getNumericCellValue()));
        } else {
            return cell.getStringCellValue();
        }
    }
}
