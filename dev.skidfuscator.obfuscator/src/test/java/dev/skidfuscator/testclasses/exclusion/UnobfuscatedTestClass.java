package dev.skidfuscator.testclasses.exclusion;

import dev.skidfuscator.annotations.Exclude;
import dev.skidfuscator.testclasses.opaque.OpaqueConditionClazz;

import javax.crypto.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class UnobfuscatedTestClass {
    private static final byte[] data = new byte[] {0, 1, 4, 3, 2};

    public void run() {
        int stage = 0;
        try {
            // Begin stage
            stage = 1;

            // Call a function capable of throwing an exception, then update stage
            OpaqueConditionClazz.AES.main(new String[0]);
            stage = 2;

            if (data[0] == 0) {
                switch (data[1]) {
                    case 0:
                        stage = -2;
                        break;
                    case 1:
                        stage = 3;
                        break;
                    default:
                        stage = -5;
                        break;
                }
            } else {
                throw new IllegalArgumentException("Failed test! Stage: " + stage);
            }

            self(stage);

            throw new IllegalArgumentException("Failed test! Stage: " + stage);
        } catch (IllegalStateException e) {
            stage = 4;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException |
                 InvalidKeyException e) {
            e.printStackTrace();

            stage = -1;
        }

        if (data[2] != stage) {
            throw new IllegalArgumentException("Failed test! Stage: " + stage);
        }
    }

    private void self(final int stage) {
        if (stage == 3)
            throw new IllegalStateException("stage=" + stage);
    }

    /**
     * @author TheAlgorithms
     */
    public static class AES {
        private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

        /**
         * 1. Generate a plain text for encryption 2. Get a secret key (printed in
         * hexadecimal form). In actual use this must by encrypted and kept safe.
         * The same key is required for decryption.
         */
        public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
            String plainText = "Hello World";
            SecretKey secKey = getSecretEncryptionKey();
            byte[] cipherText = encryptText(plainText, secKey);
            String decryptedText = decryptText(cipherText, secKey);

            System.out.println();
            System.out.println("Original Text: " + plainText);
            System.out.println("AES Key (Hex Form): " + bytesToHex(secKey.getEncoded()));
            System.out.println("Encrypted Text (Hex Form): " + bytesToHex(cipherText));
            System.out.println("Decrypted Text: " + decryptedText);
        }

        /**
         * gets the AES encryption key. In your actual programs, this should be
         * safely stored.
         *
         * @return secKey (Secret key that we encrypt using it)
         * @throws NoSuchAlgorithmException (from KeyGenrator)
         */
        public static SecretKey getSecretEncryptionKey() throws NoSuchAlgorithmException {
            KeyGenerator aesKeyGenerator = KeyGenerator.getInstance("AES");
            aesKeyGenerator.init(128); // The AES key size in number of bits
            return aesKeyGenerator.generateKey();
        }

        /**
         * Encrypts plainText in AES using the secret key
         *
         * @return byteCipherText (The encrypted text)
         * @throws NoSuchPaddingException (from Cipher)
         * @throws NoSuchAlgorithmException (from Cipher)
         * @throws InvalidKeyException (from Cipher)
         * @throws BadPaddingException (from Cipher)
         * @throws IllegalBlockSizeException (from Cipher)
         */
        public static byte[] encryptText(String plainText, SecretKey secKey)
                throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
                IllegalBlockSizeException, BadPaddingException {
            // AES defaults to AES/ECB/PKCS5Padding in Java 7
            Cipher aesCipher = Cipher.getInstance("AES");
            aesCipher.init(Cipher.ENCRYPT_MODE, secKey);
            return aesCipher.doFinal(plainText.getBytes());
        }

        /**
         * Decrypts encrypted byte array using the key used for encryption.
         *
         * @return plainText
         */
        public static String decryptText(byte[] byteCipherText, SecretKey secKey)
                throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
                IllegalBlockSizeException, BadPaddingException {
            // AES defaults to AES/ECB/PKCS5Padding in Java 7
            Cipher aesCipher = Cipher.getInstance("AES");
            aesCipher.init(Cipher.DECRYPT_MODE, secKey);
            byte[] bytePlainText = aesCipher.doFinal(byteCipherText);
            return new String(bytePlainText);
        }

        /**
         * Convert a binary byte array into readable hex form Old library is
         * deprecated on OpenJdk 11 and this is faster regarding other solution is
         * using StringBuilder
         *
         * @return hexHash
         */
        public static String bytesToHex(byte[] bytes) {
            char[] hexChars = new char[bytes.length * 2];
            for (int j = 0; j < bytes.length; j++) {
                int v = bytes[j] & 0xFF;
                hexChars[j * 2] = HEX_ARRAY[v >>> 4];
                hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
            }
            return new String(hexChars);
        }
    }
}
