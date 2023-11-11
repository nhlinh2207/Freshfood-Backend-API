package com.linh.freshfoodbackend.utils;

public class MoneyFormatUtil {

    public static String format(int money){
        String stringMoney = money+"";
        int moneySize = stringMoney.length();
        int division = moneySize / 3;
        if (division == 3)
            return  stringMoney+" đ";
        else {
            StringBuilder s = new StringBuilder(stringMoney);
            for (int i = stringMoney.length() - 3; i >= 0; i -= 3) {
                if (i == 0) break;
                s.insert(i, ',');
            }
            s.append(" đ");
            return s.toString();
        }
    }
}
