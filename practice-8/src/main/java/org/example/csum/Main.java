package org.example.csum;

public class Main {
    public static void main(String[] args) {
        var arr          = new byte[]{1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
        var corruptedArr = new byte[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
        var csOne = calcControlSum(arr);

        System.out.printf("Do control sums of corruptedArr and arr match? %b%n", checkControlSum(corruptedArr, csOne));

        var arrClone     = new byte[]{1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
        System.out.printf("Do control sums of cloneArr and arr match? %b%n", checkControlSum(arrClone, csOne));

        var longArr      = new byte[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        var longArrShift = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
        var csTwo = calcControlSum(longArr);
        System.out.printf("Do control sums of longArr and longArrShift match? %b%n", checkControlSum(longArrShift, csTwo));
    }

    public static int calcControlSum(byte[] arr) {
        var sum = 0;
        for (int i = 0; i < arr.length; i += 16) {
            var t = 0;
            for (int j = 0; j < 16; ++j) {
                t <<= 1;
                if (i + j < arr.length) {
                    t |= arr[i + j];
                }
            }
            sum ^= t;
        }
        return  sum ^ 0xFFFF;
    }

    public static boolean checkControlSum(byte[] arr, int controlSum) {
        var realSum = calcControlSum(arr);
        return realSum == controlSum;
    }
}
