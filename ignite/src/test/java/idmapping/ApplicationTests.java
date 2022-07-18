package idmapping;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

//@SpringBootTest
class ApplicationTests {

    private final static int SIZE = 5000000;

    private static List<Contact> testContacts = new ArrayList<>(SIZE);
    private final static String TYPE_ANONYMOUS_ID = "anonymousId";
    private final static String TYPE_EXTERNAL_ID = "externalId";
    private final static String TYPE_WECHAT_OPEN_ID = "wechat.openid";
    private final static String TYPE_WECHAT_UNIOIN_ID = "wechat.unionid";
    private final static String TYPE_SHOUQIANBA_ID = "shouqianba.payer_uid";
    private final static String TYPE_YOUZAN_ID = "youzan.fans_id";
    private final static String TYPE_MOBILE = "mobile";

    private final static String TYPE_WECHAT_WXA_OPEN_ID = "wechat.wxa.openid";
    private final static String TYPE_WXA_ANONYMOUS_ID = "wxa.anonymousId";

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeAll
    static void setup() {
        Random r1 = new Random();
        Random r2 = new Random();
        Random r3 = new Random();
        IntStream.range(0, SIZE).parallel().forEach(i -> {
//            System.out.println("1 contact_" + i);
            Contact c = new Contact();
            c.getIdentities().put(TYPE_ANONYMOUS_ID, "cookie_" + padLeftZeros(i + "", 10));
            c.getIdentities().put(TYPE_WXA_ANONYMOUS_ID, "wxa_cookie_" + padLeftZeros(i + "", 10));
            c.getIdentities().put(TYPE_WECHAT_WXA_OPEN_ID, "wxa_openid_" + padLeftZeros(i + "", 10));
            c.getIdentities().put(TYPE_EXTERNAL_ID, "externalID_" + padLeftZeros(i + "", 10));
            c.getIdentities().put(TYPE_WECHAT_OPEN_ID, "openid_" + padLeftZeros(i + "", 10));
            c.getIdentities().put(TYPE_WECHAT_UNIOIN_ID, "unionid_" + padLeftZeros(i + "", 10));
            c.getIdentities().put(TYPE_SHOUQIANBA_ID, "shouqianba_" + padLeftZeros(i + "", 10));
            c.getIdentities().put(TYPE_YOUZAN_ID, "youzan_" + padLeftZeros(i + "", 10));
            c.getIdentities().put(TYPE_MOBILE, String.valueOf(13900000000L + (long) i));

            testContacts.add(c);
            System.out.println("2 contact_" + i);
            int game = r1.nextInt(100);
            // 80% silence auth, 10% web login
            if (game < 80) {
                c.setSilenceAuth(true);
                int innerGame = r2.nextInt(100);
                if (innerGame < 50) {
                    c.setWebLogin(true);
                    int nestGame = r3.nextInt(100);
                    if (nestGame < 25) {
                        c.setYouzan(true);
                        c.setSubscribe(true);
                    }
                }
            } else if (game < 90) {
                c.setWebLogin(true);
                c.setShouqianba(true);
                c.setSubscribe(true);
            }

            if (game < 50) {
                c.setWxaLogin(true);
                int innerGame = r2.nextInt(100);
                if (innerGame < 20) {
                    c.setWxaRegister(true);
                }
            }
        });
    }

    @Test
    void contextLoads() throws IOException {
        Path testData = Paths.get("webapp", "src", "test", "resources", "test_data乱序.txt");
        System.out.println(testData.toAbsolutePath());

        Path f = Files.createFile(testData);

        try (BufferedWriter writer = Files.newBufferedWriter(f)) {
            testContacts.stream().forEach(c -> {
                write(writer, Lists.newArrayList(new Identity(c.getIdentities().get(TYPE_ANONYMOUS_ID), TYPE_ANONYMOUS_ID)));
                if (c.isSilenceAuth()) {
                    write(writer, Lists.newArrayList(new Identity(c.getIdentities().get(TYPE_ANONYMOUS_ID), TYPE_ANONYMOUS_ID),
                            new Identity(c.getIdentities().get(TYPE_WECHAT_OPEN_ID), TYPE_WECHAT_OPEN_ID),
                            new Identity(c.getIdentities().get(TYPE_WECHAT_UNIOIN_ID), TYPE_WECHAT_UNIOIN_ID)));
                }
                if (c.isWebLogin()) {
                    write(writer, Lists.newArrayList(new Identity(c.getIdentities().get(TYPE_ANONYMOUS_ID), TYPE_ANONYMOUS_ID),
                            new Identity(c.getIdentities().get(TYPE_EXTERNAL_ID), TYPE_EXTERNAL_ID)));
                }
                if (c.isYouzan()) {
                    write(writer, Lists.newArrayList(new Identity(c.getIdentities().get(TYPE_MOBILE), TYPE_MOBILE),
                            new Identity(c.getIdentities().get(TYPE_YOUZAN_ID), TYPE_YOUZAN_ID),
                            new Identity(c.getIdentities().get(TYPE_WECHAT_OPEN_ID), TYPE_WECHAT_OPEN_ID)));
                }
                if (c.isShouqianba()) {
                    write(writer, Lists.newArrayList(new Identity(c.getIdentities().get(TYPE_SHOUQIANBA_ID), TYPE_SHOUQIANBA_ID),
                            new Identity(c.getIdentities().get(TYPE_WECHAT_OPEN_ID), TYPE_WECHAT_OPEN_ID)));
                }
                if (c.isSubscribe()) {
                    write(writer, Lists.newArrayList(
                            new Identity(c.getIdentities().get(TYPE_WECHAT_OPEN_ID), TYPE_WECHAT_OPEN_ID)));
                }
            });
            System.out.println(testContacts.stream().filter(Contact::isWxaRegister).count());
            testContacts.stream().forEach(c -> {
                write(writer, Lists.newArrayList(new Identity(c.getIdentities().get(TYPE_WXA_ANONYMOUS_ID), TYPE_ANONYMOUS_ID)));
                if (c.isWxaLogin()) {
                    write(writer, Lists.newArrayList(new Identity(c.getIdentities().get(TYPE_WXA_ANONYMOUS_ID), TYPE_ANONYMOUS_ID),
                            new Identity(c.getIdentities().get(TYPE_WECHAT_WXA_OPEN_ID), TYPE_WECHAT_OPEN_ID),
                            new Identity(c.getIdentities().get(TYPE_WECHAT_UNIOIN_ID), TYPE_WECHAT_UNIOIN_ID)));
                }
                if (c.isWxaRegister()) {
                    write(writer, Lists.newArrayList(new Identity(c.getIdentities().get(TYPE_WXA_ANONYMOUS_ID), TYPE_ANONYMOUS_ID),
                            new Identity(c.getIdentities().get(TYPE_EXTERNAL_ID), TYPE_EXTERNAL_ID),
                            new Identity(c.getIdentities().get(TYPE_MOBILE), TYPE_MOBILE)));
                }
            });
        }
    }

    private void write(BufferedWriter writer, List<Identity> identities) {
        try {
            writer.append(objectMapper.writeValueAsString(identities));
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String padLeftZeros(String inputString, int length) {
        return String.format("%1$" + length + "s", inputString).replace(' ', '0');
    }

}
