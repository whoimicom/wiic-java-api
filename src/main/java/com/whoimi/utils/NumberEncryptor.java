package com.whoimi.utils;


public class NumberEncryptor {

    // 加密方法
    public static Long encrypt(Long originalNumber) {
        return encrypt(originalNumber, 4);
    }

    public static Long encrypt(Long originalNumber, int maxLen) {
        int subLen = maxLen - 1;
        Long[] digits = new Long[maxLen];
        // 分离每一位数字
        for (int i = 0; i < maxLen; i++) {
            digits[i] = (originalNumber / (long) Math.pow(10, subLen - i)) % 10;
        }
        // 对每一位进行加密变换（例如加5，然后取模10）
        for (int i = 0; i < maxLen; i++) {
            digits[i] = (digits[i] + 5) % 10;
        }
        // 反向排列
        long encryptedNumber = 0;
        for (int i = 0; i < maxLen; i++) {
            encryptedNumber += digits[subLen - i] * (int) Math.pow(10, subLen - i);
        }
        return encryptedNumber;
    }

    public static String encrypt(String originalString) {
        int maxLen = originalString.length();
        int subLen = maxLen - 1;
        Long[] digits = new Long[maxLen];
        // 分离每一位数字
        for (int i = 0; i < maxLen; i++) {
            long parsedLong = Long.parseLong(originalString);
            digits[i] = (parsedLong / (long) Math.pow(10, subLen - i)) % 10;
        }
        // 对每一位进行加密变换（例如加5，然后取模10）
        for (int i = 0; i < maxLen; i++) {
            digits[i] = (digits[i] + 5) % 10;
        }
        // 反向排列
        Long encryptedNumber = 0L;
        for (int i = 0; i < maxLen; i++) {
            encryptedNumber += digits[subLen - i] * (long) Math.pow(10, subLen - i);
        }
        String encryptedString = String.valueOf(encryptedNumber);
        if (encryptedString.length() < 4) {
            encryptedString = String.format("%" + maxLen + "s", encryptedString).replace(' ', '0');
        }
        return encryptedString;
    }

    public static String encryptPhone(String phone) {
        if (phone.length() != 11) {
            return phone;
        }
        String phoneStart = phone.substring(0, 7);
        String phoneEnd = phone.substring(7);
        String encrypt = encrypt(phoneEnd);
        return phoneStart + encrypt;
    }

    // 解密方法
    public static Long decrypt(Long encryptedNumber) {
        return decrypt(encryptedNumber, 4);
    }

    public static Long decrypt(Long encryptedNumber, int maxLen) {
        int subLen = maxLen - 1;
        Long[] digits = new Long[maxLen];
        // 分离每一位数字
        for (int i = 0; i < maxLen; i++) {
            digits[i] = (encryptedNumber / (long) Math.pow(10, subLen - i)) % 10;
        }
        // 反向排列
        Long[] reversedDigits = new Long[maxLen];
        for (int i = 0; i < maxLen; i++) {
            reversedDigits[i] = digits[subLen - i];
        }
        // 对每一位进行解密变换（例如减5，然后取模10）
        for (int i = 0; i < maxLen; i++) {
            reversedDigits[i] = (reversedDigits[i] - 5 + 10) % 10;
        }
        // 组合成原始数字
        long originalNumber = 0L;
        for (int i = 0; i < maxLen; i++) {
            originalNumber += reversedDigits[i] * (long) Math.pow(10, subLen - i);
        }
        return originalNumber;
    }

    public static String decrypt(String encryptedString) {
        int maxLen = encryptedString.length();
        int subLen = maxLen - 1;
        Long[] digits = new Long[maxLen];
        // 分离每一位数字
        for (int i = 0; i < maxLen; i++) {
            digits[i] = (Long.parseLong(encryptedString) / (long) Math.pow(10, subLen - i)) % 10;
        }
        // 反向排列
        Long[] reversedDigits = new Long[maxLen];
        for (int i = 0; i < maxLen; i++) {
            reversedDigits[i] = digits[subLen - i];
        }
        // 对每一位进行解密变换（例如减5，然后取模10）
        for (int i = 0; i < maxLen; i++) {
            reversedDigits[i] = (reversedDigits[i] - 5 + 10) % 10;
        }
        // 组合成原始数字
        Long originalNumber = 0L;
        for (int i = 0; i < maxLen; i++) {
            originalNumber += reversedDigits[i] * (long) Math.pow(10, subLen - i);
        }
        String originalString = String.valueOf(originalNumber);
        if (originalString.length() < maxLen) {
            originalString = String.format("%" + maxLen + "s", originalString).replace(' ', '0');
        }
        return originalString;
    }

    public static void main(String[] args) {

        String s = encryptPhone("18581427060");
        System.out.println(s);

 /*       Set<String> StringSet = new HashSet<>();
        for (int i = 6622; i <= 6622; i++) {
            String originalString = String.valueOf(i);
            String encrypt = encrypt(originalString);
            String decrypt = decrypt(encrypt);
            System.out.println(originalString + " " + encrypt + " " + decrypt);
            if (!originalString.equals(decrypt)) {
                System.err.println(originalString + " " + decrypt);
            }
            if (StringSet.contains(encrypt)) {
                System.err.println(encrypt);
            } else {
                StringSet.add(encrypt);
            }
        }
        System.out.println("size:" + StringSet.size());*/

/*        Set<Long> longSet = new HashSet<>();
        int maxLen = 1;
        double pow = Math.pow(10, maxLen);
        for (long i = 0; i < pow; i++) {
            Long originalNumber = i;
            Long encrypt = encrypt(originalNumber, maxLen);
            Long decrypt = decrypt(encrypt, maxLen);
            System.out.println(originalNumber + " " +encrypt + " " + decrypt);
            if (!originalNumber.equals(decrypt)) {
                System.err.println(originalNumber + " " + decrypt);
            }
            if (longSet.contains(encrypt)) {
                System.err.println(encrypt);
            } else {
                longSet.add(encrypt);
            }
        }
        System.out.println("size:" + longSet.size());*/


    }
}
