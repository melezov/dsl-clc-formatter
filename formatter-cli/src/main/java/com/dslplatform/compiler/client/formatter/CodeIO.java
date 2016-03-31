package com.dslplatform.compiler.client.formatter;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class CodeIO {
    private final Logger logger;
    private final Charset charset;
    private final ThreadLocal<CharsetDecoder> charsetDecoder;
    private final ThreadLocal<CharsetEncoder> charsetEncoder;
    private final ThreadLocal<MessageDigest> messageDigest;

    public CodeIO(final Logger logger, final Charset charset) {
        this.logger = logger;
        this.charset = charset;

        this.charsetDecoder = new ThreadLocal<CharsetDecoder>() {
            @Override
            protected CharsetDecoder initialValue() {
                return charset.newDecoder().onMalformedInput(CodingErrorAction.REPORT);
            }
        };

        this.charsetEncoder = new ThreadLocal<CharsetEncoder>() {
            @Override
            protected CharsetEncoder initialValue() {
                return charset.newEncoder().onMalformedInput(CodingErrorAction.REPORT);
            }
        };

        this.messageDigest = new ThreadLocal<MessageDigest>() {
            @Override
            protected MessageDigest initialValue() {
                try {
                    return MessageDigest.getInstance("SHA-1");
                } catch (final NoSuchAlgorithmException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    private byte[] digestByteBuffer(final ByteBuffer buffer) {
        final MessageDigest md = messageDigest.get();
        md.reset();
        md.update(buffer.array(), 0, buffer.limit());
        return md.digest();
    }

    private ByteBuffer readFileToBuffer(final File source) throws IOException {
        final RandomAccessFile file = new RandomAccessFile(source, "r");
        try {
            final FileChannel channel = file.getChannel();
            try {
                final ByteBuffer buffer = ByteBuffer.allocate((int) channel.size());
                channel.read(buffer);
                buffer.rewind();
                return buffer;
            } finally {
                channel.close();
            }
        } finally {
            file.close();
        }
    }

    private String decodeByteBuffer(final File source, final ByteBuffer buffer) throws CharacterCodingException {
        return charsetDecoder.get().decode(buffer).toString();
    }

    public Code read(final File source) throws IOException {
        final File file = source.getAbsoluteFile();
        final ByteBuffer buffer = readFileToBuffer(file);

        try {
            final String body = decodeByteBuffer(file, buffer);
            final byte[] digest = digestByteBuffer(buffer);
            return new Code(file, body, digest);
        } catch (final CharacterCodingException e) {
            throw new IOException("Could not decode source file \"" + file + "\" via encoding \"" + charset + "\"", e);
        }
    }

    private ByteBuffer encodeToByteBuffer(final String code) throws CharacterCodingException {
        return charsetEncoder.get().encode(CharBuffer.wrap(code));
    }

    private void writeBufferToFile(final File target, final ByteBuffer buffer) throws IOException {
        final RandomAccessFile file = new RandomAccessFile(target, "rw");
        try {
            final FileChannel channel = file.getChannel();
            try {
                channel.write(buffer);
                file.setLength(buffer.limit());
            } finally {
                channel.close();
            }
        } finally {
            file.close();
        }
    }

    public void write(final Code target, final String body) throws IOException {
        final ByteBuffer buffer;
        try {
            buffer = encodeToByteBuffer(body);
        } catch (final CharacterCodingException e) {
            throw new IOException("Could not encode source file \"" + target + "\" via encoding \"" + charset + "\"", e);
        }

        final byte[] digest = digestByteBuffer(buffer);
        final File file = target.file;
        final Code replacement = new Code(file, body, digest);

        if (!target.equals(replacement)) {
            logger.info("Overwriting file: {}", target.file.getAbsolutePath());
            writeBufferToFile(file, buffer);
        } else {
            logger.debug("There were no changes to: {}", target.file.getAbsolutePath());
        }
    }
}
