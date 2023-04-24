package hanu.a2_2001040108.mycart.Helper;

import java.text.DecimalFormat;

public class MoneyFormatter {
    private double value;

    public static String withLargeIntegers(double value) {
        DecimalFormat df = new DecimalFormat("###,###,###");
        return df.format(value);
}

}
