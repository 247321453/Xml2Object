package net.kk.xml;

import net.kk.xml.annotations.XmlElement;
import org.junit.Test;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayOutputStream;

public class WeChatTest {
    static final String XML1 = "<xml>\n" +
            "   <appid>wx2421b1c4370ec43b</appid>\n" +
            "   <attach>支付测试</attach>\n" +
            "   <body>APP支付测试</body>\n" +
            "   <mch_id>10000100</mch_id>\n" +
            "   <nonce_str>1add1a30ac87aa2db72f57a2375d8fec</nonce_str>\n" +
            "   <notify_url>http://wxpay.wxutil.com/pub_v2/pay/notify.v2.php</notify_url>\n" +
            "   <out_trade_no>1415659990</out_trade_no>\n" +
            "   <spbill_create_ip>14.23.150.211</spbill_create_ip>\n" +
            "   <total_fee>1</total_fee>\n" +
            "   <trade_type>APP</trade_type>\n" +
            "   <sign>0CB01533B8C1EF103065174F50BCA001</sign>\n" +
            "</xml>";

    @XmlElement("xml")
    public static class WxRequest {
        String appid;
        String attach;
        String body;
        String mch_id;
        String nonce_str;
        String notify_url;
        String out_trade_no;
        String spbill_create_ip;
        String total_fee;
        String trade_type;
        String sign;

        @Override
        public String toString() {
            return "WxRequest{" +
                    "appid='" + appid + '\'' +
                    ", attach='" + attach + '\'' +
                    ", body='" + body + '\'' +
                    ", mch_id='" + mch_id + '\'' +
                    ", nonce_str='" + nonce_str + '\'' +
                    ", notify_url='" + notify_url + '\'' +
                    ", out_trade_no='" + out_trade_no + '\'' +
                    ", spbill_create_ip='" + spbill_create_ip + '\'' +
                    ", total_fee='" + total_fee + '\'' +
                    ", trade_type='" + trade_type + '\'' +
                    ", sign='" + sign + '\'' +
                    '}';
        }
    }

    static final String XML2 = "<xml>\n" +
            "   <return_code><![CDATA[SUCCESS]]></return_code>\n" +
            "   <return_msg><![CDATA[OK]]></return_msg>\n" +
            "   <appid><![CDATA[wx2421b1c4370ec43b]]></appid>\n" +
            "   <mch_id><![CDATA[10000100]]></mch_id>\n" +
            "   <nonce_str><![CDATA[IITRi8Iabbblz1Jc]]></nonce_str>\n" +
            "   <sign><![CDATA[7921E432F65EB8ED0CE9755F0E86D72F]]></sign>\n" +
            "   <result_code><![CDATA[SUCCESS]]></result_code>\n" +
            "   <prepay_id><![CDATA[wx201411101639507cbf6ffd8b0779950874]]></prepay_id>\n" +
            "   <trade_type><![CDATA[APP]]></trade_type>\n" +
            "</xml>";

    public static class WxResult {
        String return_code;
        String return_msg;
        String appid;
        String mch_id;
        String nonce_str;
        String sign;
        String result_code;
        String prepay_id;
        String trade_type;

        @Override
        public String toString() {
            return "WxResult{" +
                    "return_code='" + return_code + '\'' +
                    ", return_msg='" + return_msg + '\'' +
                    ", appid='" + appid + '\'' +
                    ", mch_id='" + mch_id + '\'' +
                    ", nonce_str='" + nonce_str + '\'' +
                    ", sign='" + sign + '\'' +
                    ", result_code='" + result_code + '\'' +
                    ", prepay_id='" + prepay_id + '\'' +
                    ", trade_type='" + trade_type + '\'' +
                    '}';
        }
    }

    @Test
    public void testRequest() throws Exception {
        XmlOptions DEFAULT = new XmlOptions.Builder()
                .enableSameAsList()
                .noXmlHead()
//                .ignoreNoAnnotation()
                .dontUseSetMethod()
                .useSpace()
                .build();
        XmlReader reader = new XmlReader(XmlPullParserFactory.newInstance().newPullParser(), DEFAULT);
        WxRequest request = reader.fromXml(WxRequest.class, XML1);
        System.out.println(request.toString());
        XmlWriter writer = new XmlWriter(XmlPullParserFactory.newInstance().newSerializer(), DEFAULT);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        writer.write(request, outputStream, null);
        System.out.println(outputStream.toString());
    }

    @Test
    public void testResult() throws Exception {
        XmlOptions DEFAULT = new XmlOptions.Builder()
                .enableSameAsList()
//                .ignoreNoAnnotation()
                .dontUseSetMethod()
                .useSpace()
                .build();
        XmlReader reader = new XmlReader(XmlPullParserFactory.newInstance().newPullParser(), DEFAULT);
        WxResult result = reader.fromXml(WxResult.class, XML2);
        System.out.println(result.toString());

    }
}
