package com.yiban.javaBase.dev.encrypt;

import com.github.jsonzou.jmockdata.DataConfig;
import com.github.jsonzou.jmockdata.JMockData;
import com.github.jsonzou.jmockdata.MockConfig;
import com.github.jsonzou.jmockdata.TypeReference;
import com.github.jsonzou.jmockdata.mocker.IntegerMocker;
import com.github.jsonzou.jmockdata.mocker.StringMocker;
import com.ibm.icu.text.DecimalFormat;
import com.yiban.javaBase.User;
import com.yiban.javaBase.test.Contact;
import org.hamcrest.JMock1Matchers;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.NumberFormat;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ThreeDESTest {
    @Test
    public void test() {
        Assert.assertEquals(3,2);
    }

    @Test
    public void test1() {
//        User mock = JMockData.mock(User.class);
//        System.out.println("mock.name + \" , \" + mock.age = " + mock.getName() + " , " + mock.getAge());

        MockConfig config = new MockConfig();
        final DataConfig dateDC = config.subConfig("date");
        config.registerMocker(new StringMocker() {
            @Override
            public String mock(DataConfig mockConfig) {
                // 修改date字段为固定值
                if (dateDC == mockConfig) {
                    return "2020/01/01";
                }
                return super.mock(mockConfig);
            }
        }, String.class);
        Contact mock = JMockData.mock(Contact.class,config);
        System.out.println(mock.toString());
        Contact mock1 = JMockData.mock(Contact.class,config);
        System.out.println(mock1.toString());
//        String str = JMockData.mock(String.class);
//        System.out.println("str = " + str);
//        int i = JMockData.mock(int.class);
//        System.out.println("i = " + i);
//        Integer num = JMockData.mock(Integer.class);
//        System.out.println("num = " + num);
//        List<User> userList = JMockData.mock(new TypeReference<List<User>>(){});
//        System.out.println("userList = " + userList.size());
//        double d = 0.00005555;
//        double d1 = 1.000053335;
//        m5(d);
//        m5(d1);

//        double l = 1.000053335;
//        short offset = (short) Math.ceil(Math.log10(l) + 1);
//        short significantDigits = 10;
//        System.out.printf("%." + (significantDigits - offset) + "f", l);
    }


    /**
     *法一:
     */
    public void m1(double f) {
        BigDecimal bg = new BigDecimal(f);
        double f1 = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        System.out.println(f1);
    }

    /**
     * 法二:
     * DecimalFormat转换最简便
     */
    public void m2(double f) {
        DecimalFormat df = new DecimalFormat("#.00");
        System.out.println(df.format(f));
    }

    /**
     * 法三:
     * String.format打印最简便
     */
    public void m3(double f) {
        System.out.println(String.format("%.2e", f));
    }

    /**
     *法四:
     */
    public void m4(double f) {
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(2);
        System.out.println(nf.format(f));
    }

    public void m5(double f) {
        BigDecimal dc = new BigDecimal(f);
        dc = dc.round(new MathContext(2)); // desired significant digits
        double rounded = dc .doubleValue();
        System.out.println("rounded = " + rounded);
        BigDecimal bd = new BigDecimal(rounded);
        System.out.println("bd: " + String.format("%.2g",bd));
        DecimalFormat formatter = new DecimalFormat();
        formatter.setMaximumSignificantDigits(2);
        System.out.println(formatter.format(bd));
    }

}