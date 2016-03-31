//package com.dslplatform.compiler.client.formatter.util;
//
//import com.dslplatform.compiler.client.formatter.Formatter;
//
//import org.junit.*;
//import static org.junit.Assert.assertSame;
//import static org.junit.Assert.assertEquals;
//
//public class NewlineTrimFormatterTest {
//    @Test
//    public void testEmptyBody() {
//        final String body = "";
//        assertSame(body, new NewlineTrimFormatter("NL").format(body));
//    }
//
//    @Test
//    public void testEmptyNewline() {
//        final String body = "XYZZY";
//        assertSame(body, new NewlineTrimFormatter("").format(body));
//    }
//
//    @Test
//    public void testNewlineOnly() {
//        final String body = "NL";
//        assertSame(body, new NewlineTrimFormatter(body).format(body));
//    }
//
//    @Test
//    public void testNoopTrim() {
//        final String body = "ANL";
//        assertSame(body, new NewlineTrimFormatter("NL").format(body));
//    }
//
//    @Test
//    public void testNewlinesOnly() {
//        final StringBuilder sb = new StringBuilder();
//        final Formatter formatter = new NewlineTrimFormatter("NL");
//
//        for (int i = 0; i < 10; i++) {
//            sb.append("NL");
//            assertEquals("NL", formatter.format(sb.toString()));
//        }
//    }
//
//    @Test
//    public void testPrefixTrims() {
//        final StringBuilder sb = new StringBuilder();
//        final Formatter formatter = new NewlineTrimFormatter("NL");
//
//        for (int i = 0; i < 10; i++) {
//            sb.append("NL");
//            assertEquals("ANL", formatter.format(sb + "ANL"));
//        }
//    }
//
//    @Test
//    public void testSuffixTrims() {
//        final StringBuilder sb = new StringBuilder();
//        final Formatter formatter = new NewlineTrimFormatter("NL");
//
//        for (int i = 0; i < 10; i++) {
//            sb.append("NL");
//            assertEquals("ANL", formatter.format("A" + sb));
//        }
//    }
//
//    @Test
//    public void testBothTrim() {
//        final StringBuilder sb = new StringBuilder();
//        final Formatter formatter = new NewlineTrimFormatter("NL");
//
//        for (int i = 0; i < 10; i++) {
//            sb.append("NL");
//            assertEquals("ANL", formatter.format(sb + "A" + sb));
//        }
//    }
//}
